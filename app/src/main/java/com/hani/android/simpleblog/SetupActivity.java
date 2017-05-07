package com.hani.android.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {
     private EditText mNameField;
     private Button   mSubmitbtn;
     private ImageButton mSetupprofile;

     private Uri mImageUri=null;

     private static final int GALLERY_REQUEST=1;

     private DatabaseReference mDatabaseUsers;

     private FirebaseAuth mAuth;

     private StorageReference mStorageImage;

     private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth=FirebaseAuth.getInstance();

        mStorageImage= FirebaseStorage.getInstance().getReference().child("profile_images");

        mProgress=new ProgressDialog(this);

        mSetupprofile=(ImageButton)findViewById(R.id.profileimage);
        mNameField=(EditText)findViewById(R.id.etSetupNameField);
        mSubmitbtn=(Button)findViewById(R.id.btnSetupSubmit);

        mSubmitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });

        mSetupprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

    }

    private void startSetupAccount() {
      final String name=mNameField.getText().toString().trim();
      final String u_id=mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name) && mImageUri!=null){
            mProgress.setMessage("Setting up account..");
            mProgress.show();

            StorageReference filepath=mStorageImage.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri=taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUsers.child(u_id).child("name").setValue(name);
                    mDatabaseUsers.child(u_id).child("image").setValue(downloadUri);

                    mProgress.dismiss();

                    Intent mintent=new Intent(SetupActivity.this,MainActivity.class);
                    mintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mintent);

                }
            });



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){

            Uri imageuri=data.getData();

            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUri = result.getUri();

                mSetupprofile.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
