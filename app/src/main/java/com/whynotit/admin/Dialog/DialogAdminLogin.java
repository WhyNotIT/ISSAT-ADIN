package com.whynotit.admin.Dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.whynotit.admin.Activity.EspaceAdminActivity;
import com.whynotit.admin.Activity.MainActivity;
import com.whynotit.admin.R;


/**
 * Created by Harzallah on 13/07/2016.
 */
public class DialogAdminLogin extends Dialog {


    MainActivity activity;
    private EditText login, password;
    private Button connexion,annuler;


    public DialogAdminLogin(MainActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_admin_login);
        setCancelable(true);

        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);

        connexion = (Button) findViewById(R.id.connexion);
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login.getText().toString().toLowerCase().equals("admin") && password.getText().toString().toLowerCase().equals("issatad")) {
                    dismiss();
                    showAdminFragment();
                } else {
                    Snackbar.make(findViewById(R.id.root_layout), "Mot de passe incorrect!", Snackbar.LENGTH_SHORT).show();
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


    }

    private void showAdminFragment() {
        Intent espaceAdmin = new Intent(activity,EspaceAdminActivity.class);
        activity.startActivity(espaceAdmin);

    }
}
