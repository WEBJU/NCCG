package com.example.smartparksystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smartparksystem.R;

import java.util.ArrayList;

import model.Parking;

public class CarListAdapter extends ArrayAdapter<Parking> {

    public CarListAdapter(@NonNull Context context, ArrayList<Parking> carArrayList) {
        super(context,0, carArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView=convertView;
        if (listItemView == null){
            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.car_list,parent,false);
        }
        final Parking parking=getItem(position);

        TextView carType=listItemView.findViewById(R.id.tvCarType);
        TextView numberPlate=listItemView.findViewById(R.id.tvNumberPlate);
        TextView color=listItemView.findViewById(R.id.tvColor);
        TextView other=listItemView.findViewById(R.id.tvColor);

        carType.setText(parking.getCarType());
        numberPlate.setText(parking.getNumberPlate());
        color.setText(parking.getColor());
        color.setText(parking.getOther());
        other.setText(parking.getOther());

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "The selected car is "+parking.getCarType(), Toast.LENGTH_SHORT).show();
            }
        });
        return listItemView;

    }
}
