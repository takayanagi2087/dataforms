package dataforms.app.page.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.app.dao.user.UserDao;
import dataforms.app.field.user.LoginIdField;
import dataforms.app.field.user.PasswordField;
import dataforms.controller.ApplicationException;
import dataforms.controller.EditForm;
import dataforms.util.MessagesUtil;
import dataforms.validator.ValidationError;

/**
 * パスワード変更フォーム。
 *
 */
public class ChangePasswordForm extends EditForm {
	/**
	 * コンストラクタ。
	 */
	public ChangePasswordForm() {
		this.addField(new LoginIdField()).removeRequiredValidator();
		this.addField(new PasswordField("oldPassword"));
		this.addField(new PasswordField());
		this.addField(new PasswordField("passwordCheck"));
	}

	@Override
	public void init() throws Exception {
		super.init();
		String loginId = (String) this.getPage().getUserInfo().get("loginId");
		this.setFormData("loginId", loginId);
	}

	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> list = super.validate(param);
		if (list.size() == 0) {
			try {
				UserDao dao = new UserDao(this);
				String loginId = (String) this.getPage().getUserInfo().get("loginId");
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("loginId", loginId);
				p.put("password", param.get("oldPassword"));
				dao.login(p);
			} catch (ApplicationException e) {
				String msg = MessagesUtil.getMessage(this.getPage(), "error.oldpasswordnotmatch");
				list.add(new ValidationError("oldPassword", msg));
			}

			String password = (String) param.get("password");
			String passwordCheck = (String) param.get("passwordCheck");
			if (!password.equals(passwordCheck)) {
				String msg = MessagesUtil.getMessage(this.getPage(), "error.passwordnotmatch");
				list.add(new ValidationError("passwordCheck", msg));
			}
		}
		return list;
	}


	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data)
			throws Exception {
		return null;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return true;
	}


	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		UserDao dao = new UserDao(this);
		this.setUserInfo(data);
		data.put("userId", this.getPage().getUserId());
		dao.updatePassword(data);
	}


	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {

	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {

	}


}
