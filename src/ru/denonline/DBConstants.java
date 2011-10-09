package ru.denonline;

import android.provider.BaseColumns;

public interface DBConstants extends BaseColumns {
	public static final String TABLE_NAME = "images";
	
	// Columns in the Images database
	public static final String TITLE = "title";
	public static final String TIME = "time";
	public static final String LINK = "link";
}
