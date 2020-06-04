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

public class registeractivity extends AppCompatActivity {

    private EditText regemail;
    private EditText regpass;
    private EditText regconpass;
    private Button registerbtn;
    private Button registerloginbtn;
    private ProgressBar registerprogress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity);

        mAuth=FirebaseAuth.getInstance();

        regemail=(EditText)findViewById(R.id.regemailid);
        regpass=(EditText)findViewById(R.id.regpassid);
        regconpass=(EditText)findViewById(R.id.regconfirmid);
        registerbtn=(Button)findViewById(R.id.regbuttonid);
        registerloginbtn=(Button)findViewById(R.id.regloginbtnid);
        registerprogress=(ProgressBar)findViewById(R.id.regprogressbarid);

        registerloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mainintent=new Intent(registeractivity.this,loginactivity.class);
//                startActivity(mainintent);
                finish();
            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=regemail.getText().toString();
                String pass=regpass.getText().toString();
                String confirmpass=regconpass.getText().toString();

                if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(pass)&&!TextUtils.isEmpty(confirmpass))
                {
                    if(pass.equals(confirmpass))
                    {
                        registerprogress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                     if(task.isSuccessful())
                                     {
                                         Intent setupintent=new Intent(registeractivity.this,setupactivity.class);
                                         startActivity(setupintent);
                                         finish();

                                     }
                                     else
                                     {
                                         String e=task.getException().getMessage();
                                         Toast.makeText(registeractivity.this, e, Toast.LENGTH_SHORT).show();
                                     }
                                     registerprogress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(registeractivity.this, "Password and confirm password feild not matched", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(registeractivity.this, "Please fill all the feilds first", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser=mAuth.getCurrentUser();
        if(currentuser!=null)
        {
            sendtomain();
        }
    }

    private void sendtomain() {
        Intent mainintent=new Intent(registeractivity.this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }
}
