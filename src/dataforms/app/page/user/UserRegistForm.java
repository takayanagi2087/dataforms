package dataforms.app.page.user;

import java.util.HashMap;
import java.util.Map;

import dataforms.app.dao.user.UserInfoTable;
import dataforms.app.field.user.PasswordField;
import dataforms.controller.EditForm;

/**
 * 外部ユーザ登録フォーム。
 *
 */
public class UserRegistForm extends EditForm {
	
	
	
	/**
	 * コンストラクタ。
	 */
	public UserRegistForm() {
		UserInfoTable table = new UserInfoTable();
		this.addField(table.getLoginIdField());
		this.addField(table.getMailAddressField());
		this.addField(table.getPasswordField());
		this.addField(new PasswordField("passwordCheck"));
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
