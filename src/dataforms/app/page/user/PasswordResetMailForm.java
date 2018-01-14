package dataforms.app.page.user;

import java.util.HashMap;
import java.util.Map;

import dataforms.app.field.user.MailAddressField;
import dataforms.controller.EditForm;
import dataforms.validator.RequiredValidator;

/**
 * パスワードリセットメール送信フォームクラス。
 *
 */
public class PasswordResetMailForm extends EditForm {

	/**
	 * コンストラクタ。
	 */
	public PasswordResetMailForm() {
		this.addField(new MailAddressField()).addValidator(new RequiredValidator()).setComment("メールアドレス");
	}
	
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		return new HashMap<String, Object>();
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {

	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {

	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
	}

}
