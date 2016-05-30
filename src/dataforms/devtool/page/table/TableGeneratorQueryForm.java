package dataforms.devtool.page.table;

import dataforms.controller.QueryForm;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.validator.RequiredValidator;

/**
 * 問い合わせフォームクラス。
 */
public class TableGeneratorQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public TableGeneratorQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new ClassNameField());
	}
}
