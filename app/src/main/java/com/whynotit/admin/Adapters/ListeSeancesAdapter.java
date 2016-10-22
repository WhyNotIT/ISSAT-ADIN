package com.whynotit.admin.Adapters;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whynotit.admin.Activity.MainActivity;
import com.whynotit.admin.Database.DatabaseDAO;
import com.whynotit.admin.Dialog.DialogAuthentification;
import com.whynotit.admin.Managers.NetworkManager;
import com.whynotit.admin.Managers.SeancesToUploadManager;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.Models.Emplois;
import com.whynotit.admin.R;

import java.util.ArrayList;
import java.util.List;

public class ListeSeancesAdapter extends RecyclerView.Adapter<ListeSeancesAdapter.ViewHolder> {

    private List<Emplois> listCurrentsSeances = new ArrayList<Emplois>();
    private MainActivity activity;




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView rawFiliere, rawEnseignant, rawSalle, rawMatiere;
        private final Button exporterSeances;
        private View itemSeanceLayout;
        private RelativeLayout rootLayout;


        public ViewHolder(View v) {
            super(v);
            rawFiliere = (TextView) v.findViewById(R.id.RowFiliere);
            rawEnseignant = (TextView) v.findViewById(R.id.RowEnseignant);
            rawMatiere = (TextView) v.findViewById(R.id.RowMatiere);
            rawSalle = (TextView) v.findViewById(R.id.RowSalle);
            itemSeanceLayout = (View) v.findViewById(R.id.item_seance_layout);
            rootLayout = (RelativeLayout) v.findViewById(R.id.root_layout);
            exporterSeances = (Button) v.findViewById(R.id.exporterSeances);

        }
    }


    public ListeSeancesAdapter(MainActivity activity, List<Emplois> listCurrentsSeances) {

        this.activity = activity;
        this.listCurrentsSeances = listCurrentsSeances;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListeSeancesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case 1 : return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button_exporter, parent, false));
            default: return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seance, parent, false));
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (getItemViewType(position) == 0) {
            final Emplois emplois = listCurrentsSeances.get(position);



            holder.rawFiliere.setText(emplois.getFiliere());
            holder.rawEnseignant.setText(emplois.getEnseignant());
            holder.rawMatiere.setText(emplois.getMatiere());
            holder.rawSalle.setText(emplois.getSalle());

            Log.e("Item",emplois.getEnseignant()+" -> " + emplois.getPresence());
            if (emplois.getPresence() == 1) {
                Log.e("COLOR","WITHE");
                holder.itemSeanceLayout.setBackgroundResource(R.color.colorListSeancesBg);
                holder.rootLayout.setBackgroundResource(R.color.colorListSeancesBg);
            } else if (emplois.getPresence() == 2) {
                holder.itemSeanceLayout.setBackgroundResource(R.color.colorAbscenceGroupe);
                holder.rootLayout.setBackgroundResource(R.color.colorAbscenceGroupe);
            } else {
                holder.itemSeanceLayout.setBackgroundResource(R.color.colorAbscent);
                holder.rootLayout.setBackgroundResource(R.color.colorAbscent);
            }
            // Return the View you just created

            holder.itemSeanceLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (emplois.getPresence() == 1) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                animateRevealView(holder.rootLayout, holder.itemSeanceLayout, R.color.colorAbscent, event.getX(), event.getY());
                            } else {
                                animateView(holder.rootLayout,holder.itemSeanceLayout,R.color.colorListSeancesBg,R.color.colorAbscent);
                            }
                            updatePresenceEmplois(emplois.getId(), 0);
                            emplois.setPresence(0);

                        } else if (emplois.getPresence() == 0) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                animateRevealView(holder.rootLayout, holder.itemSeanceLayout, R.color.colorAbscenceGroupe, event.getX(), event.getY());
                            } else {
                                animateView(holder.rootLayout,holder.itemSeanceLayout,R.color.colorAbscent,R.color.colorAbscenceGroupe);
                            }
                            updatePresenceEmplois(emplois.getId(), 2);
                            emplois.setPresence(2);
                        } else {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                animateRevealView(holder.rootLayout, holder.itemSeanceLayout, R.color.colorListSeancesBg, event.getX(), event.getY());
                            } else {
                                animateView(holder.rootLayout,holder.itemSeanceLayout,R.color.colorAbscenceGroupe,R.color.colorListSeancesBg);
                            }
                            updatePresenceEmplois(emplois.getId(), 1);
                            emplois.setPresence(1);
                            //holder.itemSeanceLayout.setBackgroundResource(R.color.colorListSeancesBg);
                        }
                    }
                    return false;
                }
            });
        } else {

            holder.exporterSeances.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listCurrentsSeances.size() == 1) {
                        Snackbar.make(activity.getViewPager(),"La liste des seances est vide!",Snackbar.LENGTH_LONG).show();
                    } else {
                        if (!NetworkManager.getInstance().isNetworkAvailable (activity)) {
                            Snackbar.make(activity.getViewPager(),"Aucune connexion disponible!",Snackbar.LENGTH_LONG).show();
                        } else if (SharedPerefManager.getInstance(activity).IsSeanceUploaded(listCurrentsSeances.get(0).getDebut())) {
                            Snackbar.make(activity.getViewPager(),"L'exportation est permise une seule fois par seance!",Snackbar.LENGTH_LONG).show();
                        } else {
                            SeancesToUploadManager.getInstance().setListSeancesToUpload(listCurrentsSeances);
                            showUserAuthentification();
                        }
                    }

                    /*if (gf.selectValeur(nomFichier, "*S" + mState.getNumeroSeance() + ":").compareTo(cle) == 0) {
                        Toast.makeText(getActivity(), "L' exportation des données est permise une seule fois par jour.", Toast.LENGTH_LONG).show();
                    } else {
                        showUserAuthentification();
                    }*/


                }
            });

        }

    }

    private void animateView (final View rootLayout, final View view, final int oldColor, final int newColor) {
        rootLayout.setBackgroundResource(R.color.withe);

        int colorFrom = activity.getResources().getColor(oldColor);
        int colorTo = activity.getResources().getColor(newColor);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // milliseconds
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


    private void animateRevealView(final View rootLayout, final View view, final int color, float cx, float cy) {
        // get the center for the clipping circle
        //int cx = view.getMeasuredWidth() / 2;
        //int cy = view.getMeasuredHeight() / 2;
        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) cx, (int) cy, 0, finalRadius);

            // make the view visible and start the animation
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    view.setBackgroundResource(color);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    rootLayout.setBackgroundResource(color);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.setDuration(500);
            anim.start();

    }

    private void showUserAuthentification() {

        DialogAuthentification dialogAuthentification = new DialogAuthentification(activity);
        dialogAuthentification.show();
    }

    private void updatePresenceEmplois(int idSeance, int presence) {
        DatabaseDAO databaseDAO = new DatabaseDAO(activity);
        databaseDAO.open();
        databaseDAO.updateSeanceAbscence(idSeance,presence);
        databaseDAO.close();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listCurrentsSeances.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position == listCurrentsSeances.size()-1)
            return 1;
        else
            return 0;
    }
}
