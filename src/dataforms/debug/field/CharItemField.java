package dataforms.debug.field;

import dataforms.field.common.CharSingleSelectField;
import dataforms.validator.MaxLengthValidator;


/**
 * CharItemFieldフィールドクラス。
 *
 */
public class CharItemField extends CharSingleSelectField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 64;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "Charフィールド";
	/**
	 * コンストラクタ。
	 */
	public CharItemField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CharItemField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}
}
