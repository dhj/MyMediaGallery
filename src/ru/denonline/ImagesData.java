package ru.denonline;

import static android.provider.BaseColumns._ID;
import static ru.denonline.DBConstants.LINK;
import static ru.denonline.DBConstants.TABLE_NAME;
import static ru.denonline.DBConstants.TIME;
import static ru.denonline.DBConstants.TITLE;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * Хелпер для таблицы картинок
 *
 */

public class ImagesData extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "images.db" ;
	private static final int DATABASE_VERSION = 2;
	
	/**
	 * Получить экземпляр хелпера
	 * */
	public static ImagesData CreateDBHelper(Context context)
	{
		return new ImagesData(context);
	}
	
	public ImagesData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TIME + " INTEGER,"
				+ TITLE + " TEXT NOT NULL, " 
				+ LINK + " TEXT NOT NULL" 
				+ ");" );

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVerision, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}
