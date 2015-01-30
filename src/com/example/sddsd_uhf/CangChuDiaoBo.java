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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class CangChuDiaoBo extends Activity implements OnClickListener, OnItemSelectedListener {
	private Spinner allcotetype;//��������
	private AutoCompleteTextView allcotefromid;//����Դ
	private AutoCompleteTextView allcotetoid;//����Ŀ��
	private AutoCompleteTextView callinpeopleid;//���븺����
	private AutoCompleteTextView recallpeopleid;//����������
	private AutoCompleteTextView handledpeopleid;//����������
	//private Spinner allcoteoperater;//������
	private EditText amount;//���
	private EditText allcotequantity;//��������
	private Button scanbutton;//ɨ�谴ť
	private EditText barcode;//��ǩ��
	private Button addbutton;//��Ӱ�ť
	private Button savebutton;//���水ť
	private Button clearbutton;//��հ�ť
	
	private int quantity;
	private int onquantity;
	private String belongkey;//�ڿ�����
	
	private int allcotetypekey;//��������
	private String allcotefromidkey;//����Դ����
	private String allcotetoidkey;//����Ŀ������
	private String callinpeopleidkey;//���븺��������
	private String recallpeopleidkey;//��������������
	private String handledpeopleidkey;//��������������
	private String allcoteoperaterkey;//����Դ����
	
	private String recvString;// ���ö������񷵻صĽ��
	private ListView listViewData;// listview��ʾ�����б�
	private MediaPlayer player;
	private boolean startFlag = false;
	private int cmdCode;
	private MyReceiver myReceiver = null;// �㲥������
	private List<Map<String, Object>> listMap;// listview����Դ
	private List<TID> listTID;// TID����
	List<ContentValues> listIndetail = new ArrayList<ContentValues>(); // �����ϸ�б�
	private final String activity = "com.example.sddsd_uhf.CangChuDiaoBo";
	DBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cang_chu_diao_bo);
		SysApplication.getInstance().addActivity(this);
		db = new DBAdapter(this);
		// ��������
		Intent startServer = new Intent(CangChuDiaoBo.this,
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
		
		allcotetype=(Spinner) findViewById(R.id.allcotetype);
		allcotefromid=(AutoCompleteTextView) findViewById(R.id.allcotefromid);
		allcotetoid=(AutoCompleteTextView) findViewById(R.id.allcotetoid);
		callinpeopleid=(AutoCompleteTextView) findViewById(R.id.callinpeopleid);
		recallpeopleid=(AutoCompleteTextView) findViewById(R.id.recallpeopleid);
		handledpeopleid=(AutoCompleteTextView) findViewById(R.id.handledpeopleid);
		//allcoteoperater=(Spinner) findViewById(R.id.allcoteoperater);
		
		amount=(EditText) findViewById(R.id.amount);
		amount.setText("0.0");
		allcotequantity=(EditText) findViewById(R.id.allcotequantity);
		allcotequantity.setText("1");
		barcode=(EditText) findViewById(R.id.barcode);
		scanbutton=(Button)findViewById(R.id.scanbutton);
		addbutton=(Button)findViewById(R.id.addbutton);
		savebutton=(Button)findViewById(R.id.savebutton);
		clearbutton=(Button)findViewById(R.id.clearbutton);
		
		assetAllocattypeDropdown();
		callinpeopleidDropdown();
		recallpeopleidDropdown();
		handledpeopleidDropdown();
		scanbutton.setOnClickListener(this);
		addbutton.setOnClickListener(this);
		savebutton.setOnClickListener(this);
		clearbutton.setOnClickListener(this);
		ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,
				R.array.AsseetwarehouseType, android.R.layout.simple_spinner_dropdown_item);
		allcotetype.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		allcotetype.setOnItemSelectedListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// �������͹㲥������Ϊ��ǰactivity
		Intent toService = new Intent(CangChuDiaoBo.this, UhfService.class);
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
		case R.id.addbutton:
			String barcodetext = barcode.getText().toString().trim();
			String number = allcotequantity.getText().toString().trim();
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
					onquantity=(int)cursor.getDouble(0);
					if (onquantity >= quantity) {
						ContentValues contentvalue = new ContentValues();
						contentvalue.put("Key", CreateGuid.GenerateGUID());
						contentvalue.put("BarCode", recvString);
						contentvalue.put("Amount", amount.getText().toString().trim());
						contentvalue.put("AllocateQuantity", allcotequantity.getText().toString().trim());
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
						barcode.setText("");
					} else {
						Toast.makeText(CangChuDiaoBo.this,
								"�ò�Ʒ��ǰ�����ֻ��" + cursor.getDouble(0), Toast.LENGTH_SHORT)
								.show();
					}
				} else {

					Toast.makeText(CangChuDiaoBo.this, "�ñ�ǩ���ڿ�",
							Toast.LENGTH_SHORT).show();
				}
				cursor.close();// �ر��α꣬�ͷ���Դ
			}
			break;
		case R.id.clearbutton:
			listIndetail.removeAll(listIndetail);
			listViewData.setAdapter(null);
			break;
		case R.id.savebutton:
				if (!listIndetail.isEmpty()&&checkEdit()) {
					InsertLocalSQL();// ���浽�������ݿ�
				}
				break;
		}
		db.close();
	}
	private boolean checkEdit() {
		// TODO Auto-generated method stub
		if (allcotefromid.getText().toString().trim().equals("")||allcotetoid.getText().toString().trim().equals("")) {
			Toast.makeText(CangChuDiaoBo.this, "����Դ�����Ŀ�겻��Ϊ��", Toast.LENGTH_SHORT)
					.show();
		}else{
			if (callinpeopleid.getText().toString().trim().equals("")||recallpeopleid.getText().toString().trim().equals("")||handledpeopleid.getText().toString().trim().equals("")) {
				Toast.makeText(CangChuDiaoBo.this, "�����˺͵����˶�����Ϊ��", Toast.LENGTH_SHORT)
				.show();
			}
			return true;
		}
		return false;
	}
	
	private void InsertLocalSQL() {
		// TODO Auto-generated method stub
		db.open();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// ������ⵥ��
		SimpleDateFormat formatNumber = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateBatchnum = formatNumber.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		String BuildBatchnum = "CC-DBD" + dateBatchnum;

		String date = format.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		String stockAllocateInfokey = CreateGuid.GenerateGUID();
		ContentValues content = new ContentValues();
		content.put("Key", stockAllocateInfokey);
		content.put("BatchNumber", BuildBatchnum);// �黹��
		content.put("AllcoteDateTime", date);// �黹����
		content.put("AllcoteType", allcotetypekey);// ��������
		content.put("AllcoteToID", allcotetoidkey);// ����Ŀ��
		content.put("AllcoteFromID", allcotefromidkey);// ����Դ
		content.put("CallInPeopleID", callinpeopleidkey);// ���븺����
		content.put("RecallPeopleID", recallpeopleidkey);// ����������
		content.put("HandledPeopleID", handledpeopleidkey);// ������
		content.put("AllcoteOperater", "00000000-0000-0000-0000-000000000000");// ������allcoteoperaterkey
		content.put("CreateOperater", CurrentUser.CurrentUserGuid);
		content.put("UpdateOperater", CurrentUser.CurrentUserGuid);
		// db.insert(content, "");
		long resultInsert = 0;
		
		// �������������ϸ��
		for (int i = 0; i < this.listIndetail.size(); i++) {
			ContentValues contentvalue = listIndetail.get(i);
			contentvalue.put("StockAllocateInfoID", stockAllocateInfokey);
			// db.insert(contentvalue, "AssetInDetail");// �������Ϣ�������ݿ�
		}
		try {
			// ����ӱ��棬ֻҪ��һ�����ɹ�������ع�
			resultInsert = db.insertList(content, listIndetail,
					"StockAllocateInfo", "StockAllocateDetail");
			if (resultInsert ==listIndetail.size()+1) {
				Toast.makeText(this, "�����ɹ�", Toast.LENGTH_SHORT).show();
				listIndetail.removeAll(listIndetail);
				listViewData.setAdapter(null);
			}else{
				Toast.makeText(this, "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("�ִ������쳣");
		}
		List<String> listdetail = new ArrayList<String>(); // �ڿ���ϸ�б�
		resultInsert = 0;// ���¹���
		// ���������ڿ���ϸ
		for (int i = 0; i < this.listMap.size(); i++) {
			String tid = listMap.get(i).get("TID").toString();
			String sqlstr="Update StockDetail set WarehouseID='"+allcotetoidkey+"' where BarCode='"+tid+"'";
			listdetail.add(sqlstr);
		}
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
		/*case R.id.callinpeopleid://���븺����
			callinpeopleidkey=((CleverInfo)callinpeopleid.getSelectedItem()).getKey();
			break;
		case R.id.recallpeopleid://����������
			recallpeopleidkey=((CleverInfo)recallpeopleid.getSelectedItem()).getKey();
			break;
		case R.id.handledpeopleid://������
			handledpeopleidkey=((CleverInfo)handledpeopleid.getSelectedItem()).getKey();
			break;
			*/
		/*case R.id.allcotetoid:
			if (allcotetypekey==1) {
				allcotetoidkey=((BranchCompanyInfo)allcotetoid.getSelectedItem()).getKey();
			}else if(allcotetypekey==2){
				allcotetoidkey=((Warehouse)allcotetoid.getSelectedItem()).getKey();
			}else if(allcotetypekey==3){
				allcotetoidkey=((ProjectInfo)allcotetoid.getSelectedItem()).getKey();
				
			}
			break;
		case R.id.allcotefromid://1����˾��2�ⷿ��3��Ŀ
			if (allcotetypekey==1) {
				allcotefromidkey=((BranchCompanyInfo)allcotefromid.getSelectedItem()).getKey();
			}else if(allcotetypekey==2){
				allcotefromidkey=((Warehouse)allcotefromid.getSelectedItem()).getKey();
			}else if(allcotetypekey==3){
				allcotefromidkey=((ProjectInfo)allcotefromid.getSelectedItem()).getKey();
			}

			break;*/
		case R.id.allcotetype:
			allcotetypekey=arg2+1;//1����˾��2�ⷿ��3��Ŀ
			if(allcotetypekey==1){
				fromBranchCompanyDropdown();
				toBranchCompanyDropdown();
			}
			else if(allcotetypekey==2){
				fromwarshusDropdown(belongkey);
				towarshusDropdown();
				}
			else if(allcotetypekey==3){
				fromprojectDropdown();
				toprojectDropdown();
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	// ����Դ����˾
		public void fromBranchCompanyDropdown() {
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
			/*if (listName.isEmpty()) {
				BranchCompanyInfo itembase = new BranchCompanyInfo();
				itembase.Key = "00000000-0000-0000-0000-000000000000";
				itembase.Name ="δ֪��˾";
				listName.add(itembase);
			}*/
			adapter = new ArrayAdapter<BranchCompanyInfo>(this,
					android.R.layout.simple_dropdown_item_1line, listName);
			//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// ��adapter ��ӵ�spinner��
			allcotefromid.setAdapter(adapter);
			//��������һ���ַ�ʱ���Ϳ�ʼ����
			allcotefromid.setThreshold(1);
			// ����¼�Spinner�¼�����
			allcotefromid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					allcotefromidkey=((BranchCompanyInfo)allcotefromid.getAdapter().getItem(arg2)).getKey();
				}
				
			});
			allcotefromid.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasfoucs) {
					// TODO Auto-generated method stub
					AutoCompleteTextView view=(AutoCompleteTextView)v;
					if (hasfoucs) {
						view.showDropDown();
					}
				}
			});
			// ����Ĭ��ֵ
			allcotefromid.setVisibility(View.VISIBLE);
			db.close();
		}
		// ����Ŀ�꣬��˾
			public void toBranchCompanyDropdown() {
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
						android.R.layout.simple_dropdown_item_1line, listName);
				//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// ��adapter ��ӵ�spinner��
				allcotetoid.setAdapter(adapter);
				// ����¼�Spinner�¼�����
				allcotetoid.setThreshold(1);
				// ����¼�Spinner�¼�����
				allcotetoid.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						allcotetoidkey=((BranchCompanyInfo)allcotetoid.getAdapter().getItem(arg2)).getKey();
					}
					
				});
				allcotetoid.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasfoucs) {
						// TODO Auto-generated method stub
						AutoCompleteTextView view=(AutoCompleteTextView)v;
						if (hasfoucs) {
							view.showDropDown();
						}
					}
				});
				// ����Ĭ��ֵ
				allcotetoid.setVisibility(View.VISIBLE);
				db.close();
			}

		// ����Դ����Ŀ
			public void fromprojectDropdown() {
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
				/*if (listName.isEmpty()) {
					ProjectInfo itembase = new ProjectInfo();
					itembase.Key = "00000000-0000-0000-0000-000000000000";
					itembase.Name ="δ֪��Ŀ";
					listName.add(itembase);
				}*/
				adapter = new ArrayAdapter<ProjectInfo>(this,
						android.R.layout.simple_dropdown_item_1line, listName);
				//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// ��adapter ��ӵ�spinner��
				allcotefromid.setAdapter(adapter);
				//���ÿ�ʼ�������ַ���
				allcotefromid.setThreshold(1);
				// ����¼�Spinner�¼�����
				allcotefromid.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						allcotefromidkey=((ProjectInfo)allcotefromid.getAdapter().getItem(arg2)).getKey();
					}
				
				});
				allcotefromid.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasfoucs) {
						// TODO Auto-generated method stub
						AutoCompleteTextView view=(AutoCompleteTextView)v;
						if (hasfoucs) {
							view.showDropDown();
						}
					}
				});
				// ����Ĭ��ֵ
				allcotefromid.setVisibility(View.VISIBLE);
				db.close();
			}
			// ����Ŀ�꣬��Ŀ
					public void toprojectDropdown() {
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
								android.R.layout.simple_dropdown_item_1line, listName);
						//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						// ��adapter ��ӵ�spinner��
						allcotetoid.setAdapter(adapter);
						//���ÿ�ʼ�������ַ���
						allcotetoid.setThreshold(1);
						// ����¼�Spinner�¼�����
						allcotetoid.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,
									int arg2, long arg3) {
								// TODO Auto-generated method stub
								allcotetoidkey=((ProjectInfo)allcotetoid.getAdapter().getItem(arg2)).getKey();
							}
						
						});
						allcotetoid.setOnFocusChangeListener(new OnFocusChangeListener() {
							
							@Override
							public void onFocusChange(View v, boolean hasfoucs) {
								// TODO Auto-generated method stub
								AutoCompleteTextView view=(AutoCompleteTextView)v;
								if (hasfoucs) {
									view.showDropDown();
								}
							}
						});
						// ����Ĭ��ֵ
						allcotetoid.setVisibility(View.VISIBLE);
						db.close();
					}
			// �ֿ����Դ
			public void fromwarshusDropdown(String key) {
				ArrayAdapter<Warehouse> adapter;
				List<Warehouse> listName = new ArrayList<Warehouse>();
				db.open();
				Cursor cursor = db.getAllTitles("Warehouse","Key",key,"Name");
				if (cursor.moveToFirst()) {
					do {
						Warehouse item = new Warehouse();
						item.Key = cursor.getString(cursor.getColumnIndex("Key"));
						item.Name = cursor.getString(cursor.getColumnIndex("Name"));
						listName.add(item);
					} while (cursor.moveToNext());
				}
				adapter = new ArrayAdapter<Warehouse>(this,
						android.R.layout.simple_dropdown_item_1line, listName);
				//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// ��adapter ��ӵ�spinner��
				allcotefromid.setAdapter(adapter);
				//���ÿ�ʼ�������ַ���
				allcotefromid.setThreshold(1);
				// ����¼�Spinner�¼�����
				allcotefromid.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						allcotefromidkey=((Warehouse)allcotefromid.getAdapter().getItem(arg2)).getKey();
					}
				
				});
				allcotefromid.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasfoucs) {
						// TODO Auto-generated method stub
						AutoCompleteTextView view=(AutoCompleteTextView)v;
						if (hasfoucs) {
							view.showDropDown();
						}
					}
				});
				// ����Ĭ��ֵ
				allcotefromid.setVisibility(View.VISIBLE);
				db.close();
			}
			//�ֿ����Ŀ��
			public void towarshusDropdown() {
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
						android.R.layout.simple_dropdown_item_1line, listName);
				//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// ��adapter ��ӵ�spinner��
				allcotetoid.setAdapter(adapter);
				//���ÿ�ʼ�������ַ���
				allcotetoid.setThreshold(1);
				// ����¼�Spinner�¼�����
				allcotetoid.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						allcotetoidkey=((Warehouse)allcotetoid.getAdapter().getItem(arg2)).getKey();
					}
				
				});
				allcotetoid.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasfoucs) {
						// TODO Auto-generated method stub
						AutoCompleteTextView view=(AutoCompleteTextView)v;
						if (hasfoucs) {
							view.showDropDown();
						}
					}
				});
				// ����Ĭ��ֵ
				allcotetoid.setVisibility(View.VISIBLE);
				cursor.close();
				db.close();
			}
			//��������
		   public void assetAllocattypeDropdown() {
			ArrayAdapter<CharSequence> adaptertype = ArrayAdapter
					.createFromResource(this, R.array.AsseetwarehouseType,
							android.R.layout.simple_spinner_dropdown_item);
			// ��adapter ��ӵ�spinner��
			allcotetype.setAdapter(adaptertype);
			// ����¼�Spinner�¼�����
			allcotetype.setOnItemSelectedListener(this);
			// ����Ĭ��ֵ
			allcotetype.setVisibility(View.VISIBLE);
		}

		   
	// ���븺����
			public void callinpeopleidDropdown() {
				ArrayAdapter<CleverInfo> adapter;
				List<CleverInfo> listName = new ArrayList<CleverInfo>();
				db.open();
				Cursor cursor = db.getAllTitles("CleverInfo","Name");
				if (cursor.moveToFirst()) {
					do {
						CleverInfo item = new CleverInfo();
						item.Key = cursor.getString(cursor.getColumnIndex("Key"));
						item.Name = cursor.getString(cursor.getColumnIndex("Name"));
						item.DepatmentID=cursor.getString(cursor.getColumnIndex("DepatmentID"));
						listName.add(item);
					} while (cursor.moveToNext());
				}
				adapter = new ArrayAdapter<CleverInfo>(this,
						android.R.layout.simple_dropdown_item_1line, listName);
				//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// ��adapter ��ӵ�spinner��
				callinpeopleid.setAdapter(adapter);
				//
				callinpeopleid.setThreshold(1);
				// ����¼�Spinner�¼�����
				callinpeopleid.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						callinpeopleidkey=((CleverInfo)callinpeopleid.getAdapter().getItem(arg2)).getKey();
					}
				});
				callinpeopleid.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasfoucs) {
						// TODO Auto-generated method stub
						AutoCompleteTextView view=(AutoCompleteTextView)v;
						if (hasfoucs) {
							view.showDropDown();
						}
					}
				});
				// ����Ĭ��ֵ
				callinpeopleid.setVisibility(View.VISIBLE);
				db.close();
			}
			// �����ˣ�ְԱ
			public void handledpeopleidDropdown() {
				ArrayAdapter<CleverInfo> adapter;
				List<CleverInfo> listName = new ArrayList<CleverInfo>();
				db.open();
				Cursor cursor = db.getAllTitles("CleverInfo","Name");
				if (cursor.moveToFirst()) {
					do {
						CleverInfo item = new CleverInfo();
						item.Key = cursor.getString(cursor.getColumnIndex("Key"));
						item.Name = cursor.getString(cursor.getColumnIndex("Name"));
						item.DepatmentID = cursor.getString(cursor.getColumnIndex("DepatmentID"));
						listName.add(item);
					} while (cursor.moveToNext());
				}
				adapter = new ArrayAdapter<CleverInfo>(this,
						android.R.layout.simple_dropdown_item_1line, listName);
				//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// ��adapter ��ӵ�spinner��
				handledpeopleid.setAdapter(adapter);
				handledpeopleid.setThreshold(1);
				// ����¼�Spinner�¼�����
				handledpeopleid.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						handledpeopleidkey=((CleverInfo)handledpeopleid.getAdapter().getItem(arg2)).getKey();
					}
				});
				handledpeopleid.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasfoucs) {
						// TODO Auto-generated method stub
						AutoCompleteTextView view=(AutoCompleteTextView)v;
						if (hasfoucs) {
							view.showDropDown();
						}
					}
				});
				// ����Ĭ��ֵ
				handledpeopleid.setVisibility(View.VISIBLE);
				db.close();
			}
			// ���������ˣ�ְԱ
					public void recallpeopleidDropdown() {
						ArrayAdapter<CleverInfo> adapter;
						List<CleverInfo> listName = new ArrayList<CleverInfo>();
						db.open();
						Cursor cursor = db.getAllTitles("CleverInfo","Name");
						if (cursor.moveToFirst()) {
							do {
								CleverInfo item = new CleverInfo();
								item.Key = cursor.getString(cursor.getColumnIndex("Key"));
								item.Name = cursor.getString(cursor.getColumnIndex("Name"));
								item.DepatmentID = cursor.getString(cursor.getColumnIndex("DepatmentID"));
								listName.add(item);
							} while (cursor.moveToNext());
						}
						adapter = new ArrayAdapter<CleverInfo>(this,
								android.R.layout.simple_dropdown_item_1line, listName);
						//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						// ��adapter ��ӵ�spinner��
						recallpeopleid.setAdapter(adapter);
						//
						recallpeopleid.setThreshold(1);
						
						// ����¼�Spinner�¼�����
						recallpeopleid.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								recallpeopleidkey=((CleverInfo)recallpeopleid.getAdapter().getItem(arg2)).getKey();
							}
							
						});
						recallpeopleid.setOnFocusChangeListener(new OnFocusChangeListener() {
							@Override
							public void onFocusChange(View v, boolean hasfoucs) {
								// TODO Auto-generated method stub
								AutoCompleteTextView view=(AutoCompleteTextView)v;
								if (hasfoucs) {
									view.showDropDown();
								}
							}
						});
						// ����Ĭ��ֵ
						recallpeopleid.setVisibility(View.VISIBLE);
						cursor.close();
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
				map.put("COUNT", tiddata.get("AllocateQuantity"));
				idcount++;
				listMap.add(map);
			}
			// Toast.makeText(AssetGatherActivity.this, listMap.get(1).toString(),
			// Toast.LENGTH_SHORT).show();
			listViewData.setAdapter(new SimpleAdapter(CangChuDiaoBo.this,
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
							db.open();
							Cursor curosr=db.getTitles("StockDetail", new String[]{"WarehouseID"},"BarCode='"+recvString.toString()+"'");
							if(curosr.moveToFirst()){
								//belongtype=curosr.getInt(curosr.getColumnIndex("BelongType"));
								belongkey=curosr.getString(curosr.getColumnIndex("WarehouseID"));
								if(allcotetypekey==1){
									fromBranchCompanyDropdown();
									toBranchCompanyDropdown();
								}
								else if(allcotetypekey==2){
									fromwarshusDropdown(belongkey);
									towarshusDropdown();
									}
								else if(allcotetypekey==3){
									fromprojectDropdown();
									toprojectDropdown();
								}
							}
							db.close();
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
						 intent.setClass(CangChuDiaoBo.this,CangChuManagerActivity.class);
				            startActivity(intent);
					}
					return super.onKeyDown(keyCode, event);
				}
}
