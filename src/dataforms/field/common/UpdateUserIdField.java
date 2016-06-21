package dataforms.field.common;

import dataforms.field.sqltype.BigintField;

/**
 * 更新者IDフィールドクラス。
 */
public class UpdateUserIdField extends BigintField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "更新者ID";

	/**
	 * コンストラクタ。
	 */
	public UpdateUserIdField() {
		super(null);
		this.setComment(COMMENT);
		this.setHidden(true);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public UpdateUserIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setHidden(true);
	}
}
