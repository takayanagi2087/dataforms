package dataforms.devtool.page.query;

import dataforms.devtool.field.common.AliasNameField;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.field.base.FieldList;
import dataforms.field.sqltype.VarcharField;
import dataforms.htmltable.EditableHtmlTable;

/**
 * Joinテーブルを編集するためのHTMLテーブルクラスです。
 *
 */
public class JoinHtmlTable extends EditableHtmlTable {
	/**
	 * コンストラクタ。
	 * @param id デーブルID。
	 */
	public JoinHtmlTable(final String id) {
		super(id);
		FieldList flist = new FieldList(
			new FunctionSelectField()
			, new PackageNameField()
			, new TableClassNameField()
			, (new AliasNameField()).setCalcEventField(true)
			, (new VarcharField("joinCondition", 1024)).setReadonly(true)
		);
		flist.get("tableClassName").setAutocomplete(true).setRelationDataAcquisition(true);
		this.setFieldList(flist);
	}
}
