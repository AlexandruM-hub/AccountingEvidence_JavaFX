package sample;

public class Depozite {
    String id_depozit;
    String localitate;

    public Depozite(String id_depozit, String localitate) {
        this.id_depozit = id_depozit;
        this.localitate = localitate;
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
