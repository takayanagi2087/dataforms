package dataforms.app.dao.func;

import dataforms.app.field.func.FuncIdField;
import dataforms.app.field.func.FuncPathField;
import dataforms.dao.Table;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.SortOrderField;

/**
 * 機能情報テーブルクラス。
 *
 */
public class FuncInfoTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public FuncInfoTable() {
		this.addPkField(new FuncIdField());
		this.addField(new SortOrderField());
		this.addField(new FuncPathField());
//		this.addField(new FuncCommentField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
		this.setAutoIncrementId(true);
		this.setSequenceStartValue(Long.valueOf(1000));
	}
}
