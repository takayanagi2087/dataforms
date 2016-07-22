package dataforms.devtool.page.table;

import dataforms.controller.QueryForm;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.TableClassNameField;



/**
 * 問い合わせフォームクラス。
 */
public class TableDataQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public TableDataQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField());
		this.addField(new TableClassNameField("tableClassName")).setAutocomplete(true).setCalcEventField(true);
	}
}
