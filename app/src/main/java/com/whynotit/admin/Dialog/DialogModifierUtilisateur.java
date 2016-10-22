package com.whynotit.admin.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.whynotit.admin.Managers.UsersManager;
import com.whynotit.admin.R;
import com.whynotit.admin.Models.User;
import com.whynotit.admin.Adapters.UsersModifierAdapter;

import java.util.List;

/**
 * Created by Harzallah on 22/10/2015.
 */
public class DialogModifierUtilisateur extends Dialog {

    public static final String TAG = "CR";

    public Activity activity;
    private Button reserver;
    private DatePicker date;


    public DialogModifierUtilisateur(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_modifier_utilisateur);

        List<User> savedUsers = UsersManager.getInstance().getUserList();
        ListView listUsers = (ListView) findViewById(R.id.list_users);
        UsersModifierAdapter usersAdapter = new UsersModifierAdapter(this,activity,savedUsers);
        listUsers.setAdapter(usersAdapter);

        Button fermer = (Button) findViewById(R.id.confirmer);
        fermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if (savedUsers.size() == 0)
        {
            findViewById(R.id.no_users).setVisibility(View.VISIBLE);
        }


    }

}