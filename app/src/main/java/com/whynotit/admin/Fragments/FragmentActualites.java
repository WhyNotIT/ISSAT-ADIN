package com.whynotit.admin.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.whynotit.admin.Activity.EspaceAdminActivity;
import com.whynotit.admin.Adapters.NewsAdapter;
import com.whynotit.admin.Dialog.DialogAjouterActualite;
import com.whynotit.admin.Dialog.DialogLoadingNews;
import com.whynotit.admin.Listners.LoadNewsListner;
import com.whynotit.admin.Managers.FireBaseManager;
import com.whynotit.admin.R;
import com.whynotit.admin.Utils;

/**
 * Created by Harzallah on 19/09/2016.
 */
public class FragmentActualites extends Fragment {

    private EspaceAdminActivity activity;
    private CoordinatorLayout rootLayout;
    private RecyclerView mNewsRecyclerView;
    private NewsAdapter mNewsAdapter;
    private FloatingActionButton ajouterActualite;
    private FrameLayout pasDeNews;
    private DialogLoadingNews dialogLoadingNews;
    private FragmentActualites fragment;


    private LoadNewsListner loadNewsListner = new LoadNewsListner() {

        @Override
        public void onNewsAlreadyLoaded() {
            if (dialogLoadingNews.isShowing()) {
                hideDialogLoadingNews();
                pasDeNews.setVisibility(View.GONE);
                initRecyclerViewNews();
            }
        }

        @Override
        public void onLoadingNews() {

        }

        @Override
        public void onLoadNewsDone() {
            if (dialogLoadingNews.isShowing()) {
                hideDialogLoadingNews();
                pasDeNews.setVisibility(View.GONE);
                initRecyclerViewNews();

            }
        }

        @Override
        public void onLoadNewsErreur() {
            if (dialogLoadingNews.isShowing())
                hideDialogLoadingNews();
            showSnackBar("Erreur lors du cahrgement des actualités!");
            activity.getSupportFragmentManager().popBackStack();
        }

        @Override
        public void onNewsUpdated(int position) {
            if (fragment.isVisible()) {
                mNewsAdapter.notifyItemChanged(position);
                Log.e("CHANGER", position + "****");
            }


        }

    };

    private void initRecyclerViewNews() {


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mNewsRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mNewsLayoutManager = new LinearLayoutManager(activity);
        mNewsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mNewsRecyclerView.setLayoutManager(mNewsLayoutManager);

        mNewsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mNewsAdapter = new NewsAdapter(activity, rootLayout);
        mNewsRecyclerView.setAdapter(mNewsAdapter);


    }


    public interface NewsListner {
        void onNewNewsAdded();
    }

    NewsListner newsListner = new NewsListner() {
        @Override
        public void onNewNewsAdded() {
            if (mNewsAdapter == null) {
                initRecyclerViewNews();
            }

            mNewsAdapter.notifyItemInserted(0);
            mNewsRecyclerView.scrollToPosition(0);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (EspaceAdminActivity) getActivity();
        activity.getToolbar().setTitle("Actualités");
        fragment = this;

        rootLayout = (CoordinatorLayout) view.findViewById(R.id.root_layout);

        pasDeNews = (FrameLayout) view.findViewById(R.id.pas_de_news);
        //News RecyclerView
        mNewsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_news);



        showDialogLoadingNews();
        FireBaseManager.getInstance().fireBaseLoadNews(activity, loadNewsListner);

        ajouterActualite = (FloatingActionButton) view.findViewById(R.id.fab_ajouter_actualite);
        ajouterActualite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(activity)) {
                    showFragmentAjouterActualite();
                } else {
                    showSnackBar("Acune connexion disponible!");
                }
            }
        });



        mNewsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 10)
                    ajouterActualite.hide();
                else if (dy < 10)
                    ajouterActualite.show();
            }
        });
    }


    private void showSnackBar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FireBaseManager.getInstance().getNewsList().size() == 0) {
            pasDeNews.setVisibility(View.VISIBLE);
        } else {
            pasDeNews.setVisibility(View.GONE);
        }

    }


    public void showFragmentAjouterActualite() {
        DialogAjouterActualite dialogAjouterActualite = new DialogAjouterActualite(activity, newsListner);
        dialogAjouterActualite.show();
    }

    private void hideDialogLoadingNews() {
        if (dialogLoadingNews != null) {
            dialogLoadingNews.dismiss();
        }
    }

    private void showDialogLoadingNews() {
        dialogLoadingNews = new DialogLoadingNews(activity, loadNewsListner);
        dialogLoadingNews.show();

    }


}
