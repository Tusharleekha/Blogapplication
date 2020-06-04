package com.example.blogapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginactivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginbtn;
    private Button loginregisterbtn;
    private ProgressBar loginprogress;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        mAuth=FirebaseAuth.getInstance();
        email=(EditText)findViewById(R.id.emailid);
        password=(EditText)findViewById(R.id.passwordid);
        loginbtn=(Button)findViewById(R.id.loginbtnid);
        loginregisterbtn=(Button)findViewById(R.id.loginregisterbtnid);
        loginprogress=(ProgressBar)findViewById(R.id.loginprogressid);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String loginemail = email.getText().toString();
                String loginpass = password.getText().toString();

                if (!TextUtils.isEmpty(loginemail) && !TextUtils.isEmpty(loginpass)) {

                    loginprogress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginemail,loginpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                sendtomain();
                            }
                            else
                            {
                                String errormsg=task.getException().getMessage();
                                Toast.makeText(loginactivity.this, errormsg, Toast.LENGTH_SHORT).show();

                            }
                            loginprogress.setVisibility(View.INVISIBLE);
                        }
                    });

                }
                else
                {
                    Toast.makeText(loginactivity.this, "PLEASE ENTER EMAIL AND PASSWORD", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginregisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginactivity.this,registeractivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentuser=mAuth.getCurrentUser();

        if(currentuser!=null)
        {
           sendtomain();
        }
    }

    private void sendtomain() {

        Intent mainintent=new Intent(loginactivity.this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }
}
