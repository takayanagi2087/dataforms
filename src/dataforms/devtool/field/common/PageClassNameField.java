package dataforms.devtool.field.common;

import dataforms.controller.Page;
import dataforms.devtool.validator.ClassNameValidator;

/**
 * ページクラス名フィールドクラス。
 *
 */
public class PageClassNameField extends SimpleClassNameField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "ページクラス名";
	/**
	 * コンストラクタ。
	 */
	public PageClassNameField() {
		this.setBaseClass(Page.class);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public PageClassNameField(final String id) {
		super(id);
		this.setBaseClass(Page.class);
		this.setComment(COMMENT);
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new ClassNameValidator("Page"));
	}

}
