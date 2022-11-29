package com.hrra.parcial_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    EditText user,pass;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if( FirebaseAuth.getInstance().getCurrentUser()!=null){


            finish();
            Intent h = new Intent(Login.this,MainActivity.class);
            /*FirebaseUser user = mAuth.getCurrentUser();
            // Toast.makeText(Login.this, user.getUid(), Toast.LENGTH_SHORT).show();
            h.putExtra("user",user.getUid());*/
            startActivity(h);
        }
        //FirebaseApp.initializeApp();
        mAuth = FirebaseAuth.getInstance();

        user = findViewById(R.id.username);
        pass = findViewById(R.id.pass);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = user.getText().toString();
                String password = pass.getText().toString();

                if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(Login.this,"Debe ingresar datos",Toast.LENGTH_SHORT).show();
                }else{
                    login(email,password);
                }
            }
        });
        findViewById(R.id.SinUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    private void login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            //Intent h = new Intent(Login.this,MainActivity.class);
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(Login.this,MainActivity.class));
                           // Toast.makeText(Login.this, user.getUid(), Toast.LENGTH_SHORT).show();
                           /* h.putExtra("user",user.getUid());
                            startActivity(h);*/

                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            // FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

}