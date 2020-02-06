package com.youngwaresoft.sweetcontrol;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Dbloader {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final ArrayList<Producto> productoser = new ArrayList<Producto>();


    public Dbloader(){

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
                                productoser.add(document.toObject(Producto.class));
                                //Toast.makeText(getApplicationContext(), "Carga completa", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //Toast.makeText(getApplicationContext(), "Error conectandose al servidor. EC:" + task.getException(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }



}
