package com.example.dnina.navigator;

import java.util.List;

public class RouteResponse {
    List<Route> routes;

    public String getPoints() {
        return this.routes.get(0).overview_polyline.points;
    }

    public String getTravelTime() {
        return this.routes.get(0).legs.get(0).duration_in_traffic.text;
    }

    public String getDistance() {
        return this.routes.get(0).legs.get(0).distance.text;
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