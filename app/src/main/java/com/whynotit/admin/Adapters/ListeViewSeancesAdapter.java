package com.whynotit.admin.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whynotit.admin.Models.Emplois;
import com.whynotit.admin.R;

import java.util.List;

public class ListeViewSeancesAdapter extends BaseAdapter {

	private List<Emplois> journee;
	private final Context activity;
	LayoutInflater mInflater;

	public ListeViewSeancesAdapter(Activity activity, List<Emplois> l) {

		this.activity = activity;
		mInflater = LayoutInflater.from(activity);
		journee=l;

    }

	// Add a ToDoItem to the adapter
	// Notify observers that the data set has changed

	public void add(Emplois item) 
	{
		journee.add(item);
	}

	// Clears the list adapter of all items.
	// Returns the number of ToDoItems

	@Override
	public int getCount() {

		return journee.size();

	}

	// Retrieve the number of ToDoItems

	@Override
	public Object getItem(int pos) {

		return journee.get(pos);

	}

	// Get the ID for the ToDoItem
	// In this case it's just the position

	@Override
	public long getItemId(int pos) {

		return pos;

	}

	// Create a View for the ToDoItem at specified position
	// Remember to check whether convertView holds an already allocated View
	// before created a new View.
	// Consider using the ViewHolder pattern to make scrolling more efficient
	// See: http://developer.android.com/training/improving-layouts/smooth-scrolling.html
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// TODO - Get the current ToDoItem
		Emplois emplois = journee.get(position) ;
		
		// TODO - Inflate the View for this ToDoItem
		// from todo_item.xml
		
		convertView = mInflater.inflate(R.layout.item_seance, parent, false);
		// TODO - Fill in specific ToDoItem data
		// Remember that the data that goes in this View
		// corresponds to the user interface elements defined
		// in the layout file

        final TextView rawFiliere = (TextView)convertView.findViewById(R.id.RowFiliere);
        final TextView rawEnseignant = (TextView)convertView.findViewById(R.id.RowEnseignant);
        final TextView rawMatiere = (TextView)convertView.findViewById(R.id.RowMatiere) ;
		final TextView rawSalle = (TextView)convertView.findViewById(R.id.RowSalle) ;

        rawFiliere.setText(emplois.getFiliere());
        rawEnseignant.setText(emplois.getEnseignant());
        rawMatiere.setText(emplois.getMatiere());
        rawSalle.setText(emplois.getSalle());


		if (emplois.getPresence()==1)
		{
		    convertView.setBackgroundColor(activity.getResources().getColor(R.color.colorListSeancesBg));
		}
		else if (emplois.getPresence()==2)
		{
			convertView.setBackgroundColor(activity.getResources().getColor(R.color.colorAbscenceGroupe));
		}
		else

		{
			convertView.setBackgroundColor(activity.getResources().getColor(R.color.colorAbscent));
		}
		// Return the View you just created
		return convertView;

	}
}
