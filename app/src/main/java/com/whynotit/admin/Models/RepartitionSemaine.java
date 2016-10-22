package com.whynotit.admin.Models;

/**
 * Created by Harzallah on 30/07/2016.
 */
public class RepartitionSemaine {
    String debutSemaine;
    String finSemaine;
    String moisDebut;
    String moisFin;
    String repartition;

    public RepartitionSemaine(String debutSemaine, String finSemaine, String moisDebut, String moisFin, String repartition) {
        this.debutSemaine = debutSemaine;
        this.finSemaine = finSemaine;
        this.moisDebut = moisDebut;
        this.moisFin = moisFin;
        this.repartition = repartition;
    }

    public String getRepartition() {
        return repartition;
    }

    public void setRepartition(String repartition) {
        this.repartition = repartition;
    }

    public String getDebutSemaine() {
        return debutSemaine;
    }

    public void setDebutSemaine(String debutSemaine) {
        this.debutSemaine = debutSemaine;
    }

    public String getFinSemaine() {
        return finSemaine;
    }

    public void setFinSemaine(String finSemaine) {
        this.finSemaine = finSemaine;
    }

    public String getMoisDebut() {
        return moisDebut;
    }

    public void setMoisDebut(String moisDebut) {
        this.moisDebut = moisDebut;
    }

    public String getMoisFin() {
        return moisFin;
    }

    public void setMoisFin(String moisFin) {
        this.moisFin = moisFin;
    }

    @Override
    public String toString() {
        return "RepartitionSemaine{" +
                "debutSemaine='" + debutSemaine + '\'' +
                ", finSemaine='" + finSemaine + '\'' +
                ", moisDebut='" + moisDebut + '\'' +
                ", moisFin='" + moisFin + '\'' +
                ", repartition='" + repartition + '\'' +
                '}';
    }
}
