package dataforms.debug.field;

import dataforms.field.common.RecordIdField;


/**
 * SmallMasterIdFieldフィールドクラス。
 *
 */
public class SmallMasterIdField extends RecordIdField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "レコードID";
	/**
	 * コンストラクタ。
	 */
	public SmallMasterIdField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SmallMasterIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}
}
