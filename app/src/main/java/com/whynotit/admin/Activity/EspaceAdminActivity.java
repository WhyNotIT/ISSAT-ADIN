package com.whynotit.admin.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.whynotit.admin.Fragments.FragmentEspaceAdmin;
import com.whynotit.admin.Managers.FireBaseManager;
import com.whynotit.admin.R;

/**
 * Created by Harzallah on 26/07/2016.
 */
public class EspaceAdminActivity extends AppCompatActivity {

    private FrameLayout rootLayout;
    private EspaceAdminActivity activity;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espace_admin);
        activity = this;

        //FIRE BASE INIT
        FireBaseManager.getInstance().fireBaseLoginInit();
        FireBaseManager.getInstance().firebaseDatabaseInit();
        FireBaseManager.getInstance().fireBaseSignIn(activity);

        rootLayout = (FrameLayout) findViewById(R.id.fragment_container);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentEspaceAdmin.class.getName());

        if (fragment == null)
            ft.replace(R.id.fragment_container, new FragmentEspaceAdmin(), FragmentEspaceAdmin.class.getName());
        else
            ft.show(fragment);

        // Append this transaction to the backstack
        //ft.addToBackStack(FragmentEspaceAdmin.class.getName());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public View getRootLayout() {
        return rootLayout;
    }
}
