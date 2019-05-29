package com.example.dnina.navigator.network_module;

import java.util.List;

public class RouteResponse {
    List<Route> routes;

    public String getPoints(int i) {
        return this.routes.get(i).overview_polyline.points;
    }

    public String getTravelTime(int i) {
        return this.routes.get(i).legs.get(0).duration_in_traffic.text;
    }

    public int size() {
        return routes.size();
    }

    public String getDistance(int i) {
        return this.routes.get(i).legs.get(0).distance.text;
    }

    class Route {
        OverviewPolyline overview_polyline;
        List<Legs> legs;

    }

    class OverviewPolyline {
        String points;
    }

    class Legs {
        Distance distance;
        DurationInTraffic duration_in_traffic;


        class DurationInTraffic {
            String text;

        }

        class Distance {
            String text;

        }
    }
}