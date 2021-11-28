package sample;

import java.util.Date;

public class Invoices {
    private int idFactura, idProdus;
    private String nrFactura, cumparator, denumireProdus, tipFactura, tipMarfa;
    private Date dataVanzare;
    private float cantitate, pret, valoare;


    public Invoices(int idFactura, String nrFactura, String cumparator,
                    Date dataVanzare, String denumireProdus, float cantitate,
                    float pret, float valoare, int idProdus){
        this.idFactura = idFactura;
        this.nrFactura = nrFactura;
        this.cumparator = cumparator;
        this.dataVanzare = dataVanzare;
        this.denumireProdus = denumireProdus;
        this.cantitate = cantitate;
        this.pret = pret;
        this.valoare = valoare;
        this.idProdus = idProdus;
    }

    public Invoices(int idFactura, String nrFactura, String tipFactura, String cumparator,
                    Date dataVanzare, String tipMarfa, String denumireProdus, float cantitate,
                    float pret, float valoare, int idProdus){
        this.idFactura = idFactura;
        this.nrFactura = nrFactura;
        this.cumparator = cumparator;
        this.dataVanzare = dataVanzare;
        this.denumireProdus = denumireProdus;
        this.cantitate = cantitate;
        this.pret = pret;
        this.valoare = valoare;
        this.idProdus = idProdus;
        this.tipFactura = tipFactura;
        this.tipMarfa = tipMarfa;
    }

    @Override
    public String toString() {
        return  "" + idFactura +
                "" + idProdus +
                "" + nrFactura +
                "" + cumparator +
                "" + denumireProdus +
                "" + dataVanzare +
                "" + cantitate +
                "" + pret +
                "" + valoare +
                "" + tipMarfa +
                "" + tipFactura;

    }

    public String getTipFactura() {
        return tipFactura;
    }

    public void setTipFactura(String tipFactura) {
        this.tipFactura = tipFactura;
    }

    public String getTipMarfa() {
        return tipMarfa;
    }

    public void setTipMarfa(String tipMarfa) {
        this.tipMarfa = tipMarfa;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdProdus() {
        return idProdus;
    }

    public void setIdProdus(int idProdus) {
        this.idProdus = idProdus;
    }

    public String getNrFactura() {
        return nrFactura;
    }

    public void setNrFactura(String nrFactura) {
        this.nrFactura = nrFactura;
    }

    public String getCumparator() {
        return cumparator;
    }

    public void setCumparator(String cumparator) {
        this.cumparator = cumparator;
    }

    public String getDenumireProdus() {
        return denumireProdus;
    }

    public void setDenumireProdus(String denumireProdus) {
        this.denumireProdus = denumireProdus;
    }

    public Date getDataVanzare() {
        return dataVanzare;
    }

    public void setDataVanzare(Date dataVanzare) {
        this.dataVanzare = dataVanzare;
    }

    public float getCantitate() {
        return cantitate;
    }

    public void setCantitate(float cantitate) {
        this.cantitate = cantitate;
    }

    public float getPret() {
        return pret;
    }

    public void setPret(float pret) {
        this.pret = pret;
    }

    public float getValoare() {
        return valoare;
    }

    public void setValoare(float valoare) {
        this.valoare = valoare;
    }
}
