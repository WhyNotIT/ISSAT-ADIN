package com.whynotit.admin.GoogleSpreadsheetOperations;

/**
 * Created by Harzallah on 26/07/2016.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.whynotit.admin.Database.DatabaseDAO;
import com.whynotit.admin.Dialog.DialogEndUpload;
import com.whynotit.admin.MaDate;
import com.whynotit.admin.Managers.NetworkManager;
import com.whynotit.admin.Managers.SeancesToUploadManager;
import com.whynotit.admin.Managers.UserManager;
import com.whynotit.admin.Models.Emplois;
import com.whynotit.admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * An asynchronous task that handles the Google Sheets API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class SendAbscenceDataToSpreadsheet extends AsyncTask<Void, Void, List<Object>> {
    private com.google.api.services.sheets.v4.Sheets mService = null;
    private Exception mLastError = null;
    private Activity activity;
    private String spreadsheetId;
    private String range;
    private DialogEndUpload dialogEndUpload;

    public interface SendEmailsListner {
        void onSendEmailsDone (List<Object> nonNotifies);
    }

    private SendEmailsListner sendEmailsListner = null;
    List<Emplois> abscenceEmails = new ArrayList<>();




    public SendAbscenceDataToSpreadsheet(GoogleAccountCredential credential, Activity activity, String spreadsheetId, String range) {
        this.activity = activity;
        this.spreadsheetId = spreadsheetId;
        this.range = range;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build();

    }

    //Envoi D'email aux utilisateurs

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SpreadsheetParams.EMAIL_LOGIN, SpreadsheetParams.EMAIL_PASSWORD);
            }
        });
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SpreadsheetParams.EMAIL_LOGIN));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    public void sendMail(List<Emplois> abscencesEmails, SendEmailsListner sendEmailsListner) {
        new SendMailTask(abscencesEmails,sendEmailsListner).execute();
    }

    private class SendMailTask extends AsyncTask<Void, Void, List<Object>> {

        private final List<Emplois> abscencesEmails;
        private final SendEmailsListner sendEmailListner;

        public SendMailTask(List<Emplois> abscencesEmails, SendEmailsListner sendEmailsListner) {
            this.abscencesEmails = abscencesEmails;
            this.sendEmailListner = sendEmailsListner;
        }

        @Override
        protected List<Object> doInBackground(Void... voids) {
            DatabaseDAO databaseDAO = new DatabaseDAO(activity);
            databaseDAO.open();
            Session session = createSessionObject();
            List<Object> nonNotifes = new ArrayList<>();

            for (Emplois emplois : abscencesEmails) {

                String emailEnseignant = databaseDAO.getEmailEnseignant(emplois.getEnseignant().toLowerCase());
                if (emailEnseignant != null) {
                    Log.e("EMAIL", emplois.getEnseignant() + " -> " + emailEnseignant);
                    Message message = null;
                    try {
                        message = createMessage(emailEnseignant, "ISSAT SOUSSE ABSENCE", createEmailMessage(emplois), session);
                        Transport.send(message);

                    } catch (MessagingException e1) {
                        e1.printStackTrace();
                        nonNotifes.add(emplois.getEnseignant());
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                        nonNotifes.add(emplois.getEnseignant());
                    }


                } else {
                    nonNotifes.add(emplois.getEnseignant());
                }
            }
            databaseDAO.close();

            return nonNotifes;
        }

        private String createEmailMessage(Emplois emplois) {
            String msg = activity.getString(R.string.email_absence_body_top);
            msg +=  " " + emplois.getEnseignant() + ",\n";
            msg += activity.getString(R.string.email_absence_body_start);
            msg += emplois.toStringForEmail();
            msg += activity.getString(R.string.email_absence_body_end);
            return msg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Sending email. Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();*/
        }


        @Override
        protected void onPostExecute(List<Object> nonNotifies) {
            super.onPostExecute(nonNotifies);
            sendEmailListner.onSendEmailsDone(nonNotifies);
        }
    }


    /**
     * Background task to call Google Sheets API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<Object> doInBackground(Void... params) {

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
    private List<Object> getDataFromApi() throws IOException {

        /*
        ValueRange response = this.mService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();*/

        List<List<String>> data = new ArrayList<>();
        ValueRange valueRange = new ValueRange();

        /** ADD USER NAME **/
        List<String> rowUser = new ArrayList<>();

        //rowUser.add("");
        //rowUser.add("");
        rowUser.add("***********************");
        rowUser.add("***********************");
        rowUser.add("***********************");
        rowUser.add("***********************");
        data.add(rowUser);

        rowUser = new ArrayList<>();
        //rowUser.add("");
        //rowUser.add("");
        rowUser.add("***********************");

        try {
            rowUser.add("" + UserManager.getInstance().getUser().getNom());
        } catch (NullPointerException e) {
            rowUser.add("ADMIN UPLOAD");
        }

        MaDate date = new MaDate();
        rowUser.add(date.getAllDateFeilds());

        rowUser.add("***********************");
        data.add(rowUser);

        rowUser = new ArrayList<>();

        //rowUser.add("");
        //rowUser.add("");
        rowUser.add("***********************");
        rowUser.add("***********************");
        rowUser.add("***********************");
        rowUser.add("***********************");
        data.add(rowUser);


        for (Emplois seance : SeancesToUploadManager.getInstance().getListSeancesToUpload()) {
            List<String> row = new ArrayList<>();

            //row.add("");
            row.add(seance.getJour());
            row.add(seance.getDebut());
            row.add(seance.getFiliere());
            row.add(seance.getEnseignant());
            row.add(seance.getMatiere());
            row.add(seance.getSalle());
            row.add(seance.getPresenceToString());
            row.add(seance.getDate());
            data.add(row);

            if (seance.getPresence() == 0) {
                abscenceEmails.add(seance);
            }

        }

        valueRange.put("values", data);

        Log.e("XXXXXXXXXX", valueRange.toString());

        AppendValuesResponse response = this.mService.spreadsheets().values()
                .append(spreadsheetId, range, valueRange)
                .setValueInputOption("RAW")
                .execute();

        Log.e("XXXXXXXX :D :D", response.toString());
        //values().append(spreadsheetId, range, valueRange);
        final List<Object> result = new ArrayList<>();

        try {

            JSONObject responseJson = new JSONObject(response.toString());
            JSONObject updates = responseJson.getJSONObject("updates");

            int updatedRows = updates.getInt("updatedRows");

            //Remove User Rows
            updatedRows -= 3;
            Log.e("RESULT",updatedRows + " *** "+ SeancesToUploadManager.getInstance().getListSeancesToUpload().size());
            if (SeancesToUploadManager.getInstance().getListSeancesToUpload().size() == updatedRows) {
                result.add(true);
            } else {
                result.add(false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            result.add(false);
        }


        if ((boolean) result.get(0) && abscenceEmails.size() > 0) {
            sendEmailsListner = new SendEmailsListner() {
                @Override
                public void onSendEmailsDone(List<Object> nonNotifies) {
                    dialogEndUpload.getUploadListner().onUploadSucces(activity,nonNotifies);
                }
            };
            //TODO REMOVE //
            sendMail(abscenceEmails,sendEmailsListner);
        }

        /** PARSING DATA FROM RESULT **/
        /*
        List<List<Object>> values = response.getValues();
        if (values != null) {
            List<HoraireSeance> listSeance = new ArrayList<>();
            for (List row : values) {
                HoraireSeance horaireSeance = new HoraireSeance(row.get(0).toString(),row.get(1).toString());
                listSeance.add(horaireSeance);
            }
            SeancesHorairesManager.getInstance().setListSeances(listSeance);
            SharedPerefManager.getInstance(activity).setApplicationInitialized();
            SharedPerefManager.getInstance(activity).saveHorairesSeances(listSeance);
            activity.startMainActivity();
        }*/


        return result;
    }


    @Override
    protected void onPreExecute() {
        dialogEndUpload = new DialogEndUpload(activity);
        dialogEndUpload.show();

        //Toast.makeText(activity, "Loading...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(List<Object> output) {
        /*if (output == null || output.size() == 0) {
            Toast.makeText(activity, "No results returned.", Toast.LENGTH_LONG).show();
        } else {
            output.add(0, "Data retrieved using the Google Sheets API:");
            //Toast.makeText(activity,TextUtils.join("\n", output),Toast.LENGTH_LONG).show();
            Log.e("RESPONSE", TextUtils.join("\n", output));
        }*/

        if (output != null && output.size() > 0 && output.get(0) != null) {

            if (!(boolean) output.get(0)) {
                dialogEndUpload.getUploadListner().onUploadFailed();
            } else if ((boolean) output.get(0) && abscenceEmails.size() == 0 ) {
                dialogEndUpload.getUploadListner().onUploadSucces(activity,null);
            }

        } else {
            dialogEndUpload.getUploadListner().onUploadFailed();
        }
    }

    @Override
    protected void onCancelled() {
        dialogEndUpload.getUploadListner().onUploadFailed();
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