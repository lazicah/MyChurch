package com.disciplesbay.latterhousehq.mychurch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;



public class PlayListActivity extends ListActivity  {
	private String path;
	// Songs list
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppCompatCallback callback = new AppCompatCallback() {
			@Override
			public void onSupportActionModeStarted(ActionMode actionMode) {
			}

			@Override
			public void onSupportActionModeFinished(ActionMode actionMode) {
			}

			@Nullable
			@Override
			public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
				return null;
			}
		};

		AppCompatDelegate delegate = AppCompatDelegate.create(this, callback);

		delegate.onCreate(savedInstanceState);
		delegate.setContentView(R.layout.playlist);

		Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
		delegate.setSupportActionBar(toolbar);
		delegate.setTitle("My Downloads");
		delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NavUtils.navigateUpFromSameTask(PlayListActivity.this);
			}
		});



		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

		SongsManager plm = new SongsManager();
		// get all songs from sdcard

		// looping through playlist
		// creating new HashMap
		// adding HashList to ArrayList
		songsListData.addAll(songsList);





		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, songsListData,
				R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
						R.id.songTitle});

		setListAdapter(adapter);

		// selecting single ListView item
		ListView lv = getListView();
		// listening to single listitem click
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting listitem index
				path = songsList.get(position).get("songPath");
				int fileTypr = path.lastIndexOf(".");
				String checkExt = path.substring(fileTypr + 1);

				if (checkExt.equalsIgnoreCase("chu")){
					File file = new File(path);
					// Starting new intent
					Intent in = new Intent(PlayListActivity.this, AndroidBuildingMusicPlayerActivity.class);
					in.putExtra("songIndex", position);
					setResult(100, in);
					// Closing PlayListView
					startActivity(in);
				}else if (checkExt.equalsIgnoreCase("chv")){
					File file = new File(path);
					// Starting new intent
					Intent in = new Intent(PlayListActivity.this, VideoPlayerActivity.class);
					in.putExtra("songIndex", position);
					setResult(100, in);
					// Closing PlayListView
					startActivity(in);

				}



			}
		});





		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
										   final int pos, long id) {
				// TODO Auto-generated method stub

				Log.v("long clicked","pos: " + pos);
				AlertDialog.Builder builder = new AlertDialog.Builder(PlayListActivity.this);
				builder.setMessage("Delete Media file?");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

						path = songsList.get(pos).get("songPath");
						File file = new File(path);
						file.delete();




					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();

					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();

				return true;
			}
		});

	}
}
