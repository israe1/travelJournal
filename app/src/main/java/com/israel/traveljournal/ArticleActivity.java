package com.israel.traveljournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ArticleActivity extends AppCompatActivity {
    static final int PICTURE_RESULT = 42;
    DatabaseReference mReference;
    Article mArticle;
    ImageView mImageView;
    Button mButtonUpload;
    TextInputEditText mEditTextName;
    TextInputEditText mEditTextDescription;
    Button mButtonSave;
    Uri imageUri;
    boolean admin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        mImageView = findViewById(R.id.iv_article);
        mButtonUpload = findViewById(R.id.btn_upload);
        mEditTextName = findViewById(R.id.et_name);
        mEditTextDescription = findViewById(R.id.et_description);
        mButtonSave = findViewById(R.id.btn_save);
        mReference = FirebaseDatabase.getInstance().getReference().child("articles");
        Article article = getIntent().getParcelableExtra("article");
        if (article == null){
            article = new Article();
        }else {
            admin = article.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mButtonSave.setEnabled(admin);
            mButtonUpload.setEnabled(admin);
            mEditTextName.setEnabled(admin);
            mEditTextDescription.setEnabled(admin);
        }
        mArticle = article;
        mEditTextName.setText(mArticle.getName());
        mEditTextDescription.setText(mArticle.getDescription());
        if (article.getImageUrl() != null)imageUri = Uri.parse(article.getImageUrl());
        showImage(mArticle.getImageUrl());
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT);
            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveArticle();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            if (data == null)return;
            imageUri = data.getData();
            mImageView.setImageURI(imageUri);
        }
    }

    private void saveArticle(){
        final LoadingDialog dialog = new LoadingDialog();
        dialog.show(getSupportFragmentManager(), "LoadingDialog");
        mArticle.setName(mEditTextName.getText().toString());
        mArticle.setDescription(mEditTextDescription.getText().toString());
        mArticle.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (!imageUri.toString().contains("http")){
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("images").child(imageUri.getLastPathSegment());
            reference.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            mArticle.setImageUrl(task.getResult().toString());
                            if (mArticle.getId() == null){
                                String key = mReference.push().getKey();
                                mArticle.setId(key);
                            }
                            mReference.child(mArticle.getId()).setValue(mArticle);
                            dialog.dismiss();
                            Toast.makeText(ArticleActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(ArticleActivity.this, "The image cannot be uploaded", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            mArticle.setImageUrl(imageUri.toString());
            if (mArticle.getId() == null){
                String key = mReference.push().getKey();
                mArticle.setId(key);
            }
            mReference.child(mArticle.getId()).setValue(mArticle);
            dialog.dismiss();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void showImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty())return;
        Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(mImageView);
    }
}