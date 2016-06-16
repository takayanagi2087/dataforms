package dataforms.devtool.page.query;

import dataforms.controller.QueryForm;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.QueryClassNameField;
import dataforms.validator.RequiredValidator;

/**
 * 問い合わせフォームクラス。
 */
public class QueryGeneratorQueryForm extends QueryForm {
	
	/**
	 * コンストラクタ。
	 */
	public QueryGeneratorQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new QueryClassNameField("queryClassName")).setAutocomplete(true);
	}
}
