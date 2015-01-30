package com.example.sddsd_uhf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.UHFService.SysApplication;
import com.example.UHFService.UhfService;
import com.example.bean.BranchCompanyInfo;
import com.example.bean.CleverInfo;
import com.example.bean.DepartmentInfo;
import com.example.bean.ProjectInfo;
import com.example.bean.TID;
import com.example.common.Constants;
import com.example.common.CreateGuid;
import com.example.common.CurrentUser;
import com.example.dao.DBAdapter;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class AssetScrapStopInfo extends Activity implements OnClickListener,
		OnItemSelectedListener {

	private Spinner Cleverid;
	private Spinner BranchCompanyid;
	private Spinner mode;
	private Spinner departmentid;
	private String cleverkey;
	private String branchCompanykey;
	private String departmentidkey;
	private int modeselect;
	private Button scanButton;
	private EditText barcode;
	private Button addbutton;
	private Button savebutton;
	private Button clearbutton;
	private String recvString;// ���ö������񷵻صĽ��
	private ListView listViewData;// listview��ʾ�����б�
	private MediaPlayer player;
	private boolean startFlag = false;
	private int cmdCode;
	private MyReceiver myReceiver = null;// �㲥������
	private List<Map<String, Object>> listMap;// listview����Դ
	private List<TID> listTID;// TID����
	private final String activity = "com.example.sddsd_uhf.AssetScrapStopInfo";
	DBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asset_scrap_stop_info);
		SysApplication.getInstance().addActivity(this);
		db = new DBAdapter(this);
		// ��������
		Intent startServer = new Intent(AssetScrapStopInfo.this,
				UhfService.class);
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
		Cleverid = (Spinner) findViewById(R.id.CleverID);
		BranchCompanyid = (Spinner) findViewById(R.id.BranchCompanyID);
		mode = (Spinner) findViewById(R.id.Mode);
		departmentid = (Spinner) findViewById(R.id.departmentid);
		scanButton = (Button) findViewById(R.id.scanButton);
		barcode = (EditText) findViewById(R.id.barcode);
		addbutton = (Button) findViewById(R.id.addButton);
		savebutton = (Button) findViewById(R.id.button_save);
		clearbutton = (Button) findViewById(R.id.button_clear);
		listViewData = (ListView) findViewById(R.id.data_list);
		listTID = new ArrayList<TID>();
		listMap = new ArrayList<Map<String, Object>>();

		materialModeDropdown();// ��ʼ��״̬�б�
		// theuserCleverDropdown();//��ʼ��ְԱ�б�
		theuserCompanyDropdown();// ʹ�õ�λ�б�
		scanButton.setOnClickListener(this);
		addbutton.setOnClickListener(this);
		savebutton.setOnClickListener(this);
		clearbutton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// �������͹㲥������Ϊ��ǰactivity
		Intent toService = new Intent(AssetScrapStopInfo.this, UhfService.class);
		Intent ac = new Intent();
		ac.setAction("com.example.UHFService.UhfService");
		ac.putExtra("activity", activity);
		sendBroadcast(ac);
		cmdCode = 0;
		db.open();
		switch (v.getId()) {
		case R.id.scanButton:
			cmdCode = Constants.CMD_ISO18000_6C_READ;
			toService.putExtra("cmd", cmdCode);
			toService.putExtra("startFlag", startFlag);
			startService(toService);
			break;
		case R.id.addButton:
			String barcodetext = barcode.getText().toString().trim();
			if (barcodetext.length() != 0) {
				Cursor cursor = null;
				db.open();
				try {
					// ��ѯ�����ǩ�Ƿ����
					cursor = db.getTitle("AssetDetail",
							new String[] { "AssetState" }, "BarCode",
							recvString);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.e("addTidToList", "��ѯ�����ǩ�Ƿ����" + e.toString());
				}
				if (!cursor.moveToFirst()) {// �ж��Ƿ�Ϊ�״ι黹
					Toast.makeText(AssetScrapStopInfo.this, "����Ʒ���ڿ⣬���ܽ��д˲���",
							Toast.LENGTH_SHORT).show();
				} else {
					switch (cursor.getInt(0)) {// �ʲ�״̬(1-�ڿ�;2-����;3-��ͣ;4-����)
					case 1:
						addListView(listTID, barcodetext);
						barcode.setText("");
						break;
					case 2:
						Toast.makeText(AssetScrapStopInfo.this, "����Ʒ�ѱ�����,�����˿�",
								Toast.LENGTH_SHORT).show();
						break;
					case 3:
						Toast.makeText(AssetScrapStopInfo.this, "����Ʒ�Ѿ���ͣ",
								Toast.LENGTH_SHORT).show();
						break;
					case 4:
						Toast.makeText(AssetScrapStopInfo.this, "����Ʒ�Ѿ�����",
								Toast.LENGTH_SHORT).show();
						break;
					}
				}
				cursor.close();// �ر��α꣬�ͷ���Դ
				db.close();
			}
			break;
		case R.id.button_clear:
			listTID.removeAll(listTID);
			listViewData.setAdapter(null);
			break;
		case R.id.button_save:
			if (!listTID.isEmpty()) {
				InsertLocalSQL();// ���浽�������ݿ�
			}
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
		String BuildBatchnum = "ZC-BTBFD" + dateBatchnum;

		String date = format.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		String AssetScrapStopInfokey = CreateGuid.GenerateGUID();
		ContentValues content = new ContentValues();
		content.put("Key", AssetScrapStopInfokey);
		content.put("BatchNumber", BuildBatchnum);// �黹��
		content.put("Date", date);// �黹����
		//content.put("CleverID", cleverkey);// ʹ����
		content.put("AssetScrapStopMode", modeselect);// �豸״̬
		//content.put("BranchCompanyID", branchCompanykey);// ʹ�õ�λ
		content.put("CreateOperater", CurrentUser.CurrentUserGuid);
		content.put("UpdateOperater", CurrentUser.CurrentUserGuid);
		// db.insert(content, "");
		long resultInsert = 0;
		List<ContentValues> listIndetail = new ArrayList<ContentValues>(); // �����ϸ�б�
		// �������������ϸ��
		for (int i = 0; i < this.listMap.size(); i++) {
			String tid = listMap.get(i).get("TID").toString();
			ContentValues contentvalue = new ContentValues();
			contentvalue.put("Key", CreateGuid.GenerateGUID());
			contentvalue.put("AssetScrapStopInfoID", AssetScrapStopInfokey);
			contentvalue.put("BarCode", tid);
			contentvalue.put("Quantity", 1);
			contentvalue.put("CleverID", cleverkey);// ʹ����
			contentvalue.put("BranchCompanyID", branchCompanykey);// ʹ�õ�λ
			contentvalue.put("CreateOperater", CurrentUser.CurrentUserGuid);
			contentvalue.put("UpdateOperater", CurrentUser.CurrentUserGuid);
			// db.insert(contentvalue, "AssetInDetail");// �������Ϣ�������ݿ�
			listIndetail.add(contentvalue);
		}

		try {
			// ����ӱ��棬ֻҪ��һ�����ɹ�������ع�
			resultInsert = db.insertList(content, listIndetail,
					"AssetScrapStopInfo", "AssetScrapStopDetail");
			if (resultInsert == listIndetail.size() + 1) {
				Toast.makeText(this, "�����ɹ�", Toast.LENGTH_SHORT).show();
				listTID.removeAll(listTID);
				listViewData.setAdapter(null);
			} else {
				Toast.makeText(this, "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("���ϱ�ͣ�����쳣");
		}
		List<String> listdetail = new ArrayList<String>(); // �ڿ���ϸ�б�
		resultInsert = 0;// ���¹���
		// ���������ڿ���ϸ
		for (int i = 0; i < this.listMap.size(); i++) {
			String tid = listMap.get(i).get("TID").toString();
			String sqlstr = "Update AssetDetail set AssetState="
					+ (modeselect + 2) + " where BarCode='" + tid + "'";
			listdetail.add(sqlstr);
		}
		try {
			resultInsert = db.updateList(listdetail);
			if (resultInsert == this.listMap.size()) {
				lifecycle(listMap);
				Toast.makeText(this, "���浽�ڿ�ɹ�", Toast.LENGTH_LONG).show();
				listdetail.remove(listdetail);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("���浽�ڿ�ʧ���쳣");
		}
		db.close();
	}

	public void lifecycle(List<Map<String, Object>> listdetail) {
		// List<String> listdetail = new ArrayList<String>(); // �ڿ���ϸ�б�
		String tid = null;
		for (int i = 0; i < listdetail.size(); i++) {
			tid = listdetail.get(i).get("TID").toString();

			SimpleDateFormat formatdate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String operdate = formatdate.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
			ContentValues content = new ContentValues();
			content.put("Key", CreateGuid.GenerateGUID());
			content.put("AssetOperatingID", CurrentUser.CurrentUserGuid);
			content.put("BarCode", tid);
			if (modeselect == 1) {
				content.put("OperatingType","��ͣ" );
			} else {
				content.put("OperatingType", "����");
			}
			Cursor cursor = db
					.getTitles("AssetDetail", new String[] { "MaterialID",
							"SpecificationsID" }, "BarCode='" + tid + "'");
			if (cursor.moveToFirst()) {
				content.put("MaterialID", cursor.getString(0));
				content.put("SpecificationsID", cursor.getString(1));
			}

			content.put("OperatingDate", operdate);
			content.put("Number", 1);
			content.put("CreateOperater", CurrentUser.CurrentUserGuid);
			content.put("UpdateOperater", CurrentUser.CurrentUserGuid);
			content.put("UpdateDateTime", operdate);
			content.put("CreateDateTime", operdate);
			db.open();
			db.insert(content, "AssetLifecycleInfo");
			db.close();
		}
	}

	// ʹ���ˣ�ְԱ
	public void theuserCleverDropdown(String key) {
		ArrayAdapter<CleverInfo> adapter;
		List<CleverInfo> listName = new ArrayList<CleverInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("CleverInfo", "DepatmentID", key,
				"Name");
		if (cursor.moveToFirst()) {
			do {
				CleverInfo item = new CleverInfo();
				item.Key = cursor.getString(cursor.getColumnIndex("Key"));
				item.Name = cursor.getString(cursor.getColumnIndex("Name"));
				item.DepatmentID = cursor.getString(cursor
						.getColumnIndex("DepatmentID"));
				listName.add(item);
			} while (cursor.moveToNext());
		}
		if (listName.isEmpty()) {
			CleverInfo item = new CleverInfo();
			item.Key = "00000000-0000-0000-0000-000000000000";
			item.Name = "δְ֪Ա";
			listName.add(item);
		}
		adapter = new ArrayAdapter<CleverInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		Cleverid.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		Cleverid.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		Cleverid.setVisibility(View.VISIBLE);
		db.close();
	}

	// ʹ���ˣ���Ŀ
	public void theuserCompanyDropdown() {
		ArrayAdapter<BranchCompanyInfo> adapter;
		List<BranchCompanyInfo> listName = new ArrayList<BranchCompanyInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("BranchCompanyInfo", "Name");
		if (cursor.moveToFirst()) {
			do {
				BranchCompanyInfo item = new BranchCompanyInfo();
				item.Key = cursor.getString(cursor.getColumnIndex("Key"));
				item.Name = cursor.getString(cursor.getColumnIndex("Name"));
				listName.add(item);
			} while (cursor.moveToNext());
		}
		
		adapter = new ArrayAdapter<BranchCompanyInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		BranchCompanyid.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		BranchCompanyid.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		BranchCompanyid.setVisibility(View.VISIBLE);
		db.close();
	}

	// ʹ�ò���
	public void theuserDepartmentDropdown(String key) {
		ArrayAdapter<DepartmentInfo> adapter;
		List<DepartmentInfo> listName = new ArrayList<DepartmentInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("DepartmentInfo", "CompanyID", key,
				"Name");
		if (cursor.moveToFirst()) {
			do {
				DepartmentInfo item = new DepartmentInfo();
				item.Key = cursor.getString(cursor.getColumnIndex("Key"));
				item.Name = cursor.getString(cursor.getColumnIndex("Name"));
				item.CompanyID = cursor.getString(cursor
						.getColumnIndex("CompanyID"));
				listName.add(item);
			} while (cursor.moveToNext());
		}
		adapter = new ArrayAdapter<DepartmentInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		departmentid.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		departmentid.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		departmentid.setVisibility(View.VISIBLE);
		db.close();
	}

	public void materialModeDropdown() {
		ArrayAdapter<CharSequence> adaptertype = ArrayAdapter
				.createFromResource(this, R.array.AsseetModel,
						android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		mode.setAdapter(adaptertype);
		// ����¼�Spinner�¼�����
		mode.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		mode.setVisibility(View.VISIBLE);
	}

	@Override
	public void onItemSelected(AdapterView<?> selection, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (selection.getId()) {
		case R.id.CleverID:
			cleverkey = ((CleverInfo) Cleverid.getSelectedItem()).getKey();
			break;
		case R.id.BranchCompanyID:
			branchCompanykey = ((BranchCompanyInfo) BranchCompanyid
					.getSelectedItem()).getKey();
			theuserDepartmentDropdown(branchCompanykey);
			break;
		case R.id.departmentid:
			departmentidkey = ((DepartmentInfo) departmentid.getSelectedItem())
					.getKey();
			theuserCleverDropdown(departmentidkey);
			break;
		case R.id.Mode:
			modeselect = arg2 + 1;
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.asset_scrap_stop_info, menu);
		return true;
	}

	// ������ݵ�ListView
	private void addListView(List<TID> list, String tid) {
		// ��һ�ζ�������
		if (list.isEmpty()) {
			TID epcTag = new TID();
			epcTag.setTID(tid);
			epcTag.setCount(1);
			list.add(epcTag);
			Log.e("read tid", tid);
		} else {
			for (int i = 0; i < list.size(); i++) {
				TID mTID = list.get(i);
				// list���д�EPC
				if (tid.equals(mTID.getTID())) {
					mTID.setCount(mTID.getCount() + 1);
					list.set(i, mTID);
					break;
				} else if (i == (list.size() - 1)) {
					// list��û�д�epc
					TID newtid = new TID();
					newtid.setTID(tid);
					newtid.setCount(1);
					list.add(newtid);
				}
			}

		}
		// ��������ӵ�ListView
		listMap = new ArrayList<Map<String, Object>>();
		int idcount = 1;
		for (TID tiddata : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", idcount);
			map.put("TID", tiddata.getTID());
			map.put("COUNT", 1);
			idcount++;
			listMap.add(map);
		}
		// Toast.makeText(AssetGatherActivity.this, listMap.get(1).toString(),
		// Toast.LENGTH_SHORT).show();
		listViewData.setAdapter(new SimpleAdapter(AssetScrapStopInfo.this,
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

	// �������ؼ�
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.setClass(AssetScrapStopInfo.this,
					ZiChanManagerActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
}
