package ru.denonline;

import static android.provider.BaseColumns._ID;
import static ru.denonline.DBConstants.TABLE_NAME;
import static ru.denonline.DBConstants.TIME;
import static ru.denonline.DBConstants.TITLE;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Ёкран удалени€ картинок
 *
 */
public class RemoveActivity extends Activity implements OnItemClickListener {

	private ListView _filesList;
	private TextView _selectedImageTitle;
	private long _selectedImageId;
	private Cursor _imagesCursor;
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remove);
		setResult(RESULT_CANCELED, null);
		
		
		_selectedImageTitle = (TextView) findViewById(R.id.selectedImageTitle);

		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				RemoveImage();
				setResult(RESULT_OK, null);	
				_imagesCursor.requery();
				finish();
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED, null);				
				finish();
			}
		});

		_filesList = (ListView) findViewById(R.id.filesListView);
		_filesList.setOnItemClickListener(this);
		
		_imagesCursor = GetImages();
		ShowImages(_imagesCursor);
	}

	protected void RemoveImage() {		
		ImagesData dbHelper = ImagesData.CreateDBHelper(this);		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String[] args={String.valueOf(_selectedImageId)};
		db.delete(TABLE_NAME, "_ID=?", args);		
	}
	
	
	public void onItemClick(AdapterView<?> adapterView, View listView, int position, long id) {
		// TODO Auto-generated method stub
		Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
		String title = cursor.getString(cursor.getColumnIndex(TITLE));
		
		_selectedImageTitle.setText(title);
		_selectedImageId = cursor.getLong(cursor.getColumnIndex(_ID));
	}

	private static String[] FROM = { _ID, TIME, TITLE, };
	private static String ORDER_BY = TIME + " ASC";

	private Cursor GetImages() {
		
		ImagesData dbHelper = ImagesData.CreateDBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null, ORDER_BY);
		startManagingCursor(cursor);		
		return cursor;
	}

	private static String[] ITEM_FROM = { TITLE, };
	private static int[] TO = { R.id.title, };

	private void ShowImages(Cursor cursor) {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, ITEM_FROM, TO);		
		_filesList.setAdapter(adapter);
	}

	

}
