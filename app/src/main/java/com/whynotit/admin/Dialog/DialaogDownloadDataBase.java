package com.whynotit.admin.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whynotit.admin.Models.DownloadListner;
import com.whynotit.admin.R;

/**
 * Created by Harzallah on 17/09/2016.
 */
public class DialaogDownloadDataBase extends Dialog {

    private Activity activity;
    private LinearLayout startDownload, endDownload;
    private TextView messageProgress, messageFinOperation;
    private Button ok;
    private ImageView imageStatus;
    private DownloadListner downloadListner = new DownloadListner() {
        @Override
        public void onDataBaseIsUpToDate() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startDownload.setVisibility(View.GONE);
                    endDownload.setVisibility(View.VISIBLE);
                    messageFinOperation.setText("La base des données est déja à jour.");
                    imageStatus.setImageResource(R.drawable.ic_exporter_done);
                }
            });
        }

        @Override
        public void onDownloadFinish() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageProgress.setText("Installation de la nouvelle base des données.");
                }
            });
        }

        @Override
        public void onInstallationFinish(final boolean succes) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startDownload.setVisibility(View.GONE);
                    endDownload.setVisibility(View.VISIBLE);

                    if (succes) {
                        messageFinOperation.setText("Téléchargement et installation de la base des données réussi.");
                        imageStatus.setImageResource(R.drawable.ic_exporter_done);
                    } else {
                        messageFinOperation.setText("Téléchargement et installation de la base des données echoué.");
                        imageStatus.setImageResource(R.drawable.ic_error);
                    }
                }
            });

        }
    };

    public DialaogDownloadDataBase(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.dialog_download_database);

        startDownload = (LinearLayout) findViewById(R.id.start_download);
        endDownload = (LinearLayout) findViewById(R.id.end_download);
        messageProgress = (TextView) findViewById(R.id.message_progrees);
        messageFinOperation = (TextView) findViewById(R.id.message);
        imageStatus = (ImageView) findViewById(R.id.image_status);
        ok = (Button) findViewById(R.id.ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        messageProgress.setText("Téléchargement en cours...");
    }

    public DownloadListner getDownloadListner() {
        return downloadListner;
    }
}
