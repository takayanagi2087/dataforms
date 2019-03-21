package dataforms.debug.field;

import dataforms.field.common.VarcharSingleSelectField;
import dataforms.validator.MaxLengthValidator;


/**
 * VarcharItemFieldフィールドクラス。
 *
 */
public class VarcharItemField extends VarcharSingleSelectField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 64;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "Varcharフィールド";
	/**
	 * コンストラクタ。
	 */
	public VarcharItemField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public VarcharItemField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}
}
