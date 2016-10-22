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

import com.wang.avi.AVLoadingIndicatorView;
import com.whynotit.admin.R;

/**
 * Created by Harzallah on 01/10/2016.
 */
public class DialogNetworkOperation extends Dialog {

    public interface NetworkOperationListner {
        void onNetworkOperationSucces();
        void onNetworkOperationFailed();
    }

    private NetworkOperationListner networkOperationListner = new NetworkOperationListner() {
        @Override
        public void onNetworkOperationSucces() {
            dialogLoadingAnimation.setVisibility(View.GONE);
            dialogMessage.setText(messages[2]);
            dialogConfirmer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            dialogConfirmer.setText("OK");
            dialogConfirmer.setVisibility(View.VISIBLE);
            dialogLayoutBottons.setVisibility(View.VISIBLE);

        }

        @Override
        public void onNetworkOperationFailed() {
            dialogLoadingAnimation.setVisibility(View.GONE);
            dialogMessage.setText(messages[1]);
            dialogConfirmer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            dialogConfirmer.setText("OK");
            dialogConfirmer.setVisibility(View.VISIBLE);
            dialogLayoutBottons.setVisibility(View.VISIBLE);
        }
    };

    private final int icone;
    private final String[] messages;
    private Activity activity;
    private TextView dialogMessage;
    private LinearLayout dialogLayoutBottons;
    private Button dialogConfirmer,dialogAnnuler;
    private AVLoadingIndicatorView dialogLoadingAnimation;

    public DialogNetworkOperation(Activity activity,int dialogIcone, String[] messages) {
        super(activity);
        this.activity = activity;
        this.icone = dialogIcone;
        this.messages = messages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.dialog_network_operations);

        ImageView dialogIcone = (ImageView) findViewById(R.id.dialog_icone);
        dialogIcone.setImageResource(icone);

        dialogMessage = (TextView) findViewById(R.id.dialog_message);
        dialogMessage.setText(messages[0]);

        dialogLoadingAnimation = (AVLoadingIndicatorView) findViewById(R.id.dialog_loading_animation);
        dialogLoadingAnimation.setVisibility(View.VISIBLE);

        dialogLayoutBottons = (LinearLayout) findViewById(R.id.layout_dialog_buttons);
        dialogLayoutBottons.setVisibility(View.GONE);

        dialogConfirmer = (Button) findViewById(R.id.dialog_confirmer);
        dialogAnnuler = (Button) findViewById(R.id.dialog_annuler);

    }

    public NetworkOperationListner getNetworkOperationListner() {
        return networkOperationListner;
    }
}
