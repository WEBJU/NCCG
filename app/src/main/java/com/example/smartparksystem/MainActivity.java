package com.example.smartparksystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.Parking;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference carRef;
    EditText etCarType, etModel, etNumberPlate, etColor, etOther;
    String carType, model, numberPlate, color, other, uid;
    Button btnSave;
    ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        //get values
        etCarType = findViewById(R.id.type);
        etModel = findViewById(R.id.model);
        etNumberPlate = findViewById(R.id.number_plate);
        etColor = findViewById(R.id.color);
        etOther = findViewById(R.id.other);

        //progress bar
        progressBar = findViewById(R.id.progressBar);

        //initialize button
        btnSave = findViewById(R.id.save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCarInfo(carType, model, numberPlate, color, other, uid);
            }
        });
    }

    private void addCarInfo(String carType, String model, String numberPlate, String color, String other, String uid) {

        database = FirebaseDatabase.getInstance();
        carRef = database.getReference("Cars");
        if (auth.getCurrentUser() != null) {
            uid = auth.getCurrentUser().getUid();
        }

        carType = etCarType.getText().toString().trim();
        model = etModel.getText().toString().trim();
        numberPlate = etNumberPlate.getText().toString().trim();
        color = etColor.getText().toString().trim();
        other = etOther.getText().toString().trim();
        if (TextUtils.isEmpty(carType) || TextUtils.isEmpty(numberPlate)){
            Toast.makeText(this, "Number plate or car type cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //initialize model for save
        Parking parking = new Parking(carType, model, numberPlate, color, other, uid);
        carRef.push().setValue(parking);
        carRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Your car information has been added successfully!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,MapsActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Database Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater=getMenuInflater();
//        inflater.inflate(R.menu.main_menu,menu);
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                // do something
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            case R.id.home:
                Toast.makeText(this, "You are already home", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}