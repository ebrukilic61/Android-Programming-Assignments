package com.example.ders1;

public class Reports {

    private String raporKonu, raporId, raporMesaj, dersKodu, timestamp, userID, subjectText, personName;

    public Reports(){}

    public Reports(String raporKonu, String raporMesaj, String dersKodu, String timestamp, String subjectText, String personName) {
        this.raporKonu = raporKonu;
        this.raporMesaj = raporMesaj;
        this.dersKodu = dersKodu;
        this.timestamp = timestamp;
        this.subjectText = subjectText;
        this.personName = personName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getUserID() {
        return userID;
    }

    public String getSubjectText() {
        return subjectText;
    }

    public void setSubjectText(String subjectText) {
        this.subjectText = subjectText;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRaporId() {
        return raporId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setRaporId(String raporId) {
        this.raporId = raporId;
    }

    public String getRaporMesaj() {
        return raporMesaj;
    }

    public void setRaporMesaj(String raporMesaj) {
        this.raporMesaj = raporMesaj;
    }

    public String getDersKodu() {
        return dersKodu;
    }

    public void setDersKodu(String dersKodu) {
        this.dersKodu = dersKodu;
    }

    public String getRaporKonu() {
        return raporKonu;
    }

    public void setRaporKonu(String raporKonu) {
        this.raporKonu = raporKonu;
    }
}
