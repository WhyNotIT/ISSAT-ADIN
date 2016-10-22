package com.whynotit.admin.Listners;

import com.whynotit.admin.Models.News;

/**
 * Created by Harzallah on 20/09/2016.
 */
public interface LoadCommentsListner {
    void onLoadingNewsErreur ();
    void onNewCommentAdded(News news);
    void onLoadingCommentsDone(News news);

    void initCommentRecyclerView(News news);
}
