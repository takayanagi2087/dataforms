package dataforms.app.field.func;

import dataforms.field.common.RecordIdField;

/**
 * 機能IDフィールド。
 */
public class FuncIdField extends RecordIdField {
	/**
	 * テーブルコメント。
	 */
	private static final String COMMENT = "機能ID";

	/**
	 * コンストラクタ。
	 */
	public FuncIdField() {
		super(null);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FuncIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

}
