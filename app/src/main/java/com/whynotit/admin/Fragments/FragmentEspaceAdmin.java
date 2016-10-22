package com.whynotit.admin.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.whynotit.admin.Activity.EspaceAdminActivity;
import com.whynotit.admin.Activity.SplashActivity;
import com.whynotit.admin.Database.DatabaseDAO;
import com.whynotit.admin.Dialog.DialaogDownloadDataBase;
import com.whynotit.admin.Dialog.DialogAjouterUtilisateur;
import com.whynotit.admin.Dialog.DialogExporterTodaySeances;
import com.whynotit.admin.Dialog.DialogModifierUtilisateur;
import com.whynotit.admin.Dialog.DialogNetworkOperation;
import com.whynotit.admin.DownloadEmplois;
import com.whynotit.admin.GoogleSpreadsheetOperations.LoadContactDataFromSpreadsheet;
import com.whynotit.admin.GoogleSpreadsheetOperations.LoadHorairesSeancesDataFromSpreadsheet;
import com.whynotit.admin.GoogleSpreadsheetOperations.LoadRepartitionDataFromSpreadsheet;
import com.whynotit.admin.GoogleSpreadsheetOperations.SpreadsheetParams;
import com.whynotit.admin.Listners.InstallDataBase;
import com.whynotit.admin.MaDate;
import com.whynotit.admin.Managers.NetworkManager;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.Models.Emplois;
import com.whynotit.admin.R;
import com.whynotit.admin.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Harzallah on 26/07/2016.
 */
public class FragmentEspaceAdmin extends Fragment {

    private EspaceAdminActivity activity;
    private LinearLayout exporterDonnees, historique, ajouterUtilisateur, modifierUtilisateur, updateEmplois, consulterActualites;
    private CoordinatorLayout rootLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_espace_admin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (EspaceAdminActivity) getActivity();
        rootLayout = (CoordinatorLayout) view.findViewById(R.id.root_layout);

        exporterDonnees = (LinearLayout) view.findViewById(R.id.layout_exporter_donnees);
        exporterDonnees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogExporterTodaySeances dialogExporterTodaySeances = new DialogExporterTodaySeances(activity, null);
                dialogExporterTodaySeances.show();
            }
        });

        historique = (LinearLayout) view.findViewById(R.id.layout_historique);
        historique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaDate maDate = new MaDate();
                final String[] date = maDate.getDateInDateFormat().split("/");
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int annee, int mois, int jour) {
                        mois++;
                        String dateSelected = jour + "/" + mois + "/" + annee;

                        DialogExporterTodaySeances dialogExporterTodaySeances = new DialogExporterTodaySeances(activity, dateSelected);
                        dialogExporterTodaySeances.show();


                    }
                }, Integer.valueOf(date[2]), Integer.valueOf(date[1]) - 1, Integer.valueOf(date[0]));
                datePickerDialog.show();
            }

        });

        ajouterUtilisateur = (LinearLayout) view.findViewById(R.id.layout_ajouter_utilisateur);
        ajouterUtilisateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAjouterUtilisateur dialogAjouterUtilisateur = new DialogAjouterUtilisateur(activity, rootLayout);
                dialogAjouterUtilisateur.show();
            }

        });


        modifierUtilisateur = (LinearLayout) view.findViewById(R.id.layout_modifier_utilisateur);
        modifierUtilisateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogModifierUtilisateur dialogModifierUtilisateur = new DialogModifierUtilisateur(activity);
                dialogModifierUtilisateur.show();
            }

        });

        updateEmplois = (LinearLayout) view.findViewById(R.id.layout_mise_a_jour);
        updateEmplois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InstallDataBase installDataBaseListner = new InstallDataBase() {
                    @Override
                    public void onDownloadFinish(Activity activity, DialaogDownloadDataBase dialogDownloadDataBase, String dataBaseDate) {
                        setupDataBaseEtudiant(activity, dialogDownloadDataBase, dataBaseDate);
                    }

                    @Override
                    public void onDownloadFailed() {

                    }
                };

                new DownloadEmplois(activity, installDataBaseListner, true,false).execute();

            }
        });

        updateEmplois.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                InstallDataBase installDataBaseListner = new InstallDataBase() {
                    @Override
                    public void onDownloadFinish(Activity activity, DialaogDownloadDataBase dialogDownloadDataBase, String dataBaseDate) {
                        setupDataBaseEtudiant(activity, dialogDownloadDataBase, dataBaseDate);
                    }

                    @Override
                    public void onDownloadFailed() {

                    }
                };

                new DownloadEmplois(activity, installDataBaseListner, true,true).execute();


                return false;
            }
        });


        consulterActualites = (LinearLayout) view.findViewById(R.id.layout_ajouter_actualite);
        consulterActualites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Utils.isNetworkAvailable(activity)) {
                    showSnackBar("Acune connexion disponible!");
                } else {
                    showFragmentActualites();
                }
            }
        });


        LinearLayout downloadRepartitions = (LinearLayout) view.findViewById(R.id.layout_telecharger_repartitions);
        downloadRepartitions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String [] messages = {"Mise à jours en cours des répartitions des semaines...",
                        "Une ERREUR est survenue lors de la mise à jours des répartitions des semaines!",
                        "La mise à jours des répartitions des semaines est términée avec succés."};
                DialogNetworkOperation dialogNetworkOperation = new DialogNetworkOperation(activity,R.drawable.ic_repartitions_white,messages);
                new LoadRepartitionDataFromSpreadsheet(NetworkManager.getInstance().getCredential(activity),activity,
                        SpreadsheetParams.SHEET_ID_REPARTITION_SEANCE,
                        SpreadsheetParams.SHEET_RANGE_REPARTITION_SEANCE,
                        dialogNetworkOperation).execute();
            }
        });

        LinearLayout downloadHorairesSeances = (LinearLayout) view.findViewById(R.id.layout_telecharger_horaire_seance);
        downloadHorairesSeances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String [] messages = {"Mise à jours en cours des horaires des seances...",
                        "Une ERREUR est survenue lors de la mise à jours des horaires des seances!",
                        "La mise à jours des horaires des seances est términée avec succés."};
                DialogNetworkOperation dialogNetworkOperation = new DialogNetworkOperation(activity,R.drawable.ic_horaires_withe,messages);
                new LoadHorairesSeancesDataFromSpreadsheet(NetworkManager.getInstance().getCredential(activity),activity,
                        SpreadsheetParams.SHEET_ID_HORAIRE_SEANCE,
                        SpreadsheetParams.SHEET_RANGE_HORAIRE_SEANCE,
                        dialogNetworkOperation).execute();
            }
        });

        LinearLayout downloadEmails = (LinearLayout) view.findViewById(R.id.layout_telecharger_emails);
        downloadEmails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String [] messages = {"Mise à jours en cours des E-mails des professeurs...",
                        "Une ERREUR est survenue lors de la mise à jours des E-mails des professeurs!",
                        "La mise à jours des E-mails des professeurs est términée avec succés."};
                DialogNetworkOperation dialogNetworkOperation = new DialogNetworkOperation(activity,R.drawable.ic_email_withe,messages);
                new LoadContactDataFromSpreadsheet(NetworkManager.getInstance().getCredential(activity),activity,
                        SpreadsheetParams.SHEET_ID_CONTACT_PROF,
                        SpreadsheetParams.SHEET_RANGE_CONTACT_PROF,
                        dialogNetworkOperation).execute();
            }
        });
    }

    private void showSnackBar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }


    private static void setupDataBaseEtudiant(Activity activity, DialaogDownloadDataBase dialogDownloadDataBase, String dataBaseDate) {

        dialogDownloadDataBase.getDownloadListner().onDownloadFinish();

        String FL_PATH = activity.getFilesDir() + "/tmp.csv";
        DatabaseDAO databaseDAO = new DatabaseDAO(activity);
        try {
            databaseDAO.open();
            databaseDAO.clearTableEmplois();
        } catch (SQLiteException e) {
            dialogDownloadDataBase.getDownloadListner().onInstallationFinish(true);
            e.printStackTrace();
            Log.e("Creating Tables", "EMPLOI");
            databaseDAO.createDatabaseTables();
        }

        /** INSTALLATION EMPLOIS **/
        //String [] lines = resultEmplois.split("\n");


        BufferedReader br;
        int nbEmploiInserted = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(FL_PATH)));
            br.readLine();

            String ch = null, enseignant = null;
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
                            nbEmploiInserted++;
                            //Log.e("INSERTED",e.getMatiere());

                        }

                    } else {
                        enseignant = ch.replace(";", "");
                        //Log.e("RRRRR",enseignant);
                    }
                }

            }
            Log.e("XXXXXXX", enseignant + " = " + ch + " nb = " + nbEmploiInserted);
            databaseDAO.close();
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            dialogDownloadDataBase.getDownloadListner().onInstallationFinish(false);
        } catch (IOException e) {
            dialogDownloadDataBase.getDownloadListner().onInstallationFinish(false);
            e.printStackTrace();
        }

        //INIT TABLE ABSENCE
        databaseDAO.open();
        databaseDAO.initilizeJournee();
        databaseDAO.close();


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

    public void showFragmentActualites() {
        // Begin the transaction
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_container, new FragmentActualites(), FragmentActualites.class.getName());
        // Append this transaction to the backstack
        ft.addToBackStack(FragmentActualites.class.getName());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        activity.getToolbar().setTitle("Espace admin");
    }
}

/*
    public void exporter() {

        LayoutInflater inflater = this.getLayoutInflater();
        View myView = inflater.inflate(R.layout.exporter_dialog, null);
        final RadioGroup mRadioGroup = (RadioGroup) myView.findViewById(R.id.radiGroupExporter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Exporter !");
        builder.setView(myView);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final List<Emplois> mList;
                int numeroSeance = -1;
                switch (mRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.exporterS1: {
                        mList = mState.loadJourneeFromDB("S1");
                        numeroSeance = 1;
                        break;
                    }
                    case R.id.exporterS2: {
                        mList = mState.loadJourneeFromDB("S2");
                        numeroSeance = 2;
                        break;
                    }
                    case R.id.exporterS3: {
                        mList = mState.loadJourneeFromDB("S3");
                        numeroSeance = 3;
                        break;
                    }
                    case R.id.exporterS4: {
                        mList = mState.loadJourneeFromDB("S4");
                        numeroSeance = 4;
                        break;
                    }
                    case R.id.exporterS5: {
                        mList = mState.loadJourneeFromDB("S5");
                        numeroSeance = 5;
                        break;
                    }
                    case R.id.exporterS6: {
                        mList = mState.loadJourneeFromDB("S6");
                        numeroSeance = 6;
                        break;
                    }
                    default: {
                        mList = mState.loadJourneeFromDBWithDay(mState.getJour());
                        break;
                    }
                }

                new PostData(context, USER, numeroSeance, mList, mState.getNomFichier(), mState.getMaDate().toString()).execute();

            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }




    private void historique() {
        final Context context = this;
        final LayoutInflater inflater = this.getLayoutInflater();
        View myView = inflater.inflate(R.layout.historique_dialog, null);
        final Spinner spinnerJour = (Spinner) myView.findViewById(R.id.jour);
        final Spinner spinnerSeance = (Spinner) myView.findViewById(R.id.seance);

        List<String> spinnerArrayJour = new ArrayList<String>();
        spinnerArrayJour.add("LUNDI");
        spinnerArrayJour.add("MARDI");
        spinnerArrayJour.add("MERCREDI");
        spinnerArrayJour.add("JEUDI");
        spinnerArrayJour.add("VENDREDI");
        spinnerArrayJour.add("SAMEDI");

        ArrayAdapter<String> adapterJour = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArrayJour);
        adapterJour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJour.setAdapter(adapterJour);


        List<String> spinnerArraySeance = new ArrayList<String>();
        spinnerArraySeance.add("S1");
        spinnerArraySeance.add("S2");
        spinnerArraySeance.add("S3");
        spinnerArraySeance.add("S4");
        spinnerArraySeance.add("S5");
        spinnerArraySeance.add("S6");

        ArrayAdapter<String> adapterSeance = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArraySeance);
        adapterSeance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeance.setAdapter(adapterSeance);


        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Historique!");
        builder.setView(myView);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Exporter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final List<Emplois> mList;
                String numeroSeance = "S7";
                String jour = spinnerJour.getSelectedItem().toString();
                numeroSeance = spinnerSeance.getSelectedItem().toString();
                mList = mState.loadJourneeFromDB(jour, numeroSeance);

                new PostData(context, USER, Integer.valueOf(numeroSeance.charAt(1)), mList, mState.getNomFichier(), mState.getMaDate().getDateHistorique(jour)).execute();

            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }





public void resetBD() {

        LayoutInflater inflater = this.getLayoutInflater();
        View myView = inflater.inflate(R.layout.dialog_maj, null);
        TextView messageMaj = (TextView) myView.findViewById(R.id.messageMaj);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("RESET !");
        builder.setView(myView);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                mState.resetEmplois();

            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }





    public void miseAJour() {
        new CheckMAJ(this).execute();
        //new UpdateDataBase(mState).execute();
    }





    private void ajouterUtilisateur() {

        final Context context = this;
        LayoutInflater inflater = this.getLayoutInflater();
        final View myView = inflater.inflate(R.layout.connexion_dialog, null);
        final EditText motDePasse = (EditText) myView.findViewById(R.id.mot_de_passe);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Exporter !");
        builder.setView(myView);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (motDePasse.getText().toString().equals("issatadmin")) {
                    final DialogAjouterUtilisateur dialogAjouterUtilisateur = new DialogAjouterUtilisateur(mState);
                    DialogAuthentification.setDialogResult(new DialogAuthentification.MyDialogListener() {
                        @Override
                        public void selectedChoise(String date, String heure, String minute, String participants) {
                            dialogAjouterUtilisateur.dismiss();

                        }
                    });
                    dialogAjouterUtilisateur.show();
                } else {
                    Toast.makeText(getApplicationContext(), "mot de passe incorrect!", Toast.LENGTH_LONG).show();
                }

            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }





    private void modifierUtilisateur() {


        final Context context = this;
        LayoutInflater inflater = this.getLayoutInflater();
        final View myView = inflater.inflate(R.layout.connexion_dialog, null);
        final EditText motDePasse = (EditText) myView.findViewById(R.id.mot_de_passe);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Exporter !");
        builder.setView(myView);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (motDePasse.getText().toString().equals("issatadmin")) {
                    final DialogModifierUtilisateur dialogModifierUtilisateur = new DialogModifierUtilisateur(mState);
                    DialogAuthentification.setDialogResult(new DialogAuthentification.MyDialogListener() {
                        @Override
                        public void selectedChoise(String date, String heure, String minute, String participants) {
                            dialogModifierUtilisateur.dismiss();

                        }
                    });
                    dialogModifierUtilisateur.show();
                } else {
                    Toast.makeText(getApplicationContext(), "mot de passe incorrect!", Toast.LENGTH_LONG).show();
                }

            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }



    private void telechargerRatrappage() {
        List<String> values = new ArrayList<>();


        httpClient = new OkHttpClient();
        String URL_LISTE_ETUDIANTS = "https://spreadsheets.google.com/tq?key=1vi9klUY25RhroQn3NMCJgBbrMueYxnjFgoQDHuykR6Q";
        Request request = new Request.Builder()
                .url(URL_LISTE_ETUDIANTS)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                JSONObject resultJSON = null;
                String resultListEtudiant = response.body().string();
                resultListEtudiant = resultListEtudiant.substring(resultListEtudiant.indexOf('(') + 1, resultListEtudiant.lastIndexOf(')'));

                EmploisBDD mBD = new EmploisBDD(activity);
                mBD.open();
                mBD.clearRattrapages();
                /** INSTALLATION LISTE ETUDIANT  **/
    /*try {
        resultJSON = new JSONObject(resultListEtudiant);
        JSONObject table = resultJSON.getJSONObject("table");
        JSONArray rows = table.getJSONArray("rows");


        String enseignant = "??";
        for (int i = 0; i < rows.length(); i++) {

            JSONArray values = rows.getJSONObject(i).getJSONArray("c");
            if (!values.get(0).toString().contains("null")) {
                enseignant = values.getJSONObject(0).getString("v");
            } else {
                Emplois emplois = new Emplois();
                emplois.setDebut(values.getJSONObject(1).getString("v"));
                emplois.setJour(values.getJSONObject(2).getString("v").substring(values.getJSONObject(2).getString("v").indexOf('-') + 1));
                emplois.setSeance(values.getJSONObject(3).getString("v"));
                emplois.setFiliere(values.getJSONObject(4).getString("v"));
                emplois.setMatiere(values.getJSONObject(5).getString("v").substring(values.getJSONObject(5).getString("v").indexOf('-') + 1));
                emplois.setSalle(values.getJSONObject(6).getString("v"));
                emplois.setType(values.getJSONObject(7).getString("v"));
                emplois.setEnseignant(enseignant);
                emplois.setRegime("??");
                emplois.setFin("??");
                Log.e("XXXXXXXXXXXXXx", emplois.toString());

                mBD.insertEmplois(emplois);
            }
        }


    } catch (JSONException e) {
        e.printStackTrace();
    }
    mBD.close();
}
});


        }

        */


