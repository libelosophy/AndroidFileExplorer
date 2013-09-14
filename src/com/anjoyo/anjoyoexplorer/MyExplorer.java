package com.anjoyo.anjoyoexplorer;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MyExplorer extends Activity {
	private static final String TAG = "MyExplorer";

	private static final int RESOURCE_LAYOUT = R.layout.file_list_item_4;
	private static final String ICON = "icon", FILENAME = "filename",
			ATTR = "attribute";
	private static final String[] FROM = { ICON, FILENAME, ATTR };
	private static final int[] TO = { R.id.fileIconIv, R.id.fileNameTv,
			R.id.fileAttrTv };

	private ListView listView;
	private List<Map<String, Object>> pageData = new ArrayList<Map<String, Object>>();
	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = new ListView(this);
		listView = (ListView) findViewById(R.id.fileLv);

		File exStorage = Environment.getExternalStorageDirectory(); // /mnt/sacard
		showMSG(exStorage.getPath());
		
		Log.d(TAG, exStorage.getAbsolutePath());
		if (exStorage.isDirectory()) {
			Log.d(TAG, "is a dir");
		}
		
		if (exStorage != null && exStorage.isDirectory()) {
			File[] itemsFiles = exStorage.listFiles();

			for(File item : itemsFiles){
				Log.d(TAG, item.toString());
				Map<String, Object> infoMap = new HashMap<String, Object>();
				infoMap.put(ICON, R.drawable.folder);
				try {
					infoMap.put(FILENAME, item.getCanonicalPath().toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Date modifyDate = new Date(item.lastModified());
				infoMap.put(ATTR, (modifyDate.toString()));
				pageData.add(infoMap);
			}
//			
//			for (int i = 0; i < itemsFiles.length; i++) {
//				Log.d(TAG, itemsFiles[i].toString());
//				Map<String, Object> infoMap = new HashMap<String, Object>();
//				infoMap.put(ICON, R.drawable.folder);
//				infoMap.put(FILENAME, itemsFiles[i].getPath());
//				Date modifyDate = new Date(itemsFiles[i].lastModified());
//				infoMap.put(ATTR, (modifyDate.toString()));
//				pageData.add(infoMap);
//			}

		}
		adapter = new SimpleAdapter(this, pageData, RESOURCE_LAYOUT, FROM, TO);
		listView.setAdapter(adapter);

	}

	private void showMSG(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
