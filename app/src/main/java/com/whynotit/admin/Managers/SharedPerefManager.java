package com.whynotit.admin.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whynotit.admin.MaDate;
import com.whynotit.admin.Models.HoraireSeance;
import com.whynotit.admin.Models.RepartitionSemaine;
import com.whynotit.admin.Models.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harzallah on 27/07/2016.
 */
public class SharedPerefManager {
    private static final String DATABASE_DATE = "database_date";
    private static final String REPARTITION_SEMAINES = "repartition_semaines";
    private static SharedPerefManager instance;
    private static SharedPreferences sharedPreferences;
    public static final String PREFERENCES = "1006";
    public final String IS_INITIALIZED = "isInitialized";
    private final String HORAIRES_SEANCES = "horaires_seances";
    private final String USERS_LIST = "users";
    private static final String PREF_ACCOUNT_NAME = "accountName";


    public static SharedPerefManager getInstance(Activity activity) {
        if (instance == null) {
            sharedPreferences = activity.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            instance = new SharedPerefManager();
        }
        return instance;
    }

    public boolean isApplicationInitialized() {
        Boolean isConnected = sharedPreferences.getBoolean(IS_INITIALIZED, false);
        return isConnected;
    }

    public void setApplicationInitialized() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_INITIALIZED, true);
        editor.apply();
    }

    public void saveHorairesSeances(List<HoraireSeance> horairesSeances) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String list = gson.toJson(horairesSeances);
        editor.putString(HORAIRES_SEANCES, list);
        editor.apply();
    }

    public List<HoraireSeance> loadHorairesSeances() {
        String list = sharedPreferences.getString(HORAIRES_SEANCES, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<HoraireSeance>>() {
        }.getType();
        List<HoraireSeance> horaireSeances = gson.fromJson(list, type);
        return horaireSeances;
    }

    public void saveUsers() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String list = gson.toJson(UsersManager.getInstance().getUserList());
        editor.putString(USERS_LIST, list);
        editor.apply();
    }

    public List<User> loadUsersList() {
        String list = sharedPreferences.getString(USERS_LIST, null);
        if (list != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            List<User> usersList = gson.fromJson(list, type);
            return usersList;
        } else {
            return new ArrayList<>();
        }
    }

    public void saveDataBaseDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DATABASE_DATE, date);
        editor.apply();
    }

    public String loadDataBaseDate() {
        String dataBaseDate = sharedPreferences.getString(DATABASE_DATE, null);
        return dataBaseDate;
    }

    public void saveAccountName(String accountName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
    }

    public String getAccountName() {
        String accountName = sharedPreferences.getString(PREF_ACCOUNT_NAME, null);
        return accountName;
    }

    public void saveRegimeSemaines(List<RepartitionSemaine> listRegimeSemaines) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String list = gson.toJson(listRegimeSemaines);
        editor.putString(REPARTITION_SEMAINES, list);
        editor.apply();
    }

    public List<RepartitionSemaine> loadRepartitionSemaines() {
        String list = sharedPreferences.getString(REPARTITION_SEMAINES, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<RepartitionSemaine>>() {
        }.getType();
        List<RepartitionSemaine> repartitionSemaines = gson.fromJson(list, type);
        return repartitionSemaines;
    }

    public void saveSuccesUpload (String debutSeance) {
        MaDate date = new MaDate();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(debutSeance, date.getDateInDateFormat());
        editor.apply();
    }

    public boolean IsSeanceUploaded (String debutSeance) {
        String dateUpload = sharedPreferences.getString(debutSeance, null);
        if (dateUpload == null)
            return false;

        MaDate date = new MaDate();

        if (dateUpload.compareTo(date.getDateInDateFormat()) == 0) {
            return true;
        } else {
            return false;
        }
    }


    public void saveAbscenceIsSetForThisList(String debut) {
        MaDate date = new MaDate();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(date.getDateInDateFormat()+debut, true);
        editor.apply();
    }

    public boolean IsAbscenceIsSetForThisList (String debut) {
        MaDate date = new MaDate();
        boolean isAbscenceSet = sharedPreferences.getBoolean(date.getDateInDateFormat()+debut, false);
        return isAbscenceSet;
    }
}
