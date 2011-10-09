package ru.denonline;

import static android.provider.BaseColumns._ID;
import static ru.denonline.DBConstants.LINK;
import static ru.denonline.DBConstants.TABLE_NAME;
import static ru.denonline.DBConstants.TIME;
import static ru.denonline.DBConstants.TITLE;

import java.io.FileInputStream;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;




public class MyMediaGalleryActivity extends Activity implements OnPageChangeListener
		
{
	private static final int ADD_IMAGE_REQUEST = 1;
	private static final int REMOVE_IMAGE_REQUEST = 2;

	private Cursor _imagesCursor;

	private TextView _selectedImageTitle;
	private Button prevButton;
	private Button nextButton;
	
	private Context ctx;
	private ViewPager _imagePager;
	private int _currentItem = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		
		ctx = this;
		_imagePager = (ViewPager) findViewById(R.id.imagePager);
		_imagePager.setOnPageChangeListener(this);
		
		_selectedImageTitle = (TextView) findViewById(R.id.selectedImageTitle);

		prevButton = (Button) findViewById(R.id.prevButton);
		prevButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{	
				_imagePager.setCurrentItem(_currentItem - 1);
			}
		});

		nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				_imagePager.setCurrentItem(_currentItem + 1);
			}
		});

		LoadPicturesList();
		UpdateView();		
	}
	
	private class ImagePagerAdapter extends PagerAdapter
	{
		@Override
		public int getCount() {
			return _imagesCursor.getCount();
		}

	    /**
	     * Create the page for the given position.  The adapter is responsible
	     * for adding the view to the container given here, although it only
	     * must ensure this is done by the time it returns from
	     * {@link #finishUpdate()}.
	     *
	     * @param container The containing View in which the page will be shown.
	     * @param position The page position to be instantiated.
	     * @return Returns an Object representing the new page.  This does not
	     * need to be a View, but can be some other container of the page.
	     */
		@Override
		public Object instantiateItem(View collection, int position) {
			_imagesCursor.moveToPosition(position);
			ImageView imageView = new ImageView(ctx);
			int linkIndex = _imagesCursor.getColumnIndex(LINK);			
			try
			{
				String fileName = _imagesCursor.getString(linkIndex);
				FileInputStream fileStream = openFileInput(fileName);
				Bitmap bitmap = BitmapFactory.decodeStream(fileStream);
				imageView.setImageBitmap(bitmap);
				fileStream.close();
			} catch (Exception e)
			{
				e.printStackTrace();
				imageView.setImageBitmap(null);				
			}
			
			((ViewPager) collection).addView(imageView, 0);
			
			return imageView;			
		}

	    /**
	     * Remove a page for the given position.  The adapter is responsible
	     * for removing the view from its container, although it only must ensure
	     * this is done by the time it returns from {@link #finishUpdate()}.
	     *
	     * @param container The containing View from which the page will be removed.
	     * @param position The page position to be removed.
	     * @param object The same object that was returned by
	     * {@link #instantiateItem(View, int)}.
	     */
		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((ImageView) view);
		}

		
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==((ImageView)object);
		}

		
	    /**
	     * Called when the a change in the shown pages has been completed.  At this
	     * point you must ensure that all of the pages have actually been added or
	     * removed from the container as appropriate.
	     * @param container The containing View which is displaying this adapter's
	     * page views.
	     */
		@Override
		public void finishUpdate(View arg0) {}
		

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {}
	}

	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Auto-generated method stub
		
	}

	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Auto-generated method stub
		
	}

	public void onPageSelected(int position)
	{
		_currentItem = position;
		_imagesCursor.moveToPosition(position);
		UpdateView();		
	}
	
	/**
	 * Обновить курсор, переместиться на первую картинку
	 */
	private void LoadPicturesList()
	{
		_imagesCursor = GetImages();
		_imagesCursor.moveToFirst();
		_currentItem = 0;
		ImagePagerAdapter adapter = new ImagePagerAdapter();
		_imagePager.setAdapter(adapter);
		if (_imagePager.getChildCount() > 0)
		{
			_imagePager.setCurrentItem(0);
		}		
	}

	private static String[] FROM =
	{ _ID, TIME, TITLE, LINK, };
	private static String ORDER_BY = TIME + " ASC";

	/**
	 * Получить курсор к изображениям в БД
	 * 
	 * @return
	 */
	private Cursor GetImages()
	{
		ImagesData dbHelper = ImagesData.CreateDBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null,
				ORDER_BY);
		startManagingCursor(cursor);
		return cursor;
	}

	/**
	 * Обновить представление
	 */
	private void UpdateView()
	{
		UpdateButtons();		
		UpdateTitle();
	}

	/**
	 * Обновить возможность нажатия кнопок
	 */
	private void UpdateButtons()
	{
		int rowCount = _imagesCursor.getCount();
		prevButton.setEnabled(!(0 == rowCount || _imagesCursor.isFirst()));
		nextButton.setEnabled(!(0 == rowCount || _imagesCursor.isLast()));
	}

	/**
	 * Обновить название картинки
	 */
	private void UpdateTitle()
	{
		int rowCount = _imagesCursor.getCount();
		if (0 == rowCount)
		{
			_selectedImageTitle.setText(R.string.noPicture);
			return;
		}
		String title = _imagesCursor.getString(_imagesCursor
				.getColumnIndex(TITLE));
		if (0 == title.length())
		{
			_selectedImageTitle.setText(R.string.noTitle);
			return;
		}
		_selectedImageTitle.setText(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.addMenuItem:
			startActivityForResult(new Intent(this, AddActivity.class),
					ADD_IMAGE_REQUEST);
			return true;
		case R.id.removeMenuItem:
			startActivityForResult(new Intent(this, RemoveActivity.class),
					REMOVE_IMAGE_REQUEST);
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case ADD_IMAGE_REQUEST:
			if (resultCode == RESULT_OK)
			{
				Toast.makeText(this, R.string.pictureAdded, Toast.LENGTH_SHORT)
						.show();
				LoadPicturesList();
				UpdateView();
			}
			break;
		case REMOVE_IMAGE_REQUEST:
			if (resultCode == RESULT_OK)
			{
				Toast.makeText(this, R.string.pictureRemoved,
						Toast.LENGTH_SHORT).show();
				LoadPicturesList();
				UpdateView();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	
}