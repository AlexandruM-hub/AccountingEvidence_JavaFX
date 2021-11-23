package sample;

public class Produse {
    private int idProdus;
    private String denumireProdus;
    private float cantitateRecoltata;
    private float cantitateDepozitata;
    private int anProdus;
    private int grupTeren;

    private int idDepozit;
    private String localitateDepozit;

    public Produse(int idProdus, String denumireProdus, float cantitateRecoltata, float cantitateDepozitata, int anProdus, int grupTeren) {
        this.idProdus = idProdus;
        this.denumireProdus = denumireProdus;
        this.cantitateRecoltata = cantitateRecoltata;
        this.cantitateDepozitata = cantitateDepozitata;
        this.anProdus = anProdus;
        this.grupTeren = grupTeren;
    }

    public Produse (int idProdus, String denumireProdus, float cantitateDepozitata, int idDepozit, String localitateDepozit){

        this.idProdus = idProdus;
        this.denumireProdus = denumireProdus;
        this.cantitateDepozitata = cantitateDepozitata;
        this.idDepozit = idDepozit;
        this.localitateDepozit = localitateDepozit;
    }

    public int getIdDepozit() {
        return idDepozit;
    }

    public void setIdDepozit(int idDepozit) {
        this.idDepozit = idDepozit;
    }

    public String getLocalitateDepozit() {
        return localitateDepozit;
    }

    public void setLocalitateDepozit(String localitateDepozit) {
        this.localitateDepozit = localitateDepozit;
    }

    public int getIdProdus() {
        return idProdus;
    }

    public void setIdProdus(int idProdus) {
        this.idProdus = idProdus;
    }

    public String getDenumireProdus() {
        return denumireProdus;
    }

    public void setDenumireProdus(String denumireProdus) {
        this.denumireProdus = denumireProdus;
    }

    public float getCantitateRecoltata() {
        return cantitateRecoltata;
    }

    public void setCantitateRecoltata(float cantitateRecoltata) {
        this.cantitateRecoltata = cantitateRecoltata;
    }

    public float getCantitateDepozitata() {
        return cantitateDepozitata;
    }

    public void setCantitateDepozitata(float cantitateDepozitata) {
        this.cantitateDepozitata = cantitateDepozitata;
    }

    public int getAnProdus() {
        return anProdus;
    }

    public void setAnProdus(int anProdus) {
        this.anProdus = anProdus;
    }

    public int getGrupTeren() {
        return grupTeren;
    }

    public void setGrupTeren(int grupTeren) {
        this.grupTeren = grupTeren;
    }

}
