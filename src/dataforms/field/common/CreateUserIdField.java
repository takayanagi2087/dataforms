package dataforms.field.common;

import dataforms.field.sqltype.BigintField;

/**
 * レコード作成者フィールドクラス。
 * <pre>
 * レコード作成したユーザIDを記録するフィールド。
 * </pre>
 */
public class CreateUserIdField extends BigintField implements DoNotUpdateField {
	/**
	 * コンストラクタ。
	 */
	public CreateUserIdField() {
		super(null);
		this.setComment("レコード作成者");
		this.setHidden(true);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CreateUserIdField(final String id) {
		super(id);
		this.setComment("レコード作成者");
		this.setHidden(true);
	}

}
