package com.whynotit.admin.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whynotit.admin.Managers.SeancesToUploadManager;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.R;

import java.util.List;

/**
 * Created by Harzallah on 11/09/2016.
 */
public class DialogEndUpload extends Dialog {

    public interface UploadListner {
        void onUploadSucces(Activity activity,List<Object> output);

        void onUploadFailed();
    }


    private UploadListner uploadListner = new UploadListner() {
        @Override
        public void onUploadSucces(Activity activity,List<Object> output) {

            SharedPerefManager.getInstance(activity).saveSuccesUpload(SeancesToUploadManager.getInstance().getListSeancesToUpload().get(0).getDebut());

            imageStatus.setImageResource(R.drawable.ic_exporter_done);
            String msgResult = "L' exportation est terminée avec succés.\n";
            if (output == null) {
                msgResult += "Aucun enseignant absent.";
            } else if (output.size() == 0) {
                msgResult += "Emails envoyés avec succés.";
            } else {
                msgResult += "Nom des enseignants non notifiés:\n";
                for (Object nomEnseignant : output) {
                    msgResult += nomEnseignant + "\n";
                }
            }

            message.setText(msgResult);

            startUpload.setVisibility(View.GONE);
            endUpload.setVisibility(View.VISIBLE);
        }

        @Override
        public void onUploadFailed() {
            imageStatus.setImageResource(R.drawable.ic_error);
            message.setText("Une ERREUR est survenu dans l' exportation des données! Réessayer.");
            startUpload.setVisibility(View.GONE);
            endUpload.setVisibility(View.VISIBLE);
        }
    };

    private LinearLayout startUpload, endUpload;
    private Button ok;
    private TextView message;
    private ImageView imageStatus;

    public DialogEndUpload(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_end_upload);
        setCancelable(false);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        startUpload = (LinearLayout) findViewById(R.id.start_upload);
        endUpload = (LinearLayout) findViewById(R.id.end_upload);
        imageStatus = (ImageView) findViewById(R.id.image_status);
        message = (TextView) findViewById(R.id.message);
    }

    public UploadListner getUploadListner() {
        return uploadListner;
    }
}

