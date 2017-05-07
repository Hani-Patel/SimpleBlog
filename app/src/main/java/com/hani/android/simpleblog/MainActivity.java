package com.hani.android.simpleblog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.hani.android.simpleblog.R.id.action_add;
import static com.hani.android.simpleblog.R.id.action_logout;
import static com.hani.android.simpleblog.R.id.action_setting;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;

    private DatabaseReference mDatabaseUsers;

    private DatabaseReference mDatabaseLike;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private boolean mProcessLike=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
              if(firebaseAuth.getCurrentUser()==null){
                  Intent loginintent=new Intent(MainActivity.this,LoginActivity.class);
                  loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  startActivity(loginintent);

              }
            }
        };

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference("Likes");

        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);

        mBlogList=(RecyclerView)findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        if(mAuth.getCurrentUser()!=null) {
            checkUserExist();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_raw,
                BlogViewHolder.class,
                mDatabase) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                final String post_key=getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(post_key);

                 viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                        // Toast.makeText(MainActivity.this,post_key,Toast.LENGTH_LONG).show();

                         Intent singleblogIntent=new Intent(MainActivity.this,SingleBlogActivity.class);
                         singleblogIntent.putExtra("blog_id",post_key);
                         startActivity(singleblogIntent);

                     }
                 });

                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike=true;


                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(mProcessLike){

                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                        mProcessLike = false;

                                    } else {
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random value");

                                        mProcessLike = false;
                                    }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    }

                });
            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

       private void checkUserExist() {
        final String user_id=mAuth.getCurrentUser().getUid();
         mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user_id)){

                    Intent loginintent=new Intent(MainActivity.this,LoginActivity.class);
                    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginintent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;

        ImageButton mLikebtn;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mView=itemView;

            mLikebtn=(ImageButton)mView.findViewById(R.id.like_btn);

            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth=FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);

        }

        public void setLikeBtn(final String post_key){

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        mLikebtn.setImageResource(R.drawable.rlike);

                    }else {
                      mLikebtn.setImageResource(R.drawable.glike);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title){
            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setDescription(String description){
            TextView post_desc=(TextView)mView.findViewById(R.id.post_desc);
            post_desc.setText(description);
        }
        public void setImage(Context ctx,String image){
            ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setUsername(String username){
            TextView post_username=(TextView)mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId()==action_logout){
            logout();
        }
        if (item.getItemId()==action_setting){
            startActivity(new Intent(MainActivity.this,Setting.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
    mAuth.signOut();
    }
}
