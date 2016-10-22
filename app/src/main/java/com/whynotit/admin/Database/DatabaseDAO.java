package com.whynotit.admin.Database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.whynotit.admin.MaDate;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.Models.Contact;
import com.whynotit.admin.Models.Emplois;
import com.whynotit.admin.Models.RepartitionSemaine;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Harzallah on 06/04/2016.
 */
public class DatabaseDAO {

    public static String DATABASE_PATH;
    public static final String DATABASE_NAME = "emplois.db";

    // Champs de la base de données
    private SQLiteDatabase database;
    private DataBaseEmploisHandler dbHandler;
    private Activity activity;
    private boolean journeeInitialized;

    public DatabaseDAO(Activity activity) {
        dbHandler = new DataBaseEmploisHandler(activity);
        this.activity = activity;
    }

    public void open() throws SQLException {
        database = dbHandler.getWritableDatabase();
    }

    public void close() {
        dbHandler.close();
    }


    public boolean isDatabaseAvailable() {

        DATABASE_PATH = activity.getDatabasePath(DATABASE_NAME).getPath();
        SQLiteDatabase checkDB = null;

        try {

            checkDB = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            //database does't exist yet.
        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null;
    }

    public void copyDataBase() throws IOException {


        //Open your local db as the input stream
        InputStream myInput = activity.getAssets().open(DATABASE_NAME);


        // Path to the just created empty db
        String outFileName = DATABASE_PATH;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

        SharedPerefManager.getInstance(activity).saveDataBaseDate("01/01/2015");

    }


    public SQLiteDatabase getDatabase() {
        return database;
    }


    public long insertEmplois(Emplois e) {

        ContentValues values = new ContentValues();

        /** AUTO INCREMENT ID **/


        values.put(dbHandler.COL_FILIERE, e.getFiliere());
        values.put(dbHandler.COL_JOUR, e.getJour());
        values.put(dbHandler.COL_SEANCE, e.getSeance());
        values.put(dbHandler.COL_DEBUT, e.getDebut());
        values.put(dbHandler.COL_FIN, e.getFin());
        values.put(dbHandler.COL_MATIERE, e.getMatiere());
        values.put(dbHandler.COL_ENSEIGNANT, e.getEnseignant());
        values.put(dbHandler.COL_TYPE, e.getType());
        values.put(dbHandler.COL_SALLE, e.getSalle());
        values.put(dbHandler.COL_REGIME, e.getRegime());


        return database.insert(dbHandler.TABLE_EMPLOIS, null, values);
    }

    public Emplois getSeance(String filiere, String jour, String seance /*, String regime*/) {
        //String condition = getRegime (regime);

        Cursor c = database.query(true, dbHandler.TABLE_EMPLOIS, new String[]{dbHandler.COL_FILIERE, dbHandler.COL_JOUR, dbHandler.COL_SEANCE, dbHandler.COL_DEBUT, dbHandler.COL_FIN, dbHandler.COL_MATIERE, dbHandler.COL_ENSEIGNANT, dbHandler.COL_TYPE, dbHandler.COL_SALLE, dbHandler.COL_REGIME}, dbHandler.COL_JOUR + " LIKE '%" + jour + "' AND " + dbHandler.COL_SEANCE + " LIKE '" + seance + "%'", null, null, null, null, null);
        return cursorToFirstEmplois(c);
    }

    public ArrayList<String> getEnseignatByQuery(String query) {
        //String condition = getRegime (regime);
        Cursor c = database.query(true, dbHandler.TABLE_EMPLOIS, new String[]{dbHandler.COL_ENSEIGNANT}, dbHandler.COL_ENSEIGNANT + " LIKE '%" + query + "%'", null, null, null, null, null);
        return cursorToArrayString(c);
    }

    public List<Emplois> getEmplois(String filiere, String jour) {
        //String condition = getRegime (regime);
        Cursor c = database.query(dbHandler.TABLE_EMPLOIS, new String[]{dbHandler.COL_FILIERE, dbHandler.COL_JOUR, dbHandler.COL_SEANCE, dbHandler.COL_DEBUT, dbHandler.COL_FIN, dbHandler.COL_MATIERE, dbHandler.COL_ENSEIGNANT, dbHandler.COL_TYPE, dbHandler.COL_SALLE, dbHandler.COL_REGIME}, dbHandler.COL_FILIERE + " LIKE '" + filiere + "' AND " + dbHandler.COL_JOUR + " LIKE '%" + jour + "'", null, null, null, dbHandler.COL_SEANCE, null);
        return cursorToEmplois(c);
    }

    /*
    public List<Club> getClubs (){
        //String condition = getRegime (regime);
        Cursor c = database.query(dbHandler.TABLE_CLUBS, new String[] {dbHandler.COL_ID,dbHandler.COL_NOM, dbHandler.COL_DESCRIPTION, dbHandler.COL_EMAIL,dbHandler.COL_FACEBOOK,dbHandler.COL_IMAGE},null,null,null, null,null,null);
        return cursorToClubs(c);
    }*/

    private ArrayList<String> cursorToArrayString(Cursor c) {

        ArrayList<String> suggestions = new ArrayList<String>();
        if (c.getCount() == 0)
            return suggestions;
        else {


            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                suggestions.add(c.getString(0));
                c.moveToNext();
            }

            c.close();
        }

        return suggestions;
    }

    public List<Emplois> getEmploisEnseignant(String enseignant) {
        //String condition = getRegime (regime);
        Cursor c = database.query(dbHandler.TABLE_EMPLOIS, new String[]{dbHandler.COL_FILIERE, dbHandler.COL_JOUR, dbHandler.COL_SEANCE, dbHandler.COL_DEBUT, dbHandler.COL_FIN, dbHandler.COL_MATIERE, dbHandler.COL_ENSEIGNANT, dbHandler.COL_TYPE, dbHandler.COL_SALLE, dbHandler.COL_REGIME}, dbHandler.COL_ENSEIGNANT + " LIKE '" + enseignant + "'", null, null, null, null, null);
        return cursorToEmplois(c);
    }

    public List<Integer> getEmploisWithDay(String jour, String regime) {
        String condition = getRegime(regime);

        Cursor c = database.query(true, dbHandler.TABLE_EMPLOIS, new String[]{dbHandler.COL_ID, dbHandler.COL_FILIERE, dbHandler.COL_JOUR, dbHandler.COL_SEANCE, dbHandler.COL_DEBUT, dbHandler.COL_FIN, dbHandler.COL_MATIERE, dbHandler.COL_ENSEIGNANT, dbHandler.COL_TYPE, dbHandler.COL_SALLE, dbHandler.COL_REGIME}, dbHandler.COL_JOUR + " LIKE '%" + jour + "' AND " + dbHandler.COL_REGIME + condition, null, dbHandler.COL_SEANCE + ", " + dbHandler.COL_ENSEIGNANT, null, dbHandler.COL_SEANCE, null);
        return cursorToIDS(c);
    }

    public String getRegime(String regime) {
        String tab[] = regime.split("-");
        String condition = " IN ('H','TP'";
        int i = 0;
        while (i < tab.length) {
            if (tab[i].compareTo("") != 0) {
                    condition += ",";
                condition += "'" + tab[i] + "'";
            }
            i++;

        }
        Log.e("XXXXXXXXXX",condition+")");
        return condition + ")";

    }


    private List<Integer> cursorToIDS(Cursor c) {

        List<Integer> journee = new ArrayList<>();
        if (c.getCount() == 0)
            return journee;
        else {


            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {

                /*Emplois emplois = new Emplois();

                emplois.setID(c.getInt(dbHandler.NUM_COL_ID));
                emplois.setFiliere(c.getString(dbHandler.NUM_COL_FILIERE));
                emplois.setJour(c.getString(dbHandler.NUM_COL_JOUR));
                emplois.setSeance(c.getString(dbHandler.NUM_COL_SEANCE));
                emplois.setDebut(c.getString(dbHandler.NUM_COL_DEBUT));
                emplois.setFin(c.getString(dbHandler.NUM_COL_FIN));
                emplois.setMatiere(c.getString(dbHandler.NUM_COL_MATIERE));
                emplois.setEnseignant(c.getString(dbHandler.NUM_COL_ENSEIGNANT));
                emplois.setType(c.getString(dbHandler.NUM_COL_TYPE));
                emplois.setSalle(c.getString(dbHandler.NUM_COL_SALLE));
                emplois.setRegime(c.getString(dbHandler.NUM_COL_REGIME));

                journee.add(emplois);*/

                journee.add(c.getInt(dbHandler.NUM_COL_ID));
                c.moveToNext();
            }

            c.close();
        }

        return journee;
    }


    private List<Emplois> cursorToEmplois(Cursor c) {

        List<Emplois> journee = new ArrayList<>();
        if (c.getCount() == 0)
            return journee;
        else {


            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {

                Emplois emplois = new Emplois();

                emplois.setID(c.getInt(dbHandler.NUM_COL_ID));
                emplois.setFiliere(c.getString(dbHandler.NUM_COL_FILIERE));
                emplois.setJour(c.getString(dbHandler.NUM_COL_JOUR));
                emplois.setSeance(c.getString(dbHandler.NUM_COL_SEANCE));
                emplois.setDebut(c.getString(dbHandler.NUM_COL_DEBUT));
                emplois.setFin(c.getString(dbHandler.NUM_COL_FIN));
                emplois.setMatiere(c.getString(dbHandler.NUM_COL_MATIERE));
                emplois.setEnseignant(c.getString(dbHandler.NUM_COL_ENSEIGNANT));
                emplois.setType(c.getString(dbHandler.NUM_COL_TYPE));
                emplois.setSalle(c.getString(dbHandler.NUM_COL_SALLE));
                emplois.setRegime(c.getString(dbHandler.NUM_COL_REGIME));

                journee.add(emplois);
                c.moveToNext();
            }

            c.close();
        }

        return journee;
    }


    private Emplois cursorToFirstEmplois(Cursor c) {
        Emplois emplois;

        if (c.getCount() == 0)
            return null;
        else {


            c.moveToFirst();

            emplois = new Emplois();

            emplois.setFiliere(c.getString(dbHandler.NUM_COL_FILIERE));
            emplois.setJour(c.getString(dbHandler.NUM_COL_JOUR));
            emplois.setSeance(c.getString(dbHandler.NUM_COL_SEANCE));
            emplois.setDebut(c.getString(dbHandler.NUM_COL_DEBUT));
            emplois.setFin(c.getString(dbHandler.NUM_COL_FIN));
            emplois.setMatiere(c.getString(dbHandler.NUM_COL_MATIERE));
            emplois.setEnseignant(c.getString(dbHandler.NUM_COL_ENSEIGNANT));
            emplois.setType(c.getString(dbHandler.NUM_COL_TYPE));
            emplois.setSalle(c.getString(dbHandler.NUM_COL_SALLE));
            emplois.setRegime(c.getString(dbHandler.NUM_COL_REGIME));


            c.close();
        }

        return emplois;
    }


    public String[] getListFilieres() {
        Cursor c = database.rawQuery(
                "select distinct (" + dbHandler.COL_FILIERE + ") from " + dbHandler.TABLE_EMPLOIS
                , new String[]{});

        return cursorToFilieres(c);
    }

    public String getEmailEnseignant(String nomEnseignant) {
        Cursor c = database.rawQuery(
                "select (" + dbHandler.COL_EMAIL + ") from " + dbHandler.TABLE_CONTACT_ENSEIGNANT + " where " + dbHandler.COL_NOM_ENSEIGNANT
                        + " like ? ;"
                , new String[]{nomEnseignant});

        return cursorToEmail(c);
    }

    private String cursorToEmail(Cursor c) {
        String email = null;
        if (c.getCount() == 0) {
            return email;
        } else {
            c.moveToFirst();
            email = c.getString(0);
            c.close();
            return email;
        }
    }


    private String[] cursorToFilieres(Cursor c) {
        String[] filieres = null;
        if (c.getCount() == 0)
            return filieres;
        else {

            filieres = new String[c.getCount()];

            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {

                filieres[i] = (c.getString(dbHandler.NUM_COL_FILIERE));
                c.moveToNext();
            }
            c.close();
        }

        return filieres;

    }

    public List<Emplois> getHistoriqueListSeances(String heureDebutSeance, String dateSelected) {
        Cursor c;
        int idDate = getIdDate(dateSelected);
        if (idDate == -1) {
            return new ArrayList<>();
        }

        if (heureDebutSeance != null) {
            c = database.rawQuery(
                    "select s." + dbHandler.COL_ID + ", s." + dbHandler.COL_FILIERE + ", s." + dbHandler.COL_MATIERE + " , s." + dbHandler.COL_ENSEIGNANT + " ,s." + dbHandler.COL_SALLE + " ,a." + dbHandler.COL_ABSCENCE + ", s." + dbHandler.COL_JOUR + ", s." + dbHandler.COL_DEBUT
                            + " from " + dbHandler.TABLE_EMPLOIS + " s , " + dbHandler.TABLE_DATE_ABSCENCE + " d , " + dbHandler.TABLE_ABSCENCE + " a "
                            + "where " + "s." + dbHandler.COL_ID + " = a." + dbHandler.COL_ID_SEANCE + " and a." + dbHandler.COL_ID_DATE + " = d." + dbHandler.COL_ID
                            + " and a." + dbHandler.COL_ID_DATE + " = ? and s." + dbHandler.COL_DEBUT + " like ? order by " + dbHandler.COL_SALLE + ";"
                    , new String[]{idDate + "", heureDebutSeance});
        } else {
            c = database.rawQuery(
                    "select s." + dbHandler.COL_ID + ", s." + dbHandler.COL_FILIERE + ", s." + dbHandler.COL_MATIERE + " , s." + dbHandler.COL_ENSEIGNANT + " ,s." + dbHandler.COL_SALLE + " ,a." + dbHandler.COL_ABSCENCE + ", s." + dbHandler.COL_JOUR + ", s." + dbHandler.COL_DEBUT
                            + " from " + dbHandler.TABLE_EMPLOIS + " s , " + dbHandler.TABLE_DATE_ABSCENCE + " d , " + dbHandler.TABLE_ABSCENCE + " a "
                            + "where " + "s." + dbHandler.COL_ID + " = a." + dbHandler.COL_ID_SEANCE + " and a." + dbHandler.COL_ID_DATE + " = d." + dbHandler.COL_ID
                            + " and a." + dbHandler.COL_ID_DATE + " = ?  order by " + dbHandler.COL_DEBUT + ", " + dbHandler.COL_SALLE + ";"
                    , new String[]{idDate + ""});
        }

        return cursorToSeancesForUI(c);
    }


    public List<Emplois> getCurrentListSeances(String heureDebutSeance) {
        Cursor c;
        String query = "select s." + dbHandler.COL_ID + ", s." + dbHandler.COL_FILIERE + ", s." + dbHandler.COL_MATIERE + " , s." + dbHandler.COL_ENSEIGNANT + " ,s." + dbHandler.COL_SALLE + " ,a." + dbHandler.COL_ABSCENCE + ", s." + dbHandler.COL_JOUR + ", s." + dbHandler.COL_DEBUT
                + " from " + dbHandler.TABLE_EMPLOIS + " s , " + dbHandler.TABLE_DATE_ABSCENCE + " d , " + dbHandler.TABLE_ABSCENCE + " a "
                + "where " + "s." + dbHandler.COL_ID + " = a." + dbHandler.COL_ID_SEANCE + " and a." + dbHandler.COL_ID_DATE + " = d." + dbHandler.COL_ID
                + " and a." + dbHandler.COL_ID_DATE + " = " + getIdTodayDate() + " and s." + dbHandler.COL_DEBUT + " like " + heureDebutSeance + " order by " + dbHandler.COL_SALLE + ";";
        Log.e("QUERY", query);
        if (heureDebutSeance != null) {
            c = database.rawQuery(
                    "select s." + dbHandler.COL_ID + ", s." + dbHandler.COL_FILIERE + ", s." + dbHandler.COL_MATIERE + " , s." + dbHandler.COL_ENSEIGNANT + " ,s." + dbHandler.COL_SALLE + " ,a." + dbHandler.COL_ABSCENCE + ", s." + dbHandler.COL_JOUR + ", s." + dbHandler.COL_DEBUT
                            + " from " + dbHandler.TABLE_EMPLOIS + " s , " + dbHandler.TABLE_DATE_ABSCENCE + " d , " + dbHandler.TABLE_ABSCENCE + " a "
                            + "where " + "s." + dbHandler.COL_ID + " = a." + dbHandler.COL_ID_SEANCE + " and a." + dbHandler.COL_ID_DATE + " = d." + dbHandler.COL_ID
                            + " and a." + dbHandler.COL_ID_DATE + " = ? and s." + dbHandler.COL_DEBUT + " like ? order by " + dbHandler.COL_SALLE + ";"
                    , new String[]{getIdTodayDate() + "", heureDebutSeance});
        } else {
            c = database.rawQuery(
                    "select s." + dbHandler.COL_ID + ", s." + dbHandler.COL_FILIERE + ", s." + dbHandler.COL_MATIERE + " , s." + dbHandler.COL_ENSEIGNANT + " ,s." + dbHandler.COL_SALLE + " ,a." + dbHandler.COL_ABSCENCE + ", s." + dbHandler.COL_JOUR + ", s." + dbHandler.COL_DEBUT
                            + " from " + dbHandler.TABLE_EMPLOIS + " s , " + dbHandler.TABLE_DATE_ABSCENCE + " d , " + dbHandler.TABLE_ABSCENCE + " a "
                            + "where " + "s." + dbHandler.COL_ID + " = a." + dbHandler.COL_ID_SEANCE + " and a." + dbHandler.COL_ID_DATE + " = d." + dbHandler.COL_ID
                            + " and a." + dbHandler.COL_ID_DATE + " = ?  order by " + dbHandler.COL_DEBUT + ", " + dbHandler.COL_SALLE + ";"
                    , new String[]{getIdTodayDate() + ""});
        }

        return cursorToSeancesForUI(c);
    }

    private List<Emplois> cursorToSeancesForUI(Cursor c) {

        List<Emplois> journee = new ArrayList<>();
        if (c.getCount() == 0)
            return journee;
        else {
            MaDate maDate = new MaDate();
            String date = maDate.getDateInDateFormat();

            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {

                Emplois emplois = new Emplois();

                emplois.setID(c.getInt(0));
                emplois.setFiliere(c.getString(1));
                emplois.setMatiere(c.getString(2));
                emplois.setEnseignant(c.getString(3));
                emplois.setSalle(c.getString(4));
                emplois.setPresence(c.getInt(5));
                emplois.setJour(c.getString(6));
                emplois.setDebut(c.getString(7));
                emplois.setDate(date);


                journee.add(emplois);
                c.moveToNext();
            }

            c.close();
        }

        return journee;
    }


    public void clearTableEmplois() {
        database.execSQL("delete from " + dbHandler.TABLE_EMPLOIS);
    }

    public void createDatabaseTables() {
        database.execSQL(dbHandler.CREATE_TABLE_EMPLOIS);
    }

    public void createEmptyDatabase() {
        dbHandler.getReadableDatabase();
    }

    public void createTableAbscences() {
        database.execSQL(dbHandler.CREATE_TABLE_ABSCENCE);
    }

    public void createTableDateAbscence() {
        database.execSQL(dbHandler.CREATE_TABLE_DATE_ABSCENCE);
    }

    public boolean isJourneeInitialized() {
        MaDate maDate = new MaDate();

        Cursor c = database.rawQuery("select count (" + dbHandler.COL_ID + ")  from " + dbHandler.TABLE_DATE_ABSCENCE + " where " + dbHandler.COL_DATE + " = ?", new String[]{maDate.getDateInDateFormat()});
        c.moveToFirst();
        return c.getInt(0) > 0;
    }

    public int getIdDate(String dateSelected) {

        Cursor c = database.rawQuery("select " + dbHandler.COL_ID + " from " + dbHandler.TABLE_DATE_ABSCENCE
                + " where " + dbHandler.COL_DATE + " = ?", new String[]{dateSelected});

        if (c.getCount() == 0) {
            return -1;
        } else {
            c.moveToFirst();
            return c.getInt(0);
        }
    }

    public int getIdTodayDate() {
        MaDate maDate = new MaDate();

        Cursor c = database.rawQuery("select " + dbHandler.COL_ID + " from " + dbHandler.TABLE_DATE_ABSCENCE
                + " where " + dbHandler.COL_DATE + " = ? order by " + dbHandler.COL_ID + " desc", new String[]{maDate.getDateInDateFormat()});

        c.moveToFirst();
        return c.getInt(0);
    }

    public int initilizeJournee() {
        MaDate maDate = new MaDate();

        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHandler.COL_DATE, maDate.getDateInDateFormat());
        long id = database.insert(dbHandler.TABLE_DATE_ABSCENCE, null, contentValues);

        if (id != -1) {

            List<Integer> idsSeances;
            idsSeances = getEmploisWithDay(maDate.getJour(), planification());


            for (int idSeance : idsSeances) {
                ContentValues values = new ContentValues();
                values.put(dbHandler.COL_ID_DATE, id);
                values.put(dbHandler.COL_ID_SEANCE, idSeance);
                database.insert(dbHandler.TABLE_ABSCENCE, null, values);

            }
            return 1;
        }
        return -1;
    }

    public String planification() {
        List<RepartitionSemaine> repartitionSemaineList = SharedPerefManager.getInstance(activity).loadRepartitionSemaines();
        MaDate date = new MaDate();

        Log.e("TODAY",date.getDateInDateFormat());
        Calendar aujourdhui = date.getC();
        for (RepartitionSemaine repartitionSemaine : repartitionSemaineList) {
            Log.e("REPARTITION",repartitionSemaine.toString());
            Calendar debut = new GregorianCalendar(2016,Integer.valueOf(repartitionSemaine.getMoisDebut())-1,Integer.valueOf(repartitionSemaine.getDebutSemaine()));
            Calendar fin = new GregorianCalendar(2016,Integer.valueOf(repartitionSemaine.getMoisFin())-1,Integer.valueOf(repartitionSemaine.getFinSemaine()));
            Log.e("Compare",debut.get(Calendar.DAY_OF_MONTH)+"/"+ debut.get(Calendar.MONTH)+ "/" + debut.get(Calendar.YEAR) +" *** " +aujourdhui.get(Calendar.DAY_OF_MONTH)+"/"+ aujourdhui.get(Calendar.MONTH)+ "/" + aujourdhui.get(Calendar.YEAR)+" *** "+fin.get(Calendar.DAY_OF_MONTH)+"/"+ fin.get(Calendar.MONTH)+ "/" + fin.get(Calendar.YEAR));
            if (aujourdhui.after(debut) && aujourdhui.before(fin)) {
                Log.e("FOUND THIS",repartitionSemaine.getRepartition());
                return repartitionSemaine.getRepartition();
            }
        }


        /*Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int tabIndex[] = {123, 130, 206, 213, 220, 227, 305, 312, 410, 416, 423, 430, 507, 514};
        String tabRegime[] = {"Q_B", "Q_A", "Q_B", "Q_A", "Q_B", "Q_A", "Q_B", "Q_A", "Q_B", "Q_A", "Q_B", "Q_A", "Q_B", "Q_A"};
        String tabZone[] = {"Z_1-Z_2-Z_3-Z_4", "Z_1-Z_2-Z_3-Z_4", "Z_2-Z_3-Z_4", "Z_1-Z_3-Z_4", "Z_2-Z_3-Z_4", "Z_1-Z_4", "Z_2-Z_3", "Z_1-Z_4", "Z_2-Z_3", "Z_1-Z_4", "Z_1-Z_2-Z_3", "Z_1-Z_2-Z_4", "Z_1-Z_2-Z_3", "Z_1-Z_2-Z_3-Z_4"};

        int dateAct = (month * 100) + (day);
        int i = 0;
        while (i < tabIndex.length && dateAct > tabIndex[i]) {
            i++;
        }

        if (i < tabRegime.length)
            return tabRegime[i] + "-" + tabZone[i];*/
        Log.e("FOUND THIS","***");
        return "***";
    }

    public void updateSeanceAbscence(int idSeance, int presence) {
        database.execSQL("UPDATE " + dbHandler.TABLE_ABSCENCE + " SET " + dbHandler.COL_ABSCENCE + "='" + presence + "' WHERE " + dbHandler.COL_ID_SEANCE + "='" + idSeance + "';");

    }

    public void clearTableContactEnseignant() {
        database.execSQL("delete from " + dbHandler.TABLE_CONTACT_ENSEIGNANT);
    }


    public void insertContactProf(List<Contact> listContactProf) {
        open();
        try {
            clearTableContactEnseignant();
        } catch (SQLiteException e) {
            e.printStackTrace();
            createTableContactEnseignant();
        }

        for (Contact contact : listContactProf) {
            insertContactEnseignant(contact);
        }
        close();
    }

    private void insertContactEnseignant(Contact contact) {
        database.execSQL("insert into " + dbHandler.TABLE_CONTACT_ENSEIGNANT + " values ( '" + contact.getNom() +"' , '" + contact.getEmail() + "' );");
    }

    private void createTableContactEnseignant() {
        database.execSQL(dbHandler.CREATE_TABLE_CONTACT_ENSEIGNANT);
    }
}
