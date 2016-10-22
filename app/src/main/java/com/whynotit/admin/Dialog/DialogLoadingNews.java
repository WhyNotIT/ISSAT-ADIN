package com.whynotit.admin.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.whynotit.admin.Listners.LoadNewsListner;
import com.whynotit.admin.R;

/**
 * Created by Harzallah on 17/09/2016.
 */
public class DialogLoadingNews extends Dialog {

    private Activity activity;
    private TextView messageProgress;
    private LoadNewsListner loadNewsListner;

    public DialogLoadingNews(Activity activity, LoadNewsListner loadNewsListner) {
        super(activity);
        this.activity = activity;
        this.loadNewsListner = loadNewsListner;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading_news);

        messageProgress = (TextView) findViewById(R.id.message_progrees);
        messageProgress.setText("Chargement des actualités en cours...");
    }

}
