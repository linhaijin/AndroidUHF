package com.example.dao;

public class CreateTableStrings {

	public static String[] tables = new String[] {
		   //�����û���
		   "create table UserInfo(Key nvarchar(64) not null,LoginID varchar(32) not null,LoginPwd varchar(64) not null,UserType int,UpdateDateTime date)",
			// ������Ʒ��Ϣ��
			"create table MaterialInfo (Key nvarchar(64) NOT NULL,Name NVARCHAR (64) NOT NULL,"
			+ "MaterialModelID nvarchar(64),Price double,ProviderID nvarchar(64) NULL,UpdateDateTime date,CreateOperater nvarchar(64) NULL)",
			// �ִ�������Ϣ��
			"CREATE TABLE StockDetail (Key NVARCHAR (64) NOT NULL, MaterialID NVARCHAR (64) NULL,"
			+ "OnQuantity double,StockType int null,SpecificationsID nvarchar(64) null,WarehouseID "
			+ "nvarchar(64) null,BarCode nvarchar not null,CreateOperater nvarchar(64) NULL,"
			+ "UpdateOperater nvarchar(64) NULL,UpdateDateTime date)",
			// ������Ŀ��Ϣ��
			"CREATE TABLE ProjectInfo ( Key nvarchar(64) NOT NULL,Name NVARCHAR (64) NOT NULL,District nvarchar,UpdateDateTime date,CreateOperater nvarchar(64) NULL)",
			//��������ͺű�
			"CREATE TABLE SpecificationsInfo ( Key nvarchar(64) NOT NULL, Name NVARCHAR (64) NOT NULL,"
			+ "Unit nvarchar,MaterialID nvarchar(64) not null,UpdateDateTime date,CreateOperater nvarchar(64) NULL)",
			// ������Ӧ��
			"CREATE TABLE ProviderInfo ( Key nvarchar(64) NOT NULL,Name NVARCHAR (64) NOT NULL,Type int,UpdateDateTime date,CreateOperater nvarchar(64) NULL)",
			// ������˾��Ϣ��
			"CREATE TABLE BranchCompanyInfo ( Key nvarchar(64) NOT NULL,Name NVARCHAR (64) NOT NULL,UpdateDateTime date,CreateOperater nvarchar(64) NULL)",
			// ������˾������Ϣ
		    "CREATE TABLE DepartmentInfo ( Key nvarchar(64) NOT NULL,Name NVARCHAR (64) NOT NULL,CompanyID nvarchar(64) Not null,UpdateDateTime date,CreateOperater nvarchar(64) NULL)",
		   // �����ⷿ��
		    "CREATE TABLE Warehouse ( Key nvarchar(64) NOT NULL,Name NVARCHAR (64) NOT NULL,UpdateDateTime date,CreateOperater nvarchar(64) NULL)",
			// ������Ʒ����
			"CREATE TABLE MaterialModelInfo ( Key nvarchar(64) NOT NULL,Name NVARCHAR (64) NOT NULL,ParentID nvarchar(64) not null,UpdateDateTime date,CreateOperater nvarchar(64) NULL)",
			// ����ְԱ��
			"CREATE TABLE CleverInfo ( Key nvarchar(64) NOT NULL,Name NVARCHAR (64) NOT NULL,DepatmentID nvarchar(64) not null,UpdateDateTime date,CreateOperater nvarchar(64) NULL)",
			//�ʲ�������ϸ
			"CREATE TABLE AssetAllocateDetail ( Key nvarchar(64) NOT NULL"
			+ ",AssetAllocateInfoID nvarchar(64) not null,BarCode nvarchar not "
			+ "null,Quantity double Null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ʲ�������
			"CREATE TABLE AssetAllocateInfo ( Key nvarchar(64) NOT NULL,AssetToID NVARCHAR (64) "
			+ "NOT NULL,AssetFromID nvarchar(64) not null,AssetType int Not NULL,BatchNumber"
			+ " NVARCHAR NOT NULL,Date DATE NOT NULL,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ʲ��ڿ�
			"CREATE TABLE AssetDetail ( Key nvarchar(64) NOT NULL,MaterialID NVARCHAR (64) NOT NULL,SpecificationsID "
			+ "nvarchar(64) null,BarCode nvarchar not null,AsseetType int NOT NULL,Quantity double,AssetState int null,"
			+ "BelongType int, BelongID nvarchar(64),CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL,UpdateDateTime date)",
			//�ʲ������ϸ
			"CREATE TABLE AssetInDetail ( Key nvarchar(64) NOT NULL,AssetInInfoID NVARCHAR (64) NOT NULL,"
			+ "BarCode nvarchar not null,FactoryInformation nvarchar,MaterialID nvarchar(64) not null,SpecificationsID nvarchar(64) not null,"
			+ "MaterialModelID nvarchar(64) not null,Depreciation nvarchar null,Number int,"
			+ "Price double,ProviderID nvarchar(64) not null,WareStates nvarchar null,CreateOperater nvarchar(64)"
			+ " NULL,CleverID nvarchar(64) null,Remark nvarchar null,ManufacturerID navarcha(64) null,FactoryDateTime date null,UpdateOperater nvarchar(64) NULL)",
			//�ʲ������Ϣ
			"CREATE TABLE AssetInInfo ( Key nvarchar(64) NOT NULL,BatchNumber NVARCHAR NOT NULL,"
			+ "BelongID nvarchar(64) not null,DepartmentID nvarchar(64) not null,InDateTime date,InvoiceNumber nvarchar,"
			+ "Price double,PurchaseID nvarchar(64) not null,CreateOperater nvarchar(64) NULL,"
			+ "UpdateOperater nvarchar(64) NULL)",
			//�ʲ�������ϸ
			"CREATE TABLE AssetOutDetail ( Key nvarchar(64) NOT NULL,AssetOutInfoID NVARCHAR (64) NOT NULL,"
			+ "Quantity double,BarCode nvarchar(64) not null,"
			+ "CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//���ⵥ��Ϣ
			"CREATE TABLE AssetOutInfo ( Key nvarchar(64) NOT NULL,BatchNumber NVARCHAR (64) NOT NULL,DepartmentID nvarchar(64) not null,"
			+ "OutDate date,UseModel int,ConsumingUserID nvarchar(64) null,UserID nvarchar(64) not null,UserCompanyID nvarchar(64) not null"
			+ ",CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ʲ��黹��ϸ
			"CREATE TABLE AssetReturnDetail ( Key nvarchar(64) NOT NULL,AssetReturnInfoID NVARCHAR (64)"
			+ " NOT NULL,IOCost double,Quantity double,BarCode nvarchar(64) "
			+ "not null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ʲ��黹��Ϣ
			"CREATE TABLE AssetReturnInfo ( Key nvarchar(64) NOT NULL,BatchNumber NVARCHAR (64) "
			+ "NOT NULL,ReturnDate date not null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ʲ���ͣ������ϸ
			"CREATE TABLE AssetScrapStopDetail ( Key nvarchar(64) NOT NULL,"
			+ "AssetScrapStopInfoID nvarchar(64) not null,Quantity double,CleverID nvarchar(64) null,BranchCompanyID nvarchar(64) null,"
			+ "BarCode nvarchar(64) not null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ʲ���ͣ������Ϣ
			"CREATE TABLE AssetScrapStopInfo ( Key nvarchar(64) NOT NULL,BatchNumber NVARCHAR (64) NOT NULL,"
			+ "Date date not null,AssetScrapStopMode int not null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ�������ϸ
			"CREATE TABLE StockAllocateDetail ( Key nvarchar(64) NOT NULL,Amount double null"
			+ ",StockAllocateInfoID nvarchar(64) not null,BarCode nvarchar not null,AllocateQuantity double Not Null"
			+ ",CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ�������
			"CREATE TABLE StockAllocateInfo ( Key nvarchar(64) NOT NULL,AllcoteToID NVARCHAR (64) NOT NULL,"
			+ "AllcoteFromID nvarchar(64) not null,CallInPeopleID nvarchar(64) null,RecallPeopleID nvarchar(64) null,"
			+ "HandledPeopleID nvarchar(64) null, AllcoteOperater nvarchar(64) ,AllcoteType int Not NULL,"
			+ "BatchNumber NVARCHAR(30) NOT NULL,AllcoteDateTime DATE NOT NULL,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ������ϸ
			"CREATE TABLE StockInDetail ( Key nvarchar(64) NOT NULL,Amount double NOT NULL,BarCode nvarchar not null,"
			+ "INQuantity double not null,SpecificationsID nvarchar(64) not null,MaterialModelID nvarchar(64) not null,"
			+ "ProviderID nvarchar(64) not null,MaterialID nvarchar(64) not null,StockType int null,StockInInfoID "
			+ "nvarchar(64) not null,UnitPrice double,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ������Ϣ
			"CREATE TABLE StockInInfo ( Key nvarchar(64) NOT NULL,BatchNumber NVARCHAR NOT NULL,BelongID nvarchar(64) "
			+ "not null,StockInDateTime date,InvoiceNumber nvarchar,PurchaseID nvarchar(64) NULL,WarhouseID "
			+ "nvarchar(64) Null,IsPayment nvarchar null,Remark nvarchar null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ�������ϸ
			"CREATE TABLE StockOutDetail ( Key nvarchar(64) NOT NULL,StockOutInfoID NVARCHAR (64) NOT NULL,"
			+ "Remark nvarchar null,Amount double null,OutQuantity double,BarCode nvarchar(64) not null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ�������Ϣ
			"CREATE TABLE StockOutInfo ( Key nvarchar(64) NOT NULL,BatchNumber NVARCHAR (64) NOT NULL,StockOutDateTime date,"
			+ "ProjectID nvarchar(64) null,CustodyPeopleID nvarchar(64) null,ConsumingPeopleID nvarchar(64) null,CompanyID nvarchar(64) null,"
			+ "ChargePeopleID nvarchar(64) null,WarhouseID nvarchar(64) null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ��黹��ϸ
			"CREATE TABLE StockReturnDetail ( Key nvarchar(64) NOT NULL,StockReturnInfoID NVARCHAR (64) NOT NULL,"
			+ "WasteQuantity double null,ReturnQuantity double,BarCode nvarchar(64) not null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ��黹��Ϣ
			"CREATE TABLE StockReturnInfo ( Key nvarchar(64) NOT NULL,BatchNumber NVARCHAR (64) NOT NULL,StockReturnDateTime date "
			+ "not null,ProjectID nvarchar not null,CompanyID nvarchar(64) null,WarhouseID nvarchar not null,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ��̵���Ϣ
		    "create table StockCheckInfo(Key nvarchar(64) not null, BatchNumber nvarchar not null,CheckDateTime date not null,Description nvarchar(512) ,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
			//�ִ��̵���Ϣ��ϸ
		    "create table StockCheckDetail(Key nvarchar(64) not null,StockCheckInfoID nvarchar(64) not null,BarCode nvarchar(256) not null,StockQuantity double not null,StockRealQuantity double,Remark nvarchar,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
		  //�ʲ��̵���Ϣ
		    "create table AssetCheckInfo(Key nvarchar(64) not null,BatchNumber nvarchar not null,CheckDateTime date not null,Description nvarchar(512),CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL )",
			//�ʲ��̵���ϸ
		    "create table AssetCheckDetail(Key nvarchar(64) not null,AssetCheckInfoID nvarchar(64) not null,BarCode nvarchar(256) not null,Quantity int,Remark nvarchar,CreateOperater nvarchar(64) NULL,UpdateOperater nvarchar(64) NULL)",
		    //��ǰ��¼�����б�ǩ��
		    "create table RecodeTag (ID INTEGER PRIMARY KEY autoincrement,BarCode char(32) not null,CreateOperater nvarchar(64) NULL)",
		    //�ʲ���������
		    "create table AssetLifecycleInfo(Key nvarchar(64) not null,AssetOperatingID ncarchar(64) not null,"
		    + "BarCode nvarchar(256) not null,OperatingType nvarchar(30),MaterialID nvarchar(64),SpecificationsID nvarchar(64),OperatingDate date,"
		    + "Number int,CreateOperater nvarchar(64),UpdateOperater nvarchar(64),UpdateDateTime date,CreateDateTime date)",
		    //�˻��ɹ���
		    "create table AccountCompanyInfo(Key nvarchar(64) not null,Name nvarchar NULL,UpdateDateTime date,CreateOperater nvarchar(64) NULL)"
			};

}
