package com.example.bean;


public class MaterialInfo {
	public String Key;
	public String getKey() {
		return Key;
	}
	public void setKey(String key) {
		Key = key;
	}
	public String getMaterialModelID() {
		return MaterialModelID;
	}
	public void setMaterialModelID(String materialModelID) {
		MaterialModelID = materialModelID;
	}
	/*public String getModel() {
		return Model;
	}
	public void setModel(String model) {
		Model = model;
	}*/
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public double getPrice() {
		return Price;
	}
	public void setPrice(double price) {
		Price = price;
	}
	public String UpdateDateTime;
	public String getUpdateDateTime() {
		return UpdateDateTime;
	}
	public void setUpdateDateTime(String updateDateTime) {
		UpdateDateTime = updateDateTime;
	}
	public String MaterialModelID;
	//public String Model;
	public String Name;
	public double Price;
	public String ProviderID;
	public String getProviderID() {
		return ProviderID;
	}
	public void setProviderID(String providerID) {
		ProviderID = providerID;
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
