package com.whynotit.admin.Activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whynotit.admin.Database.DatabaseDAO;
import com.whynotit.admin.Dialog.DialaogDownloadDataBase;
import com.whynotit.admin.DownloadEmplois;
import com.whynotit.admin.Listners.InstallDataBase;
import com.whynotit.admin.Managers.NetworkManager;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.Managers.UsersManager;
import com.whynotit.admin.Models.Emplois;
import com.whynotit.admin.R;
import com.whynotit.admin.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Harzallah on 01/07/2016.
 */

public class SplashActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    /**
     * Google Spreadsheet
     **/
    private static final String PREF_ACCOUNT_NAME = "accountName";
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private TextView indicationText;
    private SplashActivity activity;
    private LinearLayout layoutConfiguration;
    private DatabaseDAO databaseDAO;
    boolean isLoadingHoraireDone = false, isLoadingRepartitionDone = false, isLoadingContactDone = false;

    public interface SpreadSheetLoadingListner {

        void onLoadingHoraireDone();

        void onLoadingRepartiotionDone();

        void onLoadingContactDone();
    }

    private SpreadSheetLoadingListner spreadSheetLoadingListner = new SpreadSheetLoadingListner() {
        @Override
        public synchronized void onLoadingHoraireDone() {
            isLoadingHoraireDone = true;
            if (isLoadingContactDone && isLoadingRepartitionDone) {
                SharedPerefManager.getInstance(activity).setApplicationInitialized();
                initJournee();
                startMainActivity();
            }
        }

        @Override
        public synchronized void onLoadingRepartiotionDone() {
            isLoadingRepartitionDone = true;
            if (isLoadingContactDone && isLoadingHoraireDone) {
                SharedPerefManager.getInstance(activity).setApplicationInitialized();
                initJournee();
                startMainActivity();
            }
        }

        @Override
        public synchronized void onLoadingContactDone() {
            isLoadingContactDone = true;
            if (isLoadingHoraireDone && isLoadingRepartitionDone) {
                SharedPerefManager.getInstance(activity).setApplicationInitialized();
                initJournee();
                startMainActivity();
            }
        }
    };

    private void initJournee() {
        //INIT TABLE ABSENCE
        databaseDAO.open();
        databaseDAO.initilizeJournee();
        databaseDAO.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        activity = this;
        layoutConfiguration = (LinearLayout) findViewById(R.id.layout_configuration);
        indicationText = (TextView) findViewById(R.id.text_configuration);

        setupDataBaseDAO();
        try {
            initBD();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void setupDataBaseDAO() {
        databaseDAO = new DatabaseDAO(activity);
    }

    public void initBD() throws SQLException {

        if (!databaseDAO.isDatabaseAvailable()) {

            if (Utils.isNetworkAvailable(activity)) {

                databaseDAO.open();
                databaseDAO.createEmptyDatabase();
                databaseDAO.createTableAbscences();
                databaseDAO.createTableDateAbscence();
                databaseDAO.close();


                InstallDataBase installDataBase = new InstallDataBase() {
                    @Override
                    public void onDownloadFinish(Activity activity, DialaogDownloadDataBase dialogDownloadDataBase, String dataBaseDate) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                indicationText.setText("Installation de la base des données...");
                            }
                        });
                        setupDataBaseEtudiant(activity, dialogDownloadDataBase, dataBaseDate);
                    }

                    @Override
                    public void onDownloadFailed() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                indicationText.setText("Erreur lors du téléchargement de la base des données!");
                            }
                        });
                    }
                };
                indicationText.setText("Téléchargement de la base des données en cours...");
                new DownloadEmplois(activity, installDataBase, false, false).execute();

            } else {

                Snackbar.make(findViewById(R.id.root_layout), "Aucune connexion disponible!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("REESSAYER", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadHoraireSeanceFromSpreadsheet();
                            }
                        })
                        .show();
                /*
                try {
                    databaseDAO.copyDataBase();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
        } else {
            loadHoraireSeanceFromSpreadsheet();
        }


    }

    public void loadHoraireSeanceFromSpreadsheet() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicationText.setText("Téléchargement des données de configuration...");
            }
        });
        /** LOAD USERS LIST **/
        UsersManager.getInstance().setUserList(SharedPerefManager.getInstance(activity).loadUsersList());

        NetworkManager.getInstance().getCredential(activity);
        if (!SharedPerefManager.getInstance(activity).isApplicationInitialized()) {
            if (Utils.isNetworkAvailable(activity)) {
                NetworkManager.getInstance().initApplicationDataFromSpreadsheet(activity, spreadSheetLoadingListner);
            } else {
                Snackbar.make(findViewById(R.id.root_layout), "Aucune connexion disponible!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("REESSAYER", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadHoraireSeanceFromSpreadsheet();
                            }
                        })
                        .show();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startMainActivity();
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onStop() {
        super.onStop();

    }


    public void startMainActivity() {
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showSaisirFiliere() {
        layoutConfiguration.setVisibility(View.GONE);
    }


    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(activity,
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
                } else {
                    NetworkManager.getInstance().initApplicationDataFromSpreadsheet(activity, spreadSheetLoadingListner);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPerefManager.getInstance(activity).saveAccountName(accountName);
                        NetworkManager.getInstance().getCredential(activity).setSelectedAccountName(accountName);
                        NetworkManager.getInstance().initApplicationDataFromSpreadsheet(activity, spreadSheetLoadingListner);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    NetworkManager.getInstance().initApplicationDataFromSpreadsheet(activity, spreadSheetLoadingListner);
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


    private void setupDataBaseEtudiant(Activity activity, DialaogDownloadDataBase dialogDownloadDataBase, String dataBaseDate) {

        String FL_PATH = activity.getFilesDir() + "/tmp.csv";
        DatabaseDAO databaseDAO = new DatabaseDAO(activity);
        try {
            databaseDAO.open();
            databaseDAO.clearTableEmplois();
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e("Creating Tables", "EMPLOI");
            databaseDAO.createDatabaseTables();
        }

        /** INSTALLATION EMPLOIS **/
        //String [] lines = resultEmplois.split("\n");


        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(FL_PATH)));
            br.readLine();

            String ch, enseignant = null;
            String tab[];
            //databaseDAO.open();
            while (br.ready()) {
                ch = br.readLine();

                if (ch != null) {
                    if (ch.startsWith(";")) {
                        tab = ch.split(";");
                        if (tab.length == 11) {

                            String filiere, jour, matiere;
                            try {
                                filiere = tab[5].substring(0, tab[5].lastIndexOf('-'));
                            } catch (IndexOutOfBoundsException e) {
                                filiere = tab[5];
                            }
                            try {
                                jour = tab[1].substring(tab[1].indexOf('-') + 1);
                            } catch (IndexOutOfBoundsException e) {
                                jour = tab[1];
                            }
                            try {
                                matiere = tab[6].substring(tab[6].indexOf('-') + 1);
                            } catch (IndexOutOfBoundsException e) {
                                matiere = tab[6];
                            }
                            Emplois e = new Emplois(
                                    filiere
                                    , jour
                                    , tab[2]
                                    , tab[7]
                                    , matiere
                                    , enseignant
                                    , tab[10]
                                    , tab[9].replace("-", "")
                                    , tab[3], tab[4]);
                            databaseDAO.insertEmplois(e);
                            //Log.e("INSERTED",e.getMatiere());

                        }

                    } else {
                        enseignant = ch.replace(";", "");
                        //Log.e("RRRRR",enseignant);
                    }
                }

            }
            databaseDAO.close();
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    indicationText.setText("Erreur lors du téléchargement de la base des données!");
                }
            });
        } catch (IOException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    indicationText.setText("Erreur lors du téléchargement de la base des données!");
                }
            });
            e.printStackTrace();
        }

        //loadHoraireSeanceFromSpreadsheet();




        //TODO Save the date of the new data base!
        SharedPerefManager.getInstance(activity).saveDataBaseDate(dataBaseDate);

        if (activity.getClass() == SplashActivity.class) {
            ((SplashActivity) activity).loadHoraireSeanceFromSpreadsheet();
        } else {
            if (dialogDownloadDataBase != null) {
                dialogDownloadDataBase.getDownloadListner().onInstallationFinish(true);
            }
        }


    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        NetworkManager.getInstance().initApplicationDataFromSpreadsheet(activity, spreadSheetLoadingListner);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
