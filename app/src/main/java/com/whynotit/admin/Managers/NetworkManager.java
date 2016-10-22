package com.whynotit.admin.Managers;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.whynotit.admin.Activity.SplashActivity;
import com.whynotit.admin.GoogleSpreadsheetOperations.LoadContactDataFromSpreadsheet;
import com.whynotit.admin.GoogleSpreadsheetOperations.LoadHorairesSeancesDataFromSpreadsheet;
import com.whynotit.admin.GoogleSpreadsheetOperations.LoadRepartitionDataFromSpreadsheet;
import com.whynotit.admin.GoogleSpreadsheetOperations.SpreadsheetParams;
import com.whynotit.admin.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Harzallah on 27/07/2016.
 */
public class NetworkManager implements EasyPermissions.PermissionCallbacks {
    public static NetworkManager instance;

    //Google Spreadsheet Api
    private GoogleAccountCredential credential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static NetworkManager getInstance () {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    private static OkHttpClient okHttpClient;
    private static final String HTTP_CACHE_DIR = "httpCache";
    private static final long HTTP_CACHE_SIZE = 1024 * 1024 * 10;
    private static final String TAG_SUCCESS = "success";

    public void initOkHttpClient(Activity activity) {
        okHttpClient = new OkHttpClient();
        //httpClient.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        //httpClient.setConnectionSpecs(Collections.singletonList(ConnectionSpec.MODERN_TLS));
        // TODO HTC 300 Crashed due to Network fail - possibly increase timeout rate to 7000 ms
        //httpClient.setConnectTimeout(HTTP_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        // Setup cache
        File cacheDir = new File(activity.getCacheDir(), HTTP_CACHE_DIR);
        Cache cache = new Cache(cacheDir, HTTP_CACHE_SIZE);
        okHttpClient.setCache(cache);

    }

    public void notifyStudentsAboutNewNews(final Activity activity, final String titre) {

        if (okHttpClient == null) {
            initOkHttpClient(activity);
        }

        final String URL_INSERT_TOKEN = activity.getString(R.string.host) + "/url.php";

        List<String> values = new ArrayList<>();
        values.add("titre");
        values.add(titre);

        Log.e("SENDING",URL_INSERT_TOKEN);

        post(URL_INSERT_TOKEN, getRequestBody(values), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //insertTokenToServer(activity,token);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                //Log.e("XXXXXXXXXXX", response.body().string());
                String result = response.body().string();
                Log.e("XXXXXXXXXXX", result.toString());

            }
        });
    }

    public void post(String url, RequestBody body, Callback callback) {

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public RequestBody getRequestBody(List<String> values) {

        MultipartBuilder requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("key", "value");

        for (int i = 0; i < values.size(); i += 2) {
            Log.e("POST",values.get(i) +" => " + values.get(i + 1));
            requestBody.addFormDataPart(values.get(i), values.get(i + 1));
        }

        return requestBody.build();
    }


    public GoogleAccountCredential getCredential(Activity activity) {
        if (credential == null) {
            /** Google SpreadSheet API **/
            // Initialize credentials and service object.
            credential = GoogleAccountCredential.usingOAuth2(
                    activity, Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            Log.e("NAME",SharedPerefManager.getInstance(activity).getAccountName()+"");
            credential.setSelectedAccountName(SharedPerefManager.getInstance(activity).getAccountName());
        }
        return credential;
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void initApplicationDataFromSpreadsheet(SplashActivity activity, SplashActivity.SpreadSheetLoadingListner spreadSheetLoadingListner) {

        if (!isGooglePlayServicesAvailable(activity)) {
            acquireGooglePlayServices(activity);
        } else if (credential.getSelectedAccountName() == null) {
            chooseAccount(activity,spreadSheetLoadingListner);
        } else if (!isDeviceOnline(activity)) {
            Toast.makeText(activity, "No network connection available.", Toast.LENGTH_LONG).show();
        } else {
            new LoadHorairesSeancesDataFromSpreadsheet(credential, activity,
                    SpreadsheetParams.SHEET_ID_HORAIRE_SEANCE,
                    SpreadsheetParams.SHEET_RANGE_HORAIRE_SEANCE,spreadSheetLoadingListner).execute();
            new LoadRepartitionDataFromSpreadsheet(credential,activity,
                    SpreadsheetParams.SHEET_ID_REPARTITION_SEANCE,
                    SpreadsheetParams.SHEET_RANGE_REPARTITION_SEANCE,spreadSheetLoadingListner).execute();
            new LoadContactDataFromSpreadsheet(credential,activity,
                    SpreadsheetParams.SHEET_ID_CONTACT_PROF,
                    SpreadsheetParams.SHEET_RANGE_CONTACT_PROF,spreadSheetLoadingListner).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount(final SplashActivity activity, SplashActivity.SpreadSheetLoadingListner spreadSheetLoadingListner) {
        if (EasyPermissions.hasPermissions(
                activity, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = activity.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                NetworkManager.getInstance().getCredential(activity).setSelectedAccountName(accountName);
                NetworkManager.getInstance().initApplicationDataFromSpreadsheet(activity, spreadSheetLoadingListner);
            } else {
                // Start a dialog from which the user can choose an account
                activity.startActivityForResult(
                        credential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog

                    EasyPermissions.requestPermissions(
                            activity,
                            "This app needs to access your Google account (via Contacts).",
                            REQUEST_PERMISSION_GET_ACCOUNTS,
                            Manifest.permission.GET_ACCOUNTS);

            }
    }



    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline(Activity activity) {
        ConnectivityManager connMgr =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(activity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(activity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity,connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    public void showGooglePlayServicesAvailabilityErrorDialog(Activity activity,
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
}
