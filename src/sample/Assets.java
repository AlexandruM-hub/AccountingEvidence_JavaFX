package sample;

import java.util.Date;

public class Assets {
    private int id_depozit;
    private String localitate;
    private int idTeren;
    private int grupTeren;
    private String nrCadastral;
    private float suprafata;
    private int idAsset;
    private  String stare;
    private int idFactura;
    private String nrFactura;
    private String Contractant;
    private Date data;
    private String denumireMijlocFix;
    private float cantitateStock;
    private float valoareaActiv;

    String tipMarfa;
    String denumireActiv;
    float pretActiv;

    //depozit
    public Assets(int id_depozit, String localitate, String stare) {
        this.id_depozit = id_depozit;
        this.localitate = localitate;
        this.stare = stare;
    }

    //pamant
    public Assets (int idTeren, int grupTeren, String nrCadastral, float suprafata, String localitate, String stare){
        this.idTeren = idTeren;
        this.grupTeren = grupTeren;
        this.nrCadastral = nrCadastral;
        this.suprafata = suprafata;
        this.localitate = localitate;
        this.stare = stare;
    }

    public Assets(int idFactura, String nrFactura, String contractant, Date data, String denumireMijlocFix, float cantitateStock, float valoareaActiv ){
        this.idFactura = idFactura;
        this.nrFactura = nrFactura;
        this.Contractant = contractant;
        this.data = data;
        this.denumireMijlocFix = denumireMijlocFix;
        this.cantitateStock = cantitateStock;
        this.valoareaActiv = valoareaActiv;
    }

    public Assets(int idAsset, String nrFactura, String contractant, Date data, String tipMarfa, String denumireActiv, float cantitateStock,
                  float pretActiv, float valoareaActiv, int id_depozit){
        this.idAsset = idAsset;
        this.nrFactura = nrFactura;
        this.Contractant = contractant;
        this.data = data;
        this.tipMarfa = tipMarfa;
        this.denumireActiv = denumireActiv;
        this.cantitateStock = cantitateStock;
        this.pretActiv = pretActiv;
        this.valoareaActiv = valoareaActiv;
        this.id_depozit = id_depozit;

    }

    public String getStare() {
        return stare;
    }

    public void setStare(String stare) {
        this.stare = stare;
    }

    public int getId_depozit() {
        return id_depozit;
    }

    public void setId_depozit(int id_depozit) {
        this.id_depozit = id_depozit;
    }

    public String getLocalitate() {
        return localitate;
    }

    public void setLocalitate(String localitate) {
        this.localitate = localitate;
    }

    public int getIdTeren() {
        return idTeren;
    }

    public void setIdTeren(int idTeren) {
        this.idTeren = idTeren;
    }

    public int getGrupTeren() {
        return grupTeren;
    }

    public void setGrupTeren(int grupTeren) {
        this.grupTeren = grupTeren;
    }

    public String getNrCadastral() {
        return nrCadastral;
    }

    public void setNrCadastral(String nrCadastral) {
        this.nrCadastral = nrCadastral;
    }

    public float getSuprafata() {
        return suprafata;
    }

    public void setSuprafata(float suprafata) {
        this.suprafata = suprafata;
    }

    public int getIdAsset() {
        return idAsset;
    }

    public void setIdAsset(int idAsset) {
        this.idAsset = idAsset;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public String getNrFactura() {
        return nrFactura;
    }

    public void setNrFactura(String nrFactura) {
        this.nrFactura = nrFactura;
    }

    public String getContractant() {
        return Contractant;
    }

    public void setContractant(String contractant) {
        Contractant = contractant;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDenumireMijlocFix() {
        return denumireMijlocFix;
    }

    public void setDenumireMijlocFix(String denumireMijlocFix) {
        this.denumireMijlocFix = denumireMijlocFix;
    }

    public float getCantitateStock() {
        return cantitateStock;
    }

    public void setCantitateStock(float cantitateStock) {
        this.cantitateStock = cantitateStock;
    }

    public float getValoareaActiv() {
        return valoareaActiv;
    }

    public void setValoareaActiv(float valoareaActiv) {
        this.valoareaActiv = valoareaActiv;
    }

    public String getTipMarfa() {
        return tipMarfa;
    }

    public void setTipMarfa(String tipMarfa) {
        this.tipMarfa = tipMarfa;
    }

    public String getDenumireActiv() {
        return denumireActiv;
    }

    public void setDenumireActiv(String denumireActiv) {
        this.denumireActiv = denumireActiv;
    }

    public float getPretActiv() {
        return pretActiv;
    }

    public void setPretActiv(float pretActiv) {
        this.pretActiv = pretActiv;
    }
}
