package dataforms.app.field.func;

import dataforms.field.sqltype.VarcharField;


/**
 * 機能名称フィールド。
 */
public class FuncNameField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 128;
	/**
	 * テーブルコメント。
	 */
	private static final String COMMENT = "機能名";

	/**
	 * コンストラクタ。
	 */
	public FuncNameField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FuncNameField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}
}
