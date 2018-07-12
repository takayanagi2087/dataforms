package dataforms.devtool.field.pagegen;


import dataforms.controller.Form;
import dataforms.devtool.field.common.SimpleClassNameField;
import dataforms.devtool.validator.ClassNameValidator;

/**
 * フォームクラス名フィールド。
 *
 */
public class FormClassNameField extends SimpleClassNameField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "フォームクラス名";
	
	/**
	 * コンストラクタ。
	 */
	public FormClassNameField() {
		this.setBaseClass(Form.class);
		this.setComment(COMMENT);
		this.setAutocomplete(false);
	}
	
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FormClassNameField(final String id) {
		super(id);
		this.setBaseClass(Form.class);
		this.setComment(COMMENT);
		this.setAutocomplete(false);
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		this.setAutocomplete(false);
		this.addValidator(new ClassNameValidator("Form"));
	}
}
