package com.hrra.parcial_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.hrra.parcial_final.Models.Pregunta;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView userr;
    private ImageView img;
    //private FirebaseAuth mAuth;
    String ID_PreguntaUltima;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference mStorage;
    Pregunta pregunta;
    CollectionReference preguntasLisa;
    LinearLayout contenedor;
    Spinner orderBY;
    boolean spiner = false;
    //CollectionReference users = db.collection("Usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.fotoPerfil);
        userr = findViewById(R.id.username);
        //usuario = getIntent().getStringExtra("user");
        mStorage = FirebaseStorage.getInstance().getReference();
        pregunta = new Pregunta();
        preguntasLisa = FirebaseFirestore.getInstance().collection("Preguntas");
        contenedor = findViewById(R.id.contender);
        contenedor.removeAllViews();
        orderBY = findViewById(R.id.sp_aplicar);

        findViewById(R.id.newPregunta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pregunta();
            }
        });

        findViewById(R.id.CerrarSesion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        });
        findViewById(R.id.aplicar).setOnClickListener(view -> {

            if(orderBY.getSelectedItem().toString().equals("Todas")){
                preguntas();
            }else{
                db.collection("Preguntas")
                        .whereEqualTo("categoria", orderBY.getSelectedItem().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    LinearLayout container = findViewById(R.id.contender);
                                    container.removeAllViewsInLayout();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Log.d(TAG, document.getId() + " => " + document.getData());
                                        View vista = getLayoutInflater().inflate(R.layout.pregunta,null);
                                        TextView pre = vista.findViewById(R.id.Pregunta);
                                        ImageView imgP = vista.findViewById(R.id.fotoPerfil);
                                        TextView des = vista.findViewById(R.id.Descripcion);
                                        TextView usser = vista.findViewById(R.id.Username);
                                        TextView fecha = vista.findViewById(R.id.Fecha);
                                        TextView categoria = vista.findViewById(R.id.categoria);

                        /*String h = "";
                        h = Pregunta.getId();*/
                                        pre.setText(document.getData().get("pregunta").toString());
                                        des.setText(document.getData().get("descripcion").toString());
                                        fecha.setText(document.getData().get("fecha").toString());
                                        categoria.setText(document.getData().get("categoria").toString());
                                        //categoria.setText(H+"");
                                        //ID_PreguntaUltima = Pregunta.getId();

                                        DocumentReference docRef = db.collection("Usuarios").document( document.getData().get("id_User").toString() );
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {


                                                        usser.setText(document.get("username").toString());

                                                        mStorage.child(document.get("imagen").toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                Picasso.get()
                                                                        .load(uri)
                                                                        .resize(50, 50)
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
                                        vista.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                container.removeAllViews();
                                                finish();
                                                startActivity(new Intent(MainActivity.this,Comentarios.class).putExtra("ID",document.getId()));


                                            }
                                        });
                                        container.addView(vista);
                                    }
                                } else {
                                    //Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }


        });

        //Toast.makeText(this, usuario, Toast.LENGTH_SHORT).show();
        Start();
        preguntas();
    }

    public void Start(){
        //FirebaseAuth.getInstance().getCurrentUser().getUid();
        //FirebaseUser user = mAuth.getCurrentUser();
        //usuario = user.getUid().toString();

        DocumentReference docRef = db.collection("Usuarios").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userr.setText(document.get("username").toString());

                        mStorage.child(document.get("imagen").toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get()
                                        .load(uri)
                                        .resize(50, 50)
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
                        /*mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get()
                                        .load(uri)
                                        .resize(50, 50)
                                        .centerCrop()
                                        .into(img);

                            }
                        });*/
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void pregunta(){
       // CardView Pregunta =
        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        //String stringDate= DateFor.format(date);
        View vista = getLayoutInflater().inflate(R.layout.pregunta_nuevas,null);
        final Dialog dialog = new Dialog(MainActivity.this);
        EditText txt_Pregunta = vista.findViewById(R.id.txt_Pregunta);
        EditText txt_Descripcion = vista.findViewById(R.id.txt_Desripcion);
        Button btn_guardar = vista.findViewById(R.id.btn_Guardar);
        Button btn_cancelar = vista.findViewById(R.id.btn_Cancelar);
        Spinner spn_Categoria = vista.findViewById(R.id.spinercategoria);
        //Switch estado = findViewById(R.id.swith);
        dialog.setContentView(vista);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pregunta.setPregunta(txt_Pregunta.getText().toString());
                pregunta.setDescripcion(txt_Descripcion.getText().toString());
                pregunta.setCategoria(spn_Categoria.getSelectedItem().toString());
                pregunta.setFecha(DateFor.format(date));
                pregunta.setId_User(FirebaseAuth.getInstance().getCurrentUser().getUid());
                db.collection("Preguntas").document().set(pregunta).addOnSuccessListener(Respuesta -> {

                });
                dialog.dismiss();
            }
        });
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*estado.setChecked(true);
                estado();
                txt.setText("Start\nRead a\nIf a = 5\na = a + 5\nelse\na = a - 5\nEndif\nPrint a\nEnd");*/
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void preguntas(){
        db.collection("Preguntas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    LinearLayout container = findViewById(R.id.contender);
                    //Toast.makeText(MainActivity.this, container.getChildCount(), Toast.LENGTH_SHORT).show();
                    container.removeAllViewsInLayout();
                    //Toast.makeText(MainActivity.this, container.getChildCount(), Toast.LENGTH_SHORT).show();
                    //int H = 0;
                    ID_PreguntaUltima = task.getResult().getDocuments().get(0).getId().toString();
                    for (QueryDocumentSnapshot Pregunta:task.getResult()) {

                        /*Toast.makeText(MainActivity.this,  Pregunta.getData().get("pregunta").toString(), Toast.LENGTH_SHORT).show();*/
                        View vista = getLayoutInflater().inflate(R.layout.pregunta,null);
                        TextView pre = vista.findViewById(R.id.Pregunta);
                        ImageView imgP = vista.findViewById(R.id.fotoPerfil);
                        TextView des = vista.findViewById(R.id.Descripcion);
                        TextView usser = vista.findViewById(R.id.Username);
                        TextView fecha = vista.findViewById(R.id.Fecha);
                        TextView categoria = vista.findViewById(R.id.categoria);

                        /*String h = "";
                        h = Pregunta.getId();*/
                        pre.setText(Pregunta.getData().get("pregunta").toString());
                        des.setText(Pregunta.getData().get("descripcion").toString());
                        fecha.setText(Pregunta.getData().get("fecha").toString());
                        categoria.setText(Pregunta.getData().get("categoria").toString());
                        //categoria.setText(H+"");
                        //ID_PreguntaUltima = Pregunta.getId();

                        DocumentReference docRef = db.collection("Usuarios").document( Pregunta.getData().get("id_User").toString() );
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {


                                        usser.setText(document.get("username").toString());

                                        mStorage.child(document.get("imagen").toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .resize(50, 50)
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
                        vista.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                container.removeAllViews();
                                finish();
                                startActivity(new Intent(MainActivity.this,Comentarios.class).putExtra("ID",Pregunta.getId()));


                            }
                        });
                        container.addView(vista);
                        //H++;
                        //ID_PreguntaUltima = Pregunta.getId();
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        preguntasLisa.addSnapshotListener((Evento,Error) -> {
            if(!(Evento == null)){
                if(!(ID_PreguntaUltima == Evento.getDocumentChanges( ).get(0).getDocument().getId())){
                    LinearLayout container = findViewById(R.id.contender);
                    View vista = getLayoutInflater().inflate(R.layout.pregunta,null);
                    TextView pre = vista.findViewById(R.id.Pregunta);
                    ImageView imgP = vista.findViewById(R.id.fotoPerfil);
                    TextView des = vista.findViewById(R.id.Descripcion);
                    TextView usser = vista.findViewById(R.id.Username);
                    TextView fecha = vista.findViewById(R.id.Fecha);
                    TextView categoria = vista.findViewById(R.id.categoria);


                    pre.setText(Evento.getDocumentChanges( ).get(0).getDocument().getData().get("pregunta").toString());
                    des.setText(Evento.getDocumentChanges( ).get(0).getDocument().getData().get("descripcion").toString());
                    fecha.setText(Evento.getDocumentChanges( ).get(0).getDocument().getData().get("fecha").toString());
                    categoria.setText(Evento.getDocumentChanges( ).get(0).getDocument().getData().get("categoria").toString());
                    //categoria.setText(Evento.getDocumentChanges( ).get(0).getDocument().getId());

                    DocumentReference docRef = db.collection("Usuarios").document( Evento.getDocumentChanges( ).get(0).getDocument().getData().get("id_User").toString() );
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {


                                    usser.setText(document.get("username").toString());
                                    //usser.setText("kevin");
                                    mStorage.child(document.get("imagen").toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get()
                                                    .load(uri)
                                                    .resize(50, 50)
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
                    vista.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            container.removeAllViews();
                            finish();
                            startActivity(new Intent(MainActivity.this,Comentarios.class).putExtra("ID",Evento.getDocumentChanges( ).get(0).getDocument().getId()));
                        }
                    });
                    container.addView(vista,0);
                }
                //Toast.makeText(this, ""+ Evento.getDocumentChanges().get(0).getDocument().getData().get("pregunta"), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, ""+ Evento.getDocumentChanges( ).get(0).getDocument().getData().get("pregunta"), Toast.LENGTH_SHORT).show();
                /*if(ID_PreguntaUltima == Evento.getDocumentChanges( ).get(0).getDocument().getId()){
                    Toast.makeText(this, "Funiona", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Nuevo documento", Toast.LENGTH_SHORT).show();
                }*/

            }else{

            }
            /*try {
                Toast.makeText(this, ""+ Evento.getDocumentChanges().get(0).getDocument().getData().get("pregunta"), Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }*/

        });
    }
}