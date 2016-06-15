package dataforms.devtool.page.query;

import dataforms.controller.QueryForm;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.field.base.FieldList;
import dataforms.htmltable.EditableHtmlTable;
import dataforms.validator.RequiredValidator;

/**
 * 問い合わせフォームクラス。
 */
public class QueryQueryForm extends QueryForm {
	/**
	 * Joinテーブルクラス。
	 *
	 */
	private class JoinHtmlTable extends EditableHtmlTable {
		/**
		 * コンストラクタ。
		 * @param id デーブルID。
		 */
		public JoinHtmlTable(final String id) {
			super(id);
			FieldList flist = new FieldList(new FunctionSelectField(), new PackageNameField(), new TableClassNameField());
			flist.get("tableClassName").setAutocomplete(true);
			this.setFieldList(flist);
		}
	};
	
	/**
	 * コンストラクタ。
	 */
	public QueryQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new TableClassNameField("mainTableClassName")).setAutocomplete(true);
		EditableHtmlTable joinTableList = new JoinHtmlTable("joinTableList");
		this.addHtmlTable(joinTableList);
		EditableHtmlTable leftJoinTableList = new JoinHtmlTable("leftJoinTableList");
		this.addHtmlTable(leftJoinTableList);
		EditableHtmlTable rightJoinTableList = new JoinHtmlTable("rightJoinTableList");
		this.addHtmlTable(rightJoinTableList);
	}
}
