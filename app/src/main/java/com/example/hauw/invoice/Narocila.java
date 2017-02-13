package com.example.hauw.invoice;

/**
 * Created by MSI on 1.4.2015.
 */
public class Narocila {
    private String idNarocilo;
    private String izdelek = "";
    private String kolicina = "";
    private String cena = "";

    public void SetIzdelek(String izdelek){
        this.izdelek = izdelek;
    }

    public void SetKolicina(String kolicina){
        this.kolicina = kolicina;
    }

    public void SetCena(String cena){
        this.cena = cena;
    }

    public void SetIdNarocilo(String idNarocilo){ this.idNarocilo = idNarocilo;}

    public String GetIzdelek(){
        return this.izdelek;
    }

    public String GetKolicina(){
        return this.kolicina;
    }

    public String GetCena() {
        return this.cena;
    }

    public String GetIdNarocilo(){return this.idNarocilo; }
}
