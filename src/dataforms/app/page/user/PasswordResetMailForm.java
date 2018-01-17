package dataforms.app.page.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.app.dao.user.UserDao;
import dataforms.app.dao.user.UserInfoTable;
import dataforms.app.field.user.MailAddressField;
import dataforms.controller.EditForm;
import dataforms.validator.RequiredValidator;
import net.arnx.jsonic.JSON;

/**
 * パスワードリセットメール送信フォームクラス。
 *
 */
public class PasswordResetMailForm extends EditForm {
	
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(PasswordResetMailForm.class);

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
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		UserDao dao = new UserDao(this);
		List<Map<String, Object>> list = dao.queryUserListByMail(e.getMailAddress());
		logger.debug("developer=" + JSON.encode(list, true));
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
	}

}
