package com.example.bean;

public class CleverInfo {
	public String getKey() {
		return Key;
	}
	public void setKey(String key) {
		Key = key;
	}
	public String Key;
	public String Name;
	public String DepatmentID;
	public String getDepatmentID() {
		return DepatmentID;
	}
	public void setDepatmentID(String depatmentID) {
		DepatmentID = depatmentID;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String UpdateDateTime;
	public String getUpdateDateTime() {
		return UpdateDateTime;
	}
	public void setUpdateDateTime(String updateDateTime) {
		UpdateDateTime = updateDateTime;
	}
	public String CreateOperater;
	public String getCreateOperater() {
		return CreateOperater;
	}
	public void setCreateOperater(String createOperater) {
		CreateOperater = createOperater;
	}
	@Override
	public String toString() {
		// ΪʲôҪ��дtoString()�أ���Ϊ����������ʾ���ݵ�ʱ����������������Ķ������ַ���������£�ֱ�Ӿ�ʹ�ö���.toString()
		// TODO Auto-generated method stub
		return Name;
	}
}
