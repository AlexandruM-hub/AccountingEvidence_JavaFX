package sample;

import java.util.Date;

public class Claims_Debts {
    private int Id;
    private String aferenta;
    private Date termenAchitare;
    private float valoare;
    private Date dataAchitat;
    private String contractant;

    public Claims_Debts(int id, String aferenta, Date termenAchitare, float valoare, Date dataAchitat, String contractant) {
        Id = id;
        this.aferenta = aferenta;
        this.termenAchitare = termenAchitare;
        this.valoare = valoare;
        this.dataAchitat = dataAchitat;
        this.contractant = contractant;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAferenta() {
        return aferenta;
    }

    public void setAferenta(String aferenta) {
        this.aferenta = aferenta;
    }

    public Date getTermenAchitare() {
        return termenAchitare;
    }

    public void setTermenAchitare(Date termenAchitare) {
        this.termenAchitare = termenAchitare;
    }

    public float getValoare() {
        return valoare;
    }

    public void setValoare(float valoare) {
        this.valoare = valoare;
    }

    public Date getDataAchitat() {
        return dataAchitat;
    }

    public void setDataAchitat(Date dataAchitat) {
        this.dataAchitat = dataAchitat;
    }

    public String getContractant() {
        return contractant;
    }

    public void setContractant(String contractant) {
        this.contractant = contractant;
    }

    @Override
    public String toString() {
        return  "" + Id +
                "" + aferenta +
                "" + termenAchitare +
                "" + valoare +
                "" + dataAchitat +
                "" + contractant ;
    }
}
