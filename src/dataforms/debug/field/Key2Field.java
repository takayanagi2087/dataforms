package dataforms.debug.field;

import dataforms.field.sqltype.VarcharField;
import dataforms.validator.MaxLengthValidator;


/**
 * Key2Fieldフィールドクラス。
 *
 */
public class Key2Field extends VarcharField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 8;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "キー2";
	/**
	 * コンストラクタ。
	 */
	public Key2Field() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public Key2Field(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}
}
