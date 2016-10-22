package com.whynotit.admin.Models;

public class Emplois 
{
    private String filiere;
	private String jour;
	private String seance;
	private String type;
	private String matiere;
	private String enseignant;
	private String regime;
	private String salle;
	private String debut;
	private String fin;
    private int presence;
	private int id;
	private String date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Emplois ()
	{
		
	}
	
	public Emplois (String filiere,String jour, String seance,String type, String matiere,String enseignant,String regime, String salle,String debut,String fin)
	{
        this.filiere=filiere;
		this.jour=jour;
		this.seance=seance;
		this.type=type;
		this.matiere=matiere;
		this.enseignant=enseignant;
		this.regime=regime;
		this.salle=salle;
		this.debut=debut;
		this.fin=fin;
	}


    public String getFiliere ()
    {
        return filiere;
    }

    public void setFiliere (String filiere)
    {
        this.filiere=filiere;
    }

    public int getPresence ()
    {
        return presence;
    }

    public String getPresenceToString ()
    {
        if (presence == 1)
            return "Present";
        else if (presence == 0)
            return "Absent";
		else
			return "Absence du groupe";
    }

    public void setPresence (int presence)
    {
        this.presence=presence;
    }

	public void setEnseignant (String enseignant)
	{
		this.enseignant=enseignant;
	}
	
	public String getEnseignant ()
	{
		return this.enseignant;
	}
	
	public void setJour (String jour)
	{
		this.jour=jour;
	}

    public void setDebut (String debut) { this.debut=debut;}

    public void setFin (String fin) {this.fin=fin;}

    public void setRegime (String regime) { this.regime=regime;}

    public void setSeance (String seance)
	{
		this.seance=seance;
	}
	
	public void setMatiere (String matiere)
	{
		this.matiere=matiere;
	}
	
	public void setSalle (String salle)
	{
		this.salle=salle;
	}
	
	public String getDebut ()
	{
		return debut;
	}
	
	public String getFin ()
	{
		return fin;
	}
	
	public String getJour ()
	{
		return jour;
	}
	
	public String getSeance ()
	{
		return seance;
	}
	
	public String getMatiere ()
	{
		return matiere;
	}
	
	public String getSalle ()
	{
		return salle;
	}
	
	public String getRegime ()
	{
		return regime;
	}
	
	public String getType() 
	{
		return type;
	}
	
	public String toString ()
	{
		return (matiere.toUpperCase()+" ( "+type+" )\n ( "+regime+" ) ");
	}
	
	public String toStringListe ()
	{
		return (jour+"!"+seance+"!"+type+"!"+matiere+"!"+regime+"!"+salle);
	}

	public void setType(String type) 
	{
		this.type=type;
		
	}

	public String toStringForEmail () {
		return date + " * " + jour + " * " + debut + " * " + filiere + " * " + enseignant + " * " + matiere + " * " + salle;
	}
	
	public String getHoraireSeance ()
	{
		return (debut+" - "+fin);
	}


	public void setID(int ID) {
		this.id = ID;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}
}