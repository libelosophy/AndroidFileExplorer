package com.anjoyo.anjoyoexplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	protected static final String TAG = "MainActivity";
	
	private static final String ICON = "icon";
	private static final String FILENAME = "filename";
	private static final String FILEATTR = "fileattr";
	private TextView pathTv;
	private ListView filesLv;
	//ҳ�����ջ
	private LinkedList<List<Map<String, Object>>> pageStack;
	private List<Map<String, Object>> data;
	private SimpleAdapter adapter;
	public static final String[][] commSuffix = {
		{"txt", "c", "cpp", "java", "html", "css", "js"},//�ı�����
		{"jpg", "jpeg", "png", "gif", "bmp"},//ͼƬ��ʽ
		{"mp3", "wav", "ape", "flac"},//���ָ�ʽ
		{"mp4", "avi", "rmvb", "rm", "mkv"},
		{"doc", "docx", "xls", "xlsx", "ppt", "pptx"}
	};
	public static final int TEXTINDEX = 0;
	public static final int PICINDEX = 1;
	public static final int MUSICINDEX = 2;
	public static final int VIEDOINDEX = 3;
	
	private File currentPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pageStack = new LinkedList<List<Map<String,Object>>>();
		prepareUI();
	}

	private void prepareUI() {
		pathTv = (TextView)findViewById(R.id.pathTv);
		filesLv = (ListView)findViewById(R.id.fileLv);
		filesLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				//��֮ǰ���Ǹ�����б?���ջ��
				String path = currentPath + "/" 
				+((TextView)(v.findViewById(R.id.fileNameTv))).getText();
				try {
					//����ǵ�һ����Ҳ���sd����Ŀ¼��
					if(position == 0 
							&& path.startsWith(Environment.getExternalStorageDirectory()
									  .getCanonicalPath())){
						pageStack.removeLast();
						data.clear();
						data.addAll(pageStack.getLast());
						Log.i(TAG, "----------------------->");
					}else{
						List<Map<String, Object>> list = listFiles(new File(path));
						pageStack.addLast(list);
						data.clear();
						data.addAll(list);
					}
					//���Դ�е���ݷ����˱仯--��ɾ��������޸���
					adapter.notifyDataSetChanged();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Log.i(TAG, "position="+position+",path="+path+",---->"+Environment.getExternalStorageDirectory()
							  .getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		//����sd����Ŀ¼��������list��
		data = getFileListFromSdcard();
		
		//��copy
		List<Map<String, Object>> tmpList = new ArrayList<Map<String,Object>>();
		Collections.copy(data, tmpList);
		pageStack.addLast(tmpList);
		adapter = new SimpleAdapter(
						this, 
						data, 
						R.layout.file_list_item_3, 
						new String[]{
								ICON,
								FILENAME,
								FILEATTR
						}, 
						new int[]{
								R.id.fileIconIv,
								R.id.fileNameTv,
								R.id.fileAttrTv
						});
		filesLv.setAdapter(adapter);
	}

	private List<Map<String, Object>> listFiles(File pathFile) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			///mnt/sdcard/ -- /storage/sdcard
			File[] files = pathFile.listFiles();
			try {
				if(!pathFile.getCanonicalPath()
						.equalsIgnoreCase(
								Environment
								.getExternalStorageDirectory()
								.getCanonicalPath())){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ICON, R.drawable.folder);
					map.put(FILENAME, "parent");
					map.put(FILEATTR, "...");
					list.add(map);
				}else{
					int iconId = 0;
					String fileName = "";
					String attr = "";
					for(File file : files){
						Map<String, Object> map = new HashMap<String, Object>();
						if(file.isFile()){
							Log.d(TAG,"suffixes");
							
							String[] suffixes = null;
							try {
								suffixes = file.getName().split(".");
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							String suffix = suffixes[suffixes.length - 1];
							int i = 0;
							for(; i < commSuffix.length; i++){
								for(int j = 0; j < commSuffix[i].length; j++){
									if(commSuffix[i][j].equalsIgnoreCase(suffix)){
										break;
									}
								}
							}
							switch(i){
							case TEXTINDEX:
								iconId = R.drawable.default2;
								break;
							case PICINDEX:
								iconId = R.drawable.gallery;
								break;
							case MUSICINDEX:
								iconId = R.drawable.music;
								break;
							case VIEDOINDEX:
								iconId = R.drawable.video_player;
								break;
							}
							
						}else if(file.isDirectory()){
							iconId = R.drawable.folder;
						}
						fileName = file.getName();
						map.put(FILENAME, fileName);
						attr = (new Date(file.lastModified())).toString()
								+"   "+file.length();
						map.put(FILEATTR, attr);
						map.put(ICON, iconId);
						list.add(map);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Toast.makeText(this, "�Բ������sd��Ŀǰ������", Toast.LENGTH_LONG).show();
		}
		return list;
	}
	/**
	 * 
	 * @return ����sd��Ŀ¼�������ļ�
	 */
	private List<Map<String, Object>> getFileListFromSdcard() {
		currentPath = Environment.getExternalStorageDirectory();
		return listFiles(currentPath);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * ����back��
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			data.clear();
			data.addAll(pageStack.getLast());
			adapter.notifyDataSetChanged();
			break;
		}
		return true;
	}

}
