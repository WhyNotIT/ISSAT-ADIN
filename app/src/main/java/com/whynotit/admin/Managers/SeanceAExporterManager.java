package com.whynotit.admin.Managers;

import android.widget.RadioButton;

/**
 * Created by Harzallah on 26/07/2016.
 */
public class SeanceAExporterManager {
    private static SeanceAExporterManager instance;
    private RadioButton selectedSeance = null;
    private int positionSelectedSeance = -1;


    public static SeanceAExporterManager getInstance () {
        if (instance == null) {
            instance = new SeanceAExporterManager();
        }
        return instance;
    }

    public RadioButton getSelectedSeance() {
        return selectedSeance;
    }

    public void setSelectedSeance(RadioButton selectedSeance) {
        this.selectedSeance = selectedSeance;
    }

    public int getPositionSelectedSeance() {
        return positionSelectedSeance;
    }

    public void setPositionSelectedSeance(int positionSelectedSeance) {
        this.positionSelectedSeance = positionSelectedSeance;
    }
}
