package com.whynotit.admin.Models;

import java.io.Serializable;

/**
 * Created by Harzallah on 27/07/2016.
 */
public class HoraireSeance implements Serializable{
    String heureDebut;
    String heureFin;

    public HoraireSeance(String heureDebut, String heureFin) {
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public HoraireSeance(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }
}
