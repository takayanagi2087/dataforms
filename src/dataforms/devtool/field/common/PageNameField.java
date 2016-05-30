package dataforms.devtool.field.common;

import dataforms.field.sqltype.VarcharField;

/**
 * ページ名フィールドクラス。
 *
 */
public class PageNameField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 128;
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "ページ名";

	/**
	 * コンストラクタ。
	 */
	public PageNameField() {
		super(null, LENGTH);
		this.setSpanField(true);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public PageNameField(final String id) {
		super(id, LENGTH);
		this.setSpanField(true);
		this.setComment(COMMENT);
	}
}
