package com.whynotit.admin.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.whynotit.admin.Activity.EspaceAdminActivity;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.Managers.UsersManager;
import com.whynotit.admin.R;
import com.whynotit.admin.Models.User;

/**
 * Created by Harzallah on 16/02/2016.
 */
public class DialogAjouterUtilisateur extends Dialog {


    private Activity activity;
    private EditText nom, pass;
    private Button ajouter, annuler;
    private CoordinatorLayout rootLayout,fragmentRootLayout;


    public DialogAjouterUtilisateur(EspaceAdminActivity activity, CoordinatorLayout fragmentRootLayout) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.fragmentRootLayout = fragmentRootLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ajouter_utilisateur);

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);

        nom = (EditText) findViewById(R.id.utilisateur);
        pass = (EditText) findViewById(R.id.password);

        ajouter = (Button) findViewById(R.id.ajouter);
        annuler = (Button) findViewById(R.id.annuler);

        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nom.getText().toString().length() < 2) {
                    Snackbar.make(rootLayout,"Nom tros court!",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (pass.getText().toString().length() < 2) {
                    Snackbar.make(rootLayout,"Mot de passe tros court!",Snackbar.LENGTH_LONG).show();
                    return;
                }

                UsersManager.getInstance().getUserList().add(new User(nom.getText().toString(),pass.getText().toString()));
                SharedPerefManager.getInstance(activity).saveUsers();
                Snackbar.make(fragmentRootLayout,"Utilisateur ajouté.",Snackbar.LENGTH_LONG).show();

                dismiss();
            }
        });

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
