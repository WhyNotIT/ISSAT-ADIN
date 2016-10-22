package com.whynotit.admin.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.whynotit.admin.Activity.EspaceAdminActivity;
import com.whynotit.admin.Managers.SeanceAExporterManager;
import com.whynotit.admin.Managers.SeancesHorairesManager;
import com.whynotit.admin.Models.HoraireSeance;
import com.whynotit.admin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harzallah on 26/07/2016.
 */
public class ListSeancesRadioButtonAdapter extends RecyclerView.Adapter<ListSeancesRadioButtonAdapter.ViewHolder> {

    private List<HoraireSeance> listHorairesSeances = new ArrayList<>();
    private EspaceAdminActivity activity;




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RadioButton radioButton;


        public ViewHolder(View v) {
            super(v);
            radioButton = (RadioButton) v.findViewById(R.id.radio_button_seance);

        }
    }


    public ListSeancesRadioButtonAdapter(EspaceAdminActivity activity) {

        this.activity = activity;
        this.listHorairesSeances = SeancesHorairesManager.getInstance().getListSeancesForExportation();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListSeancesRadioButtonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_radio_horaire_seance, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.radioButton.setText(listHorairesSeances.get(position).getHeureDebut());

        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                SeanceAExporterManager seanceAExporterManager = SeanceAExporterManager.getInstance();
                    if (seanceAExporterManager.getSelectedSeance() != null) {
                        seanceAExporterManager.getSelectedSeance().setChecked(false);
                    }
                seanceAExporterManager.setSelectedSeance(holder.radioButton);
                seanceAExporterManager.setPositionSelectedSeance(position);

            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listHorairesSeances.size();
    }

}
