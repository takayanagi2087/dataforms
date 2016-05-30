package dataforms.devtool.field.common;

import dataforms.field.sqltype.VarcharField;

/**
 * テーブル名フィールドクラス。
 *
 */
public class TableNameField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 64;
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "テーブル名";

	/**
	 * コンストラクタ。
	 */
	public TableNameField() {
		super(null, LENGTH);
		this.setSpanField(true);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TableNameField(final String id) {
		super(id, LENGTH);
		this.setSpanField(true);
		this.setComment(COMMENT);
	}
}
