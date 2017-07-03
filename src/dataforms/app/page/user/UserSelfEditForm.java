package dataforms.app.page.user;

import java.util.List;
import java.util.Map;

import dataforms.app.dao.user.UserDao;
import dataforms.app.dao.user.UserInfoTable;
import dataforms.app.field.user.LoginIdField;
import dataforms.app.field.user.MailAddressField;
import dataforms.app.field.user.UserNameField;
import dataforms.controller.EditForm;
import dataforms.validator.RequiredValidator;
import dataforms.validator.ValidationError;

/**
 * ユーザ情報編集フォームクラス。
 *
 *
 *
 */
public class UserSelfEditForm extends EditForm {

    /**
     * Logger。
     */
//    private static Logger log = Logger.getLogger(UserSelfEditForm.class.getName());

	/**
	 * コンストラクタ。
	 * <pre>
	 * 監理者モードの場合全ユーザの情報が更新可能です。
	 * </pre>
	 */
	public UserSelfEditForm() {
		//this.addField(new UserIdField());
		this.addField(new LoginIdField());
		this.addField(new UserNameField());
		this.addField(new MailAddressField()).addValidator(new RequiredValidator());
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 監理者でない場合、ログインユーザの情報を取得します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
		// 自分自身のユーザ情報を取得.
		Map<String, Object> userInfo = this.getPage().getUserInfo();
		UserDao dao = new UserDao(this);
		Map<String, Object> data = dao.getSelectedData(userInfo);
		this.setFormDataMap(data);
	}

	
	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> list =  super.validate(param);
		UserDao dao = new UserDao(this);
		Map<String, Object> data = this.convertToServerData(param);
		if (dao.existLoginId(data, this.isUpdate(data))) {
			String msg = this.getPage().getMessage("error.duplicate");
			ValidationError err = new ValidationError(UserInfoTable.Entity.ID_LOGIN_ID, msg);
			list.add(err);
		}
		return list;
	}
	/**
	 * {@inheritDoc}
	 * <pre>
	 * 指定されたユーザの情報を取得します。
	 * </pre>
	 */
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data)
			throws Exception {
		UserDao dao = new UserDao(this);
		Map<String, Object> ret = dao.getSelectedData(data);
		return ret;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
//		Long userId = (Long) data.get("userId");
/*		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		Long userId = e.getUserId();
		return (userId != null);*/
		return true;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		UserDao dao = new UserDao(this);
		dao.insertUser(data);
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		e.setUserId(this.getPage().getUserId());
		UserDao dao = new UserDao(this);
		dao.updateSelfUser(data);
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {

	}
}
