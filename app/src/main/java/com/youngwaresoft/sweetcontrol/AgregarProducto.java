package com.youngwaresoft.sweetcontrol;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AgregarProducto extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> producto = new HashMap<>();
    final ArrayList<Producto> productos = new ArrayList<Producto>();
    Button btnAdd;
    EditText txtNombre, txtDesc, txtPrecio, txtCodigo;
    String cbarras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnAdd=findViewById(R.id.btnAgregarProd);
        txtNombre=findViewById(R.id.txtNombreProd);
        txtDesc=findViewById(R.id.txtDescripcionProd);
        txtPrecio=findViewById(R.id.txtPrecioProd);
        txtCodigo=findViewById(R.id.txtCodigoProd);
        cbarras="";

        descarga();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                cbarras="";

                onScannear();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {//Agregar el producto
            @Override
            public void onClick(View v) {
                if(isAllFull())
                    registrarProd();
                else
                    Toast.makeText(AgregarProducto.this, "Debes llenar todos los campos requeridos", Toast.LENGTH_SHORT).show();



            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean isAllFull(){
        if (txtNombre.getText().toString().isEmpty())
            return false;
        if (txtCodigo.getText().toString().isEmpty())
            return false;
        if (txtPrecio.getText().toString().isEmpty())
            return false;


        return true;
    }

    private void cleanF(){
        txtNombre.setText("");
        txtCodigo.setText("");
        txtDesc.setText("");
        txtPrecio.setText("");
    }

    private void registrarProd(){

        //onScannear();
        if(isUnique(txtCodigo.getText().toString())){
            Producto prod = new Producto(txtNombre.getText().toString(),Double.parseDouble(txtPrecio.getText().toString()),
                    txtDesc.getText().toString(),txtCodigo.getText().toString());
            subirFB(prod);
        }else {
            cleanF();
            Toast.makeText(this, "Error: el producto ya ha sido registrado.", Toast.LENGTH_SHORT).show();
        }





    }

    private void subirFB(Producto p){
        producto.put("codigo",p.getCodigo());
        producto.put("nombre",p.getNombre());
        producto.put("descripcion",p.getDesc());
        producto.put("precio",p.getPrecio());


        // Add a new document with a generated ID
        db.collection("productos")
                .add(producto)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                       // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(AgregarProducto.this, "ID: "+ documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AgregarProducto.this, "Error al subir a FireBase.", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void onScannear(){
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

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result!=null){
            if(result.getContents()==null){
                Toast.makeText(this,"Cancelado",Toast.LENGTH_SHORT).show();
            }else {
                cbarras=result.getContents().toString(); //Aqui esta el codigo =============================================================
                txtCodigo.setText(cbarras);
                //Toast.makeText(this,"Codigo: "+codigoobt,Toast.LENGTH_LONG).show();

            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    private boolean isUnique(String c){
        for (int i=0;i<productos.size();i++){
            if (productos.get(i).getCodigo().equals(c))
                return false;
        }
        return true;
    }

}
