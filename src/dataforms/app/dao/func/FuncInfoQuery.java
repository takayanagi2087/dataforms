package dataforms.app.dao.func;

import dataforms.dao.Query;
import dataforms.field.base.FieldList;

/**
 * 機能テーブルの問い合わせクラス。
 *
 */
public class FuncInfoQuery extends Query {
	/**
	 * コンストラクタ.
	 */
	public FuncInfoQuery() {
		FuncInfoTable tbl = new FuncInfoTable();
		this.setFieldList(tbl.getFieldList());
		this.setMainTable(tbl);
		this.setOrderByFieldList(new FieldList(tbl.getField("sortOrder")));
	}
}
