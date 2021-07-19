package com.example.smartparksystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.smartparksystem.adapters.CarListAdapter;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import model.Parking;

import static android.R.layout.*;

public class ViewCarsActivity extends AppCompatActivity {
    DatabaseReference carsRef;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ListView carList;
    Button car;
    ArrayList<Parking> parkingArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cars);

        carList=findViewById(R.id.carsList);
        parkingArrayList=new ArrayList<>();
        car=findViewById(R.id.new_car);

        database=FirebaseDatabase.getInstance();
        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewCarsActivity.this,MainActivity.class));
            }
        });

        loadCars();
    }

    private  void loadCars(){
        String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        carsRef= FirebaseDatabase.getInstance().getReference();
        carsRef.child("Cars").child("").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Parking parking=dataSnapshot.getValue(Parking.class);
                        parkingArrayList.add(parking);
                    }
                    CarListAdapter adapter=new CarListAdapter(ViewCarsActivity.this,parkingArrayList);
                    carList.setAdapter(adapter);
                }
                else {
                    Toast.makeText(ViewCarsActivity.this, "No data found in the database!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}