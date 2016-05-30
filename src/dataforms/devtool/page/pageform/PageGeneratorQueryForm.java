package dataforms.devtool.page.pageform;

import dataforms.controller.QueryForm;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.validator.RequiredValidator;

/**
 * ページクラス検索フォーム。
 *
 */
public class PageGeneratorQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public PageGeneratorQueryForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new ClassNameField());
	}
}
