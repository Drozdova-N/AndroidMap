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
import com.example.dnina.navigator.R;
import com.example.dnina.navigator.Route;

import java.util.List;

public class AdapterRoute extends RecyclerView.Adapter<AdapterRoute.ViewHolder> {

    private List<Route> routes;
    private Activity activity;

    public AdapterRoute() {
    }

    private static CallbackAddRoute callbackRoute;

    public interface CallbackAddRoute {
        void callBackAddRoute(Route r);
    }


    public void registerCallBack(CallbackAddRoute callback) {
        this.callbackRoute = callback;
    }

    public AdapterRoute(List<Route> reportList, Activity activity) {
        this.routes = reportList;
        this.activity = activity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_route, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.bind(routes.get(position));
        viewHolder.createListener();

    }

    @Override
    public int getItemCount() {

        return routes.size();


    }


    class ViewHolder extends RecyclerView.ViewHolder {


        private TextView points_start_to_end;
        private ImageView isSave;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            points_start_to_end = itemView.findViewById(R.id.points_start_to_end);
            isSave = itemView.findViewById(R.id.save_route);

        }

        void bind(Route route) {
            points_start_to_end.setText(route.getRout() + "\n" + route.getDistance());
            isSave.setImageResource(R.drawable.startrue);
            isSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSave.setImageResource(R.drawable.starfalse);
                    FirebaseRequest.removeRoute(routes.get(getAdapterPosition()));

                }
            });
        }

        void createListener() {
            points_start_to_end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                    callbackRoute.callBackAddRoute(routes.get(getAdapterPosition()));

                }
            });
        }

    }
}