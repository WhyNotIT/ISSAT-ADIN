package com.whynotit.admin.GoogleSpreadsheetOperations;

/**
 * Created by Harzallah on 26/07/2016.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.whynotit.admin.Activity.SplashActivity;
import com.whynotit.admin.Dialog.DialogNetworkOperation;
import com.whynotit.admin.Managers.NetworkManager;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.Models.RepartitionSemaine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Google Sheets API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class LoadRepartitionDataFromSpreadsheet extends AsyncTask<Void, Void, List<String>> {
    private Object listnerOrDialog;
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private Activity activity;
    private String spreadsheetId;
    private String range;

    public LoadRepartitionDataFromSpreadsheet(GoogleAccountCredential credential, Activity activity, String spreadsheetId, String range, Object listnerOrDialog) {
        this.activity = activity;
        this.spreadsheetId = spreadsheetId;
        this.range = range;
        this.listnerOrDialog = listnerOrDialog;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build();
    }

    /**
     * Background task to call Google Sheets API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     *
     * @return List of names and majors
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {


        ValueRange response = this.mService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        /*
        ValueRange valueRange = new ValueRange();
        //valueRange.set("Harzallah","mezri");

        List<List<String>> data = new ArrayList<>();
        List<String> firstColumn = new ArrayList<>();
        List<String> secondColumn = new ArrayList<>();
        firstColumn.add("MEZRI");
        firstColumn.add("HARZALLAH");

        data.add(firstColumn);
        data.add(secondColumn);
        valueRange.put("values", data);

        Log.e("XXXXXXXXXX", valueRange.toString());

        AppendValuesResponse response = this.mService.spreadsheets().values()
                .append(spreadsheetId, range, valueRange)
                .setValueInputOption("RAW")
                .execute();

        Log.e("XXXXXXXX :D :D", response.toString());
        //values().append(spreadsheetId, range, valueRange);

        */

        /** PARSING DATA FROM RESULT **/
        List<List<Object>> values = response.getValues();
        List row = null;
        if (values != null) {
            List<RepartitionSemaine> listRegimeSemaines = new ArrayList<>();
            try {

                for (int i = 0; i < values.size(); i++) {
                    row = values.get(i);
                    Log.e("XXX REPARTITION",row.toString());
                    String [] dates = row.get(0).toString().split("-");
                    String datePartie1 = dates[0];
                    String datePartie2 = dates[1];
                    String [] date1 = datePartie1.split("/");
                    String debutSemaine,finSemaine, debutMois, finMois;
                    debutSemaine = date1[0];
                    if (date1.length == 1) {
                        debutMois = null;
                    } else {
                        debutMois = date1[1];
                    }
                    String [] date2 = datePartie2.split("/");
                    finSemaine = date2[0];
                    finMois = date2[1];

                    if (debutMois == null) {
                        debutMois = finMois;
                    }

                    String repartition = row.get(1).toString();

                    RepartitionSemaine repartitionSemaine = new RepartitionSemaine(debutSemaine, finSemaine, debutMois, finMois,repartition);
                    listRegimeSemaines.add(repartitionSemaine);
                }

                SharedPerefManager.getInstance(activity).saveRegimeSemaines(listRegimeSemaines);



            } catch (Exception e) {
                final List finalRow = row;
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "FORMAT INVALIDE : " + finalRow.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        }


        return new ArrayList<>();
    }


    @Override
    protected void onPreExecute() {
        if (listnerOrDialog instanceof DialogNetworkOperation) {
            Log.e("XXXXXXXXXX","INSTANCE OF");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((DialogNetworkOperation) listnerOrDialog).show();
                }
            });
        }
    }

    @Override
    protected void onPostExecute(List<String> output) {
        //mProgress.hide();
        if (output == null || output.size() == 0) {
            Toast.makeText(activity, "No results returned.", Toast.LENGTH_LONG).show();
        } else {
            output.add(0, "Data retrieved using the Google Sheets API:");
            //Toast.makeText(activity,TextUtils.join("\n", output),Toast.LENGTH_LONG).show();
            Log.e("RESPONSE", TextUtils.join("\n", output));
        }

        /**  CHECK IF THE LOAD FROM SHEETS IS DONE AND LAUNCH MAIN ACTIVITY   **/
        if (listnerOrDialog instanceof DialogNetworkOperation) {
            ((DialogNetworkOperation) listnerOrDialog).getNetworkOperationListner().onNetworkOperationSucces();
        } else if (listnerOrDialog instanceof SplashActivity.SpreadSheetLoadingListner) {
            ((SplashActivity.SpreadSheetLoadingListner)listnerOrDialog).onLoadingRepartiotionDone();
        }

    }

    @Override
    protected void onCancelled() {
        //mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                NetworkManager.getInstance().showGooglePlayServicesAvailabilityErrorDialog(activity,
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        NetworkManager.REQUEST_AUTHORIZATION);
            } else {
                Log.e("ERREUR", "The following error occurred:\n"
                        + mLastError.getMessage());
                Toast.makeText(activity, "The following error occurred:\n"
                        + mLastError.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, "Request cancelled.", Toast.LENGTH_LONG).show();

        }
    }
}