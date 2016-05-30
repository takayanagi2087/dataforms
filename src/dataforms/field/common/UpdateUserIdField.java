package dataforms.field.common;

import dataforms.field.sqltype.BigintField;

/**
 * 更新者IDフィールドクラス。
 */
public class UpdateUserIdField extends BigintField {
	/**
	 * コンストラクタ。
	 */
	public UpdateUserIdField() {
		super(null);
		this.setComment("更新者ID");
		this.setHidden(true);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public UpdateUserIdField(final String id) {
		super(id);
		this.setComment("更新者ID");
		this.setHidden(true);
	}
}
