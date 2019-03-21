package dataforms.debug.field;

import dataforms.field.common.BigintSingleSelectField;


/**
 * BigintItemFieldフィールドクラス。
 *
 */
public class BigintItemField extends BigintSingleSelectField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "Bigintフィールド";
	/**
	 * コンストラクタ。
	 */
	public BigintItemField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BigintItemField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}
}
