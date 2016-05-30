package dataforms.app.field.user;

import dataforms.app.page.user.UserEditForm;
import dataforms.controller.Form;
import dataforms.field.sqltype.VarcharField;
import dataforms.validator.MailAddressValidator;
import dataforms.validator.RequiredValidator;

/**
 * メールアドレスフィールドクラス。
 *
 */
public class MailAddressField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 64;
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "メールアドレス";

	/**
	 * コンストラクタ。
	 */
	public MailAddressField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public MailAddressField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		Form form = this.getParentForm();
		if (form instanceof UserEditForm) {
			this.addValidator(new RequiredValidator());
		}
		this.addValidator(new MailAddressValidator());
	}
}
