package dataforms.app.field.func;

import dataforms.field.sqltype.VarcharField;


/**
 * 機能名称フィールド。
 */
public class FuncPathField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 1024;
	/**
	 * テーブルコメント。
	 */
	private static final String COMMENT = "機能パス";

	/**
	 * コンストラクタ。
	 */
	public FuncPathField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FuncPathField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}
}
