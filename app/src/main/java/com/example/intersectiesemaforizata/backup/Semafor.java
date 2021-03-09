package com.example.intersectiesemaforizata.backup;

import com.google.android.gms.maps.model.LatLng;

public class Semafor {
    private LatLng coordonite;
    private String strada;
    private int nrSemafor;

    public Semafor(LatLng coordonite, String strada, int nrSemafor) {
        this.coordonite = coordonite;
        this.strada = strada;
        this.nrSemafor = nrSemafor;
    }

    public LatLng getCoordonite() {
        return coordonite;
    }

    public void setCoordonite(LatLng coordonite) {
        this.coordonite = coordonite;
    }

    public String getStrada() {
        return strada;
    }

    public void setStrada(String strada) {
        this.strada = strada;
    }

    public int getNrSemafor() {
        return nrSemafor;
    }

    public void setNrSemafor(int nrSemafor) {
        this.nrSemafor = nrSemafor;
    }
}
