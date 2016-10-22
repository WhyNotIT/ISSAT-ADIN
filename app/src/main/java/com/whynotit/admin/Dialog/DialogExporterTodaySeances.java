package com.whynotit.admin.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.whynotit.admin.Activity.EspaceAdminActivity;
import com.whynotit.admin.Adapters.ListSeancesRadioButtonAdapter;
import com.whynotit.admin.Database.DatabaseDAO;
import com.whynotit.admin.GoogleSpreadsheetOperations.SendAbscenceDataToSpreadsheet;
import com.whynotit.admin.GoogleSpreadsheetOperations.SpreadsheetParams;
import com.whynotit.admin.Managers.NetworkManager;
import com.whynotit.admin.Managers.SeanceAExporterManager;
import com.whynotit.admin.Managers.SeancesHorairesManager;
import com.whynotit.admin.Managers.SeancesToUploadManager;
import com.whynotit.admin.Models.Emplois;
import com.whynotit.admin.R;

import java.util.List;


/**
 * Created by Harzallah on 13/07/2016.
 */
public class DialogExporterTodaySeances extends Dialog {


    private final String dateSelected;
    private EspaceAdminActivity activity;
    private RecyclerView recyclerView;
    private Button exporter;
    private Button annuler;
    private CoordinatorLayout rootLayout;


    public DialogExporterTodaySeances(EspaceAdminActivity activity, String dateSelected) {
        super(activity);
        this.activity = activity;
        this.dateSelected = dateSelected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_exporter_donnees);
        setCancelable(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        recyclerView = (RecyclerView) findViewById(R.id.list_seance_exporter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ListSeancesRadioButtonAdapter mAdapter = new ListSeancesRadioButtonAdapter(activity);
        recyclerView.setAdapter(mAdapter);

        exporter = (Button) findViewById(R.id.exporter);
        exporter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SeanceAExporterManager.getInstance().getPositionSelectedSeance() == -1) {
                    Snackbar.make(rootLayout,"Selectionnez un horaire d' abord!",Snackbar.LENGTH_LONG).show();
                    return;
                }
                Log.e("XXXXXXXXX", SeanceAExporterManager.getInstance().getPositionSelectedSeance() + "/88");
                dismiss();
                String debut;
                if (SeanceAExporterManager.getInstance().getPositionSelectedSeance() == SeancesHorairesManager.getInstance().getListSeancesForExportation().size() - 1) {
                    /** NULL FOR ALL SEANCES OF THE DAY */
                    debut = null;
                } else {
                    debut = SeancesHorairesManager.getInstance().getListSeancesForExportation().get(SeanceAExporterManager.getInstance().getPositionSelectedSeance()).getHeureDebut();
                }

                SeanceAExporterManager.getInstance().setPositionSelectedSeance(-1);
                SeanceAExporterManager.getInstance().setSelectedSeance(null);

                List<Emplois> emploisList;

                DatabaseDAO databaseDAO = new DatabaseDAO(activity);
                databaseDAO.open();

                if (dateSelected == null)
                    emploisList = databaseDAO.getCurrentListSeances(debut);
                else
                    emploisList = databaseDAO.getHistoriqueListSeances(debut, dateSelected);

                databaseDAO.close();

                for (Emplois e : emploisList)
                    Log.e("SEANCE", e.toString());

                if (emploisList.size() == 0) {
                    Snackbar.make(activity.getRootLayout(),"Aucune seance trouvée pour la date : " + dateSelected +".",Snackbar.LENGTH_LONG).show();
                } else {
                    /** **** IMPORTANT ****
                     * Add one more empty pbject to avoid loosing the last seance */
                    //emploisList.add(null);

                    SeancesToUploadManager.getInstance().setListSeancesToUpload(emploisList);
                    new SendAbscenceDataToSpreadsheet(NetworkManager.getInstance().getCredential(activity), activity, SpreadsheetParams.SHEET_ID_ABSCENCE, SpreadsheetParams.SHEET_RANGE_ABSCENCE).execute();
                }

            }
        });

        annuler = (Button) findViewById(R.id.annuler);
        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //new PostData(context, USER, numeroSeance, mList, mState.getNomFichier(), mState.getMaDate().toString()).execute();

    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        SeanceAExporterManager.getInstance().setSelectedSeance(null);
        SeanceAExporterManager.getInstance().setPositionSelectedSeance(-1);
        super.setOnDismissListener(listener);
    }
}
