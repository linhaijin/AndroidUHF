package com.example.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.example.bean.IListTable;

public class TableNameStrings {

	public static String[] tablenames = new String[] { "MaterialInfo",
			"StockDetail", "ProjectInfo", "ProviderInfo", "BranchCompanyInfo",
			"DepartmentInfo", "Warehouse", "MaterialModelInfo", "CleverInfo",
			"AssetDetail", "UserInfo", "SpecificationsInfo",
			"AccountCompanyInfo" };

	public static List<IListTable> getAssetTables() {
		List<IListTable> ilist = new ArrayList<IListTable>();
		// �ʲ����
		IListTable assetin = new IListTable(1);
		assetin.main = "AssetInInfo";
		assetin.detail = "AssetInDetail";
		ilist.add(assetin);
		// �ʲ�����
		IListTable assetout = new IListTable(2);
		assetout.main = "AssetOutInfo";
		assetout.detail = "AssetOutDetail";
		ilist.add(assetout);
		// �ʲ��黹
		IListTable assetreturn = new IListTable(3);
		assetreturn.main = "AssetReturnInfo";
		assetreturn.detail = "AssetReturnDetail";
		ilist.add(assetreturn);

		// �ʲ�����
		IListTable assetdiaobo = new IListTable(4);
		assetdiaobo.main = "AssetAllocateInfo";
		assetdiaobo.detail = "AssetAllocateDetail";
		ilist.add(assetdiaobo);

		// �ʲ����ϱ�ͤ
		IListTable assetscrapstop = new IListTable(5);
		assetscrapstop.main = "AssetScrapStopInfo";
		assetscrapstop.detail = "AssetScrapStopDetail";
		ilist.add(assetscrapstop);

		// �ʲ��ڿ�
		IListTable assetdetail = new IListTable(6);
		assetdetail.main = "AssetDetail";
		assetdetail.detail = "";
		ilist.add(assetdetail);

		// �ʲ���������
		IListTable assetcycle = new IListTable(7);
		assetcycle.main = "AssetLifecycleInfo";
		assetcycle.detail = "";
		ilist.add(assetcycle);

		// �ʲ��̵�
		IListTable assetinventory = new IListTable(8);
		assetinventory.main = "AssetCheckInfo";
		assetinventory.detail = "AssetCheckDetail";
		ilist.add(assetinventory);
		return ilist;
	}

	public static List<IListTable> getStockTables() {
		List<IListTable> ilist = new ArrayList<IListTable>();
		// �ִ����
		IListTable stockin = new IListTable(1);
		stockin.main = "StockInInfo";
		stockin.detail = "StockInDetail";
		ilist.add(stockin);
		// �ִ�����
		IListTable stockout = new IListTable(2);
		stockout.main = "StockOutInfo";
		stockout.detail = "StockOutDetail";
		ilist.add(stockout);
		// �ִ��˻�
		IListTable assetback = new IListTable(3);
		assetback.main = "StockReturnInfo";
		assetback.detail = "StockReturnDetail";
		ilist.add(assetback);
		// �ִ�����
		IListTable stockdiaobo = new IListTable(4);
		stockdiaobo.main = "StockAllocateInfo";
		stockdiaobo.detail = "StockAllocateDetail";
		ilist.add(stockdiaobo);
		// �ִ��ڿ�
		IListTable stockdetail = new IListTable(5);
		stockdetail.main = "StockDetail";
		stockdetail.detail = "";
		ilist.add(stockdetail);
		// �ִ��̵�
		IListTable stockinventory = new IListTable(6);
		stockinventory.main = "StockCheckInfo";
		stockinventory.detail = "StockCheckDetail";
		ilist.add(stockinventory);

		return ilist;
	}
}
