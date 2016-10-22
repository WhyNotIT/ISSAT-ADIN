package com.whynotit.admin.Adapters;

/**
 * Created by Harzallah on 15/02/2016.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whynotit.admin.Dialog.DialogModifierUtilisateur;
import com.whynotit.admin.Managers.SharedPerefManager;
import com.whynotit.admin.Managers.UsersManager;
import com.whynotit.admin.R;
import com.whynotit.admin.Models.User;

import java.util.List;


/**
 * Created by Harzallah on 27/11/2015.
 */
public class UsersModifierAdapter extends BaseAdapter {

    private final List<User> users;
    private final DialogModifierUtilisateur dialogAuthentification;
    private LayoutInflater mInflater;
    private Activity activity;




    public UsersModifierAdapter(DialogModifierUtilisateur dialogAuthentification, Activity activity, List<User> users) {
        this.mInflater = LayoutInflater.from(activity);
        this.users = users;
        this.activity = activity;
        this.dialogAuthentification = dialogAuthentification;
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

        convertView = mInflater.inflate(R.layout.user_modifier_item, parent, false);
        //mState.addView(convertView);

        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        userName.setText(users.get(position).getNom());

        LinearLayout delete = (LinearLayout) convertView.findViewById(R.id.layout_delete);

        final View finalConvertView = convertView;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GestionFichier.deleteLine(position,mState.getUsersFile());
                UsersManager.getInstance().getUserList().remove(position);
                SharedPerefManager.getInstance(activity).saveUsers();
                notifyDataSetChanged();
            }
        });


        return convertView;

    }
}
