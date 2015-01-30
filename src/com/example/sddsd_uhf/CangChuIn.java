package com.example.sddsd_uhf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.UHFService.SysApplication;
import com.example.UHFService.UhfService;
import com.example.bean.AccountCompanyInfo;
import com.example.bean.BranchCompanyInfo;
import com.example.bean.CleverInfo;
import com.example.bean.MaterialInfo;
import com.example.bean.MaterialModelInfo;
import com.example.bean.ProviderInfo;
import com.example.bean.SpecificationsInfo;
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
import android.content.SharedPreferences;
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
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class CangChuIn extends Activity implements OnClickListener, OnItemSelectedListener {

	private Spinner materialname;// ��Ʒ����
	private Spinner specificationsid;// ����ͺ�
	private Spinner materialcategory;// ��Ʒ���
	private AutoCompleteTextView providerid;// ��Ӧ��
	private AutoCompleteTextView warhouseid;// �ֿ�
	private Spinner belongid;// ������
	private Spinner purchaseid;// �ɹ���
	private Spinner stocktype;// �������
	private CheckBox ispayment;
	private String materialnamekey;
	private String specificationsidkey;
	private String materialcategorykey;
	private String provideridkey;
	private String warhouseidkey;
	private String belongidkey;
	private String purchaseidkey;
	private int stocktypekey;
	
	
	private EditText price;// ����
	private EditText inquantity;// ����
	//private EditText amount;// �ܼ�
	private EditText invoicenum;// ��Ʊ��Ϣ
	private EditText remark;// ��ע
	private Button scanButton;// ɨ�谴ť
	private EditText barcode;// ��ǩ��
	private Button addbutton;// ��ӵ��б�ť
	private Button savebutton;// ���水ť
	private Button cancelbutton;// ȡ��

	private String recvString;// ���ö������񷵻صĽ��
	private ListView listViewData;// listview��ʾ�����б�
	private MediaPlayer player;
	private boolean startFlag = false;
	private int cmdCode;
	private MyReceiver myReceiver = null;// �㲥������
	private List<Map<String, Object>> listMap;// listview����Դ
	private List<TID> listTID;// TID����
	List<ContentValues> listdetail = new ArrayList<ContentValues>(); // �ڿ���ϸ�б�
	List<ContentValues> listIndetail = new ArrayList<ContentValues>(); // �����ϸ�б�
	List<String> listdetailstr = new ArrayList<String>(); // �ڿ���ϸ�б�
	private final String activity = "com.example.sddsd_uhf.CangChuIn";
	DBAdapter db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cang_chu_in);
		SysApplication.getInstance().addActivity(this);
		db = new DBAdapter(this);
		// ��������
		Intent startServer = new Intent(CangChuIn.this,
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
		materialname = (Spinner) findViewById(R.id.materialname);
		specificationsid = (Spinner) findViewById(R.id.specificationsid);
		materialcategory = (Spinner) findViewById(R.id.materialcategory);
		providerid = (AutoCompleteTextView) findViewById(R.id.providerid);
		warhouseid = (AutoCompleteTextView) findViewById(R.id.warhouseid);
		belongid = (Spinner) findViewById(R.id.belongid);
		purchaseid = (Spinner) findViewById(R.id.purchaseid);
		stocktype = (Spinner) findViewById(R.id.stocktype);
		ispayment=(CheckBox)findViewById(R.id.ispayment);
		price = (EditText) findViewById(R.id.price);
		price.setText("0.0");
		inquantity = (EditText) findViewById(R.id.inquantity);
		inquantity.setText("1");
		/*amount = (EditText) findViewById(R.id.amount);
		amount.setText("0.0");*/
		invoicenum = (EditText) findViewById(R.id.invoicenum);
		remark = (EditText) findViewById(R.id.remark);
		barcode = (EditText) findViewById(R.id.barcode);
		
		listViewData = (ListView) findViewById(R.id.data_list);
		listTID = new ArrayList<TID>();
		listMap = new ArrayList<Map<String, Object>>();
		
		scanButton = (Button) findViewById(R.id.scanButton);
		addbutton = (Button) findViewById(R.id.addbutton);
		savebutton = (Button) findViewById(R.id.savebutton);
		cancelbutton = (Button) findViewById(R.id.cancelbutton);

		materialcategoryDropdown();
		provideridDropdown();
		warhouseidDropdown();
		purchaseidDropdown();
		belongidDropdown();
		
		scanButton.setOnClickListener(this);
		addbutton.setOnClickListener(this);
		savebutton.setOnClickListener(this);
		cancelbutton.setOnClickListener(this);
		
		ArrayAdapter<CharSequence> adaptertype = ArrayAdapter
				.createFromResource(this, R.array.AsseetTypes,
						android.R.layout.simple_spinner_dropdown_item);
		stocktype.setAdapter(adaptertype);
		// ����¼�Spinner�¼�����
		// Statespinner.setOnItemSelectedListener(this);
		stocktype.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// �������͹㲥������Ϊ��ǰactivity
				Intent toService = new Intent(CangChuIn.this, UhfService.class);
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
			toService.putExtra("cmd", cmdCode);
			toService.putExtra("startFlag", startFlag);
			startService(toService);
			break;
		case R.id.addbutton:
			String barcodetext = barcode.getText().toString().trim();
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
				
				System.out.println(provideridkey+"���");
				if (!cursor.moveToFirst()) {// �ж��Ƿ�Ϊ���
					ContentValues contentvalue = new ContentValues();
					contentvalue.put("Key", CreateGuid.GenerateGUID());
					contentvalue.put("MaterialID", materialnamekey);
					contentvalue.put("BarCode", recvString);
					contentvalue.put("ProviderID", provideridkey);
					double inquantitys=0;
					double prices=0;
					
					//�ܽ��������
					if(inquantity.getText().toString().trim().equals("")){
						
					}else{
						inquantitys=Double.parseDouble(inquantity.getText().toString().trim());
					}
					if(price.getText().toString().trim().equals("")){
						
					}else{
						   prices=Double.parseDouble(price.getText().toString().trim());
					}
					contentvalue.put("Amount",inquantitys*prices);
					contentvalue.put("MaterialModelID", materialcategorykey);
					contentvalue.put("StockType", stocktypekey);
					contentvalue.put("INQuantity", inquantity.getText().toString().trim());
					contentvalue.put("UnitPrice", price.getText().toString().trim());// ����������
					contentvalue.put("SpecificationsID", specificationsidkey);// ��������ͺţ���ȡid
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
					addListView(listIndetail);//�������ϸ��ӵ��б���
					
					ContentValues contentvaluedetail = new ContentValues();
					contentvaluedetail.put("Key", CreateGuid.GenerateGUID());
					contentvaluedetail.put("BarCode", recvString);
					contentvaluedetail.put("StockType", stocktypekey);
					contentvaluedetail.put("OnQuantity", inquantity.getText().toString().trim());
					contentvaluedetail.put("MaterialID", materialnamekey);
					//contentvaluedetail.put("WarehouseID", warhouseidkey);
					contentvaluedetail.put("SpecificationsID", specificationsidkey);// ��������ͺţ���ȡid
					contentvaluedetail.put("CreateOperater", CurrentUser.CurrentUserGuid);
					contentvaluedetail.put("UpdateOperater", CurrentUser.CurrentUserGuid);
					// resultInsert= db.insert(contentvalue, "AssetDetail");
					if (listdetail.isEmpty()) {
						listdetail.add(contentvaluedetail);
						Log.e("read tid", listdetail.get(0).get("BarCode").toString());
					} else {
					for (int i = 0; i < listdetail.size(); i++) {
						ContentValues mTID = listdetail.get(i);
						// list���д�EPC
						if (recvString.equals(mTID.get("BarCode").toString())) {
							break;
						} else if (i == (listdetail.size() - 1)) {
							// list��û�д�epc
							listdetail.add(contentvaluedetail);
						}
					}
					}
					barcode.setText("");
				}else{
					double onquantity=cursor.getDouble(0);
					ContentValues contentvalue = new ContentValues();
					contentvalue.put("Key", CreateGuid.GenerateGUID());
					contentvalue.put("MaterialID", materialnamekey);
					contentvalue.put("BarCode", recvString);
					contentvalue.put("ProviderID", provideridkey);
					double inquantitys=0;
					double prices=0;
					
					//�ܽ��������
					if(inquantity.getText().toString().trim().equals("")){
						
					}else{
						inquantitys=Double.parseDouble(inquantity.getText().toString().trim());
					}
					if(price.getText().toString().trim().equals("")){
						
					}else{
						   prices=Double.parseDouble(price.getText().toString().trim());
					}
					contentvalue.put("Amount",inquantitys*prices);
					contentvalue.put("MaterialModelID", materialcategorykey);
					contentvalue.put("StockType", stocktypekey);
					contentvalue.put("INQuantity", inquantity.getText().toString().trim());
					contentvalue.put("UnitPrice", price.getText().toString().trim());// ����������
					contentvalue.put("SpecificationsID", specificationsidkey);// ��������ͺţ���ȡid
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
					addListView(listIndetail);//�������ϸ��ӵ��б���
					String sqlstr="Update StockDetail set OnQuantity="+(onquantity+inquantitys)+" where BarCode='"+recvString+"'";
					listdetailstr.add(sqlstr);
					barcode.setText("");
				}
				cursor.close();// �ر��α꣬�ͷ���Դ
				
			}
			break;
		case R.id.savebutton:
			if (!listIndetail.isEmpty()&&checkEdit()) {
				InsertLocalSQL();// ���浽�������ݿ�
			}
			break;
		case R.id.cancelbutton:
			listIndetail.removeAll(listIndetail);
			listdetail.removeAll(listdetail);
			listViewData.setAdapter(null);
			break;
		}
		db.close();
	}
	private boolean checkEdit() {
		// TODO Auto-generated method stub
		if (providerid.getText().toString().trim().equals("")) {
			Toast.makeText(CangChuIn.this, "��Ӧ�̲���Ϊ��", Toast.LENGTH_SHORT)
					.show();
		}else{
			if (warhouseid.getText().toString().trim().equals("")) {
				Toast.makeText(CangChuIn.this, "�ⷿ����Ϊ��", Toast.LENGTH_SHORT)
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
		String BuildBatchnum = "CC-RKD" + dateBatchnum;

		String date = format.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		String StockInInfokey = CreateGuid.GenerateGUID();
		ContentValues content = new ContentValues();
		content.put("Key", StockInInfokey);
		content.put("BatchNumber", BuildBatchnum);
		content.put("BelongID", belongidkey);
		content.put("StockInDateTime", date);
		content.put("InvoiceNumber", invoicenum.getText().toString().trim());
		if (warhouseidkey==null) {
			warhouseidkey=((Warehouse) warhouseid.getAdapter().getItem(0)).getKey();
			//Toast.makeText(this, warhouseidkey, Toast.LENGTH_SHORT).show();
		}
		content.put("WarhouseID", warhouseidkey);// �ܽ��
		content.put("PurchaseID", purchaseidkey);// �ɹ���
		content.put("IsPayment", ispayment.isChecked()==true?"true":"false");// �Ƿ񸶿�
		content.put("Remark", remark.getText().toString().trim());//��ע
		content.put("CreateOperater", CurrentUser.CurrentUserGuid);
		content.put("UpdateOperater", CurrentUser.CurrentUserGuid);
		// db.insert(content, "AssetInInfo");// ���������Ϣ���ݿ�
		long resultInsert = 0;
		
		// �������������ϸ��
		for (int i = 0; i < this.listIndetail.size(); i++) {
			// View view = listViewData.getChildAt(i);
			ContentValues contentvalue = listIndetail.get(i);
			contentvalue.put("StockInInfoID", StockInInfokey);
		}

		try {
			
			// ����ӱ��棬ֻҪ��һ�����ɹ�������ع�
			resultInsert = db.insertList(content, listIndetail, "StockInInfo",
					"StockInDetail");
			if (resultInsert ==listIndetail.size()+1) {
				Toast.makeText(this, "���ɹ�", Toast.LENGTH_SHORT).show();
				listIndetail.removeAll(listIndetail);
				listViewData.setAdapter(null);
			}else{
				Toast.makeText(this, "���ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("�ִ�����쳣");
		}
		
		resultInsert = 0;// ���¹���
		// �������������ϸ��
				for (int i = 0; i < this.listdetail.size(); i++) {
					// View view = listViewData.getChildAt(i);
					ContentValues contentvalue = listdetail.get(i);
					contentvalue.put("WarehouseID", warhouseidkey);
				}
		// ���������ڿ���ϸ
		try {
			resultInsert = db.insertList(listdetail, "StockDetail");
			if (resultInsert > 0) {
				Toast.makeText(this, "���浽�ڿ�ɹ�", Toast.LENGTH_SHORT).show();
				listdetail.remove(listdetail);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "���浽�ڿ�ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
		}
		resultInsert = 0;// ���¹���
		// ���������ڿ���ϸ
		try {
			resultInsert = db.updateList(listdetailstr);
			if (resultInsert > 0) {
				Toast.makeText(this, "�����ڿ�ɹ�", Toast.LENGTH_SHORT).show();
				listdetailstr.remove(listdetailstr);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "�����ڿ�ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
		}
		db.close();

	}
	@Override
	public void onItemSelected(AdapterView<?> selection, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch(selection.getId()){
		case R.id.stocktype:
			if (arg2 == 0) {
				stocktypekey = 1;
			}
			if (arg2 == 1) {
				stocktypekey = 2;
			}
			break;
		//case R.id.providerid:
			//provideridkey = ((ProviderInfo) providerid.getSelectedItem())
			//		.getKey();
			//break;
		case R.id.materialname:
			materialnamekey = ((MaterialInfo) materialname.getSelectedItem())
					.getKey();
			specificationsidDropdown(materialnamekey);
			break;
		case R.id.belongid:
			belongidkey = ((BranchCompanyInfo) belongid.getSelectedItem())
					.getKey();
			break;
		case R.id.purchaseid:
			purchaseidkey = ((AccountCompanyInfo) purchaseid
					.getSelectedItem()).getKey();
			break;
		case R.id.materialcategory:
			materialcategorykey = ((MaterialModelInfo) materialcategory
					.getSelectedItem()).getKey();
			materialnameDropdown(materialcategorykey);
			break;
		case R.id.specificationsid:
			specificationsidkey = ((SpecificationsInfo) specificationsid
					.getSelectedItem()).getKey();
			break;
		//case R.id.warhouseid:
			//warhouseidkey = ((Warehouse) warhouseid.getAdapter().getItem(0)).getKey();
			//break;
		/*case R.id.newproviderid:
			newproviderkey = ((ProviderInfo) newprovider.getSelectedItem()).getKey();
			break;*/
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	 //��ѡ���Ʒ���
	public void materialcategoryDropdown() {
		ArrayAdapter<MaterialModelInfo> adapter;
		List<MaterialModelInfo> listName = new ArrayList<MaterialModelInfo>();
		db.open();
		
		//78af83a7-c9bb-4382-bfc4-23b0ac736514
		Cursor cursor = db.getAllTitles("MaterialModelInfo","ParentID","78af83a7-c9bb-4382-bfc4-23b0ac736514","Name");
		if (cursor.moveToFirst()) {
			do {
				MaterialModelInfo item1 = new MaterialModelInfo();
				Cursor cursorsecond = db.getAllTitles("MaterialModelInfo","ParentID",cursor.getString(cursor.getColumnIndex("Key")),"Name");
				if (cursorsecond.moveToFirst()) {
					do{
						MaterialModelInfo item2 = new MaterialModelInfo();
						Cursor cursorlast = db.getAllTitles("MaterialModelInfo","ParentID",cursorsecond.getString(cursorsecond.getColumnIndex("Key")),"Name");
						if(cursorlast.moveToFirst()){
							
							do{
								MaterialModelInfo item3 = new MaterialModelInfo();
								Cursor cursorlast2 = db.getAllTitles("MaterialModelInfo","ParentID",cursorlast.getString(cursorlast.getColumnIndex("Key")),"Name");
								if(cursorlast2.moveToFirst()){
									//Toast.makeText(this, "��������", Toast.LENGTH_SHORT).show();
									System.out.println("����𣬱߽糬����");
								}else{
									item3.Key = cursorlast.getString(cursorlast.getColumnIndex("Key"));
									item3.Name = cursorlast.getString(cursorlast.getColumnIndex("Name"));
									listName.add(item3);
								}
								cursorlast2.close();
							}while(cursorlast.moveToNext());
							
						}else{
							item2.Key = cursorsecond.getString(cursorsecond.getColumnIndex("Key"));
							item2.Name = cursorsecond.getString(cursorsecond.getColumnIndex("Name"));
							listName.add(item2);
						}
						cursorlast.close();
					}while(cursorsecond.moveToNext());
				}else{
					item1.Key = cursor.getString(cursor.getColumnIndex("Key"));
					item1.Name = cursor.getString(cursor.getColumnIndex("Name"));
					listName.add(item1);
				}
				cursorsecond.close();
			} while (cursor.moveToNext());
		}
		cursor.close();
		adapter = new ArrayAdapter<MaterialModelInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		materialcategory.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		materialcategory.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		materialcategory.setVisibility(View.VISIBLE);
		db.close();
	}
	   //��Ʒ
		public void materialnameDropdown(String key) {
			ArrayAdapter<MaterialInfo> adapter;
			List<MaterialInfo> listName = new ArrayList<MaterialInfo>();
			db.open();
			Cursor cursor = db.getAllTitles("MaterialInfo","MaterialModelID",key,"Name");
			if (cursor.moveToFirst()) {
				do {
					MaterialInfo item = new MaterialInfo();
					item.Key = cursor.getString(cursor.getColumnIndex("Key"));
					item.Name = cursor.getString(cursor.getColumnIndex("Name"));
					listName.add(item);
				} while (cursor.moveToNext());
			}
			adapter = new ArrayAdapter<MaterialInfo>(this,
					android.R.layout.simple_spinner_item, listName);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// ��adapter ��ӵ�spinner��
			materialname.setAdapter(adapter);
			// ����¼�Spinner�¼�����
			materialname.setOnItemSelectedListener(this);
			// ����Ĭ��ֵ
			materialname.setVisibility(View.VISIBLE);
			db.close();
		}
		 //��Ʒ����ͺ�
		public void provideridDropdown() {
			ArrayAdapter<ProviderInfo> adapter;
			List<ProviderInfo> listName = new ArrayList<ProviderInfo>();
			db.open();
			Cursor cursor = db.getAllTitles("ProviderInfo","Type",1,"Name");
			if (cursor.moveToFirst()) {
				do {
					ProviderInfo item = new ProviderInfo();
					item.Key = cursor.getString(cursor.getColumnIndex("Key"));
					item.Name = cursor.getString(cursor.getColumnIndex("Name"));
					listName.add(item);
				} while (cursor.moveToNext());
			}
			adapter = new ArrayAdapter<ProviderInfo>(this,
					android.R.layout.simple_dropdown_item_1line, listName);
			//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// ��adapter ��ӵ�spinner��
			providerid.setAdapter(adapter);
			//���õ�����һ���ַ�֮��Ϳ�ʼ����
			providerid.setThreshold(1);
			// ����¼�Spinner�¼�����
			providerid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					provideridkey = ((ProviderInfo) providerid.getAdapter().getItem(arg2))
									.getKey();
					//Toast.makeText(CangChuIn.this,((ProviderInfo)providerid.getAdapter().getItem(arg2)).getKey(),Toast.LENGTH_LONG).show();
				}
			});
			providerid.setOnFocusChangeListener(new OnFocusChangeListener() {
				
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
			providerid.setVisibility(View.VISIBLE);
			cursor.close();
			db.close();
		}
		 //��Ʒ����ͺ�warhouseid
		public void specificationsidDropdown(String key) {
			ArrayAdapter<SpecificationsInfo> adapter;
			List<SpecificationsInfo> listName = new ArrayList<SpecificationsInfo>();
			db.open();
			Cursor cursor = db.getAllTitles("SpecificationsInfo","MaterialID",key,"Name");
			if (cursor.moveToFirst()) {
				do {
					SpecificationsInfo item = new SpecificationsInfo();
					item.Key = cursor.getString(cursor.getColumnIndex("Key"));
					item.Name = cursor.getString(cursor.getColumnIndex("Name"));
					//item.MaterialID=cursor.getString(cursor.getColumnIndex("MaterialID"));
					//item.Unit=cursor.getString(cursor.getColumnIndex("Unit"));
					listName.add(item);
				} while (cursor.moveToNext());
			}
			if (listName.isEmpty()) {
				SpecificationsInfo item = new SpecificationsInfo();
				item.Key ="00000000-0000-0000-0000-000000000000";
				item.Name = "δ֪���";
				listName.add(item);
			}
			adapter = new ArrayAdapter<SpecificationsInfo>(this,
					android.R.layout.simple_spinner_item, listName);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// ��adapter ��ӵ�spinner��
			specificationsid.setAdapter(adapter);
			// ����¼�Spinner�¼�����
			specificationsid.setOnItemSelectedListener(this);
			// ����Ĭ��ֵ
			specificationsid.setVisibility(View.VISIBLE);
			db.close();
		}
		 //������˾
		public void belongidDropdown() {
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
			belongid.setAdapter(adapter);
			// ����¼�Spinner�¼�����
			belongid.setOnItemSelectedListener(this);
			// ����Ĭ��ֵ
			belongid.setVisibility(View.VISIBLE);
			db.close();
		}
		 //��Ʒ����ͺ�purchaseid
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
					android.R.layout.simple_dropdown_item_1line, listName);
			//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// ��adapter ��ӵ�spinner��
			warhouseid.setAdapter(adapter);
			warhouseid.setThreshold(1);
			// ����¼�Spinner�¼�����
			//warhouseid.setOnItemSelectedListener(this);
			warhouseid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					//����ѡ�������б��¼�
					warhouseidkey = ((Warehouse) warhouseid.getAdapter().getItem(arg2)).getKey();
					
				}
				
			});
			// ����Ĭ��ֵ
			warhouseid.setVisibility(View.VISIBLE);
			db.close();
		}
		 //�ɹ���˾
		public void purchaseidDropdown() {
			ArrayAdapter<AccountCompanyInfo> adapter;
			List<AccountCompanyInfo> listName = new ArrayList<AccountCompanyInfo>();
			db.open();
			Cursor cursor = db.getAllTitles("AccountCompanyInfo","Name");
			if (cursor.moveToFirst()) {
				do {
					AccountCompanyInfo item = new AccountCompanyInfo();
					item.Key = cursor.getString(cursor.getColumnIndex("Key"));
					item.Name = cursor.getString(cursor.getColumnIndex("Name"));
					listName.add(item);
				} while (cursor.moveToNext());
			}
			adapter = new ArrayAdapter<AccountCompanyInfo>(this,
					android.R.layout.simple_spinner_item, listName);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// ��adapter ��ӵ�spinner��
			purchaseid.setAdapter(adapter);
			// ����¼�Spinner�¼�����
			purchaseid.setOnItemSelectedListener(this);
			// ����Ĭ��ֵ
			purchaseid.setVisibility(View.VISIBLE);
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
			map.put("COUNT", tiddata.get("INQuantity"));
			idcount++;
			listMap.add(map);
		}
		listViewData.setAdapter(new SimpleAdapter(CangChuIn.this,
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
					 intent.setClass(CangChuIn.this,CangChuManagerActivity.class);
			            startActivity(intent);
				}
				return super.onKeyDown(keyCode, event);
			}
	
}
