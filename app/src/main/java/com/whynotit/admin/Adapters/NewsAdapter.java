package com.whynotit.admin.Adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.whynotit.admin.Activity.EspaceAdminActivity;
import com.whynotit.admin.Listners.LoadCommentsListner;
import com.whynotit.admin.Managers.FireBaseManager;
import com.whynotit.admin.Models.News;
import com.whynotit.admin.R;
import com.whynotit.admin.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harzallah on 12/07/2016.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    public interface NewsListner {
        void onDataChanged();
    }

    private final View rootLayout;
    private List<News> newsList = new ArrayList<>();
    private EspaceAdminActivity activity;
    private NewsAdapter.NewsListner newsListner = new NewsAdapter.NewsListner() {
        @Override
        public void onDataChanged() {

            notifyDataSetChanged();
        }
    };


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titre, contenu;
        private final FrameLayout conteneur;
        private final TextView likes,dislike,comments;
        private final RecyclerView recyclerViewComments;
        private final AVLoadingIndicatorView loadingCommentsAnimation;


        // each data item is just a string in this case
        public TextView mNomClub;

        public ViewHolder(View v) {
            super(v);
            titre = (TextView) v.findViewById(R.id.news_titre);
            contenu = (TextView) v.findViewById(R.id.news_contenu);
            //consulter = (Button) v.findViewById(R.id.consulter_lien);
            conteneur = (FrameLayout) v.findViewById(R.id.contenu_news);
            likes = (TextView) v.findViewById(R.id.news_likes_nb);
            dislike = (TextView) v.findViewById(R.id.news_dislikes_nb);
            comments = (TextView) v.findViewById(R.id.news_comments_nb);
            recyclerViewComments = (RecyclerView) v.findViewById(R.id.recycler_view_comments);
            loadingCommentsAnimation = (AVLoadingIndicatorView) v.findViewById(R.id.loading_comments);
        }
    }


    public NewsAdapter(EspaceAdminActivity activity,View rootLayout) {

        this.activity = activity;
        this.rootLayout = rootLayout;
        this.newsList = FireBaseManager.getInstance().getNewsList();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final News news = newsList.get(position);


        holder.titre.setText(Utils.rectifierPolice(news.getTitre()));
        holder.contenu.setText(Utils.rectifierPolice(news.getContenu()));
        holder.likes.setText(news.getNbLike()+"");
        holder.dislike.setText(news.getNbDislike()+"");
        holder.comments.setText(news.getNbComment()+"");

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.loadingCommentsAnimation.setVisibility(View.VISIBLE);
                LoadCommentsListner loadCommentsListner = new LoadCommentsListner() {
                    CommentsAdapter mCommentsAdapter;
                    boolean isRecyclerViewInitialized = false;

                    private void initRecyclerView (News news) {
                        // use this setting to improve performance if you know that changes
                        // in content do not change the layout size of the RecyclerView
                        holder.recyclerViewComments.setHasFixedSize(true);
                        // use a linear layout manager
                        LinearLayoutManager mNewsLayoutManager = new LinearLayoutManager(activity);
                        mNewsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                        holder.recyclerViewComments.setLayoutManager(mNewsLayoutManager);

                        holder.recyclerViewComments.setItemAnimator(new DefaultItemAnimator());
                        holder.recyclerViewComments.addItemDecoration(new SimpleDividerItemDecoration(activity));


                        mCommentsAdapter = new CommentsAdapter(activity,news, rootLayout,newsListner);
                        holder.recyclerViewComments.setAdapter(mCommentsAdapter);

                        holder.loadingCommentsAnimation.setVisibility(View.GONE);
                        holder.recyclerViewComments.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onLoadingNewsErreur() {
                        holder.loadingCommentsAnimation.setVisibility(View.GONE);
                        showSnackBar("Erreur lors du chargements des commentaires!");
                    }

                    @Override
                    public void onNewCommentAdded(News news) {
                        initCommentRecyclerView(news);
                        mCommentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onLoadingCommentsDone(News news) {
                        initCommentRecyclerView(news);
                    }

                    @Override
                    public void initCommentRecyclerView(News news) {
                        if (!isRecyclerViewInitialized) {
                            initRecyclerView(news);
                            isRecyclerViewInitialized = true;
                        }
                    }
                };
                FireBaseManager.getInstance().fireBaseLoadComments(news,loadCommentsListner);
            }
        });
            /*holder.consulter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFragmentActualite();
                    /*try {
                        String url = news.getUrl();

                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;

                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });*/



    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return newsList.size();
    }

    private void showSnackBar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }
}

