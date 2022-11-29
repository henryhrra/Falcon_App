package com.hrra.parcial_final;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hrra.parcial_final.Models.Comentario;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.text.DateFormat;

public class Comentarios extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference mStorage;
    TextView username, pregunta, descripcion;
    ImageView img;
    EditText txt_Comentatio;
    Comentario comentario;
    String ID_PreguntaUltima = "";
    CollectionReference preguntasLisa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        start(getIntent().getStringExtra("ID"));

        mStorage = FirebaseStorage.getInstance().getReference();
        username = findViewById(R.id.Username);
        pregunta = findViewById(R.id.Pregunta);
        descripcion = findViewById(R.id.Descripcion);
        img = findViewById(R.id.fotoPerfil);
        txt_Comentatio = findViewById(R.id.comentario);
        comentario = new Comentario();
        preguntasLisa = FirebaseFirestore.getInstance().collection("Comentarios");

        findViewById(R.id.Publicar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txt_Comentatio.getText().toString().isEmpty()){
                    comentario.setComentario(txt_Comentatio.getText().toString());
                    comentario.setIdPregunta(getIntent().getStringExtra("ID"));
                    comentario.setIdUsuerio(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    db.collection("Comentarios").document().set(comentario);
                    txt_Comentatio.setText("");
                }

            }
        });
        comentarios();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                startActivity(new Intent(Comentarios.this,MainActivity.class));
            }
        };
        //requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== event.KEYCODE_BACK){
            startActivity(new Intent(Comentarios.this, MainActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    public void comentarios(){
        try {
            db.collection("Comentarios")
                    .whereEqualTo("idPregunta", getIntent().getStringExtra("ID"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(!task.getResult().isEmpty()){
                                    ID_PreguntaUltima = task.getResult().getDocuments().get(0).getId();
                                    //Toast.makeText(Comentarios.this, task.getResult().getDocuments().get(0).getData().get("comentario").toString(), Toast.LENGTH_SHORT).show();
                                    LinearLayout container = findViewById(R.id.Container_comentarios);
                                    container.removeAllViews();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Log.d(TAG, document.getId() + " => " + document.getData());

                                        View vista = getLayoutInflater().inflate(R.layout.comentario,null);
                                        TextView u = vista.findViewById(R.id.Usuario);
                                        TextView c = vista.findViewById(R.id.comentario);
                                        ImageView imgP = vista.findViewById(R.id.fotoPerfil);
                                        c.setText(document.get("comentario").toString());
                                        container.addView(vista);
                                        DocumentReference docRef = db.collection("Usuarios").document( document.get("idUsuerio").toString() );
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {


                                                        u.setText(document.get("username").toString());
                                                        //Toast.makeText(Comentarios.this, document.get("imagen").toString(), Toast.LENGTH_SHORT).show();

                                                        mStorage.child(document.get("imagen").toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                Picasso.get()
                                                                        .load(uri)
                                                                        .resize(150, 150)
                                                                        .centerCrop()
                                                                        .into(imgP);
                                                                // Got the download URL for 'users/me/profile.png'
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception exception) {
                                                                // Handle any errors
                                                            }
                                                        });

                                                    } else {
                                                        //Log.d(TAG, "No such document");
                                                    }
                                                } else {
                                                    //Log.d(TAG, "get failed with ", task.getException());
                                                }
                                            }
                                        });


                                        //Toast.makeText(Comentarios.this, document.get("comentario").toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {
                                //Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        catch(Exception e) {
            //  Block of code to handle errors
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            preguntasLisa.addSnapshotListener((Evento,Error) -> {
                if(!(Evento == null)){
                    if(Evento.getDocumentChanges().get(0).getDocument().getData().get("idPregunta").equals(getIntent().getStringExtra("ID"))){
                        if(!(ID_PreguntaUltima == Evento.getDocumentChanges().get(0).getDocument().getId())){
                            Toast.makeText(this, Evento.getDocumentChanges( ).get(0).getDocument().getData().get("comentario").toString(), Toast.LENGTH_SHORT).show();

                            LinearLayout container = findViewById(R.id.Container_comentarios);
                            View vista = getLayoutInflater().inflate(R.layout.comentario,null);
                            TextView u = vista.findViewById(R.id.Usuario);
                            TextView c = vista.findViewById(R.id.comentario);
                            ImageView imgP = vista.findViewById(R.id.fotoPerfil);
                            c.setText(Evento.getDocumentChanges( ).get(0).getDocument().getData().get("comentario").toString());
                            container.addView(vista,0);
                            DocumentReference docRef = db.collection("Usuarios").document(Evento.getDocumentChanges( ).get(0).getDocument().getData().get("idUsuerio").toString());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {


                                            u.setText(document.get("username").toString());
                                            //Toast.makeText(Comentarios.this, document.get("imagen").toString(), Toast.LENGTH_SHORT).show();

                                            mStorage.child(document.get("imagen").toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Picasso.get()
                                                            .load(uri)
                                                            .resize(150, 150)
                                                            .centerCrop()
                                                            .into(imgP);
                                                    // Got the download URL for 'users/me/profile.png'
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle any errors
                                                }
                                            });

                                        } else {
                                            //Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        //Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                        //Toast.makeText(this, Evento.getDocumentChanges( ).get(0).getDocument().getData().get("comentario").toString(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(this, Evento.getDocumentChanges( ).get(0).getDocument().getId(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch(Exception e) {
            //  Block of code to handle errors
        }

    }

    public void start(String ID) {
        //Toast.makeText(this, getIntent().getStringExtra("ID"), Toast.LENGTH_SHORT).show();
        DocumentReference docRef = db.collection("Preguntas").document(ID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Toast.makeText(Comentarios.this, document.get("pregunta").toString(), Toast.LENGTH_SHORT).show();
                        pregunta.setText(document.get("pregunta").toString());
                        descripcion.setText(document.get("descripcion").toString());
                        DocumentReference docRef = db.collection("Usuarios").document( document.get("id_User").toString() );
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {


                                        username.setText(document.get("username").toString());
                                        //Toast.makeText(Comentarios.this, document.get("imagen").toString(), Toast.LENGTH_SHORT).show();

                                        mStorage.child(document.get("imagen").toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .resize(150, 150)
                                                        .centerCrop()
                                                        .into(img);
                                                // Got the download URL for 'users/me/profile.png'
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle any errors
                                            }
                                        });

                                    } else {
                                        //Log.d(TAG, "No such document");
                                    }
                                } else {
                                    //Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    } else {

                    }
                } else {

                }
            }
        });
    }

}