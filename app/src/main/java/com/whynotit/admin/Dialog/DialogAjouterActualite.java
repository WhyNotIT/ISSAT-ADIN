package com.whynotit.admin.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.whynotit.admin.Activity.EspaceAdminActivity;
import com.whynotit.admin.Fragments.FragmentActualites;
import com.whynotit.admin.MaDate;
import com.whynotit.admin.Managers.FireBaseManager;
import com.whynotit.admin.Models.Comment;
import com.whynotit.admin.Models.News;
import com.whynotit.admin.R;
import com.whynotit.admin.Utils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Harzallah on 19/09/2016.
 */
public class DialogAjouterActualite extends Dialog {

    private EspaceAdminActivity activity;
    private CoordinatorLayout rootLayout;
    private FragmentActualites.NewsListner newsListner;

    public DialogAjouterActualite(EspaceAdminActivity activity, FragmentActualites.NewsListner newsListner) {
        super(activity);
        this.activity = activity;
        this.newsListner = newsListner;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ajouter_actualite);
        setCancelable(false);

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);

        final EditText titre = (EditText) findViewById(R.id.news_titre);
        final EditText description = (EditText) findViewById(R.id.news_description);
        final EditText lien = (EditText) findViewById(R.id.news_lien);

        Button annuler = (Button) findViewById(R.id.news_button_annuler);
        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button ajouter = (Button) findViewById(R.id.news_button_ajouter);
        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Utils.isNetworkAvailable(activity)) {
                    showSnackBar("Acune connexion disponible!");
                } else {

                    if (titre.getText().toString().length() < 3) {
                        showSnackBar("Titre tros court!");
                        return;
                    }

                    if (description.getText().toString().length() < 3) {
                        showSnackBar("Description tros courte!");
                        return;
                    }

                    News news = new News(titre.getText().toString()
                            , description.getText().toString()
                            , lien.getText().toString().length() == 0 ? null : lien.getText().toString());

                    news.setTimestamp(-Calendar.getInstance().getTimeInMillis());
                    MaDate date = new MaDate();
                    news.setDate(date.getDateInDateFormat());
                    news.setNbComment(0);
                    news.setNbLike(0);
                    news.setNewsComments(new ArrayList<Comment>());
                    FireBaseManager.getInstance().getNewsList().add(0, news);
                    newsListner.onNewNewsAdded();
                    FireBaseManager.getInstance().fireBasePushNews(activity,news);
                    dismiss();
                }
            }
        });

    }

    private void showSnackBar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }


}
