package com.hani.android.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText mLoginEmailField;
    private EditText mLoginPasswordField;
    private Button mLoginBtn;
    private Button mNewAccount;

    private ProgressDialog mProgress;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference();
        mDatabaseUsers.keepSynced(true);

        mProgress=new ProgressDialog(this);

        mLoginEmailField=(EditText)findViewById(R.id.loginemailField);
        mLoginPasswordField=(EditText)findViewById(R.id.loginpasswordField);
        mLoginBtn=(Button)findViewById(R.id.btnLogin);
        mNewAccount=(Button)findViewById(R.id.btnnewAccount);

        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rIntent=new Intent(LoginActivity.this,RegisterActivity.class);
                rIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(rIntent);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginCheck();
            }
        });

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    private void loginCheck() {
        String email=mLoginEmailField.getText().toString().trim();
        String password=mLoginPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Checking Login...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful()){

                       mProgress.dismiss();

                       checkUserExist();
                   }
                   else {
                       mProgress.dismiss();

                       Toast.makeText(LoginActivity.this,"Error login",Toast.LENGTH_LONG).show();
                    }
                 }
             });
        }
        else {
            Toast.makeText(LoginActivity.this,"Field Empty",Toast.LENGTH_LONG).show();
        }
    }

    private void checkUserExist() {
      //final String user_id=mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               //if(dataSnapshot.hasChild(user_id)){

                   Intent mainintent=new Intent(LoginActivity.this,MainActivity.class);
                   mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(mainintent);


               //}else{
                 //  Intent mainintent=new Intent(LoginActivity.this,MainActivity.class);
                  // mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  // startActivity(mainintent);

               //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
