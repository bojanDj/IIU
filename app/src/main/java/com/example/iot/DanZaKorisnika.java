package com.example.iot;

public class DanZaKorisnika {
    private String id;
    private String datum;
    private String dolazak;
    private String odlazak;
    private String idEvid;

    public DanZaKorisnika(String id, String datum, String dolazak, String odlazak) {
        this.id = id;
        this.datum = datum;
        this.dolazak = dolazak;
        this.odlazak = odlazak;
    }

    public DanZaKorisnika(String id, String datum, String dolazak, String odlazak, String idEvid) {
        this.id = id;
        this.datum = datum;
        this.dolazak = dolazak;
        this.odlazak = odlazak;
        this.idEvid = idEvid;
    }

    public String getIdEvid() {
        return idEvid;
    }

    public void setIdEvid(String idEvid) {
        this.idEvid = idEvid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getDolazak() {
        return dolazak;
    }

    public void setDolazak(String dolazak) {
        this.dolazak = dolazak;
    }

    public String getOdlazak() {
        return odlazak;
    }

    public void setOdlazak(String odlazak) {
        this.odlazak = odlazak;
    }
}
