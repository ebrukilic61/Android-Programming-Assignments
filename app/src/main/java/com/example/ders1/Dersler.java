package com.example.ders1;

import java.io.Serializable;

public class Dersler implements Serializable {
    String dersAdi, dersGrubu, dersKodu, gun, ogretimGorevlisi,  stdSayisi, donem, dersId;

    Dersler(){

    }
    public Dersler(String dersKodu, String dersAdi, String gun, String donem, String ogretimGorevlisi, String dersGrubu, String stdSayisi) {
        this.dersKodu = dersKodu;
        this.dersAdi = dersAdi;
        this.gun = gun;
        this.donem = donem;
        this.ogretimGorevlisi = ogretimGorevlisi;
        this.dersGrubu = dersGrubu;
        this.stdSayisi = stdSayisi;
    }

    public String getDersKodu(){
        return dersKodu;
    }

    public String getDersAdi() {
        return dersAdi;
    }

    public String getGun() {
        return gun;
    }

    public String getDersGrubu() {
        return dersGrubu;
    }

    public String getOgretimGorevlisi() {
        return ogretimGorevlisi;
    }

    public String getDonem() {
        return donem;
    }

    public String getStdSayisi() { return stdSayisi; }
    public String getDersId() {
        return dersId;
    }

    public void setDersId(String dersId) {
        this.dersId = dersId;
    }

    public void setDersAdi(String dersAdi) {
        this.dersAdi = dersAdi;
    }

    public void setDersGrubu(String dersGrubu) {
        this.dersGrubu = dersGrubu;
    }

    public void setDersKodu(String dersKodu) {
        this.dersKodu = dersKodu;
    }

    public void setGun(String gun) {
        this.gun = gun;
    }

    public void setOgretimGorevlisi(String ogretimGorevlisi) {
        this.ogretimGorevlisi = ogretimGorevlisi;
    }

    public void setStdSayisi(String stdSayisi) {
        this.stdSayisi = stdSayisi;
    }

    public void setDonem(String donem) {
        this.donem = donem;
    }

}

