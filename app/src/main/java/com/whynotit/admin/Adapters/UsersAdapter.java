package com.whynotit.admin.Adapters;

/**
 * Created by Harzallah on 15/02/2016.
 */

import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whynotit.admin.Dialog.DialogAuthentification;
import com.whynotit.admin.GoogleSpreadsheetOperations.SendAbscenceDataToSpreadsheet;
import com.whynotit.admin.GoogleSpreadsheetOperations.SpreadsheetParams;
import com.whynotit.admin.Managers.NetworkManager;
import com.whynotit.admin.Managers.UserManager;
import com.whynotit.admin.R;
import com.whynotit.admin.Models.User;

import java.util.List;


/**
 * Created by Harzallah on 27/11/2015.
 */
public class UsersAdapter extends BaseAdapter {

    private final List<User> users;
    private final DialogAuthentification dialogAuthentification;
    private LayoutInflater mInflater;
    private Activity activity;
    private CoordinatorLayout rootLayout;


    public UsersAdapter(DialogAuthentification dialogAuthentification, Activity activity, List<User> users) {
        this.mInflater = LayoutInflater.from(activity);
        this.users = users;
        this.activity = activity;
        this.dialogAuthentification = dialogAuthentification;
        rootLayout = (CoordinatorLayout) dialogAuthentification.findViewById(R.id.root_layout);
    }

    @Override
    public int getCount() {

        return users.size();

    }

    // Retrieve the number of ToDoItems

    @Override
    public Object getItem(int pos) {

        return users.get(pos);

    }

    // Get the ID for the ToDoItem
    // In this case it's just the position

    @Override
    public long getItemId(int pos) {

        return pos;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.user_item, parent, false);
        //activity.addView(convertView);

        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        userName.setText(users.get(position).getNom());
        final View finalConvertView = convertView;

        final View finalConvertView1 = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserManager.getInstance().getUserLayout() != null) {
                    UserManager.getInstance().getUserLayout().setBackgroundResource(R.color.colorPrimaryLight);
                }
                UserManager.getInstance().setUserLayout(finalConvertView1);
                UserManager.getInstance().setUser(users.get(position));


                finalConvertView.setBackgroundResource(R.color.colorPrimary);
                LinearLayout submitPassword = (LinearLayout) dialogAuthentification.findViewById(R.id.submit_password);
                submitPassword.setVisibility(View.VISIBLE);
                final EditText password = (EditText) dialogAuthentification.findViewById(R.id.password);
                Button confirmer = (Button) dialogAuthentification.findViewById(R.id.exporter);
                confirmer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (password.getText().toString().equals(users.get(position).getMotDePasse())) {
                            dialogAuthentification.dismiss();
                            new SendAbscenceDataToSpreadsheet(NetworkManager.getInstance().getCredential(activity),activity, SpreadsheetParams.SHEET_ID_ABSCENCE,SpreadsheetParams.SHEET_RANGE_ABSCENCE).execute();
                            //new PostData(mState.getActivity(), users.get(position).getNom(), mState.getNumeroSeance(), mState.loadJourneeFromDB("S" + mState.getNumeroSeance()), mState.getNomFichier(), mState.getMaDate().toString()).execute();
                        } else {
                            Snackbar.make(rootLayout, "Mot de passe incorrect!", Snackbar.LENGTH_LONG).show();
                        }

                    }
                });

                Button annuler = (Button) dialogAuthentification.findViewById(R.id.annuler);
                annuler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogAuthentification.dismiss();
                    }
                });
            }
        });
        return convertView;

    }
}
