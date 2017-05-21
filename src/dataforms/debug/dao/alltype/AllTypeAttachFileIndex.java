package dataforms.debug.dao.alltype;

import dataforms.dao.Index;
import dataforms.field.base.FieldList;

/**
 * AllTypeTableのインデックス。
 *
 */
public class AllTypeAttachFileIndex extends Index {
	/**
	 * コンストラクタ。
	 */
	public AllTypeAttachFileIndex() {
		AllTypeAttachFileTable table = new AllTypeAttachFileTable();
		this.setUnique(false);
		this.setTable(table);
		this.setFieldList(new FieldList(
			table.getField("fileComment")
		));
	}

}
