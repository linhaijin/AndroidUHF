package com.example.UHFService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.android.hdhe.uhf.SerialPort;
import com.android.hdhe.uhf.reader.SendCommendManager;
import com.android.hdhe.uhf.util.Tools;
import com.example.common.Constants;

public class UhfService extends Service {
	
	private SerialPort mSerialPort;//���ڲ������
	private InputStream mInputStream;//����������
	private OutputStream mOutputStream;//���������
	private String recvActivity = null;//���������Activity
	private ServiceReceiver myReceiver = null;//�㲥������
	private int cmdCode = 0;
	private boolean runFlag = true; //��ѯ�߳�����״̬
	private final int port = 13;
	private final int baudrate = 115200;
	private SendCommendManager cmdManager = null;//ָ�������
	private InventoryThread inventoryThread = null;//��ѯ�߳�

	private int addr;//��ʼ��ַ
	private int readDataLength;//�����ݳ���
	private byte[] accessPassword;//��������
	private byte[] dataBytes;//д�������
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		initSerialport();
		//ע��㲥������
		myReceiver = new ServiceReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.UHFService.UhfService");
		registerReceiver(myReceiver, filter);
	    inventoryThread = new InventoryThread();
	    inventoryThread.start();
		super.onCreate();
	}
	
	private void initSerialport(){
		try {
			mSerialPort = new SerialPort(port, baudrate, 0);//�򿪴���
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mSerialPort == null){
			return;
		}
		mSerialPort.uhfPowerOn();
		mInputStream = mSerialPort.getInputStream();
		mOutputStream = mSerialPort.getOutputStream();
		//
		cmdManager = new SendCommendManager(mInputStream, mOutputStream);
		
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//ж��Receiver
		unregisterReceiver(myReceiver);
		//������ѯ�߳�
		runFlag = false;
		//�رմ���
		if(mSerialPort != null){
			try {
				mInputStream.close();
				mOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//�رն�д����Դ
			mSerialPort.uhfPowerOff();
		}
		mSerialPort.close(port);
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent!=null){
			 cmdCode = intent.getIntExtra("cmd", 0);
			//ִ��ָ��
			 if (cmdCode!=0) {
					accessPassword = intent.getByteArrayExtra("accessPassword");
					dataBytes = intent.getByteArrayExtra("dataBytes");
				 exeCmd(cmdCode);
			}
		}else{
			return 0;
		}
		 //runFlag=intent.getBooleanExtra("startflag", false);
		return super.onStartCommand(intent, flags, startId);
	}
	
	//��ѯ�߳�
	private class InventoryThread extends Thread{
		
		@Override
		public void run() {
			while(runFlag){
				if(cmdCode == Constants.CMD_ISO18000_6C_INVENTORY){
					Inventory(cmdCode);
				}
			}
			super.run();
		}
	}
	private void Inventory(int cmdCode){
	    Intent toActivity = new Intent();
		toActivity.setAction(recvActivity);
		int count = 0;
		while(count < 1){
			//���ó���Ϊ32���֣���32�Σ��޲��
			byte[] recvData = cmdManager.readFrom6C(2,3,2);
			if(recvData != null){
				String readData = Tools.Bytes2HexString(recvData,recvData.length);
				Log.e("read tag", readData);
				toActivity.putExtra("result", readData.substring(readData.length() - 4*2, readData.length()));
			}
			sendBroadcast(toActivity);
			count++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//ִ��ָ��
	private void exeCmd(int cmdCode){
		Intent toActivity = new Intent();
		toActivity.setAction(recvActivity);
		switch (cmdCode) {
		/*//����ǩ
		case Constants.CMD_ISO18000_6C_INVENTORY:
			int count = 0;
			while(count < 2){
				//���ó���Ϊ32���֣���32�Σ��޲��
				byte[] recvData = cmdManager.readFrom6C(2,3,2);
				if(recvData != null){
					String readData = Tools.Bytes2HexString(recvData,recvData.length);
					Log.e("read tag", readData);
					toActivity.putExtra("result", readData.substring(readData.length() - 4*2, readData.length()));
				}
				sendBroadcast(toActivity);
//				addr += 32;
				count++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;*/
		case Constants.CMD_ISO18000_6C_READ:
			int read = 0;
			while(read < 1){
				//���ó���Ϊ32���֣���32�Σ��޲��
				byte[] recvData = cmdManager.readFrom6C(1,2,2);//ԭ��Ϊ2,3,2
				if(recvData != null){
					String readData = Tools.Bytes2HexString(recvData,recvData.length);
					Log.e("read tag", readData);
					System.out.println("yaha :"+readData);
					toActivity.putExtra("result", readData.substring(readData.length() - 4*2, readData.length()));
				}
				sendBroadcast(toActivity);
//				addr += 32;
				read++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case Constants.CMD_ISO18000_6C_WRITE:
			addr = 0;
			Log.i("dataBytes***", Tools.Bytes2HexString(dataBytes, dataBytes.length));
			while( addr < 8){
				boolean writeFlag = cmdManager.writeTo6C(accessPassword, 1, 2, 2, dataBytes);
				//��һ��ûд�ɹ�����дһ��
				if(!writeFlag){
					writeFlag = cmdManager.writeTo6C(accessPassword, 1, 2, 2, dataBytes);
				}
				toActivity.putExtra("writeFlag", writeFlag);
				sendBroadcast(toActivity);
				addr += 8;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i("write addr value", "/////////////// "+ addr+"  //////////////" + writeFlag);
			}
			break;
		default:
			break;
		}
	}
	
	//����㲥������
	private class ServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String ac = intent.getStringExtra("activity");
			if(ac!=null)
				Log.e("receive activity", ac);
			    recvActivity = ac; // ��ȡactivity
			if (intent.getBooleanExtra("stopflag", false))
				stopSelf(); // �յ�ֹͣ�����ź�
			Log.e("stop service", intent.getBooleanExtra("stopflag", false)
					+ "");

		}

	}
}
