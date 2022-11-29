package com.hrra.parcial_final;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hrra.parcial_final.Models.Usuarios;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email, password, user;
    private CircleImageView fotoPefil;
    private Usuarios usuario;
    LinearLayout LLRegistro, LLPerfil;
    StorageReference mStorage;
    Uri selectedImageUri;
    FirebaseUser actualUser;
    //Task<QuerySnapshot> Hola;
    //int SELECT_PICTURE = 200;
    private static final int GALLERY_INTENT = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        fotoPefil = findViewById(R.id.fotoPerfil);
        usuario = new Usuarios();
        user = findViewById(R.id.user);
        mStorage = FirebaseStorage.getInstance().getReference();
        LLRegistro = findViewById(R.id.container);
        LLPerfil = findViewById(R.id.Peril);

        findViewById(R.id.Registar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
                    Toast.makeText(Register.this, "Debe ingresar datos", Toast.LENGTH_SHORT).show();
                } else {
                    registar(email.getText().toString(), password.getText().toString());
                    LLRegistro.setVisibility(View.GONE);
                    LLPerfil.setVisibility(View.VISIBLE);
                }
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        findViewById(R.id.fotoPerfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*db.collection("Usuarios")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Log.d(TAG, document.getId() + " => " + document.getData());
                                        Toast.makeText(Register.this,document.getId() + " => " + document.getData(),Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });*/

               /*db.collection("Usuarios")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Hola = task;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Log.d(TAG, document.getId() + " => " + document.getData());
                                        Toast.makeText(Register.this,document.getId() + " => " + document.getData(),Toast.LENGTH_SHORT).show();
                                        Hola = task;
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });*/

                //Actualizar datos
               DocumentReference usuarioReference = db.collection("Usuarios").document(actualUser.getUid());
               DocumentReference imgReference = db.collection("Usuarios").document(actualUser.getUid());

                usuarioReference
                        .update("username", user.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
                if (null != selectedImageUri) {
                    imgReference.update("imagen", "imgPerfil"+user.getText().toString() + ".jpg")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });

                    StorageReference almacenar = mStorage.child("imgPerfil"+user.getText().toString() + ".jpg" );
                    mStorage = almacenar;

                    almacenar.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        /* mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get()
                                        .load(uri)
                                        .resize(50, 50)
                                        .centerCrop()
                                        .into(imagen);

                            }
                        });*/
                            Log.d("img", "onSuccess: Imagen subida correctamente");
                        }
                    });
                }
                startActivity(new Intent(Register.this, Login.class));
                //leer datos
                /*DocumentReference docRef = db.collection("Usuarios").document("LA");
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        usuario = documentSnapshot.toObject(Usuarios.class);
                    }
                });

                Toast.makeText(Register.this,usuario.getId(),Toast.LENGTH_SHORT).show();*/
            }
        });
    }
    void imageChooser() {
        Intent im = new Intent(Intent.ACTION_PICK);
        im.setType("image/*");
        startActivityForResult(im, GALLERY_INTENT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                fotoPefil.setImageURI(selectedImageUri);
            }
        }
    }

    private void registar(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            actualUser = mAuth.getCurrentUser();
                            usuario.setEmail(email);
                            usuario.setId(actualUser.getUid());
                            usuario.setUsername("");
                            usuario.setImagen("");

                            db.collection("Usuarios").document(actualUser.getUid()).set(usuario);

                            //Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Coreo ya registrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}