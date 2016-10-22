package com.whynotit.admin;

import java.util.Calendar;

public class MaDate
{
	int jourN;
	String jour;
	int moisN;
	String mois;
	int annee;
	Calendar c;

	public Calendar getC() {
		return c;
	}

	public MaDate ()
	{
		c=Calendar.getInstance ();
			configDate();
	}


	public String getMois() {
		return mois;
	}

	public int getAnnee() {
		return annee;
	}

	public String getDateHistorique (String jour)
	{
		MaDate maDate = new MaDate();
		while (maDate.jour.compareTo(jour) != 0)
		{
			maDate.jourPrecedant();
		}

		return maDate.toString();

	}

	private void configDate()
	{
		jourN=c.get(Calendar.DATE);
		moisN=c.get(Calendar.MONTH)+1;
		annee=c.get(Calendar.YEAR);
		
		switch (moisN) 
		{
		case 1:mois="Janvier";break;
		case 2:mois="Février";break;
		case 3:mois="Mars";break;
		case 4:mois="Avril";break;
		case 5:mois="Mai";break;
		case 6:mois="Juin";break;
		case 7:mois="Juillet";break;
		case 8:mois="Août";break;
		case 9:mois="Septembre";break;
		case 10:mois="Octobre";break;
		case 11:mois="Novembre";break;
		default:mois="Décembre";break;
		}
		
		switch (c.get(Calendar.DAY_OF_WEEK))
		{
		case 1:jour="DIMANCHE";break;
		case 2:jour="LUNDI";break;
		case 3:jour="MARDI";break;
		case 4:jour="MERCREDI";break;
		case 5:jour="JEUDI";break;
		case 6:jour="VENDREDI";break;
		default:jour="SAMEDI";break;
		}
	}
	
	public void jourSuivant ()
	{
		c.add(5, 1);
		configDate();
	}
	
	public void jourPrecedant ()
	{
		c.add(5,-1);
		configDate();
	}
	
	public String toStringAllData ()
	{
		return (jour+" "+jourN+" "+mois+" "+moisN+" "+annee);
	}
	
	public String toString ()
	{
		return (jour+" "+jourN+" "+mois+" "+annee);
	}

	public String getDateInDateFormat () {
		return jourN+"/"+moisN+"/"+annee;
	}
	
	public void updateDate (String d)
	{
		String []tab=d.split(" ");
        jour=tab[0];
        jourN=Integer.parseInt(tab[1]);
        mois=tab[2];
        moisN=Integer.parseInt(tab[3]);
        annee=Integer.parseInt(tab[4]);
        c.set(annee, moisN-1, jourN);
	}

	public String getJour() {
		return jour;
	}

	public String getAllDateFeilds() {
		return getDateInDateFormat() + " - " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
	}
}