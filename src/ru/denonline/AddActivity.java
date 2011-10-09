package ru.denonline;

import static ru.denonline.DBConstants.LINK;
import static ru.denonline.DBConstants.TABLE_NAME;
import static ru.denonline.DBConstants.TIME;
import static ru.denonline.DBConstants.TITLE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.UUID;

import ru.denonline.utils.Utils;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Ёкран добавлени€ картинок
 * 
 * @author Hunter
 * 
 */
public class AddActivity extends Activity implements OnItemClickListener {

	private ListView _filesList;
	private EditText _titleEditText;
	private String[] _filesNames;
	private String _lastClickedFileName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add);
		setResult(RESULT_CANCELED, null);

		_titleEditText = (EditText) findViewById(R.id.titleEditText);

		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AddImage();
				setResult(RESULT_OK, null);
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
		FillFilesList();
		UpdateButtons();

	}
	
	private void FillFilesList() {
		File f = Environment.getExternalStorageDirectory();
		if (f.isDirectory()) {
			_filesNames = f.list(new PictureFilenameFilter());
			_filesList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _filesNames));
		}
	}

	public class PictureFilenameFilter implements FilenameFilter {

		
		public boolean accept(File dir, String filename) {
			return filename.endsWith(".jpg") || filename.endsWith(".JPG") || filename.endsWith(".png") || filename.endsWith(".PNG")
					|| filename.endsWith(".gif") || filename.endsWith(".GIF");

		}

	}

	private void UpdateButtons() {
		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setEnabled(_lastClickedFileName != null);
	}

	
	public void onItemClick(AdapterView<?> adapterView, View listView, int position, long id) {
		boolean needUpdateButtons = _lastClickedFileName == null;
		_lastClickedFileName = _filesNames[position];
		_titleEditText.setText(_lastClickedFileName);
		if (needUpdateButtons)
			UpdateButtons();
	}

	private void AddImage() {
		try {

			// получаем введенное пользователем название
			String title = _titleEditText.getText().toString();			
			if (0 == title.length()) title = getString(R.string.noTitle);
			
			// получим входной поток
			String path = Environment.getExternalStorageDirectory().getPath().concat(File.separator).concat(_lastClickedFileName);
			File image = new File(path);						
			FileInputStream inputStream = new FileInputStream(image);
			
			// получим выходной поток
			String outputFilename = UUID.randomUUID().toString();
			FileOutputStream outputStream = openFileOutput(outputFilename, MODE_PRIVATE);
			
			// копируем вход на выход
			Utils.copy(inputStream, outputStream);
					
						
			inputStream.close();
			outputStream.close();

			// запишем в Ѕƒ
			ImagesData images = ImagesData.CreateDBHelper(this);
			try{
				SQLiteDatabase db = images.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put(TIME, System.currentTimeMillis());
				values.put(TITLE, title);
				values.put(LINK, outputFilename);
				db.insertOrThrow(TABLE_NAME, null, values);
			}finally
			{
				images.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}

}
