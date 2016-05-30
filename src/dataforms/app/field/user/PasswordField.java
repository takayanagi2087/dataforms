package dataforms.app.field.user;

import dataforms.field.sqltype.VarcharField;
import dataforms.validator.RequiredValidator;

/**
 * パスワードフィールドクラス。
 */
public class PasswordField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 64;
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "パスワード";

	/**
	 * コンストラクタ。
	 */
	public PasswordField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドのID。
	 */
	public PasswordField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		this.addValidator(new RequiredValidator());
	}
}
