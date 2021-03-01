package com.example.intersectiesemaforizata;

import com.google.android.gms.maps.model.LatLng;

public class Spital {
    private LatLng coordonate;
    private String numeSpital;

    public Spital(LatLng coordonate, String numeSpital) {
        this.coordonate = coordonate;
        this.numeSpital = numeSpital;
    }

    public LatLng getCoordonate() {
        return coordonate;
    }

    public void setCoordonate(LatLng coordonate) {
        this.coordonate = coordonate;
    }

    public String getNumeSpital() {
        return numeSpital;
    }

    public void setNumeSpital(String numeSpital) {
        this.numeSpital = numeSpital;
    }
}
