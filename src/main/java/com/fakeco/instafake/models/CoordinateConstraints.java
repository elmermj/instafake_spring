package com.fakeco.instafake.models;

public class CoordinateConstraints {
    private Coordinate northEast;
    private Coordinate southWest;

    public CoordinateConstraints() {}

    public CoordinateConstraints(Coordinate northEast, Coordinate southWest) {
        this.northEast = northEast;
        this.southWest = southWest;
    }

    public Coordinate getNorthEast() {
        return northEast;
    }

    public void setNorthEast(Coordinate northEast) {
        this.northEast = northEast;
    }

    public Coordinate getSouthWest() {
        return southWest;
    }

    public void setSouthWest(Coordinate southWest) {
        this.southWest = southWest;
    }

    public static class Coordinate {
        private double lat;
        private double lng;

        public Coordinate() {}

        public Coordinate(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}

