package dataforms.field.common;

import dataforms.field.sqltype.VarcharField;

/**
 * メモフィールドクラス。
 *
 */
public class MemoField extends VarcharField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "メモフィールド";
	/**
	 * コンストラクタ。
	 */
	public MemoField() {
		super(null, 1024);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public MemoField(final String id) {
		super(id, 1024);
		this.setComment(COMMENT);
	}
}
