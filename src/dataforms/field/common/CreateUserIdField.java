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
	 * フィールドコメント。
	 */
	private static final String COMMENT = "作成者ID";

	/**
	 * コンストラクタ。
	 */
	public CreateUserIdField() {
		super(null);
		this.setComment(COMMENT);
		this.setHidden(true);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CreateUserIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setHidden(true);
	}

}
