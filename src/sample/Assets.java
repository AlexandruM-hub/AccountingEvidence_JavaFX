package sample;

public class Assets {
    String id_depozit;
    String localitate;
    String idTeren;
    String grupTeren;
    String nrCadastral;
    String suprafata;
    String idAsset;

    public Assets(String id_depozit, String localitate) {
        this.id_depozit = id_depozit;
        this.localitate = localitate;
    }

    public Assets (String idTeren, String grupTeren, String nrCadastral, String suprafata, String localitate){
        this.idTeren = idTeren;
        this.grupTeren = grupTeren;
        this.nrCadastral = nrCadastral;
        this.suprafata = suprafata;
        this.localitate = localitate;
    }

    public Assets(String idAsset){
        this.idAsset = idAsset;
    }

    public String getIdAsset() {
        return idAsset;
    }

    public void setIdAsset(String idAsset) {
        this.idAsset = idAsset;
    }

    public String getIdTeren() {
        return idTeren;
    }

    public void setIdTeren(String idTeren) {
        this.idTeren = idTeren;
    }

    public String getGrupTeren() {
        return grupTeren;
    }

    public void setGrupTeren(String grupTeren) {
        this.grupTeren = grupTeren;
    }

    public String getNrCadastral() {
        return nrCadastral;
    }

    public void setNrCadastral(String nrCadastral) {
        this.nrCadastral = nrCadastral;
    }

    public String getSuprafata() {
        return suprafata;
    }

    public void setSuprafata(String suprafata) {
        this.suprafata = suprafata;
    }

    public String getId_depozit() {
        return id_depozit;
    }

    public void setId_depozit(String id_depozit) {
        this.id_depozit = id_depozit;
    }

    public String getLocalitate() {
        return localitate;
    }

    public void setLocalitate(String localitate) {
        this.localitate = localitate;
    }
}
