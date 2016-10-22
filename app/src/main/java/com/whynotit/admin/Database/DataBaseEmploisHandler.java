package com.whynotit.admin.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseEmploisHandler extends SQLiteOpenHelper
{

	public static String DATABASE_PATH;
	public static final String DATABASE_NAME = "emplois.db";
	public static final int DATABASE_VERSION = 1;

	public static final String TABLE_EMPLOIS = "emplois";

	public static final String COL_ID= "ID";
	public static final int NUM_COL_ID = 0;
	public static final String COL_FILIERE= "FILIERE";
	public static final int NUM_COL_FILIERE = 1;
	public static final String COL_JOUR = "JOUR";
	public static final int NUM_COL_JOUR = 2;
	public static final String COL_SEANCE = "SEANCE";
	public static final int NUM_COL_SEANCE = 3;
	public static final String COL_DEBUT = "DEBUT";
	public static final int NUM_COL_DEBUT = 4;
	public static final String COL_FIN = "FIN";
	public static final int NUM_COL_FIN = 5;
	public static final String COL_MATIERE = "MATIERE";
	public static final int NUM_COL_MATIERE = 6;
	public static final String COL_ENSEIGNANT = "ENSEIGNANT";
	public static final int NUM_COL_ENSEIGNANT = 7;
	public static final String COL_TYPE = "TYPE";
	public static final int NUM_COL_TYPE = 8;
	public static final String COL_SALLE = "SALLE";
	public static final int NUM_COL_SALLE = 9;
	public static final String COL_REGIME = "REGIME";
	public static final int NUM_COL_REGIME = 10;

	public static final String TABLE_DATE_ABSCENCE = "dateabscence";

	public static final String COL_DATE= "DATE";
	public static final int NUM_COL_DATE = 1;

	public static final String TABLE_ABSCENCE = "abscence";

	public static final String COL_ID_DATE= "IDDATE";
	public static final int NUM_COL_ID_DATE = 0;
	public static final String COL_ID_SEANCE= "IDSEANCE";
	public static final int NUM_COL_ID_SEANCE = 1;
	public static final String COL_ABSCENCE= "ABSCENCE";
	public static final int NUM_COL_ABSCENCE = 2;

	public static final String TABLE_CONTACT_ENSEIGNANT = "contactenseignant";

	public static final String COL_NOM_ENSEIGNANT= "nom";
	public static final int NUM_COL_NOM_ENSEIGNANT = 0;
	public static final String COL_EMAIL = "email";
	public static final int NUM_COL_EMAIL = 1;



	public static final String CREATE_TABLE_CONTACT_ENSEIGNANT = "CREATE TABLE " + TABLE_CONTACT_ENSEIGNANT + " ("
			+ COL_NOM_ENSEIGNANT + " VARCHAR(100) NOT NULL , " + COL_EMAIL + " VARCHAR(100) NOT NULL);";


	public static final String CREATE_TABLE_EMPLOIS = "CREATE TABLE " + TABLE_EMPLOIS + " ("
			+ COL_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " + COL_FILIERE + " TEXT NOT NULL, " + COL_JOUR + " TEXT NOT NULL, " + COL_SEANCE + " TEXT NOT NULL, "
            + COL_DEBUT + " TEXT NOT NULL, " + COL_FIN + " TEXT NOT NULL, " + COL_MATIERE + " TEXT NOT NULL, "
            + COL_ENSEIGNANT + " TEXT NOT NULL, " + COL_TYPE + " TEXT NOT NULL, " + COL_SALLE + " TEXT NOT NULL, "
            + COL_REGIME + " TEXT NOT NULL );";

	public static final String CREATE_TABLE_DATE_ABSCENCE = "CREATE TABLE " + TABLE_DATE_ABSCENCE + " ("
			+ COL_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " + COL_DATE + " DATE NOT NULL );";


	public static final String CREATE_TABLE_ABSCENCE = "CREATE TABLE " + TABLE_ABSCENCE + " ("
			+ COL_ID_DATE + " INTEGER NOT NULL, " + COL_ID_SEANCE + " INTEGER NOT NULL, " + COL_ABSCENCE + " INTEGER NOT NULL DEFAULT 1);";


	private SQLiteDatabase database;

	public DataBaseEmploisHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).getPath();
	}
		
	@Override
	public void onCreate(SQLiteDatabase database) {
		this.database = database;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(DataBaseEmploisHandler.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		/*
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOIS);
		onCreate(database);
		*/
	}


}