package dataforms.devtool.field.common;

import dataforms.field.base.Field;

/**
 * フィールドクラス名フィールドクラス。
 *
 */
public class FieldFullClassNameField extends SimpleClassNameField {
	/**
	 * autocompleteの例外文字列。
	 */
	private static final String EXCEPTION_PATTERN = "dataforms\\.field\\.sqlfunc";
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "フィールドクラス名";
	/**
	 * コンストラクタ。
	 */
	public FieldFullClassNameField() {
		this.setBaseClass(Field.class);
		this.setComment(COMMENT);
		this.addExceptionPattern(EXCEPTION_PATTERN);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FieldFullClassNameField(final String id) {
		super(id);
		this.setBaseClass(Field.class);
		this.setComment(COMMENT);
		this.addExceptionPattern(EXCEPTION_PATTERN);
	}

	@Override
	protected void onBind() {
		super.onBind();
//		this.addValidator(new ClassNameValidator("Field"));
//		this.setAutocomplete(true);
//		this.setRelationDataAcquisition(true);
	}
}
