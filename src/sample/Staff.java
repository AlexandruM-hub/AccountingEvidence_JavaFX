package sample;

import java.util.Date;

public class Staff {
    private int idPersoana;
    private String nume, prenume, email, telefon;
    private Date dataNastere, dataAngajare;

    public Staff(int idPersoana, String nume, String prenume, String telefon, Date dataNastere, String email, Date dataAngajare){
        this.idPersoana = idPersoana;
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
        this.dataNastere = dataNastere;
        this.email = email;
        this.dataAngajare = dataAngajare;
    }

    @Override
    public String toString() {
        return  "" + idPersoana +
                "" + telefon +
                "" + nume +
                "" + prenume +
                "" + email +
                "" + dataNastere +
                "" + dataAngajare;
    }

    public int getIdPersoana() {
        return idPersoana;
    }

    public void setIdPersoana(int idPersoana) {
        this.idPersoana = idPersoana;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataNastere() {
        return dataNastere;
    }

    public void setDataNastere(Date dataNastere) {
        this.dataNastere = dataNastere;
    }

    public Date getDataAngajare() {
        return dataAngajare;
    }

    public void setDataAngajare(Date dataAngajare) {
        this.dataAngajare = dataAngajare;
    }
}
