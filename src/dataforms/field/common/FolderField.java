package dataforms.field.common;

import dataforms.field.sqltype.VarcharField;

/**
 * フォルダフィールドクラス。
 *
 */
public class FolderField extends VarcharField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "フォルダのパス。";

	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 1024;

	/**
	 * コンストラクタ。
	 */
	public FolderField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FolderField(final String id) {
		super(id, 1024);
		this.setComment(COMMENT);

	}
}
