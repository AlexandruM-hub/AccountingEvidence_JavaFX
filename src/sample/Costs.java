package sample;

import java.util.Date;

public class Costs {
    private int idCost, idElement, idProdus;
    private String tipCost, scopCost;
    private Date dataCost;
    private float cantitate, valoare;

    public Costs(int idCost, String tipCost, String scopCost, Date dataCost, float cantitate, float valoare, int idElement, int idProdus){
        this.idCost = idCost;
        this.tipCost = tipCost;
        this.scopCost = scopCost;
        this.dataCost = dataCost;
        this.cantitate = cantitate;
        this.valoare = valoare;
        this.idElement = idElement;
        this.idProdus = idProdus;
    }

    public int getIdCost() {
        return idCost;
    }

    public void setIdCost(int idCost) {
        this.idCost = idCost;
    }

    public int getIdElement() {
        return idElement;
    }

    public void setIdElement(int idElement) {
        this.idElement = idElement;
    }

    public int getIdProdus() {
        return idProdus;
    }

    public void setIdProdus(int idProdus) {
        this.idProdus = idProdus;
    }

    public String getTipCost() {
        return tipCost;
    }

    public void setTipCost(String tipCost) {
        this.tipCost = tipCost;
    }

    public String getScopCost() {
        return scopCost;
    }

    public void setScopCost(String scopCost) {
        this.scopCost = scopCost;
    }

    public Date getDataCost() {
        return dataCost;
    }

    public void setDataCost(Date dataCost) {
        this.dataCost = dataCost;
    }

    public float getCantitate() {
        return cantitate;
    }

    public void setCantitate(float cantitate) {
        this.cantitate = cantitate;
    }

    public float getValoare() {
        return valoare;
    }

    public void setValoare(float valoare) {
        this.valoare = valoare;
    }

    @Override
    public String toString() {
        return  "" + idCost +
                "" + idElement +
                "" + idProdus +
                "" + tipCost +
                "" + scopCost +
                "" + dataCost +
                "" + cantitate +
                "" + valoare ;

    }
}
