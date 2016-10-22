package com.whynotit.admin.Managers;

import com.whynotit.admin.Models.Emplois;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harzallah on 28/07/2016.
 */
public class SeancesToUploadManager {
    private static SeancesToUploadManager instance;
    private List<Emplois> listSeancesToUpload = new ArrayList<>();


    public static SeancesToUploadManager getInstance() {
        if (instance == null) {
            instance = new SeancesToUploadManager();
        }
        return instance;
    }

    public List<Emplois> getListSeancesToUpload() {
        List<Emplois> listTMPToUpload = new ArrayList<>();
        listTMPToUpload.addAll(listSeancesToUpload);
        return listTMPToUpload;
    }

    public void setListSeancesToUpload(List<Emplois> listSeancesToUpload) {
        this.listSeancesToUpload = listSeancesToUpload;
    }
}
