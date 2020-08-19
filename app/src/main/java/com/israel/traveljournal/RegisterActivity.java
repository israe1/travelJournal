package com.israel.traveljournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText mEditTextEmail;
    TextInputEditText mEditTextPassword;
    TextInputEditText mEditTextCPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEditTextEmail = findViewById(R.id.et_email);
        mEditTextPassword = findViewById(R.id.et_password);
        mEditTextCPassword = findViewById(R.id.et_confirm_password);
    }

    public void handleAuth(View view) {
        if (view.getId() == R.id.btn_register){
            if (!TextUtils.isEmpty(mEditTextEmail.getText())
                    && !TextUtils.isEmpty(mEditTextPassword.getText())
                    && !TextUtils.isEmpty(mEditTextCPassword.getText())
            ){
                if (TextUtils.equals(mEditTextPassword.getText(), mEditTextCPassword.getText())){
                    final LoadingDialog dialog = new LoadingDialog();
                    dialog.show(getSupportFragmentManager(), "LoadingDialog");
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                            mEditTextEmail.getText().toString(),
                            mEditTextPassword.getText().toString()
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()){
                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                finish();
                            }else {
                                Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(this, "Password confirmation is different from password", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        }
    }
}