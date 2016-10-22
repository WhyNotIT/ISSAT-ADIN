package com.whynotit.admin.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.whynotit.admin.Adapters.ViewPagerAdapter;
import com.whynotit.admin.Database.DatabaseDAO;
import com.whynotit.admin.Dialog.DialogAdminLogin;
import com.whynotit.admin.Fragments.FragmentListSeances;
import com.whynotit.admin.MaDate;
import com.whynotit.admin.Managers.SeancesHorairesManager;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.Models.Emplois;
import com.whynotit.admin.Models.HoraireSeance;
import com.whynotit.admin.R;

import java.util.List;


public class MainActivity extends AppCompatActivity {


    //NEW APP
    private Toolbar toolbar;
    private ViewPager viewPager;

    //DATE
    MaDate maDate;

    private static String USER = "";
    private MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        /** Loading list Horaires Seances     **/
        SeancesHorairesManager.getInstance().setListSeances(SharedPerefManager.getInstance(activity).loadHorairesSeances());


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        //Selection des seances actuelles
        maDate = new MaDate();

        tabLayout.setupWithViewPager(viewPager);


    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public List<Emplois> loadJourneeFromDBWithDayAndSeance(String heureDebutSeance) {
        DatabaseDAO databaseDAO = new DatabaseDAO(activity);

        databaseDAO.open();
        if (!databaseDAO.isJourneeInitialized()) {
            Log.e("XXXXX","INIT");
            databaseDAO.initilizeJournee();
        }

        List<Emplois> l = databaseDAO.getCurrentListSeances(heureDebutSeance);
        databaseDAO.close();
        return l;


    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (HoraireSeance horaireSeance : SeancesHorairesManager.getInstance().getListSeances()) {

            Bundle bundle = new Bundle();
            bundle.putString("heureDebutSeances", horaireSeance.getHeureDebut());
            // set Fragmentclass Arguments
            FragmentListSeances fragmentListSeances = new FragmentListSeances();
            fragmentListSeances.setArguments(bundle);

            adapter.addFragment(fragmentListSeances, horaireSeance.getHeureDebut());
        }

        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.espace_admin: {
                adminLogin();
                break;
            }
            case R.id.database_info: {
                Toast.makeText(activity,"Date de la base des données :\n" +
                        SharedPerefManager.getInstance(activity).loadDataBaseDate(),Toast.LENGTH_LONG).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void adminLogin() {
        DialogAdminLogin dialogAdminLogin = new DialogAdminLogin(activity);
        dialogAdminLogin.show();
    }

    public static String getUser() {
        return USER;
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
