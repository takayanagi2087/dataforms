package dataforms.debug.field;

import dataforms.field.common.RecordIdField;


/**
 * SingleSelectIdFieldフィールドクラス。
 *
 */
public class SingleSelectIdField extends RecordIdField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "レコードID";
	/**
	 * コンストラクタ。
	 */
	public SingleSelectIdField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SingleSelectIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}
}
