package dataforms.debug.dao;

import dataforms.dao.Index;
import dataforms.field.base.FieldList;

/**
 * AllTypeTableのインデックス。
 *
 */
public class SmallMasterIndex extends Index {
	/**
	 * コンストラクタ。
	 */
	public SmallMasterIndex() {
		SmallMasterTable table = new SmallMasterTable();
		this.setUnique(true);
		this.setTable(table);
		this.setFieldList(new FieldList(table.getComment1Field()));
	}

}
