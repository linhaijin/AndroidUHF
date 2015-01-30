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
import com.example.bean.ProjectInfo;
import com.example.bean.TID;
import com.example.bean.Warehouse;
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

public class CangChuBack extends Activity implements OnClickListener, OnItemSelectedListener {

	private Spinner warhouseid;
	private Spinner projectid;
	private Spinner companyid;
	private EditText returnquantitys;//�黹����
	private EditText wastequantitys;//�������
	private Button scanbutton;
	private EditText barcode;
	private Button addbutton;
	private Button savebutton;//���水ť
	private Button clearbutton;//��հ�ť
	
	private String warhouseidkey;
	private String projectidkey;
	private String companyidkey;
	
	private int quantity;
	private int onquantity;
	
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
	private final String activity = "com.example.sddsd_uhf.CangChuBack";
	DBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cangchu_back);
		SysApplication.getInstance().addActivity(this);
		db = new DBAdapter(this);
		// ��������
		Intent startServer = new Intent(CangChuBack.this,
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
		listViewData = (ListView) findViewById(R.id.data_list);
		listTID = new ArrayList<TID>();
		listMap = new ArrayList<Map<String, Object>>();
		warhouseid=(Spinner) findViewById(R.id.warhouseid);
		projectid=(Spinner) findViewById(R.id.projectid);
		companyid=(Spinner) findViewById(R.id.companyid);
		returnquantitys=(EditText) findViewById(R.id.returnquantity);
		wastequantitys=(EditText) findViewById(R.id.wastequantity);
		returnquantitys.setText("0");
		wastequantitys.setText("1");
		barcode=(EditText) findViewById(R.id.barcode);
		scanbutton=(Button) findViewById(R.id.scanbutton);
		addbutton=(Button) findViewById(R.id.addbutton);
		savebutton=(Button) findViewById(R.id.savebutton);
		clearbutton=(Button) findViewById(R.id.clearbutton);
		
		companyidDropdown();
		projectidDropdown();
		warhouseidDropdown();
		scanbutton.setOnClickListener(this);
		addbutton.setOnClickListener(this);
		savebutton.setOnClickListener(this);
		clearbutton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cangchu_back, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent toService=new Intent(CangChuBack.this,UhfService.class);
		Intent ac=new Intent();
		ac.setAction("com.example.UHFService.UhfService");
		ac.putExtra("activity", activity);
		sendBroadcast(ac);
		cmdCode = 0;
		db.open();
		switch(v.getId()){
		case R.id.scanbutton:
			cmdCode = Constants.CMD_ISO18000_6C_READ;
			toService.putExtra("cmd", cmdCode);
			toService.putExtra("startFlag", startFlag);
			startService(toService);
			break;
		case R.id.addbutton:
			String barcodetext = barcode.getText().toString().trim();
			String number = returnquantitys.getText().toString().trim();
			quantity = Integer.parseInt(number);
			if (barcodetext.length() != 0) {
				Cursor cursor = null;
				db.open();
				try {
					// ��ѯ�����ǩ�Ƿ����
					cursor = db.getTitle("StockDetail",
							new String[] { "OnQuantity" }, "BarCode",
							recvString);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.e("addTidToList", "��ѯ�����ǩ�Ƿ����" + e.toString());
				}
				if (cursor.moveToFirst()) {// �ж�
					onquantity= (int)cursor.getDouble(0);
						ContentValues contentvalue = new ContentValues();
						contentvalue.put("Key", CreateGuid.GenerateGUID());
						contentvalue.put("BarCode", recvString);
						contentvalue.put("ReturnQuantity", returnquantitys.getText().toString().trim());
						contentvalue.put("WasteQuantity", wastequantitys.getText().toString().trim());
						contentvalue.put("CreateOperater", CurrentUser.CurrentUserGuid);
						contentvalue.put("UpdateOperater", CurrentUser.CurrentUserGuid);
						if (listIndetail.isEmpty()) {
							listIndetail.add(contentvalue);
							Log.e("read tid", listIndetail.get(0).get("BarCode").toString());
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
						//addListView(listTID, barcodetext);
						addListView(listIndetail);
						
						String sqlstr="Update StockDetail set OnQuantity="+(onquantity+quantity)+" where BarCode='"+recvString+"'";
						listdetail.add(sqlstr);
						barcode.setText("");
					
				} else {

					Toast.makeText(CangChuBack.this, "�ñ�ǩ���ڿ�",
							Toast.LENGTH_SHORT).show();
				}
				cursor.close();// �ر��α꣬�ͷ���Դ
			}
			break;
		case R.id.clearbutton:
			listIndetail.removeAll(listIndetail);
			listdetail.removeAll(listdetail);
			listViewData.setAdapter(null);
			break;
		case R.id.savebutton:
				if (!listIndetail.isEmpty()) {
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
	String BuildBatchnum = "CC-GHD" + dateBatchnum;

	String date = format.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
	String stockAllocateInfokey = CreateGuid.GenerateGUID();
	ContentValues content = new ContentValues();
	content.put("Key", stockAllocateInfokey);
	content.put("BatchNumber", BuildBatchnum);// �黹��
	content.put("StockReturnDateTime", date);// �黹����
	content.put("WarhouseID", warhouseidkey);//�黹�ֿ�
	content.put("ProjectID", projectidkey);// ʹ����Ŀ
	content.put("CompanyID", companyidkey);// ʹ�ù�˾
	
	content.put("CreateOperater", CurrentUser.CurrentUserGuid);
	content.put("UpdateOperater", CurrentUser.CurrentUserGuid);
	// db.insert(content, "");
	long resultInsert = 0;
	
	// �������������ϸ��
	for (int i = 0; i < this.listIndetail.size(); i++) {
		ContentValues contentvalue = listIndetail.get(i);
		contentvalue.put("StockReturnInfoID", stockAllocateInfokey);
		// db.insert(contentvalue, "AssetInDetail");// �������Ϣ�������ݿ�
	}
	try {
		// ����ӱ��棬ֻҪ��һ�����ɹ�������ع�
		resultInsert = db.insertList(content, listIndetail,
				"StockReturnInfo", "StockReturnDetail");
		if (resultInsert ==listIndetail.size()+1) {
			Toast.makeText(this, "�˿�ɹ�", Toast.LENGTH_LONG).show();
			listIndetail.removeAll(listIndetail);
			listViewData.setAdapter(null);
		}else{
			Toast.makeText(this, "�˿�ʧ�ܣ�������", Toast.LENGTH_LONG).show();
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		System.out.println("�ִ��˿��쳣");
	}
	
	resultInsert = 0;// ���¹���
	/*// ���������ڿ���ϸ
	for (int i = 0; i < this.listMap.size(); i++) {
		String tid = listMap.get(i).get("TID").toString();
		String sqlstr="Update StockDetail set OnQuantity="+(onquantity+quantity)+" where BarCode='"+tid+"'";
		listdetail.add(sqlstr);
	}*/
	try {
		resultInsert = db.updateList(listdetail);
		if (resultInsert==this.listMap.size()) {
			Toast.makeText(this, "���浽�ڿ�ɹ�", Toast.LENGTH_LONG).show();
			listdetail.remove(listdetail);
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		Toast.makeText(this, "���浽�ڿ�ʧ�ܣ�������", Toast.LENGTH_LONG).show();
	}
	db.close();
}
@Override
public void onItemSelected(AdapterView<?> selection, View arg1, int arg2,
		long arg3) {
	// TODO Auto-generated method stub
	switch(selection.getId()){
	case R.id.companyid://ʹ�ù�˾
		companyidkey=((BranchCompanyInfo)companyid.getSelectedItem()).getKey();
		break;
	case R.id.warhouseid://�ֿ�
		warhouseidkey=((Warehouse)warhouseid.getSelectedItem()).getKey();
		break;
	case R.id.projectid://��Ŀ
		projectidkey=((ProjectInfo)projectid.getSelectedItem()).getKey();
		break;
	}
}

@Override
public void onNothingSelected(AdapterView<?> arg0) {
	// TODO Auto-generated method stub
	
}

	// ʹ�ã���˾
		public void companyidDropdown() {
			ArrayAdapter<BranchCompanyInfo> adapter;
			List<BranchCompanyInfo> listName = new ArrayList<BranchCompanyInfo>();
			db.open();
			Cursor cursor = db.getAllTitles("BranchCompanyInfo","Name");
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
			companyid.setAdapter(adapter);
			// ����¼�Spinner�¼�����
			companyid.setOnItemSelectedListener(this);
			// ����Ĭ��ֵ
			companyid.setVisibility(View.VISIBLE);
			db.close();
		}

	// ����Դ����Ŀ
		public void projectidDropdown() {
			ArrayAdapter<ProjectInfo> adapter;
			List<ProjectInfo> listName = new ArrayList<ProjectInfo>();
			db.open();
			Cursor cursor = db.getAllTitles("ProjectInfo","Name");
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
			projectid.setAdapter(adapter);
			// ����¼�Spinner�¼�����
			projectid.setOnItemSelectedListener(this);
			// ����Ĭ��ֵ
			projectid.setVisibility(View.VISIBLE);
			db.close();
		}
		
		// �ֿ����Դ
		public void warhouseidDropdown() {
			ArrayAdapter<Warehouse> adapter;
			List<Warehouse> listName = new ArrayList<Warehouse>();
			db.open();
			Cursor cursor = db.getAllTitles("Warehouse","Name");
			if (cursor.moveToFirst()) {
				do {
					Warehouse item = new Warehouse();
					item.Key = cursor.getString(cursor.getColumnIndex("Key"));
					item.Name = cursor.getString(cursor.getColumnIndex("Name"));
					listName.add(item);
				} while (cursor.moveToNext());
			}
			adapter = new ArrayAdapter<Warehouse>(this,
					android.R.layout.simple_spinner_item, listName);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// ��adapter ��ӵ�spinner��
			warhouseid.setAdapter(adapter);
			// ����¼�Spinner�¼�����
			warhouseid.setOnItemSelectedListener(this);
			// ����Ĭ��ֵ
			warhouseid.setVisibility(View.VISIBLE);
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
			map.put("COUNT", tiddata.get("ReturnQuantity"));
			idcount++;
			listMap.add(map);
		}
		// Toast.makeText(AssetGatherActivity.this, listMap.get(1).toString(),
		// Toast.LENGTH_SHORT).show();
		listViewData.setAdapter(new SimpleAdapter(CangChuBack.this,
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
	//�������ؼ�
		public boolean onKeyDown(int keyCode, KeyEvent event) {

			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				Intent intent=new Intent();
				 intent.setClass(CangChuBack.this,CangChuManagerActivity.class);
		            startActivity(intent);
			}
			return super.onKeyDown(keyCode, event);
		}

}
