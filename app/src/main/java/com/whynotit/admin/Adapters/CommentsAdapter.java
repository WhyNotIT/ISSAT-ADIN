package com.whynotit.admin.Adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.whynotit.admin.Activity.EspaceAdminActivity;
import com.whynotit.admin.Managers.FireBaseManager;
import com.whynotit.admin.Models.Comment;
import com.whynotit.admin.Models.News;
import com.whynotit.admin.R;
import com.whynotit.admin.Utils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Harzallah on 20/09/2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private final EspaceAdminActivity activity;
    private final News news;
    private final View rootLayout;
    private NewsAdapter.NewsListner newsListner;

    public CommentsAdapter(EspaceAdminActivity activity, News news, View rootLayout, NewsAdapter.NewsListner newsListner) {
        this.activity = activity;
        this.news = news;
        this.rootLayout = rootLayout;
        this.newsListner = newsListner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commenter, parent, false));
        else
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final List<Comment> newsComments = news.getNewsComments();

        if (position == newsComments.size()-1) {
            holder.buttonSendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Utils.isNetworkAvailable(activity)) {
                        showSnackBar("Aucune connexion disponible!");
                    } else {
                        if (holder.editTextComment.getText().toString().length() < 2 ) {
                            showSnackBar("Votre commentaire est tros court!");
                        } else {
                            Comment comment = new Comment();
                            comment.setIdNews(news.getKey());
                            comment.setComment(holder.editTextComment.getText().toString());
                            comment.setIdWriter("Administration");
                            comment.setTimestamp(Calendar.getInstance().getTimeInMillis());

                            holder.editTextComment.setText("");
                            newsComments.add(newsComments.size()-1,comment);
                            news.setNbComment(news.getNbComment()+1);

                            FireBaseManager.getInstance().fireBasePushComment(news,comment);

                            notifyDataSetChanged();
                            newsListner.onDataChanged();

                        }
                    }
                }
            });
        } else {

            final Comment comment = newsComments.get(position);

            if (comment.getIdWriter().compareTo("Administration") == 0) {
                holder.imageProfil.setImageResource(R.drawable.ic_admin);
            } else {
                holder.imageProfil.setImageResource(R.drawable.ic_student);
            }

            holder.nomWriter.setText(comment.getIdWriter());
            holder.comment.setText(comment.getComment());
            holder.date.setText(comment.getDate());

        }
    }

    @Override
    public int getItemCount() {
        return news.getNewsComments().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == news.getNewsComments().size()-1) return 0;
        else return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageProfil;
        private TextView nomWriter;
        private TextView comment;
        private TextView date;
        private EditText editTextComment;
        private ImageView buttonSendComment;

        public ViewHolder(View itemView) {
            super(itemView);

            imageProfil = (ImageView) itemView.findViewById(R.id.image_profil);
            nomWriter = (TextView) itemView.findViewById(R.id.nom_writer);
            comment = (TextView) itemView.findViewById(R.id.comment);
            date = (TextView) itemView.findViewById(R.id.date);
            editTextComment = (EditText) itemView.findViewById(R.id.edittext_comment);
            buttonSendComment = (ImageView) itemView.findViewById(R.id.button_send_comment);
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
