package dataforms.devtool.page.query;

import dataforms.devtool.field.common.FieldClassNameField;
import dataforms.devtool.field.common.FieldIdField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.field.base.FieldList;
import dataforms.field.common.FlagField;
import dataforms.field.sqltype.VarcharField;
import dataforms.htmltable.HtmlTable;

/**
 * 選択フィールドHtmlテーブルクラス。
 *
 */
public class SelectFieldHtmlTable extends HtmlTable {
	/**
	 * コンストラクタ。
	 * @param id デーブルID。
	 */
	public SelectFieldHtmlTable(final String id) {
		super(id);
		FieldList flist = new FieldList(
			new TableClassNameField("selectTableClass")
			, new TableClassNameField()
			, new TableClassNameField("selectTableClassName")
			, new FlagField("sel")
			, (new FieldIdField()).setReadonly(true)
			, new FieldClassNameField()
			, new VarcharField("comment", 1024));
		this.setFieldList(flist);
	}

}
