package com.youngwaresoft.sweetcontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogin, btnScanear;
    private EditText txtUser, txtPass;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final ArrayList<Producto> productos = new ArrayList<Producto>();
    String codigoobt = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        btnLogin=findViewById(R.id.btnLogin);
        txtUser=findViewById(R.id.txtUser);
        txtPass=(EditText)findViewById(R.id.txtPass);
        btnScanear=findViewById(R.id.btnBuscarC);




        descarga();//Bajar los datos

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singIn(String.valueOf(txtUser.getText().toString()), txtPass.getText().toString());
            }
        });

        btnScanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScannear();
            }
        });





    }

    public void singIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent in=new Intent(getApplicationContext(),MainActivity.class);
                            in.putExtra("SESSION_ID",user);
                            startActivity(in);

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    public void descarga() {

        db.collection("productos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                productos.add(document.toObject(Producto.class));
                                Toast.makeText(getApplicationContext(), "Carga completa", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error conectandose al servidor. EC:" + task.getException(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public void onScannear() {
        IntentIntegrator intent = new IntentIntegrator(this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);

        intent.setPrompt("ESCANEE EL PRODUCTO");
        intent.setCameraId(0);
        intent.setBeepEnabled(true);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
            } else {
                codigoobt = result.getContents().toString(); //Aqui esta el codigo =============================================================
                //Toast.makeText(this,"Codigo: "+codigoobt,Toast.LENGTH_LONG).show();
                String resu="";
                resu=busquedaPC(codigoobt);
                Toast.makeText(this, "Precio: " + resu, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String busquedaPC(String codi){
        for (int i=0;i<productos.size();i++){
            if (productos.get(i).getCodigo().equals(codi))
                return String.valueOf(productos.get(i).getPrecio());
        }
        return "";
    }





}
