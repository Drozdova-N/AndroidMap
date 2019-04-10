package com.example.dnina.navigator;

import java.util.List;

public class RouteResponse {
     List<Route> routes;

    public String getPoints() {
        return this.routes.get(0).overviewPolyline.points;
    }

    class Route {
         OverviewPolyline overviewPolyline;
    }

    class OverviewPolyline {
        String points;
    }
}