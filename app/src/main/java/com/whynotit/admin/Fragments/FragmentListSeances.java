package com.whynotit.admin.Fragments;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.whynotit.admin.Activity.MainActivity;
import com.whynotit.admin.Adapters.ListeViewSeancesAdapter;
import com.whynotit.admin.Database.DatabaseDAO;
import com.whynotit.admin.Dialog.DialogAuthentification;
import com.whynotit.admin.MaDate;
import com.whynotit.admin.Managers.NetworkManager;
import com.whynotit.admin.Managers.SeancesToUploadManager;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.Models.Emplois;
import com.whynotit.admin.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentListSeances extends Fragment {

    //private RecyclerView mRecyclerView;
    //private ListeSeancesAdapter mAdapter;
    private MainActivity activity;
    private List<Emplois> listCurrentsSeances = new ArrayList<>();
    private String heureDebutSeances;
    private boolean firstStart = true;
    private ListView listview;
    private LinearLayout footerView;
    private ListeViewSeancesAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        heureDebutSeances = getArguments().getString("heureDebutSeances");
        return inflater.inflate(R.layout.fragment_list_seances, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();

        listCurrentsSeances = activity.loadJourneeFromDBWithDayAndSeance(heureDebutSeances);
        Log.e("SEANCE FOUND", listCurrentsSeances.size() + "");
        //Always Add one Empty record for the footer
        //listCurrentsSeances.add(new Emplois());

        /*
        mRecyclerView = (RecyclerView) view.findViewById(R.id.listViewSeance);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);



        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(activity));


        mAdapter = new ListeSeancesAdapter(activity, listCurrentsSeances);
        mRecyclerView.setAdapter(mAdapter);
        */

        listview = (ListView) view.findViewById(R.id.listViewSeance);
        listview.setHeaderDividersEnabled(true);

        footerView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_button_exporter, null);
        listview.addFooterView(footerView);

        Button exporterSeances = (Button) footerView.findViewById(R.id.exporterSeances);

        exporterSeances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listCurrentsSeances.size() == 0) {
                    Snackbar.make(activity.getViewPager(), "La liste des seances est vide!", Snackbar.LENGTH_LONG).show();
                } else {
                    if (!NetworkManager.getInstance().isNetworkAvailable(activity)) {
                        Snackbar.make(activity.getViewPager(), "Aucune connexion disponible!", Snackbar.LENGTH_LONG).show();
                        SharedPerefManager.getInstance(activity).saveAbscenceIsSetForThisList(listCurrentsSeances.get(0).getDebut());
                    } else if (SharedPerefManager.getInstance(activity).IsSeanceUploaded(listCurrentsSeances.get(0).getDebut())) {
                        Snackbar.make(activity.getViewPager(), "L'exportation est permise une seule fois par seance!", Snackbar.LENGTH_LONG).show();
                    } else {
                        SeancesToUploadManager.getInstance().setListSeancesToUpload(listCurrentsSeances);
                        showUserAuthentification();
                    }


                }
            }
        });

        listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Emplois emplois = listCurrentsSeances.get(position);
                if (!SharedPerefManager.getInstance(activity).IsAbscenceIsSetForThisList(emplois.getDebut())) {

                    if (emplois.getPresence() == 1) {

                        view.setBackgroundColor(activity.getResources().getColor(R.color.colorAbscent));

                        updatePresenceEmplois(emplois.getId(), 0);
                        emplois.setPresence(0);

                    } else if (emplois.getPresence() == 0) {

                        view.setBackgroundColor(activity.getResources().getColor(R.color.colorAbscenceGroupe));

                        updatePresenceEmplois(emplois.getId(), 2);
                        emplois.setPresence(2);
                    } else {

                        view.setBackgroundColor(activity.getResources().getColor(R.color.colorListSeancesBg));

                        updatePresenceEmplois(emplois.getId(), 1);
                        emplois.setPresence(1);
                        //holder.itemSeanceLayout.setBackgroundResource(R.color.colorListSeancesBg);
                    }

                } else {
                    Toast.makeText(activity,"Modification non permise!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    private void updatePresenceEmplois(int idSeance, int presence) {
        DatabaseDAO databaseDAO = new DatabaseDAO(activity);
        databaseDAO.open();
        databaseDAO.updateSeanceAbscence(idSeance,presence);
        databaseDAO.close();
    }

    private void animateView(final View view, final int oldColor, final int newColor) {

        int colorFrom = activity.getResources().getColor(oldColor);
        int colorTo = activity.getResources().getColor(newColor);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
        //view.setBackgroundResource(color);
        //rootLayout.setBackgroundResource(color);

    }

    private void showUserAuthentification() {

        DialogAuthentification dialogAuthentification = new DialogAuthentification(activity);
        dialogAuthentification.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        //if (!firstStart) {
            Log.e("XXXXXx", "RELOAD");
            listCurrentsSeances.clear();
            listCurrentsSeances.addAll(activity.loadJourneeFromDBWithDayAndSeance(heureDebutSeances));
            mAdapter = new ListeViewSeancesAdapter(activity,listCurrentsSeances);
            listview.setAdapter(mAdapter);
            //Always Add one Empty record for the footer

        //} else {
          //  firstStart = false;
        //}

        MaDate date = new MaDate();
        activity.getToolbar().setSubtitle(date.toString());

    }

    @Override
    public void onStart() {
        super.onStart();

    }

}