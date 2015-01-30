package com.example.sddsd_uhf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.UHFService.SysApplication;
import com.example.UHFService.UhfService;
import com.example.bean.TID;
import com.example.common.Constants;
import com.example.common.CreateGuid;
import com.example.common.CurrentUser;
import com.example.dao.DBAdapter;
import com.example.sddsd_uhf.R;
import com.example.sddsd_uhf.R.layout;
import com.example.sddsd_uhf.R.menu;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PropertyInventoryActivity extends Activity implements OnClickListener {
	private Button scanbutton;// ɨ�谴ť
	private EditText barcode;// ��ǩ��
	private Button querybutton;// ��ѯ��ť
	private EditText remark;// ��ע
	private TextView materialname;// �豸����
	private TextView materialmodel;// �豸����ͺ�
	private EditText description;//�̵�����
	private Button addbutton;// ��ѯ��ť
	private Button savebutton;// ���水ť
	private Button clearbutton;// ��հ�ť
	private String materialnamekey;// ��Ʒid
	private String materialmodelkey;// ��Ʒ����ͺ�id

	private String recvString;// ���ö������񷵻صĽ��
	private ListView listViewData;// listview��ʾ�����б�
	private MediaPlayer player;
	private boolean startFlag = false;
	private int cmdCode;
	private MyReceiver myReceiver = null;// �㲥������
	private List<Map<String, Object>> listMap;// listview����Դ
	private List<TID> listTID;// TID����
	List<ContentValues> listIndetail = new ArrayList<ContentValues>(); // �����ϸ�б�
	private final String activity = "com.example.sddsd_uhf.PropertyInventoryActivity";
	DBAdapter db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proporty_inventory);
		SysApplication.getInstance().addActivity(this);
		db = new DBAdapter(this);
		// ��������
		Intent startServer = new Intent(PropertyInventoryActivity.this, UhfService.class);
		startService(startServer);
		initView();// ��ʼ���ؼ�
		initSoundPool();

		// ע��㲥������
		myReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(activity);
		registerReceiver(myReceiver, filter);
	}
	private void initView() {
		// TODO Auto-generated method stub
		listViewData = (ListView) findViewById(R.id.data_list);// ��ȡlistview
		listTID = new ArrayList<TID>();// ʵ����һ��list
		listMap = new ArrayList<Map<String, Object>>();// ����һ�����ͼ���
		scanbutton = (Button) findViewById(R.id.scanbutton);
		querybutton = (Button) findViewById(R.id.querybutton);
		addbutton = (Button) findViewById(R.id.addbutton);
		savebutton = (Button) findViewById(R.id.button_save);
		clearbutton = (Button) findViewById(R.id.button_clear);
		barcode = (EditText) findViewById(R.id.barcode);
		remark = (EditText) findViewById(R.id.remark);
		materialname = (TextView) findViewById(R.id.materialname);
		materialmodel = (TextView) findViewById(R.id.materialmodel);
		description=(EditText) findViewById(R.id.description);
		
		scanbutton.setOnClickListener(this);
		querybutton.setOnClickListener(this);
		addbutton.setOnClickListener(this);
		savebutton.setOnClickListener(this);
		clearbutton.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent toService = new Intent(PropertyInventoryActivity.this, UhfService.class);
		Intent ac = new Intent();
		ac.setAction("com.example.UHFService.UhfService");
		ac.putExtra("activity", activity);
		sendBroadcast(ac);
		cmdCode = 0;
		db.open();
		switch (v.getId()) {
		case R.id.scanbutton:
			cmdCode = Constants.CMD_ISO18000_6C_READ;
			toService.putExtra("cmd", cmdCode);
			toService.putExtra("startFlag", startFlag);
			startService(toService);
			break;
		case R.id.querybutton:
			String barcodetext = barcode.getText().toString().trim();
			if (barcodetext.length() != 0) {
				Cursor cursor = null;
				db.open();
				try {
					// ��ѯ�����ǩ�Ƿ����
					cursor = db.getTitle("AssetDetail", new String[] {
							"MaterialID", "Quantity", "SpecificationsID" },
							"BarCode", recvString);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.e("addTidToList", "��ѯ�����ǩ�Ƿ����" + e.toString());
				}
				if (cursor.moveToFirst()) {
					materialnamekey = cursor.getString(cursor
							.getColumnIndex("MaterialID"));
					materialmodelkey = cursor.getString(cursor
							.getColumnIndex("SpecificationsID"));
					materialname.setText(db.getNameBykey("MaterialInfo",
							materialnamekey));
					materialmodel.setText(db.getNameBykey("SpecificationsInfo",
							materialmodelkey));
				} else {
					Toast.makeText(PropertyInventoryActivity.this, "�ñ�ǩ���ڿ�",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.addbutton:
			String barcodetext1 = barcode.getText().toString().trim();
			if (barcodetext1.length() != 0) {
				// �½�һ��ContentValues����������¼��ӵ�����
				ContentValues contentvalue = new ContentValues();
				contentvalue.put("Key", CreateGuid.GenerateGUID());
				contentvalue.put("BarCode", recvString);
				if (materialname.getText().toString().trim().equals("")) {
					contentvalue.put("Quantity",0);//���ڿ�
				}else{
					contentvalue.put("Quantity",1);//�ڿ�
				}
				contentvalue.put("Remark", remark.getText().toString().trim());
				contentvalue.put("CreateOperater", CurrentUser.CurrentUserGuid);
				contentvalue.put("UpdateOperater", CurrentUser.CurrentUserGuid);
				if (listIndetail.isEmpty()) {
					listIndetail.add(contentvalue);
					Log.e("read tid", listIndetail.get(0).get("BarCode")
							.toString());
				} else {
					for (int i = 0; i < listIndetail.size(); i++) {
						ContentValues mTID = listIndetail.get(i);
						// list���д�EPC
						if (recvString.equals(mTID.get("BarCode").toString())) {
							break;
						} else if (i == (listIndetail.size() - 1)) {
							// list��û�д�epc
							listIndetail.add(contentvalue);
						}
					}
				}
				addListView(listIndetail);
				barcode.setText("");

				/*
				 * } else {
				 * 
				 * Toast.makeText(PanDianActivity.this, "�ñ�ǩ���ڿ�",
				 * Toast.LENGTH_SHORT).show(); } cursor.close();// �ر��α꣬�ͷ���Դ
				 */}
			break;
		case R.id.button_save:
			if (!listIndetail.isEmpty()) {
				InsertLocalSQL();// ���浽�������ݿ�
			}
			break;
		case R.id.button_clear:
			listIndetail.removeAll(listIndetail);
			listViewData.setAdapter(null);
			break;
		}
		db.close();
	}
	private void InsertLocalSQL() {
		// TODO Auto-generated method stub
		db.open();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// ������ⵥ��
		SimpleDateFormat formatNumber = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateBatchnum = formatNumber.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		String BuildBatchnum = "ZC-PDD" + dateBatchnum;

		String date = format.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		String stockCheckInfokey = CreateGuid.GenerateGUID();
		ContentValues content = new ContentValues();
		content.put("Key", stockCheckInfokey);
		content.put("BatchNumber", BuildBatchnum);//�̵�����Description
		content.put("CheckDateTime", date);//�̵�����
		content.put("Description", description.getText().toString());//�̵�����
		content.put("CreateOperater", CurrentUser.CurrentUserGuid);
		content.put("UpdateOperater", CurrentUser.CurrentUserGuid);
		// db.insert(content, "");
		long resultInsert = 0;
		
		// �������������ϸ��
		for (int i = 0; i < this.listIndetail.size(); i++) {
			ContentValues contentvalue = listIndetail.get(i);
			contentvalue.put("AssetCheckInfoID", stockCheckInfokey);
		}
		try {
			// ����ӱ��棬ֻҪ��һ�����ɹ�������ع�
			resultInsert = db.insertList(content, listIndetail,
					"AssetCheckInfo", "AssetCheckDetail");
			if (resultInsert ==listIndetail.size()+1) {
				Toast.makeText(this, "�����ɹ�", Toast.LENGTH_SHORT).show();
				listIndetail.removeAll(listIndetail);
				listViewData.setAdapter(null);
			}else{
				Toast.makeText(this, "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("�ʲ��̵��쳣");
		}
		db.close();
	}
	// ������ݵ�ListView
		private void addListView(List<ContentValues> list) {
			// ��������ӵ�ListView
			listMap = new ArrayList<Map<String, Object>>();
			int idcount = 1;
			for (ContentValues tiddata : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ID", idcount);
				map.put("TID", tiddata.get("BarCode"));
				map.put("COUNT",tiddata.get("Quantity"));
				idcount++;
				listMap.add(map);
			}
			// Toast.makeText(AssetGatherActivity.this, listMap.get(1).toString(),
			// Toast.LENGTH_SHORT).show();
			listViewData.setAdapter(new SimpleAdapter(PropertyInventoryActivity.this,
					listMap, R.layout.listview_item, new String[] { "ID", "TID",
							"COUNT" }, new int[] { R.id.textView_id,
							R.id.textView_TID, R.id.textView_count }));

		}

		private SoundPool sp;
		private Map<Integer, Integer> suondMap;

		// ��ʼ��������
		private void initSoundPool() {
			sp = new SoundPool(1, AudioManager.STREAM_ALARM, 1);
			suondMap = new HashMap<Integer, Integer>();
			suondMap.put(1, sp.load(this, R.raw.msg, 1));
		}

		// ��������������
		private void play(int sound, int number) {
			AudioManager am = (AudioManager) this
					.getSystemService(this.AUDIO_SERVICE);
			// ���ص�ǰAlarmManager�������
			float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_ALARM);

			// ���ص�ǰAudioManager���������ֵ
			float audioCurrentVolume = am
					.getStreamVolume(AudioManager.STREAM_ALARM);
			float volumnRatio = audioCurrentVolume / audioMaxVolume;
			sp.play(suondMap.get(sound), // ���ŵ�����Id
					audioCurrentVolume, // ����������
					audioCurrentVolume, // ����������
					1, // ���ȼ���0Ϊ���
					number, // ѭ��������0�޲�ѭ����-1����Զѭ��
					1);// �ط��ٶȣ�ֵ��0.5-2.0֮�䣬1Ϊ�����ٶ�
		}

		// ������ʾ��
		private void playMedia(Context context) {
			player = MediaPlayer.create(context, R.raw.msg);
			if (player.isPlaying()) {
				// player.reset();
				player.stop();
				return;
			}

			player.start();
			// player.release();
		}

		protected void onDestroy() {
			// ж�ع㲥������
			unregisterReceiver(myReceiver);
			super.onDestroy();
		}

		// �㲥������
		private class MyReceiver extends BroadcastReceiver {
			@Override
			public void onReceive(Context context, Intent intent) {
				// ���񷵻ص�����
				recvString = intent.getStringExtra("result");
				switch (cmdCode) {
				case Constants.CMD_ISO18000_6C_READ:
					if (recvString != null) {
						// ������ʾ��
						// playMedia(ISO18000_6C_Inventory.this);
						play(1, 0);
						if (recvString != null) {
							// batchNum.setText(recvString);
							barcode.setText(recvString.toString());
						} else {
							Toast.makeText(getApplicationContext(),
									"Failure to read data", 0).show();
						}
					}
					break;

				default:
					break;
				}
			}
		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.proporty_inventory, menu);
		return true;
	}
	//�������ؼ�
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent=new Intent();
			 intent.setClass(PropertyInventoryActivity.this,ZiChanManagerActivity.class);
	            startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
}
