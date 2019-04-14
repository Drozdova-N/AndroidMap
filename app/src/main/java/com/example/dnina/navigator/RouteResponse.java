package com.example.dnina.navigator;

import java.util.List;

public class RouteResponse {
     List<Route> routes;

    public String getPoints() {
        return this.routes.get(0).overview_polyline.points;
    }

    class Route {
         OverviewPolyline overview_polyline;

    }

    class OverviewPolyline {
        String points;
    }
}