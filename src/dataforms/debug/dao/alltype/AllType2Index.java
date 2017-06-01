package dataforms.debug.dao.alltype;

import dataforms.dao.Index;
import dataforms.field.base.FieldList;

/**
 * AllTypeTableのインデックス。
 *
 */
public class AllType2Index extends Index {
	/**
	 * コンストラクタ。
	 */
	public AllType2Index() {
		AllTypeTable table = new AllTypeTable();
		this.setUnique(false);
		this.setTable(table);
		this.setFieldList(new FieldList(
			table.getField("smallintField")
		));
	}

}
