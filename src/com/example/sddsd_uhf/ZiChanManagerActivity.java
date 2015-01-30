package com.example.sddsd_uhf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.example.UHFService.SysApplication;
import com.example.bean.IListTable;
import com.example.bean.UserInfo;
import com.example.common.Constants;
import com.example.common.CurrentUser;
import com.example.common.ImageButtonTemplate;
import com.example.common.TableNameStrings;
import com.example.dao.DBAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSerializationContext;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class ZiChanManagerActivity extends Activity implements OnClickListener,
		OnTouchListener {

	private ImageButtonTemplate infogether;
	private ImageButtonTemplate chuku;
	private ImageButtonTemplate tuiku;
	private ImageButtonTemplate pandian;
	private ImageButtonTemplate diaobo;
	private ImageButtonTemplate upload;
	private ImageButtonTemplate assetin;
	private ImageButtonTemplate scrapstop;
	ProgressDialog dialogs;
	private boolean Confirm;
	DBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zi_chan_manager);
		SysApplication.getInstance().addActivity(this);
		db = new DBAdapter(this);
		initActivity();
	}

	private void initActivity() {
		// TODO Auto-generated method stub
		infogether = (ImageButtonTemplate) findViewById(R.id.zichan_infogether);
		chuku = (ImageButtonTemplate) findViewById(R.id.zichan_chuku);
		tuiku = (ImageButtonTemplate) findViewById(R.id.zichan_tuiku);
		pandian = (ImageButtonTemplate) findViewById(R.id.zichan_pandian);
		diaobo = (ImageButtonTemplate) findViewById(R.id.zichan_diaobo);
		upload = (ImageButtonTemplate) findViewById(R.id.zichan_upload);
		assetin = (ImageButtonTemplate) findViewById(R.id.zichan_assetIn);
		scrapstop = (ImageButtonTemplate) findViewById(R.id.zichan_scrapstop);

		infogether.setOnClickListener(this);
		chuku.setOnClickListener(this);
		tuiku.setOnClickListener(this);
		pandian.setOnClickListener(this);
		diaobo.setOnClickListener(this);
		upload.setOnClickListener(this);
		assetin.setOnClickListener(this);
		scrapstop.setOnClickListener(this);

		infogether.setOnTouchListener(this);
		chuku.setOnTouchListener(this);
		tuiku.setOnTouchListener(this);
		pandian.setOnTouchListener(this);
		diaobo.setOnTouchListener(this);
		upload.setOnTouchListener(this);
		assetin.setOnTouchListener(this);
		scrapstop.setOnTouchListener(this);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.zichan_infogether:
			infogether();
			break;
		case R.id.zichan_chuku:
			intent.setClass(ZiChanManagerActivity.this,
					PropertyOutActivity.class);
			startActivity(intent);
			break;
		case R.id.zichan_tuiku:
			intent.setClass(ZiChanManagerActivity.this,
					PropertyReturnActivity.class);
			startActivity(intent);
			break;
		case R.id.zichan_pandian:
			intent.setClass(ZiChanManagerActivity.this,
					PropertyInventoryActivity.class);
			startActivity(intent);
			break;
		case R.id.zichan_diaobo:
			intent.setClass(ZiChanManagerActivity.this,
					PropertyDiaoBoActivity.class);
			startActivity(intent);
			break;
		case R.id.zichan_upload:
			showDialog(this);
			break;
		case R.id.zichan_assetIn:

			intent.setClass(ZiChanManagerActivity.this, AssetIn.class);
			startActivity(intent);
			break;
		case R.id.zichan_scrapstop:

			intent.setClass(ZiChanManagerActivity.this,
					AssetScrapStopInfo.class);
			startActivity(intent);
			break;
		}
	}

	private void showDialog(Context context) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setTitle("ȷ��");
		builder.setMessage("ȷ��Ҫ�ϴ�����������");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialogs = new ProgressDialog(getApplicationContext());
				dialogs = ProgressDialog.show(ZiChanManagerActivity.this,
						"Loading...", "Please wait...", true, false);
				// �½��߳�
				new Thread() {
					@Override
					public void run() {
						// ��Ҫ��ʱ�����ķ���
						Looper.prepare();
						Confirm = upload();
						baseupload();//���������ϴ�
						handler.sendEmptyMessage(0);
						Looper.loop();
					}

					
				}.start();
			}
		});
		builder.setNegativeButton("��", null);
		builder.show();
	}

	// ���ɼ������ʲ��ϴ���������
	private boolean upload() {
		// TODO Auto-generated method stub
		db.open();
		String main = new String();
		int successCount = 0;
		for (int i = 0; i < TableNameStrings.getAssetTables().size(); i++) {
			Cursor maincursor = null;
			IListTable maintable = TableNameStrings.getAssetTables().get(i);
			try {
				maincursor = db.getAllTitles(maintable.main);// ���ݱ�������ȡ�ñ�����м�¼
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (maincursor.moveToFirst()) {
				do {
					JSONObject jsonObj = new JSONObject(); // ����������ַ���
					JSONArray arrya = new JSONArray();// ����������ϸ���б�
					for (int j = 0; j < maincursor.getColumnNames().length; j++) {
						String colum = maincursor.getColumnNames()[j];
						String value = maincursor.getString(j);
						try {
							jsonObj.put(colum, value);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					main = jsonObj.toString();// ������Json����ת��Ϊjson�ַ���
					int resultInt = 0;
					Cursor detailcursor = null;// ����һ����ϸ��Ľ��ռ���
					String mainkey = maincursor.getString((maincursor
							.getColumnIndex("Key")));
					if (!maintable.detail.equals("")) { // �ж�detail�Ƿ�Ϊ��
						detailcursor = db.getAllTitles(maintable.detail,
								maintable.main + "ID ='"+mainkey+"'",0);
						if (detailcursor.moveToFirst()) {

							do {
								JSONObject jsonObjDetial = new JSONObject();// ����Json��������������ϸ����
								for (int j = 0; j < detailcursor
										.getColumnNames().length; j++) {
									String colum = detailcursor
											.getColumnNames()[j];
									String value = detailcursor.getString(j);
									try {
										jsonObjDetial.put(colum, value);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										Log.e("jsonObjDetial.put(colum, value)",
												e.toString());
									}
								}
								arrya.put(jsonObjDetial);

							} while (detailcursor.moveToNext());
						}
						detailcursor.close();// �ر��α꣬�ͷ���Դ
						resultInt = singleUpload(main, arrya.toString(),
								maintable.main);
						if (resultInt == 1) {
							successCount++;
						}
					} else {
						resultInt = singleUpload(main, "",// ���õ����ϴ�
								maintable.main);
						if (resultInt == 1) {
							successCount++;
						}
					}
					// sb.append(main);
				} while (maincursor.moveToNext());
			}
			maincursor.close();// �ر��α꣬�ͷ���Դ
		}
		db.close();// �ر����ݿ�
		if (successCount > 1) {
			Toast.makeText(ZiChanManagerActivity.this, "���������ϴ��ɹ���",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	// �����ϴ�
	private int singleUpload(String mainInfo, String detaialList, String table) {

		HttpPost requestget = new HttpPost("http://" + Constants.ip + ":"
				+ Constants.port + "/SetService.asmx/insert" + table);
		requestget.setHeader("Accept", "application/json");
		requestget.addHeader("Content-Type", "application/json; charset=utf-8");

		JSONObject jsonParams = new JSONObject();
		try {
			jsonParams.put("mainInfo", mainInfo);// ��¼��
			jsonParams.put("detaialList", detaialList);// ����
			HttpEntity bodyEntity = new StringEntity(jsonParams.toString(),
					"utf8");
			requestget.setEntity(bodyEntity);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse responsepost = client.execute(requestget);
			if (responsepost.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(responsepost.getEntity());
				int resultobj = new JSONObject(result).getInt("d");
				// �жϷ���ֵ
				if (resultobj > 0) {
					return 1;
				}
			} else {
				// ������
				Toast.makeText(ZiChanManagerActivity.this, "���ʷ�����ʧ�ܣ�",
						Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("�豸�ϴ������쳣��");
		}
		return 0;
	}
	
	private int basesingleUpload(String tableinfo,String tablestr) {
		// �����ռ�
				final String serviceNameSpace = "http://tempuri.org/";
				//final String example = "SpecificationsInfo";
				// ����URL
				//final String serviceURL = "http://192.168.1.150:8080/GetService.asmx";
				final String serviceURL="http://" + Constants.ip + ":"+ Constants.port + "/SetService.asmx";
				// ʵ����SoapObject����,ָ��webService�������ռ��Լ����÷���������
				SoapObject request = new SoapObject(serviceNameSpace, "insertBase");
				// example��������һ��String�Ĳ��������ｫ��android client�����ݵ�example��
				request.addProperty("tablename", tableinfo);
				request.addProperty("tablestr", tablestr);
				// ������л���Envelope
				SoapSerializationEnvelope envelope;
				envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.bodyOut = request;
				 envelope.dotNet=true;  
				// Android�������
				HttpTransportSE transport = new HttpTransportSE(serviceURL);
				transport.debug = true;
				// ����WebService
				try {
					transport.call(serviceNameSpace + "insertBase", envelope);
					if (envelope.getResponse()!=null) {
						SoapObject object= (SoapObject)envelope.bodyIn;
						int resultobj =Integer.parseInt(object.getProperty(0).toString());
						// �жϷ���ֵ
						if (resultobj > 0) {
							return 1;
						}
					} else {
						// ������
						Toast.makeText(ZiChanManagerActivity.this, "���ʷ�����ʧ�ܣ�",
								Toast.LENGTH_SHORT).show();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return 0;
	}
	
	
	//�ϴ������Ļ�������
	private boolean baseupload() {
		// TODO Auto-generated method stub
		db.open();
		Cursor cursor = null;
		int successCount = 0;
		for (int i = 0; i < TableNameStrings.tablenames.length; i++) {
			/*HttpPost requestget = new HttpPost("http://" + Constants.ip + ":"
					+ Constants.port + "/GetService.asmx/"
					+ TableNameStrings.tablenames[i]);// TableNameStrings.tablenames[i]
			requestget.setHeader("Accept", "application/json");
			requestget.addHeader("Content-Type",
					"application/json; charset=utf-8");*/
			if (TableNameStrings.tablenames[i].equals("StockDetail")||TableNameStrings.tablenames[i].equals("AssetDetail"))
				continue;
			
			cursor = db.getAllTitles(TableNameStrings.tablenames[i],
					"UpdateDateTime is null",0);
			JSONArray arrya = new JSONArray();// ����������ϸ���б�
			if (cursor.moveToFirst()) {
				do{
					JSONObject jsonObj = new JSONObject();// ����Json��������������ϸ����
					for (int j = 0; j < cursor.getColumnNames().length; j++) {
						String colum = cursor.getColumnNames()[j];
						String value = cursor.getString(j);
						try {
							jsonObj.put(colum, value);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					arrya.put(jsonObj);
				}while(cursor.moveToNext());
				
			}
			int result=basesingleUpload(TableNameStrings.tablenames[i],arrya.toString());//ͨ�������ͱ�Json�ַ����������ϴ�
              if (result==1) {
            	  successCount++;
			}
		}
		cursor.close();
		db.close();
		if (successCount > 1) {
			Toast.makeText(ZiChanManagerActivity.this, "���������ϴ��ɹ���",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
	
	/*// �������ݵ����ϴ�
	private int basesingleUpload(String tableinfo,String tablestr) {

		HttpPost requestget = new HttpPost("http://" + Constants.ip + ":"
				+ Constants.port + "/SetService.asmx/insert" + tableinfo);
		requestget.setHeader("Accept", "application/json");
		requestget.addHeader("Content-Type", "application/json; charset=utf-8");

		JSONObject jsonParams = new JSONObject();
		try {
			jsonParams.put("tableinfo", tableinfo);//Ҫ��ӵı�
			jsonParams.put("tableinfo", tablestr);//Ҫ��ӱ��json�ַ���
			HttpEntity bodyEntity = new StringEntity(jsonParams.toString(),
					"utf8");
			requestget.setEntity(bodyEntity);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse responsepost = client.execute(requestget);
			if (responsepost.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(responsepost.getEntity());
				int resultobj = new JSONObject(result).getInt("d");
				// �жϷ���ֵ
				if (resultobj > 0) {
					return 1;
				}
			} else {
				// ������
				Toast.makeText(ZiChanManagerActivity.this, "���ʷ�����ʧ�ܣ�",
						Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("�豸�ϴ������쳣��");
		}
		return 0;
	}*/
	private void infogether() {
		// TODO Auto-generate d method stub
		Intent intent = new Intent();
		intent.setClass(ZiChanManagerActivity.this, AssetGatherActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.zi_chan_manager, menu);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		boolean result = true;
		switch (v.getId()) {
		case R.id.zichan_infogether:// �ɼ���Ϣ
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				infogether.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				infogether.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.zichan_chuku:// �ʲ�����
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				chuku.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				chuku.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.zichan_tuiku:// �����ϴ�
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				tuiku.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				tuiku.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.zichan_pandian:// ��������
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				pandian.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				pandian.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.zichan_diaobo:// �ʲ�����
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				diaobo.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				diaobo.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.zichan_upload:// �����ϴ�
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				upload.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				upload.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.zichan_assetIn:// �ʲ����
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				assetin.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				assetin.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		case R.id.zichan_scrapstop:// �ʲ����
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				scrapstop.setBackgroundColor(Color.rgb(111, 166, 234));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				scrapstop.setBackgroundColor(Color.TRANSPARENT);
			}
			result = false;
			break;
		}
		return result;
	}

	// �������ؼ�
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.setClass(ZiChanManagerActivity.this, MainActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

}
