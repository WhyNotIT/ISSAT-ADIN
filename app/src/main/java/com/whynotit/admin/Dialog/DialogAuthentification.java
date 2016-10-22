package com.whynotit.admin.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.whynotit.admin.Managers.UsersManager;
import com.whynotit.admin.R;
import com.whynotit.admin.Adapters.UsersAdapter;

/**
 * Created by Harzallah on 22/10/2015.
 */
public class DialogAuthentification extends Dialog {

    public Activity activity;


    public DialogAuthentification(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_user);

        ListView listUsers = (ListView) findViewById(R.id.list_users);
        UsersAdapter usersAdapter = new UsersAdapter(this, activity, UsersManager.getInstance().getUserList());
        listUsers.setAdapter(usersAdapter);

        if (UsersManager.getInstance().getUserList().size() == 0) {
            findViewById(R.id.no_users).setVisibility(View.VISIBLE);
        }

    }

}