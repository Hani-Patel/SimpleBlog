package com.hani.android.simpleblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleBlogActivity extends AppCompatActivity {

    private String mPost_key=null;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private ImageView mBlogSingleImage;
    private TextView mBlogSingleTitle;
    private TextView mBlogSingleDesc;
    private Button mSingleRemoveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);

        mAuth=FirebaseAuth.getInstance();

        mPost_key=getIntent().getExtras().getString("blog_id");
        //Toast.makeText(SingleBlogActivity.this,post_key,Toast.LENGTH_LONG).show();

        mBlogSingleImage=(ImageView)findViewById(R.id.singleblogpost_image);
        mBlogSingleTitle=(TextView)findViewById(R.id.singleblogpost_title);
        mBlogSingleDesc=(TextView)findViewById(R.id.singleblogpost_desc);
        mSingleRemoveBtn=(Button)findViewById(R.id.btnRemovePost);



        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title=(String)dataSnapshot.child("title").getValue();
                String post_desc=(String)dataSnapshot.child("description").getValue();
                String post_image=(String)dataSnapshot.child("image").getValue();
                String post_uid=(String)dataSnapshot.child("uid").getValue();

                mBlogSingleTitle.setText(post_title);
                mBlogSingleDesc.setText(post_desc);

                Picasso.with(SingleBlogActivity.this).load(post_image).into(mBlogSingleImage);

                if(mAuth.getCurrentUser().getUid().equals(post_uid)){
                    mSingleRemoveBtn.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mDatabase.child(mPost_key).removeValue();

                Intent mainIntent=new Intent(SingleBlogActivity.this,MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            }
        });

    }

}
