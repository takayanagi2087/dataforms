package dataforms.devtool.page.query;

import dataforms.devtool.field.common.FieldFullClassNameField;
import dataforms.devtool.field.common.FieldIdField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.devtool.field.common.TableFullClassNameField;
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
			new TableFullClassNameField("selectTableClass")
			, new TableClassNameField()
			, new TableFullClassNameField("selectTableClassName")
			, new FlagField("sel")
			, (new FieldIdField()).setReadonly(true)
			, new FieldFullClassNameField("fieldClassName")
			, new VarcharField("comment", 1024));
		this.setFieldList(flist);
	}

}
