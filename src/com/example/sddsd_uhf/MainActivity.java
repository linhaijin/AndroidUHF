package com.example.sddsd_uhf;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.SyncStateContract.Columns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.hdhe.uhf.util.Tools;
import com.example.UHFService.SysApplication;
import com.example.UHFService.UhfService;
import com.example.bean.TID;
import com.example.common.Constants;
import com.example.common.CurrentUser;
import com.example.common.ImageButtonTemplate;
import com.example.common.MD5Utility;
import com.example.common.TableNameStrings;
import com.example.dao.ConvertToContentvalues;
import com.example.dao.DBAdapter;

public class MainActivity extends Activity implements OnClickListener,
		OnTouchListener {
	ImageButtonTemplate cangchuManager;
	ImageButtonTemplate ziChanManager;
	ImageButtonTemplate shuJuDownload;
	ImageButtonTemplate cleardata;
	ImageButtonTemplate readwritetag;
	ProgressDialog dialogs;
	private long exitTime = 0;
	private boolean isHaveData = true;
	private boolean Confirm;
	private boolean clearcangchu = false;
	private boolean clearzichan = false;
	public String barcode = null;
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
	private final String activity = "com.example.sddsd_uhf.MainActivity";
	DBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		SysApplication.getInstance().addActivity(this);
		setProgressBarVisibility(true);
		db = new DBAdapter(this);// �������ݿⴴ��
		// ��������
		Intent startServer = new Intent(MainActivity.this, UhfService.class);
		startService(startServer);
		initSoundPool();

		// ע��㲥������
		myReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(activity);
		registerReceiver(myReceiver, filter);
		Bundle receivedata = this.getIntent().getExtras();
		try {
			isHaveData = receivedata.getBoolean("IsHaveData");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("isHaveData Ϊ��");
		}
		InitMain();
	}

	/**
	 * ��Handler������UI
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// �ر�ProgressDialog
			dialogs.dismiss();

		}
	};

	private void InitMain() {
		// TODO Auto-generated method stub
		// �ִ�����
		cangchuManager = (ImageButtonTemplate) findViewById(R.id.cangChuManager);
		// cangChuManager.setClickable(true);
		// �ִ��̵�ɨ��
		ziChanManager = (ImageButtonTemplate) findViewById(R.id.storageScan);
		// ziChanManager.setClickable(true);
		// �ʲ�����ɨ��
		shuJuDownload = (ImageButtonTemplate) findViewById(R.id.shuJuDownload);
		// shuJuDownload.setClickable(true);
		cleardata = (ImageButtonTemplate) findViewById(R.id.cleardata);
		// shuJuDownload.setClickable(true);
		readwritetag = (ImageButtonTemplate) findViewById(R.id.readwritetag);
		// shuJuDownload.setClickable(true);
		cangchuManager.setOnClickListener(this);
		ziChanManager.setOnClickListener(this);
		shuJuDownload.setOnClickListener(this);
		cleardata.setOnClickListener(this);
		readwritetag.setOnClickListener(this);

		cangchuManager.setOnTouchListener(this);
		ziChanManager.setOnTouchListener(this);
		shuJuDownload.setOnTouchListener(this);
		cleardata.setOnTouchListener(this);
		readwritetag.setOnTouchListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.cangChuManager:// �ִ�����
			if (isHaveData) {
				if (CurrentUser.userType == 4 || CurrentUser.userType == 1
						|| CurrentUser.userType == 2) {
					intent.setClass(MainActivity.this,
							CangChuManagerActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(this, "����ǰû��Ȩ��", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "��ǰû�����ݣ������������ݣ�", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		case R.id.storageScan:// �ʲ�����
			if (isHaveData) {
				if (CurrentUser.userType == 3 || CurrentUser.userType == 1
						|| CurrentUser.userType == 2) {
					intent.setClass(MainActivity.this,
							ZiChanManagerActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(this, "����ǰû��Ȩ��", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "��ǰû�����ݣ������������ݣ�", Toast.LENGTH_SHORT)
						.show();
			}

			break;

		case R.id.shuJuDownload:// ��ʼ�����ݿ�
			new AlertDialog.Builder(this)
					.setTitle("ȷ��")
					.setMessage("ȷ��Ҫ���±������ݿ���")
					.setPositiveButton("��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									dialogs = new ProgressDialog(
											getApplicationContext());
									dialogs = ProgressDialog.show(
											MainActivity.this, "Loading...",
											"Please wait...", true, false);
									// �½��߳�
									new Thread() {
										@Override
										public void run() {
											// ��Ҫ��ʱ�����ķ���
											Looper.prepare();
											Confirm = initDatabase();
											// ��handler����Ϣ
											handler.sendEmptyMessage(0);
											Toast.makeText(MainActivity.this,
													"�������سɹ���",
													Toast.LENGTH_SHORT).show();
											if (Confirm) {
												Intent intentlogin = new Intent(
														MainActivity.this,
														LoginActivity.class);
												startActivity(intentlogin);
											}
											Looper.loop();
										}
									}.start();
								}
							}).setNegativeButton("��", null).show();
			break;
		case R.id.cleardata:
			if (isHaveData) {
				showDialog_Layout(this);
			}
			break;
		case R.id.readwritetag:
			final EditText shownum = new EditText(MainActivity.this);
			shownum.setClickable(false);
			shownum.setFocusable(false);
			db.open();
			Cursor cursor = null;

			try {
				cursor = db.getAllTitles("RecodeTag", null, null, null, null,
						null, "BarCode DESC");// new
				// String[]{"BarCode"}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("��ȡ��ǩ���쳣");
			}
			if (cursor.moveToFirst()) {
				barcode = cursor.getString(cursor.getColumnIndex("BarCode"));
				if (barcode != null) {
					int barcodeint = Integer.parseInt(barcode);
					barcodeint++;// ��ż�1
					DecimalFormat df = new DecimalFormat("00000000");// Ҫת���ı�Ÿ�ʽ
					barcode = df.format(barcodeint);// ��ʽ���õ���int���
				}
			} else {
				barcode = "00000001";
			}

			shownum.setText(barcode);
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					MainActivity.this);
			dialog.setTitle("��Ҫд��ı�ǩ����").setIcon(R.drawable.dialogicon)
					.setView(shownum).setNegativeButton("ȡ��", null);
			dialog.setPositiveButton("д��",
					new DialogInterface.OnClickListener() {
						private Intent toService = new Intent(
								MainActivity.this, UhfService.class);

						@Override
						public void onClick(DialogInterface dialogin, int whitch) {
							// TODO Auto-generated method stub
							Intent ac = new Intent();
							ac.setAction("com.example.UHFService.UhfService");
							ac.putExtra("activity", activity);
							sendBroadcast(ac);

							byte[] accessPassword = null;
							byte[] dataBytes = null;
							accessPassword = Tools.HexString2Bytes("00000000");

							int writeDataLen = barcode.length() / 4;
							if (writeDataLen % 2 != 0) {
								barcode = barcode + "0";
							}
							dataBytes = Tools.HexString2Bytes(barcode);
							cmdCode = Constants.CMD_ISO18000_6C_WRITE;
							toService.putExtra("cmd", cmdCode);
							// д��ǩ�������������롢����������ʼ��ַ��д�����ݵĳ��ȡ�����
							toService
									.putExtra("accessPassword", accessPassword);
							// toService.putExtra("dataLen", writeDataLen);
							toService.putExtra("dataBytes", dataBytes);
							startService(toService);

						}
					});
			dialog.show();
			db.close();
		}
	}

	private void showDialog_Layout(MainActivity context) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(this); // �����ʾ
		final View textEntryView = inflater.inflate(R.layout.clearpage, null);
		final CheckBox selectcangchu = (CheckBox) textEntryView
				.findViewById(R.id.selectcangchu);
		selectcangchu.setClickable(false);
		final CheckBox selectzichan = (CheckBox) textEntryView
				.findViewById(R.id.selectzichan);
		selectzichan.setClickable(false);
		if (CurrentUser.userType == 3 || CurrentUser.userType == 1
				|| CurrentUser.userType == 2) {
			selectzichan.setClickable(true);
		}
		// �ж��Ƿ�������
		if (CurrentUser.userType == 4 || CurrentUser.userType == 1
				|| CurrentUser.userType == 2) {
			selectcangchu.setClickable(true);

		}
		// �ʲ���ѡ��ѡ��״̬�ı��¼�
		selectcangchu.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					clearcangchu = true;
				}
			}
		});
		// �ִ���ѡ��ѡ��״̬�ı��¼�
		selectzichan.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					clearzichan = true;
				}
			}
		});
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setIcon(R.drawable.dialogicon);
		builder.setTitle("�������");
		builder.setView(textEntryView);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialogs = new ProgressDialog(getApplicationContext());
				dialogs = ProgressDialog.show(MainActivity.this, "Clearing...",
						"Please wait...", true, false);
				new Thread() {
					public void run() {
						// ��Ҫ��ʱ�����ķ���
						Looper.prepare();
						deletedata(clearcangchu, clearzichan);
						/*
						 * try { Thread.sleep(500); } catch
						 * (InterruptedException e) { // TODO Auto-generated
						 * catch block System.out.println("��ʱ500����"); }
						 */
						// ��handler����Ϣ
						handler.sendEmptyMessage(0);

						Looper.loop();
					}
				}.start();

			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				setTitle("");
			}
		});
		builder.show();
		clearcangchu = false;
		clearzichan = false;
	}

	// ������ݷ���
	public void deletedata(boolean clearcangchu, boolean clearzichan) {
		db.open();
		// ѭ��ɾ�������ж��Ƿ�Ϊ��
		if (clearcangchu) {
			boolean result = false;
			// ѭ��ɾ��
			for (int i = 0; i < TableNameStrings.getStockTables().size(); i++) {

				if (!TableNameStrings.getStockTables().get(i).detail.equals("")) {
					db.delete(TableNameStrings.getStockTables().get(i).main);
					db.delete(TableNameStrings.getStockTables().get(i).detail);
				}
			}
			Cursor cursormain = null;
			Cursor cursordetail = null;
			for (int i = 0; i < TableNameStrings.getStockTables().size(); i++) {

				if (!TableNameStrings.getStockTables().get(i).detail.equals("")) {
					cursormain = db.getAllTitles(TableNameStrings
							.getStockTables().get(i).main);
					cursordetail = db.getAllTitles(TableNameStrings
							.getStockTables().get(i).detail);
				}

				if (cursormain.moveToFirst() != false
						|| cursordetail.moveToFirst() != false) {
					result = false;
					break;
				} else {
					result = true;
				}
			}
			if (result) {
				Toast.makeText(this, "ɾ���ִ����ݳɹ�", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "ɾ���ִ�����ʧ��", Toast.LENGTH_SHORT).show();
			}
		}
		// ѭ��ɾ�������ж��Ƿ�Ϊ��
		if (clearzichan) {
			boolean result = false;
			// ѭ��ɾ��
			for (int i = 0; i < TableNameStrings.getAssetTables().size(); i++) {

				if (!TableNameStrings.getAssetTables().get(i).detail.equals("")) {
					db.delete(TableNameStrings.getAssetTables().get(i).main);
					db.delete(TableNameStrings.getAssetTables().get(i).detail);
				}
			}
			Cursor cursormain = null;
			Cursor cursordetail = null;
			for (int i = 0; i < TableNameStrings.getAssetTables().size(); i++) {
				if (!TableNameStrings.getAssetTables().get(i).detail.equals("")) {
					cursormain = db.getAllTitles(TableNameStrings
							.getAssetTables().get(i).main);
					cursordetail = db.getAllTitles(TableNameStrings
							.getAssetTables().get(i).detail);
				}
				if (cursormain.moveToFirst() != false
						|| cursordetail.moveToFirst() != false) {
					result = false;
					break;
				} else {
					result = true;
				}
			}
			if (result) {
				Toast.makeText(this, "ɾ���ʲ����ݳɹ�", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "ɾ���ʲ�����ʧ��", Toast.LENGTH_SHORT).show();
			}

		}

		db.close();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		boolean result = true;
		switch (v.getId()) {
		case R.id.cangChuManager:// �ִ�����
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				cangchuManager.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				cangchuManager.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.storageScan:// �ʲ�����
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				ziChanManager.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				ziChanManager.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.shuJuDownload:// ��������
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				shuJuDownload.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				shuJuDownload.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.cleardata:// ��������
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				cleardata.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				cleardata.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.readwritetag:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				readwritetag.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				readwritetag.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		}
		return result;
	}

	/*
	 * ��ʼ�����ݿ�
	 */
	public boolean initDatabase() {
		db.open();
		boolean result = false;
		// Cursor cursor = db.getAllTitles("RecodeUpdateTime");
		Cursor cursor = null;
		for (int i = 0; i < TableNameStrings.tablenames.length; i++) {
			String date = "2011-05-21 10:37:12";
			cursor = db.getAllByDate(TableNameStrings.tablenames[i],
					"UpdateDateTime");
			if (cursor.moveToFirst()) {
				date = cursor
						.getString(cursor.getColumnIndex("UpdateDateTime"));
			}
			if (date == null) {
				date = "2011-05-21 10:37:12";
			}
			try {
				result = getRequestData(TableNameStrings.tablenames[i], date);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("�����쳣��" + e.toString());
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				System.out.println("���ظ�ʽ�쳣��" + e.toString());
			}
			// downloadByTables(TableNameStrings.tablenames[i], requestget,
			// date);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * ����ksoap2����ASP.netwebservice
	 * 
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private boolean getRequestData(String example, String date)
			throws IOException, XmlPullParserException {
		boolean result = false;
		// �����ռ�
		final String serviceNameSpace = "http://tempuri.org/";
		// final String example = "SpecificationsInfo";
		// ����URL
		// final String serviceURL =
		// "http://192.168.1.150:8080/GetService.asmx";
		final String serviceURL = "http://" + Constants.ip + ":"
				+ Constants.port + "/GetService.asmx";
		// ʵ����SoapObject����,ָ��webService�������ռ��Լ����÷���������
		SoapObject request = new SoapObject(serviceNameSpace, example);
		// example��������һ��String�Ĳ��������ｫ��android client�����ݵ�example��
		request.addProperty("entityType", example);
		request.addProperty("date", date);
		// ������л���Envelope
		SoapSerializationEnvelope envelope;
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = request;
		envelope.dotNet = true;
		// Android�������
		HttpTransportSE transport = new HttpTransportSE(serviceURL);
		transport.debug = true;
		try {
			// ����WebService
			transport.call(serviceNameSpace + example, envelope);
			if (envelope.getResponse() != null) {
				SoapObject object = (SoapObject) envelope.bodyIn;

				byte[] resultstream = Base64.decode(object.getProperty(0)
						.toString());
				resultstream = MD5Utility.unGZip(resultstream);
				// base64����
				String strResult = new String(resultstream, "UTF-8");
				JSONArray jsonArray = null;
				// �жϻ�ȡ�����Ƿ�Ϊ��
				if (strResult == null || strResult.equals("null")) {
					return false;
				} else {
					jsonArray = new JSONArray(strResult); // ����ֱ��Ϊһ��������ʽ�����Կ���ֱ��
															// ��android�ṩ�Ŀ��JSONArray��ȡJSON���ݣ�ת����Array
				}

				if (jsonArray.isNull(0)) {
					return false;
				}
				// db.open();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jobject = (JSONObject) jsonArray.get(i);
					ContentValues content = new ContentValues();
					// �˴������෽��������ContentValues����,����Ϊcontent��table
					content = ConvertToContentvalues.convertToClass(jobject,
							example);
					String key = jobject.getString("Key");
					Cursor cursors = null;
					try {

						cursors = db.getAllTitles(example, "Key='" + key + "'",
								0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int resultInt = 0;
					if (cursors.moveToFirst()) {// �ж����ص������Ǹ��»�������
						resultInt = db.updateData(example, content, "Key",
								new String[] { key });
					} else {
						db.insert(content, example);
					}
				}

			} else {
				// ������
				Toast.makeText(MainActivity.this, example + "��ȡʧ�ܣ�",
						Toast.LENGTH_SHORT).show();
				return false;
			}
			result = true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("���������쳣");
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

	/*
	 * ����д��������Ƿ���ʮ������
	 */
	private boolean checkWriteData(String data) {
		boolean flag = false;
		String reg = "[a-f0-9A-F]+";
		flag = Pattern.matches(reg, data);
		return flag;
	}

	/*
	 * ���������Ƿ�������ȷ
	 */
	private boolean checkPassword(String password) {
		boolean flag = false;
		String reg = "[a-f0-9A-F]{8}";
		flag = Pattern.matches(reg, password);
		return flag;
	}

	// �㲥������
	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// ���񷵻ص�����
			// String recvString = intent.getStringExtra("result");
			boolean writeFlag = intent.getBooleanExtra("writeFlag", false);
			switch (cmdCode) {
			case Constants.CMD_ISO18000_6C_WRITE:
				if (writeFlag) {
					play(1, 0);
					final ContentValues contentvalues = new ContentValues();
					contentvalues.put("BarCode", barcode);
					contentvalues.put("CreateOperater",
							CurrentUser.CurrentUserGuid);
					new Thread() {
						public void run() {
							db.open();
							int result = (int) db.insert(contentvalues,
									"RecodeTag");
							db.close();
							Looper.prepare();
							if (result > 0) {
								Toast.makeText(MainActivity.this, "д�����ݳɹ�",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(MainActivity.this, "д������ʧ��",
										Toast.LENGTH_SHORT).show();
							}
							Looper.loop();
						}
					}.start();

				} else {
					Toast.makeText(getApplicationContext(), "д������ʧ��",
							Toast.LENGTH_SHORT).show();
				}

				break;
			default:
				break;
			}
		}
	}

	// �������ؼ�
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(MainActivity.this, "�ٰ�һ���˳�����",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				SysApplication.getInstance().exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
