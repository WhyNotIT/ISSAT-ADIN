package com.whynotit.admin.Listners;

/**
 * Created by Harzallah on 19/09/2016.
 */
public interface LoadNewsListner {
    void onNewsAlreadyLoaded ();
    void onLoadingNews ();
    void onLoadNewsDone ();
    void onLoadNewsErreur();

    void onNewsUpdated(int position);
}
