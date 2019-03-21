package dataforms.debug.field;

import dataforms.field.common.IntegerSingleSelectField;


/**
 * IntegerIetmFieldフィールドクラス。
 *
 */
public class IntegerIetmField extends IntegerSingleSelectField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "Integerフィールド";
	/**
	 * コンストラクタ。
	 */
	public IntegerIetmField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public IntegerIetmField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}
}
