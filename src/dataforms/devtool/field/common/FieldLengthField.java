package dataforms.devtool.field.common;

import dataforms.field.sqltype.VarcharField;

/**
 * フィールド長フィールドクラス。
 *
 */
public class FieldLengthField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 128;
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "フィールド長";

	/**
	 * コンストラクタ。
	 */
	public FieldLengthField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FieldLengthField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}
}
