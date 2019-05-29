package com.example.dnina.navigator.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dnina.navigator.database.FirebaseRequest;
import com.example.dnina.navigator.util.MyPlace;
import com.example.dnina.navigator.R;

import java.util.ArrayList;

public class AdapterPlace extends RecyclerView.Adapter<AdapterPlace.ViewHolder> {

    private static AdapterPlace.CallbackAddPlace callbackPlace;

    public interface CallbackAddPlace {
        void callBackAddPlace(MyPlace p);
    }


    public void registerCallBack(AdapterPlace.CallbackAddPlace callback) {
        this.callbackPlace = callback;
    }

    private ArrayList<MyPlace> places;
    private Activity activity;

    public AdapterPlace(ArrayList<MyPlace> places, Activity activity) {
        this.places = places;
        this.activity = activity;
    }

    public AdapterPlace() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_place, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(places.get(i));

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView namePlace;
        private ImageView isSave;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namePlace = itemView.findViewById(R.id.my_place);
            isSave = itemView.findViewById(R.id.save_place);
        }

        public void bind(MyPlace place) {
            namePlace.setText(place.getNamePlace());
            if (place.isSave())
                isSave.setImageResource(R.drawable.startrue);
            else isSave.setImageResource(R.drawable.starfalse);

            isSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseRequest.removePlace(places.get(getAdapterPosition()));
                }
            });

            namePlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                    callbackPlace.callBackAddPlace(places.get(getAdapterPosition()));

                }
            });
        }

    }

}
