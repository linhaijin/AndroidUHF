package com.example.sddsd_uhf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.UHFService.SysApplication;
import com.example.UHFService.UhfService;
import com.example.bean.AccountCompanyInfo;
import com.example.bean.EPC;
import com.example.bean.MaterialInfo;
import com.example.bean.MaterialModelInfo;
import com.example.bean.ProviderInfo;
import com.example.bean.SpecificationsInfo;
import com.example.common.Constants;
import com.example.common.CreateGuid;
import com.example.common.CurrentUser;
import com.example.dao.DBAdapter;

public class AssetGatherActivity extends Activity implements
		OnItemSelectedListener {
	private String providerkey;// ��Ӧ������ֵ
	private String belongkey = "00000000-0000-0000-0000-000000000000";// ����������ֵ��ʼ��
	private String materialkey;// ��Ʒ����ֵ
	private String materialTypekey;// ��Ʒ�������ֵ
	private String procurementkey;// �ɹ�������ֵ
	private String specificationskey = "";// ����ͺ�����ֵ
	private String newproviderkey;// ������������ֵ
	private String recvString;// ���ö������񷵻صĽ��
	private String departmentidkey = "00000000-0000-0000-0000-000000000000";// ��������
	private String providerspinnerdialogkey;// ��Ӧ������
	private Button saveSetButton;//���水ť
	private Button setBaseInfo;// ���û�����Ϣ
	private Button buttonSave;// ����ɼ���Ϣ
	private Button buttonClear; // ��հ�ť
	private ImageButton imagebuttonadd;// �������б�����µ�ѡ����
	private ImageButton provideradd;//����Ӧ�������б�����µ�ѡ��
	private ImageButton materialnameadd;//���豸��������µ�ѡ����
	private Button addButton;//��Ӱ�ť
	private ListView listViewData;// listview��ʾ�����б�
	private Button dateSelectButton;//ѡ��
	private TextView dateselect;
	// private Spinner Statespinner;//��Ʒ״̬ѡ���
	private Spinner typespinner;// �������ѡ���
	private Spinner specifications;// ����ͺ�
	// private Spinner Belongspinner;// �����б�������
	private Spinner materialspinner;// ��Ʒ�����б�
	private Spinner materialTypespinner;// ��Ʒ��������б�
	private Spinner procurementspinner;// �ɹ����б�
	private AutoCompleteTextView providerspinner;// ��Ӧ�������б�
	// private Spinner clevers;
	private AutoCompleteTextView newprovider;// ��������
	// private Spinner departmentid;// ʹ�ò���
	private Spinner providerspinnerdialog;// ������ѡ��

	private String states;// ����ѡ��Ŀ������
	private int assettypes;// ����ѡ��Ŀ������
	// private int types;//����ѡ��Ĳ�Ʒ״̬
	private EditText invoiceNumber;// ��Ʊ��Ϣ
	private EditText factoryInformation;// ������Ϣ
	private EditText remark;// ��ע
	private EditText barcode;// ��ȡ��ǩ��
	private List<Map<String, Object>> listMap;// listview����Դ
	private List<EPC> listEPC;// EPC����
	private boolean startFlag = false;
	private MyReceiver myReceiver = null;// �㲥������
	private int cmdCode;
	// private EditText markPriceText;
	private EditText depreCiation;// ʹ������
	private EditText price;// �۸�
	private LinearLayout layoutinvoic;
	private MediaPlayer player;
	List<ContentValues> listIndetail = new ArrayList<ContentValues>(); // �����ϸ�б�
	List<ContentValues> listdetail = new ArrayList<ContentValues>(); // �ڿ���ϸ�б�
	private Calendar c = null;
	private final String activity = "com.example.sddsd_uhf.AssetGatherActivity";
	DBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asset_gather);
		SysApplication.getInstance().addActivity(this);
		db = new DBAdapter(this);
		// ��������
		Intent startServer = new Intent(this, UhfService.class);
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

		setBaseInfo = (Button) findViewById(R.id.setbaseInfo);// �����ʲ�������Ϣ
		buttonClear = (Button) findViewById(R.id.button_clear);
		// markscan = (Button) findViewById(R.id.markscan);
		listViewData = (ListView) findViewById(R.id.data_list);
		buttonSave = (Button) findViewById(R.id.button_save);
		// markPriceText = (EditText) findViewById(R.id.markprice);
		dateSelectButton = (Button) findViewById(R.id.dateBtn);
		// markPriceText.setText("0.0");
		dateselect = (TextView) findViewById(R.id.dateSelect);
		imagebuttonadd = (ImageButton) findViewById(R.id.imagebuttonadd);// ʵ���б��������
		materialnameadd = (ImageButton) findViewById(R.id.materialnameadd);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		dateselect.setText(date);
		listEPC = new ArrayList<EPC>();
		listMap = new ArrayList<Map<String, Object>>();
		// Statespinner = (Spinner) findViewById(R.id.spinner_State);
		typespinner = (Spinner) findViewById(R.id.spinner_types);
		// clevers = (Spinner) findViewById(R.id.cleverid);
		providerspinner = (AutoCompleteTextView) findViewById(R.id.spinner_providerid);
		specifications = (Spinner) findViewById(R.id.specificationsid);
		invoiceNumber = (EditText) findViewById(R.id.invoicenumber);
		newprovider = (AutoCompleteTextView) findViewById(R.id.newproviderid);// ��������
		factoryInformation = (EditText) findViewById(R.id.factorynum);
		depreCiation = (EditText) findViewById(R.id.depreciation);
		price = (EditText) findViewById(R.id.price);
		price.setText("0.0");
		remark = (EditText) findViewById(R.id.remark);
		addButton = (Button) findViewById(R.id.addbutton);
		barcode = (EditText) findViewById(R.id.batchNum);
		// departmentid = (Spinner) findViewById(R.id.spinner_departmentid);
		// ���������б��ѡ��
		providerDropDown();
		newproviderDropDown();
		// belongDropDown();
		materialTypeDropDown();
		procurementDropDown();
		// cleversDropDown();
		// ���ð�ť����
		buttonClear.setOnClickListener(new MyOnClickable());
		// markscan.setOnClickListener(new MyOnClickable());
		buttonSave.setOnClickListener(new MyOnClickable());
		setBaseInfo.setOnClickListener(new MyOnClickable());
		addButton.setOnClickListener(new MyOnClickable());
		dateSelectButton.setOnClickListener(new MyOnClickable());
		imagebuttonadd.setOnClickListener(new MyOnClickable());
		materialnameadd.setOnClickListener(new MyOnClickable());

		ArrayAdapter<CharSequence> adaptertype = ArrayAdapter
				.createFromResource(this, R.array.AsseetTypes,
						android.R.layout.simple_spinner_dropdown_item);
		typespinner.setAdapter(adaptertype);
		// ����¼�Spinner�¼�����
		// Statespinner.setOnItemSelectedListener(this);
		typespinner.setOnItemSelectedListener(this);
	}

	// ��ť����¼�
	private class MyOnClickable implements OnClickListener {
		private Intent toService = new Intent(AssetGatherActivity.this,
				UhfService.class);

		@Override
		public void onClick(View v) {
			// �������͹㲥������Ϊ��ǰactivity
			Intent ac = new Intent();
			ac.setAction("com.example.UHFService.UhfService");
			ac.putExtra("activity", activity);
			sendBroadcast(ac);
			cmdCode = 0;
			switch (v.getId()) {
			// ��հ�ť
			case R.id.button_clear:
				listIndetail.removeAll(listIndetail);
				listdetail.remove(listdetail);
				listViewData.setAdapter(null);
				break;
			case R.id.button_save:
				if (!listIndetail.isEmpty() && checkEdit()) {
					InsertLocalSQL();
				}
				break;
			case R.id.setbaseInfo:
				cmdCode = Constants.CMD_ISO18000_6C_READ;
				toService.putExtra("cmd", cmdCode);
				toService.putExtra("cmd", cmdCode);
				toService.putExtra("startFlag", startFlag);
				startService(toService);
				break;
			case R.id.dateBtn:
				showDialog(0);// ѡ��ʱ��
				break;
			case R.id.addbutton:
				String barcodetext = barcode.getText().toString().trim();
				if (barcode.getText().toString().trim().length() != 0) {
					Cursor cursor = null;
					db.open();
					try {
						// ��ѯ�����ǩ�Ƿ����
						cursor = db.getTitle("AssetInDetail",
								new String[] { "BarCode" }, "BarCode",
								recvString);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						Log.d("��ѯ��ǩ", e.toString());
					}
					if (!cursor.moveToFirst()) {
						ContentValues contentvalue = new ContentValues();
						contentvalue.put("Key", CreateGuid.GenerateGUID());
						contentvalue.put("MaterialID", materialkey);
						contentvalue.put("FactoryInformation",
								factoryInformation.getText().toString());
						contentvalue.put("BarCode", recvString);
						contentvalue.put("ProviderID", providerkey);
						contentvalue.put("Price", price.getText().toString()
								.trim());
						contentvalue.put("MaterialModelID", materialTypekey);
						contentvalue.put("Number", 1);
						contentvalue.put("CleverID",
								"00000000-0000-0000-0000-000000000000");// ����������cleverskey
						contentvalue.put("Remark", remark.getText().toString());// ��ע
						contentvalue.put("SpecificationsID", specificationskey);// ��������ͺţ���ȡid
						contentvalue.put("Depreciation", depreCiation.getText()
								.toString());// ʹ������
						contentvalue.put("WareStates", states);// �ⷿ״̬
						contentvalue.put("ManufacturerID", newproviderkey);// ��������
						contentvalue.put("FactoryDateTime", dateselect
								.getText().toString().trim());// ��������

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
						addListView(listIndetail);
						ContentValues contentvaluelist = new ContentValues();
						contentvaluelist.put("Key", CreateGuid.GenerateGUID());
						contentvaluelist.put("BarCode", recvString);
						contentvaluelist.put("AsseetType", assettypes);
						contentvaluelist.put("Quantity", 1);
						contentvaluelist.put("AssetState", 1);//
						contentvaluelist.put("BelongID", belongkey);
						contentvaluelist.put("MaterialID", materialkey);
						contentvaluelist.put("BelongType", 1);
						contentvaluelist.put("SpecificationsID",
								specificationskey);// ��������ͺţ���ȡid
						contentvaluelist.put("CreateOperater",
								CurrentUser.CurrentUserGuid);
						contentvaluelist.put("UpdateOperater",
								CurrentUser.CurrentUserGuid);
						if (listdetail.isEmpty()) {
							listdetail.add(contentvaluelist);
							Log.e("read tid", listdetail.get(0).get("BarCode")
									.toString());
						} else {
							for (int i = 0; i < listdetail.size(); i++) {
								ContentValues mTID = listdetail.get(i);
								// list���д�EPC
								if (recvString.equals(mTID.get("BarCode")
										.toString())) {
									break;
								} else if (i == (listdetail.size() - 1)) {
									// list��û�д�epc
									listdetail.add(contentvaluelist);
								}
							}
						}
						barcode.setText("");
					} else {
						Toast.makeText(AssetGatherActivity.this, "�ñ�ǩ�Ѿ�����",
								Toast.LENGTH_SHORT).show();
					}
					cursor.close();
					db.close();
				}
				break;
			case R.id.imagebuttonadd:
				LayoutInflater inflater = LayoutInflater
						.from(AssetGatherActivity.this); // �����ʾ
				final View textEntryView = inflater.inflate(R.layout.zichanadd,
						null);
				final EditText specificationsname = (EditText) textEntryView
						.findViewById(R.id.name);
				final EditText specificationsunit = (EditText) textEntryView
						.findViewById(R.id.unit);

				final AlertDialog.Builder builder = new AlertDialog.Builder(
						AssetGatherActivity.this);
				builder.setCancelable(false);
				builder.setIcon(R.drawable.dialogicon);
				builder.setTitle("��ӹ���ͺ�");
				builder.setView(textEntryView);
				builder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// new Thread(){
								// public void run(){
								// Looper.prepare();
								ContentValues contentvalues = new ContentValues();
								contentvalues.put("Key",
										CreateGuid.GenerateGUID());
								contentvalues.put("MaterialID", materialkey);
								contentvalues.put("Name", specificationsname
										.getText().toString().trim());
								contentvalues.put("Unit", specificationsunit
										.getText().toString().trim());
								contentvalues.put("CreateOperater",
										CurrentUser.CurrentUserGuid);
								db.open();
								if (specificationsname.getText().toString()
										.trim().equals("")
										|| specificationsunit.getText()
												.toString().trim().equals("")) {
									Toast.makeText(AssetGatherActivity.this,
											"���ƻ�λ����Ϊ��", Toast.LENGTH_SHORT)
											.show();
								} else {
									long result = db.insert(contentvalues,
											"SpecificationsInfo");
									if (result > 0) {
										specificationsDropDown(materialkey);
									}
								}
								db.close();
								// Looper.loop();
								// }
								// }.start();

							}
						});
				builder.setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								setTitle("");
							}
						});
				builder.show();

				break;
			case R.id.materialnameadd:
				db.open();
				LayoutInflater inflater1 = LayoutInflater
						.from(AssetGatherActivity.this); // �����ʾ
				final View textEntryView1 = inflater1.inflate(
						R.layout.addmaterialname, null);
				final EditText materialname = (EditText) textEntryView1
						.findViewById(R.id.materialname);
				final EditText materialprice = (EditText) textEntryView1
						.findViewById(R.id.materialprice);
				/*
				 * providerspinnerdialog = (Spinner) textEntryView1
				 * .findViewById(R.id.providerid); provideradd = (ImageButton)
				 * textEntryView1 .findViewById(R.id.provideradd);
				 */
				// providerDropDown(providerspinnerdialog);
				/*
				 * provideradd.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View arg0) { // TODO
				 * Auto-generated method stub final EditText inputServer = new
				 * EditText( AssetGatherActivity.this); AlertDialog.Builder
				 * builder = new AlertDialog.Builder( AssetGatherActivity.this);
				 * builder.setTitle("��ӹ�Ӧ��") .setIcon(R.drawable.dialogicon)
				 * .setView(inputServer) .setNegativeButton("ȡ��", null);
				 * builder.setPositiveButton("ȷ��", new
				 * DialogInterface.OnClickListener() {
				 * 
				 * public void onClick(DialogInterface dialog, int which) {
				 * ContentValues contentvalues1 = new ContentValues();
				 * contentvalues1.put("Key", CreateGuid.GenerateGUID());
				 * contentvalues1.put("Name", inputServer
				 * .getText().toString().trim());
				 * 
				 * if (inputServer.getText().toString() .trim().equals("")) {
				 * Toast.makeText( AssetGatherActivity.this, "������Ϊ��",
				 * Toast.LENGTH_SHORT) .show(); } else { long result =
				 * db.insert( contentvalues1, "ProviderInfo"); if (result > 0) {
				 * providerDropDown(providerspinnerdialog); providerDropDown();
				 * } }
				 * 
				 * } }); builder.show(); } });
				 */
				final AlertDialog.Builder builder1 = new AlertDialog.Builder(
						AssetGatherActivity.this);
				builder1.setCancelable(false);
				builder1.setIcon(R.drawable.dialogicon);
				builder1.setTitle("����豸����");
				builder1.setView(textEntryView1);
				builder1.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								ContentValues contentvalues = new ContentValues();
								contentvalues.put("Key",
										CreateGuid.GenerateGUID());
								contentvalues.put("MaterialModelID",
										materialTypekey);
								contentvalues.put("Name", materialname
										.getText().toString().trim());
								contentvalues.put("Price", materialprice
										.getText().toString().trim());
								contentvalues.put("ProviderID",
										"00000000-0000-0000-0000-000000000000");
								contentvalues.put("CreateOperater",
										CurrentUser.CurrentUserGuid);
								if (materialname.getText().toString().trim()
										.equals("")
										|| materialprice.getText().toString()
												.trim().equals("")) {
									Toast.makeText(AssetGatherActivity.this,
											"���ƻ�۸���Ϊ��", Toast.LENGTH_SHORT)
											.show();
								} else {
									long result = db.insert(contentvalues,
											"MaterialInfo");
									if (result > 0) {
										materialDropDown(materialTypekey);
										// specificationsDropDown(materialkey);
									}
								}
								db.close();
							}
						});
				builder1.setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								setTitle("");
							}
						});
				builder1.show();
				break;
			default:
				break;
			}
		}
	}

	private boolean checkEdit() {
		// TODO Auto-generated method stub
		if (providerspinner.getText().toString().trim().equals("")) {
			Toast.makeText(AssetGatherActivity.this, "��Ӧ�̲���Ϊ��",
					Toast.LENGTH_SHORT).show();
		} else {
			if (newprovider.getText().toString().trim().equals("")) {
				Toast.makeText(AssetGatherActivity.this, "�������Ҳ���Ϊ��",
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}

	/*
	 * ���뱾�����ݿ�
	 */
	private void InsertLocalSQL() {
		// TODO Auto-generated method stub
		// StringBuffer sb = new StringBuffer();
		db.open();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// ������ⵥ��
		SimpleDateFormat formatNumber = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateBatchnum = formatNumber.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		String BuildBatchnum = "ZC-RKD" + dateBatchnum;

		String date = format.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
		String AssetInInfokey = CreateGuid.GenerateGUID();
		ContentValues content = new ContentValues();
		content.put("Key", AssetInInfokey);
		content.put("BatchNumber", BuildBatchnum);
		content.put("BelongID", belongkey);// �������
		content.put("DepartmentID", departmentidkey);
		content.put("InDateTime", date);
		content.put("InvoiceNumber", invoiceNumber.getText().toString());
		content.put("Price", 0);// �ܽ��
		content.put("PurchaseID", procurementkey);// �ɹ���
		content.put("CreateOperater", CurrentUser.CurrentUserGuid);
		content.put("UpdateOperater", CurrentUser.CurrentUserGuid);
		// db.insert(content, "AssetInInfo");// ���������Ϣ���ݿ�
		long resultInsert = 0;

		// �������������ϸ��
		for (int i = 0; i < this.listIndetail.size(); i++) {
			ContentValues contentvalue = listIndetail.get(i);
			contentvalue.put("AssetInInfoID", AssetInInfokey);
		}
		try {
			// ����ӱ��棬ֻҪ��һ�����ɹ�������ع�
			resultInsert = db.insertList(content, listIndetail, "AssetInInfo",
					"AssetInDetail");
			if (resultInsert == listIndetail.size() + 1) {
				Toast.makeText(this, "���ɹ�", Toast.LENGTH_LONG).show();
				listIndetail.removeAll(listIndetail);
				listViewData.setAdapter(null);

			} else {
				Toast.makeText(this, "���ʧ�ܣ�������", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "���ʧ�ܣ�������", Toast.LENGTH_LONG).show();
		}
		resultInsert = 0;// ���¹���
		try {
			resultInsert = db.insertList(listdetail, "AssetDetail");
			if (resultInsert == listdetail.size()) {
				lifecycle(listdetail);
				Toast.makeText(this, "���浽�ڿ�ɹ�", Toast.LENGTH_LONG).show();
				listdetail.remove(listdetail);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "���浽�ڿ�ʧ�ܣ�������", Toast.LENGTH_LONG).show();
		}
		db.close();

	}

	public void lifecycle(List<ContentValues> listdetail) {
		String tid = null;
		for (int i = 0; i < listdetail.size(); i++) {
			ContentValues contentvalue = listdetail.get(i);
			tid = contentvalue.getAsString("BarCode");

			SimpleDateFormat formatdate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String operdate = formatdate.format(new Date());// ��ȡ��ǰʱ�䲢��ʽ��
			ContentValues content = new ContentValues();
			content.put("Key", CreateGuid.GenerateGUID());
			content.put("AssetOperatingID", CurrentUser.CurrentUserGuid);
			content.put("BarCode", tid);
			content.put("OperatingType", "���");
			content.put("MaterialID", materialkey);
			content.put("SpecificationsID", specificationskey);
			content.put("OperatingDate", operdate);
			content.put("Number", 1);
			content.put("CreateOperater", CurrentUser.CurrentUserGuid);
			content.put("UpdateOperater", CurrentUser.CurrentUserGuid);
			content.put("UpdateDateTime", operdate);
			content.put("CreateDateTime", operdate);
			db.insert(content, "AssetLifecycleInfo");
		}
	}

	// ���ð�ť�Ƿ����
	private void setButtonClickable(Button button, boolean flag) {
		button.setClickable(flag);
		if (flag) {
			button.setTextColor(Color.BLACK);
		} else {
			button.setTextColor(Color.GRAY);
		}
	}

	// ������ݵ�ListView
	private void addListView(List<ContentValues> list) {
		// ��������ӵ�ListView
		listMap = new ArrayList<Map<String, Object>>();
		int idcount = 1;
		for (ContentValues epcdata : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ID", idcount);
			map.put("TID", epcdata.get("BarCode"));
			map.put("COUNT", 1);
			idcount++;
			listMap.add(map);
		}
		// Toast.makeText(AssetGatherActivity.this, listMap.get(1).toString(),
		// Toast.LENGTH_SHORT).show();
		listViewData.setAdapter(new SimpleAdapter(AssetGatherActivity.this,
				listMap, R.layout.listview_item, new String[] { "ID", "TID",
						"COUNT" }, new int[] { R.id.textView_id,
						R.id.textView_TID, R.id.textView_count }));

	}

	@Override
	protected void onPause() {
		// ҳ���л���ʱ��Ӧ��ֹ��ѯ
		Intent toStopInventory = new Intent(AssetGatherActivity.this,
				UhfService.class);
		toStopInventory.putExtra("cmd", 0);
		startService(toStopInventory);
		super.onPause();
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

	@Override
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
			 * addListView(listEPC, recvString); } break;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.asset_gather, menu);
		return true;
	}

	// ѡ��״̬�Ϳ������
	@Override
	public void onItemSelected(AdapterView<?> selectitem, View v, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (selectitem.getId()) {
		/*
		 * case R.id.spinner_State: //states=arg2+1; types=arg2+1;
		 * //Toast.makeText(AssetGatherActivity.this, "states:"+states,
		 * Toast.LENGTH_LONG).show(); break;
		 */
		case R.id.spinner_types:

			if (arg2 == 0) {
				states = "false";
				assettypes = 1;
			}
			if (arg2 == 1) {
				states = "true";
				assettypes = 2;
			}
			break;
		// case R.id.spinner_providerid:
		// providerkey = ((ProviderInfo) providerspinner.getSelectedItem())
		// .getKey();
		// break;
		case R.id.materialID:
			materialkey = ((MaterialInfo) materialspinner.getSelectedItem())
					.getKey();
			specificationsDropDown(materialkey);
			break;
		/*
		 * case R.id.spinner_BelongID: belongkey = ((BranchCompanyInfo)
		 * Belongspinner.getSelectedItem()) .getKey();
		 * departmentDropDown(belongkey); break;
		 */
		case R.id.procurement:
			procurementkey = ((AccountCompanyInfo) procurementspinner
					.getSelectedItem()).getKey();
			break;
		case R.id.materialIDType:
			materialTypekey = ((MaterialModelInfo) materialTypespinner
					.getSelectedItem()).getKey();
			materialDropDown(materialTypekey);
			break;
		case R.id.specificationsid:
			specificationskey = ((SpecificationsInfo) specifications
					.getSelectedItem()).getKey();
			break;
		/*
		 * case R.id.cleverid: cleverskey = ((CleverInfo)
		 * clevers.getSelectedItem()).getKey(); break;
		 */
		// case R.id.newproviderid:
		// newproviderkey = ((ProviderInfo) newprovider.getSelectedItem())
		// .getKey();
		// break;
		case R.id.providerid:
			providerspinnerdialogkey = ((ProviderInfo) providerspinnerdialog
					.getSelectedItem()).getKey();
			break;
		/*
		 * case R.id.spinner_departmentid: departmentidkey = ((DepartmentInfo)
		 * departmentid.getSelectedItem()) .getKey(); break;
		 */
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private void providerDropDown() {
		// TODO Auto-generated method stub

		ArrayAdapter<ProviderInfo> adapter;
		List<ProviderInfo> listName = new ArrayList<ProviderInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("ProviderInfo", "Type", 1, "Name");
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
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		providerspinner.setAdapter(adapter);
		// ���õ�����һ���ַ�ʱ���Ϳ�ʼ����
		providerspinner.setThreshold(1);
		// ����¼�Spinner�¼�����
		providerspinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				providerkey = ((ProviderInfo) providerspinner.getAdapter()
						.getItem(arg2)).getKey();
			}
		});

		providerspinner.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasfoucs) {
				// TODO Auto-generated method stub
				AutoCompleteTextView view = (AutoCompleteTextView) v;
				if (hasfoucs) {
					view.showDropDown();
				}
			}
		});
		// ����Ĭ��ֵ
		providerspinner.setVisibility(View.VISIBLE);
		cursor.close();
		db.close();
	}

	private void providerDropDown(Spinner spinner) {
		// TODO Auto-generated method stub
		ArrayAdapter<ProviderInfo> adapter;
		List<ProviderInfo> listName = new ArrayList<ProviderInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("ProviderInfo", "Name");
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
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		spinner.setAdapter(adapter);

		// ����¼�Spinner�¼�����
		spinner.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		spinner.setVisibility(View.VISIBLE);

	}

	// ��������
	private void newproviderDropDown() {
		// TODO Auto-generated method stub
		ArrayAdapter<ProviderInfo> adapter;
		List<ProviderInfo> listName = new ArrayList<ProviderInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("ProviderInfo", "Type", 1, "Name");
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
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		newprovider.setAdapter(adapter);
		// ������������ַ�ʱ��ʼ����
		newprovider.setThreshold(1);
		// ����¼�Spinner�¼�����
		newprovider.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				newproviderkey = ((ProviderInfo) newprovider.getAdapter()
						.getItem(arg2)).getKey();
			}

		});
		newprovider.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasfoucs) {
				// TODO Auto-generated method stub
				AutoCompleteTextView view = (AutoCompleteTextView) v;
				if (hasfoucs) {
					view.showDropDown();
				}
			}
		});
		// ����Ĭ��ֵ
		newprovider.setVisibility(View.VISIBLE);
		cursor.close();
		db.close();
	}

	/*
	 * // ʹ�ò��� private void departmentDropDown(String key) { // TODO
	 * Auto-generated method stub ArrayAdapter<DepartmentInfo> adapter;
	 * List<DepartmentInfo> listName = new ArrayList<DepartmentInfo>();
	 * db.open(); Cursor cursor = db.getAllTitles("DepartmentInfo", "CompanyID",
	 * key); if (cursor.moveToFirst()) { do { DepartmentInfo item = new
	 * DepartmentInfo(); item.Key =
	 * cursor.getString(cursor.getColumnIndex("Key")); item.Name =
	 * cursor.getString(cursor.getColumnIndex("Name")); listName.add(item); }
	 * while (cursor.moveToNext()); } adapter = new
	 * ArrayAdapter<DepartmentInfo>(this, android.R.layout.simple_spinner_item,
	 * listName);
	 * adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
	 * ); // ��adapter ��ӵ�spinner�� departmentid.setAdapter(adapter); //
	 * ����¼�Spinner�¼����� departmentid.setOnItemSelectedListener(this); // ����Ĭ��ֵ
	 * departmentid.setVisibility(View.VISIBLE);
	 * 
	 * }
	 */

	/*
	 * // ʹ�õ�λ private void belongDropDown() { // TODO Auto-generated method
	 * stub Belongspinner = (Spinner) findViewById(R.id.spinner_BelongID);
	 * ArrayAdapter<BranchCompanyInfo> adapter; List<BranchCompanyInfo> listName
	 * = new ArrayList<BranchCompanyInfo>(); db.open(); Cursor cursor =
	 * db.getAllTitles("BranchCompanyInfo", "Name"); if (cursor.moveToFirst()) {
	 * do { BranchCompanyInfo item = new BranchCompanyInfo(); item.Key =
	 * cursor.getString(cursor.getColumnIndex("Key")); item.Name =
	 * cursor.getString(cursor.getColumnIndex("Name")); listName.add(item); }
	 * while (cursor.moveToNext()); } adapter = new
	 * ArrayAdapter<BranchCompanyInfo>(this,
	 * android.R.layout.simple_spinner_item, listName);
	 * adapter.setDropDownViewResource
	 * (android.R.layout.simple_spinner_dropdown_item); // ��adapter ��ӵ�spinner��
	 * Belongspinner.setAdapter(adapter); // ����¼�Spinner�¼�����
	 * Belongspinner.setOnItemSelectedListener(this); // ����Ĭ��ֵ
	 * Belongspinner.setVisibility(View.VISIBLE);
	 * 
	 * }
	 */

	private void materialDropDown(String key) {
		// TODO Auto-generated method stub
		materialspinner = (Spinner) findViewById(R.id.materialID);
		ArrayAdapter<MaterialInfo> adapter;
		List<MaterialInfo> listName = new ArrayList<MaterialInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("MaterialInfo", "MaterialModelID", key,
				"Name");
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
		materialspinner.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		materialspinner.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		materialspinner.setVisibility(View.VISIBLE);
	}

	// ѡ�����
	private void materialTypeDropDown() {
		// TODO Auto-generated method stub
		materialTypespinner = (Spinner) findViewById(R.id.materialIDType);
		ArrayAdapter<MaterialModelInfo> adapter;
		List<MaterialModelInfo> listName = new ArrayList<MaterialModelInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("MaterialModelInfo", "ParentID",
				"3d40c585-591f-444b-a78b-45bd4b61c3f8", "Name");
		if (cursor.moveToFirst()) {
			do {
				MaterialModelInfo item = new MaterialModelInfo();
				item.Key = cursor.getString(cursor.getColumnIndex("Key"));
				item.Name = cursor.getString(cursor.getColumnIndex("Name"));
				listName.add(item);
			} while (cursor.moveToNext());
		}
		adapter = new ArrayAdapter<MaterialModelInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		materialTypespinner.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		materialTypespinner.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		materialTypespinner.setVisibility(View.VISIBLE);
	}

	private void procurementDropDown() {
		// TODO Auto-generated method stub
		procurementspinner = (Spinner) findViewById(R.id.procurement);
		ArrayAdapter<AccountCompanyInfo> adapter;
		List<AccountCompanyInfo> listName = new ArrayList<AccountCompanyInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("AccountCompanyInfo", "Name");
		if (cursor.moveToFirst()) {
			do {
				AccountCompanyInfo item = new AccountCompanyInfo();
				item.Key = cursor.getString(cursor.getColumnIndex("Key"));
				item.Name = cursor.getString(cursor.getColumnIndex("Name"));
				listName.add(item);
			} while (cursor.moveToNext());
		}
		cursor.close();// �ر��α꣬�ͷ���Դ
		adapter = new ArrayAdapter<AccountCompanyInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		procurementspinner.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		procurementspinner.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		procurementspinner.setVisibility(View.VISIBLE);
	}

	private void specificationsDropDown(String key) {
		// TODO Auto-generated method stub
		specifications = (Spinner) findViewById(R.id.specificationsid);
		ArrayAdapter<SpecificationsInfo> adapter;
		List<SpecificationsInfo> listName = new ArrayList<SpecificationsInfo>();
		db.open();
		Cursor cursor = db.getAllTitles("SpecificationsInfo", "MaterialID",
				key, "Name");
		if (cursor.moveToFirst()) {
			do {
				SpecificationsInfo item = new SpecificationsInfo();
				item.Key = cursor.getString(cursor.getColumnIndex("Key"));
				item.Name = cursor.getString(cursor.getColumnIndex("Name"));
				item.Unit = cursor.getString(2);
				listName.add(item);
			} while (cursor.moveToNext());
		}
		if (listName.isEmpty()) {
			SpecificationsInfo item = new SpecificationsInfo();
			item.Key = "00000000-0000-0000-0000-000000000000";
			item.Name = "δ֪���";
			listName.add(item);
		}
		cursor.close();// �ر��α꣬�ͷ���Դ
		adapter = new ArrayAdapter<SpecificationsInfo>(this,
				android.R.layout.simple_spinner_item, listName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// ��adapter ��ӵ�spinner��
		specifications.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		specifications.setOnItemSelectedListener(this);
		// ����Ĭ��ֵ
		specifications.setVisibility(View.VISIBLE);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case 0:
			c = Calendar.getInstance();
			dialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker arg0, int year,
								int month, int dayOfMonth) {
							// TODO Auto-generated method stub
							dateselect.setText(year + "-" + (month + 1) + "-"
									+ dayOfMonth);
						}
					}, c.get(Calendar.YEAR), // �������
					c.get(Calendar.MONTH), // �����·�
					c.get(Calendar.DAY_OF_MONTH) // ��������
			);
			break;
		}
		return dialog;
	}

	// �������ؼ�
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.setClass(AssetGatherActivity.this,
					ZiChanManagerActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

}
