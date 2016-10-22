package com.whynotit.admin.Managers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whynotit.admin.GoogleSpreadsheetOperations.SpreadsheetParams;
import com.whynotit.admin.Listners.LoadCommentsListner;
import com.whynotit.admin.Listners.LoadNewsListner;
import com.whynotit.admin.Models.Comment;
import com.whynotit.admin.Models.News;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Harzallah on 18/09/2016.
 */
public class FireBaseManager {
    private static final String TAG = "FIRE BASE";
    private static final String COMMENTS_ID = "comments";
    private static FireBaseManager instance;
    //FireBase STRING
    private static final String UNIVERSITY_NEWS_ID = "issatsousse";
    private static final String NEWS_ID = "news";

    //FireBase LOGIN
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;

    //FireBase DATABASE
    public FirebaseDatabase fireDatabase;
    private List<News> newsList;
    //private List<List<Object>> commentsList;


    public static FireBaseManager getInstance() {
        if (instance == null) {
            instance = new FireBaseManager();
        }
        return instance;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public void fireBaseSignIn(final Activity activity) {
        mAuth.signInWithEmailAndPassword(SpreadsheetParams.FIREBASE_LOGIN, SpreadsheetParams.FIREBASE_PASSWORD)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void firebaseDatabaseInit() {
        fireDatabase = FirebaseDatabase.getInstance();
    }

    public void fireBaseLoginInit() {

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }

    public void fireBasePushNews(Activity activity, News news) {
        DatabaseReference myNewsReferences = fireDatabase.getReference(UNIVERSITY_NEWS_ID).child(NEWS_ID);
        String result = myNewsReferences.push().getKey();
        Log.e("XXXXXXXXX", result);
        myNewsReferences.child(result).setValue(news);
        NetworkManager.getInstance().notifyStudentsAboutNewNews(activity, news.getTitre());
    }

    public void fireBasePushComment(News news, Comment comment) {
        DatabaseReference myNewsReferences = fireDatabase.getReference(UNIVERSITY_NEWS_ID).child(COMMENTS_ID)
                .child(news.getKey());
        String result = myNewsReferences.push().getKey();
        myNewsReferences.child(result).setValue(comment);
        incrementCommentsNumber(comment.getIdNews(), news.getNbComment());
    }

    private void incrementCommentsNumber(String idNews, int nbComment) {
        Log.e("IDNEWS", idNews + "/" + nbComment);
        DatabaseReference myNewsReferences = fireDatabase.getReference(UNIVERSITY_NEWS_ID).child(NEWS_ID)
                .child(idNews);
        Map<String, Object> values = new HashMap<>();
        values.put("nbComment", nbComment);
        myNewsReferences.updateChildren(values);
    }


    public void fireBaseLoadNews(final Activity activity, final LoadNewsListner loadNewsListner) {

        if (newsList != null) {
            loadNewsListner.onNewsAlreadyLoaded();
            return;
        } else {
            loadNewsListner.onLoadingNews();
            newsList = new ArrayList<>();
        }

        DatabaseReference myNewsReferences = fireDatabase.getReference(UNIVERSITY_NEWS_ID).child(NEWS_ID);

        myNewsReferences.orderByChild("timestamp").limitToFirst(10).addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        News news = dataSnapshot.getValue(News.class);
                        news.setKey(dataSnapshot.getKey());
                        if (!isNewsAdded(news)) {
                            newsList.add(news);
                            Log.e("NEWS", news.toString());
                            loadNewsListner.onLoadNewsDone();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        News news = dataSnapshot.getValue(News.class);
                        news.setKey(dataSnapshot.getKey());

                        updateNews(news);



                        /*if (position != -1) {
                            loadNewsListner.onNewsUpdated(position);
                        }*/
                    }

                    private boolean isNewsAdded(News news) {

                        for (News n : newsList) {
                            if (n.getTimestamp() == news.getTimestamp() && n.getContenu().compareTo(news.getContenu()) == 0) {
                                n.setKey(news.getKey());
                                Log.e("COMMENT UPDATED", "///");
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        loadNewsListner.onLoadNewsErreur();
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private int updateNews(News news) {
        for (int i = 0; i < newsList.size(); i++) {
            News oldNews = newsList.get(i);
            if (oldNews.getKey().compareTo(news.getKey()) == 0) {
                news.setListnerAddedForComments(true);
                news.setNewsComments(oldNews.getNewsComments());
                newsList.set(i, news);
                return i;
            }
        }
        return -1;
    }

    public void fireBaseLoadComments(final News news, final LoadCommentsListner loadCommentsListner) {

        Log.e("XXXXXXXXXX", "listner Added = " + news.isListnerAddedForComments());

        if (news.isListnerAddedForComments()) {
            loadCommentsListner.onLoadingCommentsDone(news);
        } else {
            Log.e("XXXXXXXXXX", "NEW LISTNER");
            List<Comment> newsComments = new ArrayList<>();
            newsComments.add(null);
            news.setNewsComments(newsComments);
            news.setListnerAddedForComments(true);
            loadCommentsListner.initCommentRecyclerView(news);
            DatabaseReference myNewsReferences = fireDatabase.getReference(UNIVERSITY_NEWS_ID).child(COMMENTS_ID).child(news.getKey());
            myNewsReferences.orderByChild("timestamp").addChildEventListener(
                    new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            comment.setKey(dataSnapshot.getKey());

                            Log.e("COMMENT Recieved", comment.toString());

                            if (!isCommentAdded(news.getNewsComments(), comment)) {
                                Log.e("COMMENT ADDED", "///");
                                news.getNewsComments().add(news.getNewsComments().size() - 1, comment);
                                loadCommentsListner.onNewCommentAdded(news);
                            }


                        }

                        private boolean isCommentAdded(List<Comment> newsComments, Comment comment) {

                            for (int i = 0; i < newsComments.size() - 1; i++) {
                                Comment c = newsComments.get(i);
                                if (c.getTimestamp() == comment.getTimestamp() && c.getComment().compareTo(comment.getComment()) == 0) {
                                    c.setKey(comment.getKey());
                                    Log.e("COMMENT UPDATED", "///");
                                    return true;
                                }
                            }
                            return false;
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            loadCommentsListner.onLoadingNewsErreur();
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });

        }
    }

    private boolean isCommentsListLoaded(News news, int nbComment) {
        //Log.e("TEST", "nbCommenet" + nbComment + "nbNewsCommentList" + news.getNewsComments().size());
        return news.getNewsComments() != null && (nbComment == 0 || (news.getNewsComments().size() - 1 == nbComment));
    }
}
