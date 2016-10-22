package com.whynotit.admin;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.whynotit.admin.Dialog.DialaogDownloadDataBase;
import com.whynotit.admin.GoogleSpreadsheetOperations.SpreadsheetParams;
import com.whynotit.admin.Listners.InstallDataBase;
import com.whynotit.admin.Managers.SharedPerefManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Harzallah on 17/09/2016.
 */
public class DownloadEmplois extends AsyncTask<Void, Void, Void> {

    private final boolean showDialog;
    private final boolean isForced;
    private Activity activity;
    private InstallDataBase installDataBaseListner;
    private DialaogDownloadDataBase dialogDownloadDataBase;

    public DownloadEmplois(Activity activity, InstallDataBase installDataBaseListner, boolean showDialog, boolean isForced) {
        this.activity = activity;
        this.installDataBaseListner = installDataBaseListner;
        this.showDialog = showDialog;
        this.isForced = isForced;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showDialog) {
            dialogDownloadDataBase = new DialaogDownloadDataBase(activity);
            dialogDownloadDataBase.show();
        }

    }

    @Override
    protected Void doInBackground(Void... voids) {


        String FL_PATH = activity.getFilesDir() + "/tmp.csv";
        String dataBaseDate = null;

        ByteArrayInputStream inputStream = null;
        String response = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(SpreadsheetParams.URL_EMPLOIS);
            //httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            if (!isForced) {

            Header[] headers = httpResponse.getAllHeaders();
                for (Header header : headers) {
                    if (header.getName().compareTo("Last-Modified") == 0) {
                        dataBaseDate = header.getValue();

                        if (dialogDownloadDataBase != null && SharedPerefManager.getInstance(activity).loadDataBaseDate() != null && dataBaseDate.compareTo(SharedPerefManager.getInstance(activity).loadDataBaseDate()) == 0) {
                            dialogDownloadDataBase.getDownloadListner().onDataBaseIsUpToDate();
                            return null;
                        }
                    }
                }
        }

        response = EntityUtils.toString(httpEntity, "ISO-8859-1");
        //Log.e("TRRRRRRRRRRR",response.length()+"");

        // Read content & Log

        inputStream = new ByteArrayInputStream(response.getBytes(HTTP.UTF_8));
    }

    catch(
    UnsupportedEncodingException e1
    )

    {
        Log.e("UnsupportedEncoding", e1.toString());
        e1.printStackTrace();
    }

    catch(
    ClientProtocolException e2
    )

    {
        Log.e("ClientProtocolException", e2.toString());
        e2.printStackTrace();
    }

    catch(
    IllegalStateException e3
    )

    {
        Log.e("IllegalStateException", e3.toString());
        e3.printStackTrace();
    }

    catch(
    IOException e4
    )

    {
        Log.e("IOException", e4.toString());
        e4.printStackTrace();
    }


    try

    {

        //Log.e("TRRRRRRRRRRR",inputStream.toString().length()+"");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, HTTP.UTF_8), 8);
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FL_PATH), "UTF-8"));

        br.write(response);
        br.close();

            /*
            String line;
            while ((line = bReader.readLine()) != null) {
                br.write(line + "\n");
            }*/

        installDataBaseListner.onDownloadFinish(activity, dialogDownloadDataBase, dataBaseDate);

    }

    catch(
    Exception e
    )

    {
        if (showDialog)
            dialogDownloadDataBase.getDownloadListner().onInstallationFinish(false);
        else {
            installDataBaseListner.onDownloadFailed();
        }
        e.printStackTrace();
        Log.e("ERROR", "Error converting" + e.toString());
    }

    return null;
}

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
