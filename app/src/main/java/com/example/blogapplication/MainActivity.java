package com.example.blogapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Toolbar maintoolbar;
    private FirebaseAuth mAuth;
    private FloatingActionButton addpostbtn;
    private FirebaseFirestore firebaseFirestore;
    private String curruserid;
    private BottomNavigationView mainbottom;
    private homefragment homeFragment;
    private notificationfragment notificationFragment;
    private accountfragment accountFragment;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        maintoolbar = (Toolbar) findViewById(R.id.maintoolbarid);
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setTitle("Bloggers App");

        if (mAuth.getCurrentUser() != null) {
            mainbottom = findViewById(R.id.mainbottomnew);
            homeFragment = new homefragment();
            notificationFragment = new notificationfragment();
            accountFragment = new accountfragment();

            replaceFragment(homeFragment);

            mainbottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override

                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottomhomeid:
                            replaceFragment(homeFragment);
                            return true;
//                        case R.id.bottomnotificationid:
//                            replaceFragment(notificationFragment);
//                            return true;
                        case R.id.bottomaccountid:
                            replaceFragment(accountFragment);
                            return true;
                        default:
                            return false;

                    }
                }
            });


            addpostbtn = findViewById(R.id.addpostbtn);
            addpostbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, postblogactivity.class);
                    startActivity(intent);
                }
            });
        }
    }

        @Override
        protected void onStart () {
            super.onStart();
            FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentuser == null) {
                senttologin();
            }
//        else
//        {
//            curruserid=mAuth.getCurrentUser().getUid();
//            firebaseFirestore.collection("Users").document(curruserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                    if(task.isSuccessful())
//                    {
//                        if(task.getResult().exists())
//                        {
//                            Intent intent=new Intent(MainActivity.this,setupactivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                        else
//                        {
//                            String e=task.getException().getMessage();
//                            Toast.makeText(MainActivity.this, e, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            });
//        }

        }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){

            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;

        }

        @Override
        public boolean onOptionsItemSelected (@NonNull MenuItem item){

            switch (item.getItemId()) {
                case R.id.actionlogoutbtnid:
                    logout();
                    return true;

                case R.id.settingbtnid:
                    Intent settingintent = new Intent(MainActivity.this, setupactivity.class);
                    startActivity(settingintent);
                    return true;

                default:
                    return false;
            }
        }

        private void logout () {

            mAuth.signOut();

            senttologin();
        }
        private void senttologin () {

            Intent intent = new Intent(MainActivity.this, loginactivity.class);
            startActivity(intent);
            finish();
        }

        private void replaceFragment (Fragment fragment)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.maincontainer, fragment);
            fragmentTransaction.commit();
        }

}
