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
import com.example.bean.EPC;
import com.example.bean.ProjectInfo;
import com.example.bean.ProviderInfo;
import com.example.bean.SpecificationsInfo;
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

public class PropertyOutActivity extends Activity implements
		OnItemSelectedListener, OnClickListener {

	private Button scanButton;// ɨ�谴ť
	private EditText barcode;// ��ʾɨ�赽�ı�ǩ��
	// private EditText Depreciation;// �۾ɽ��
	// private EditText Depreciationyear;// �۾�����
	private Button addbutton;// ��ɨ�赽�ı�ǩ����ӵ�list�б�
	private Button savebutton;// ���水ť
	private Button clearbutton;// ����б�ť
	private Spinner Recipients;// �����������б�
	private Spinner Departmentid;// ��������
	private Spinner theuser;// ʹ�����б�
	private Spinner theusertype;// ʹ���������б�
	private Spinner usercompany;// ʹ���߹�˾
	private String Recipientskey;// �����ߡ���ְԱ
	private String Departmentkey;// �����ֹ�˾
	private String theuserkey;// ʹ���ߣ�����Ŀ��ְԱ
	private String userstyle;// ʹ��������
	private int userstype;// ʹ��������

	private String usercompanykey;// ʹ�ù�˾id
	private String recvString;// ���ö������񷵻صĽ��
	private ListView listViewData;// listview��ʾ�����б�
	private MediaPlayer player;
	private boolean startFlag = false;
	private int cmdCode;
	private MyReceiver myReceiver = null;// �㲥������
	private List<Map<String, Object>> listMap;// listview����Դ
	private List<TID> listTID;// TID����
	List<ContentValues> listIndetail = new ArrayList<ContentValues>(); // �����ϸ�б�
	List<String> listdetail = new ArrayList<String>(); // �ڿ���ϸ�б�
	private final String activity = "com.example.sddsd_uhf.PropertyOutActivity";
	DBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_out);
		SysApplication.getInstance().addActivity(this);
		db = new DBAdapter(this);
		// ��������
		Intent startServer = new Intent(PropertyOutActivity.this,
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
		scanButton = (Button) findViewById(R.id.scanbutton);
		barcode = (EditText) findViewById(R.id.barcodes);
		// Depreciation = (EditText) findViewById(R.id.depreciation);
		// Depreciation.setText("0.0");
		addbutton = (Button) findViewById(R.id.button_add);
		savebutton = (Button) findViewById(R.id.button_save);
		clearbutton = (Button) findViewById(R.id.button_clear);
		listViewData = (ListView) findViewById(R.id.data_list);
		usercompany = (Spinner) findViewById(R.id.usercompany);
		Recipients = (Spinner) findViewById(R.id.recipients);
		Departmentid = (Spinner) findViewById(R.id.departmentid);
		theuser = (Spinner) findViewById(R.id.theusers);
		theusertype = (Spinner) findViewById(R.id.theuserstype);
		listTID = new ArrayList<TID>();
		listMap = new ArrayList<Map<String, Object>>();
		// RecipientsDropdown();
		theusertypeDropdown();
		userCompanyDropdown();

		scanButton.setOnClickListener(this);
		addbutton.setOnClickListener(this);
		savebutton.setOnClickListener(this);
		clearbutton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.property_out, menu);
		return true;
	}

	/*
	 * ��ť�����¼�
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// �������͹㲥������Ϊ��ǰactivity
		Intent toService = new Intent(PropertyOutActivity.this,
				UhfService.class);
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
			toService.putExtra("cmd", cmdCode);
			toService.putExtra("startFlag", startFlag);
			startService(toService);
			break;
		case R.id.button_add:
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
				/*
				 * if (!cursor.moveToFirst()) { addListView(listTID,
				 * barcodetext); barcode.setText(""); } else {
				 * Toast.makeText(PropertyOutActivity.this, "����Ʒ�Ѿ�����",
				 * Toast.LENGTH_SHORT).show(); }
				 */
				if (cursor.moveToFirst()) {
					switch (cursor.getInt(0)) {// �ʲ�״̬(1-�ڿ�;2-����;3-��ͣ;4-����)
					case 1:
						ContentValues contentvalue = new ContentValues();
						contentvalue.put("Key", CreateGuid.GenerateGUID());
						contentvalue.put("BarCode", recvString);
						contentvalue.put("Quantity", 1);
						contentvalue.put("CreateOperater",
								CurrentUser.CurrentUserGuid);
						contentvalue.put("UpdateOperater",
								CurrentUser.CurrentUserGuid);
						if (listIndetail.isEmpty()) {
							listIndetail.add(contentvalue);
							Log.e("read tid", listIndetail.get(0)
									.get("BarCode").toString());
						} else {
							for (int i = 0; i < listIndetail.size(); i++) {
								ContentValues mTID = listIndetail.get(i);
								// list���д�EPC
								if (recvString.equals(mTID.get("BarCode")
										.toString())) {
									break;
								} else if (i == (listIndetail.size() - 1)) {
									// list��û�д�epc
									listIndetail.add(contentvalue);
								}
							}
						}
						// addListView(listTID, barcodetext);
						addListView(listIndetail);

						String sqlstr = "Update AssetDetail set AssetState=2 where BarCode='"
								+ recvString + "'";
						listdetail.add(sqlstr);
						barcode.setText("");
						break;
					case 2:
						Toast.makeText(PropertyOutActivity.this, "����Ʒ�Ѿ�������",
								Toast.LENGTH_SHORT).show();
						break;
					case 3:
						Toast.makeText(PropertyOutActivity.this, "����Ʒ�Ѿ���ͣ",
								Toast.LENGTH_SHORT).show();
						break;
					case 4:
						Toast.makeText(PropertyOutActivity.this, "����Ʒ�Ѿ�����",
								Toast.LENGTH_SHORT).show();
						break;
					}
				} else {
					Toast.makeText(PropertyOutActivity.this, "�ñ�ǩ���ڿ�",
							Toast.LENGTH_SHORT).show();
				}
				cursor.close();// �ر��α꣬�ͷ���Դ
				db.close();
			}
			break;
		case R.id.button_clear:
			listIndetail.removeAll(listIndetail);
			listdetail.removeAll(listdetail);
			listViewData.setAdapter(null);
			break;
		case R.id.button_save:
			if (!listIndetail.isEmpty()) {// ���浽�������ݿ�
				InsertLocalSQL();
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
		String BuildBatchnum = "ZC-CKD" + dateBatchnum;

		String date = format.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		String AssetOutInfokey = CreateGuid.GenerateGUID();
		ContentValues content = new ContentValues();
		content.put("Key", AssetOutInfokey);
		content.put("BatchNumber", BuildBatchnum);// ���ⵥ
		content.put("DepartmentID", Departmentkey);// ������˾����
		content.put("OutDate", date);// ��������
		content.put("UseModel", userstype);// ʹ�÷�ʽ(1-�ڲ�ʹ��;2-��Ŀʹ��)
		content.put("ConsumingUserID", Recipientskey);// ������
		content.put("UserID", theuserkey);// ʹ����
		content.put("UserCompanyID", usercompanykey);// ʹ�ù�˾
		// content.put("Depreciation", 0);// �۾ɽ��
		content.put("CreateOperater", CurrentUser.CurrentUserGuid);
		content.put("UpdateOperater", CurrentUser.CurrentUserGuid);
		// db.insert(content, "");
		long resultInsert = 0;

		// �������������ϸ��
		for (int i = 0; i < this.listIndetail.size(); i++) {
			// View view = listViewData.getChildAt(i);
			ContentValues contentvalue = listIndetail.get(i);
			contentvalue.put("AssetOutInfoID", AssetOutInfokey);
		}

		try {
			// ����ӱ��棬ֻҪ��һ�����ɹ�������ع�
			resultInsert = db.insertList(content, listIndetail, "AssetOutInfo",
					"AssetOutDetail");
			if (resultInsert == listIndetail.size() + 1) {
				Toast.makeText(this, "����ɹ�", Toast.LENGTH_LONG).show();
				listIndetail.removeAll(listIndetail);
				listViewData.setAdapter(null);
			} else {
				Toast.makeText(this, "����ʧ�ܣ�������", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("�ʲ������쳣");
		}

		resultInsert = 0;// ���¹���
		/*
		 * // ���������ڿ���ϸ for (int i = 0; i < this.listMap.size(); i++) { String
		 * tid = listMap.get(i).get("TID").toString(); String
		 * sqlstr="Update AssetDetail set AssetState=2 where BarCode='"+tid+"'";
		 * listdetail.add(sqlstr); }
		 */
		try {
			resultInsert = db.updateList(listdetail);
			if (resultInsert == this.listMap.size()) {
				lifecycle(listMap);
				Toast.makeText(this, "�����ڿ�ɹ�", Toast.LENGTH_LONG).show();
				listdetail.remove(listdetail);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "���µ��ڿ�ʧ�ܣ�������", Toast.LENGTH_LONG).show();
		}
		db.close();
	}

	public void lifecycle(List<Map<String, Object>> listdetail) {
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
			content.put("OperatingType", "����");
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

	// ������ݵ�ListView
	private void addListView(List<ContentValues> list) {
		// ��������ӵ�ListView
		listMap = new ArrayList<Map<String, Object>>();
		int idcount = 1;
		for (ContentValues tiddata : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", idcount);
			map.put("TID", tiddata.get("BarCode"));
			map.put("COUNT", tiddata.get("Quantity"));
			idcount++;
			listMap.add(map);
		}
		// Toast.makeText(AssetGatherActivity.this, listMap.get(1).toString(),
		// Toast.LENGTH_SHORT).show();
		listViewData.setAdapter(new SimpleAdapter(PropertyOutActivity.this,
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

	// ������
	public void RecipientsDropdown(String key) {
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
			item.Name = "δ֪���";
			listName.add(item);
		}
		adapter = new ArrayAdapter<CleverInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		Recipients.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		Recipients.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		Recipients.setVisibility(View.VISIBLE);
		db.close();
	}

	// ��������������
	public void RecipientstypeDropdown(String key) {
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
		Departmentid.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		Departmentid.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		Departmentid.setVisibility(View.VISIBLE);
		db.close();
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
			item.Name = "δ֪���";
			listName.add(item);
		}
		adapter = new ArrayAdapter<CleverInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		theuser.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		theuser.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		theuser.setVisibility(View.VISIBLE);
		db.close();
	}

	// ʹ���ˣ���Ŀ
	public void theuserProjectDropdown() {
		ArrayAdapter<ProjectInfo> adapter;
		List<ProjectInfo> listName = new ArrayList<ProjectInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("ProjectInfo", "Name");
		if (cursor.moveToFirst()) {
			do {
				ProjectInfo item = new ProjectInfo();
				item.Key = cursor.getString(cursor.getColumnIndex("Key"));
				item.Name = cursor.getString(cursor.getColumnIndex("Name"));
				listName.add(item);
			} while (cursor.moveToNext());
		}

		adapter = new ArrayAdapter<ProjectInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		theuser.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		theuser.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		theuser.setVisibility(View.VISIBLE);
		db.close();
	}

	// ʹ�ù�˾
	public void userCompanyDropdown() {
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
		usercompany.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		usercompany.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		usercompany.setVisibility(View.VISIBLE);
		db.close();
	}

	public void theusertypeDropdown() {
		ArrayAdapter<CharSequence> adaptertype = ArrayAdapter
				.createFromResource(this, R.array.AsseetUserTypes,
						android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		theusertype.setAdapter(adaptertype);
		// ����¼�Spinner�¼�����
		theusertype.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		theusertype.setVisibility(View.VISIBLE);
	}

	@Override
	public void onItemSelected(AdapterView<?> selectitem, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (selectitem.getId()) {
		case R.id.recipients:
			Recipientskey = ((CleverInfo) Recipients.getSelectedItem())
					.getKey();
			/*
			 * RecipientstypeDropdown(((CleverInfo)
			 * Recipients.getSelectedItem()) .getDepatmentID());
			 */
			break;
		case R.id.usercompany:
			usercompanykey = ((BranchCompanyInfo) usercompany.getSelectedItem())
					.getKey();
			RecipientstypeDropdown(usercompanykey);
			break;
		case R.id.departmentid:
			Departmentkey = ((DepartmentInfo) Departmentid.getSelectedItem())
					.getKey();

			RecipientsDropdown(Departmentkey);
			if (userstype == 1) {
				// ѡ��ְԱ
				theuserCleverDropdown(Departmentkey);
			}
			break;
		case R.id.theusers:
			// �ж�ΪְԱ������Ŀ

			if (userstyle == "CleverInfo") {
				theuserkey = ((CleverInfo) theuser.getSelectedItem()).getKey();
			}
			if (userstyle == "ProjectInfo") {
				theuserkey = ((ProjectInfo) theuser.getSelectedItem()).getKey();
			}
			break;
		case R.id.theuserstype:
			userstype = arg2 + 1;
			if (arg2 == 0) {
				userstyle = "CleverInfo";
				// ѡ��ְԱ
				theuserCleverDropdown(Departmentkey);
			}
			if (arg2 == 1) {
				userstyle = "ProjectInfo";
				// ѡ����Ŀ
				theuserProjectDropdown();
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

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
			/*
			 * case Constants.CMD_ISO18000_6C_INVENTORY: if (recvString != null)
			 * { // ������ʾ�� // playMedia(ISO18000_6C_Inventory.this); play(1, 0);
			 * addListView(listTID, recvString); } break;
			 */
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
			intent.setClass(PropertyOutActivity.this,
					ZiChanManagerActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
}
