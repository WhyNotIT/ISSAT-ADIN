package com.whynotit.admin.Managers;

import com.whynotit.admin.Models.HoraireSeance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harzallah on 26/07/2016.
 */
public class SeancesHorairesManager {
    public static SeancesHorairesManager instance;

    public List<HoraireSeance> listSeances = new ArrayList<>();
    private List<String> listSeancesForExportation;


    public static SeancesHorairesManager getInstance() {
        if (instance == null) {
            instance = new SeancesHorairesManager();

        }
        return instance;
    }

    public List<HoraireSeance> getListSeances() {
        return listSeances;
    }

    public void setListSeances(List<HoraireSeance> listSeances) {
        this.listSeances = listSeances;
    }

/*
        listSeances.add("08H30 - 10H00");
        listSeances.add("10H10 - 11H40");
        listSeances.add("11H50 - 13H20");
        listSeances.add("13H50 - 15H20");
        listSeances.add("15H30 - 17H00");
        listSeances.add("17H10 - 18H40"); */


    public int getNombreSeances() {
        return listSeances.size();
    }

    public List<HoraireSeance> getListSeancesForExportation() {
        List<HoraireSeance> list = new ArrayList<>();
        list.addAll(listSeances);
        list.add(new HoraireSeance("Toutes les seances"));
        return list;
    }


}
