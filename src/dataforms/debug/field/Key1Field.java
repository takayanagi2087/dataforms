package dataforms.debug.field;

import dataforms.field.sqltype.VarcharField;
import dataforms.validator.MaxLengthValidator;


/**
 * Key1Fieldフィールドクラス。
 *
 */
public class Key1Field extends VarcharField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 8;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "キー1";
	/**
	 * コンストラクタ。
	 */
	public Key1Field() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public Key1Field(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}
}
