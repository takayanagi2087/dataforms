package dataforms.app.page.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import dataforms.app.dao.user.UserAttributeTable;
import dataforms.app.dao.user.UserDao;
import dataforms.app.dao.user.UserInfoTable;
import dataforms.app.field.user.MailAddressField;
import dataforms.app.field.user.PasswordField;
import dataforms.controller.EditForm;
import dataforms.controller.WebComponent;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.mail.MailSender;
import dataforms.mail.MailTemplate;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.CryptUtil;
import dataforms.util.UserAdditionalInfoTableUtil;
import dataforms.validator.MailAddressValidator;
import dataforms.validator.RequiredValidator;
import dataforms.validator.ValidationError;
import net.arnx.jsonic.JSON;

/**
 * 外部ユーザ登録フォーム。
 *
 */
public class UserRegistForm extends EditForm {
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(UserRegistForm.class);


	/**
	 * ユーザ有効化ページ。
	 */
	private static String userEnablePage = null;

	/**
	 * ユーザ登録時のメール送信確認フラグ。
	 */
	private static Map<String, Object> config = null;

	/**
	 * コンストラクタ。
	 */
	public UserRegistForm() {
		UserInfoTable table = new UserInfoTable();
		Field<?> loginIdField = this.addField(table.getLoginIdField())
			.setRelationDataAcquisition(false).setAutocomplete(false);
		Boolean loginIdIsMail = (Boolean) config.get("loginIdIsMail");
		if (loginIdIsMail) {
			loginIdField.addValidator(new MailAddressValidator());
		}
		this.addField(table.getUserNameField()).addValidator(new RequiredValidator()).setAutocomplete(false);
		this.addField(table.getMailAddressField()).addValidator(new RequiredValidator());
		Boolean mailCheck = (Boolean) config.get("mailCheck");
		if (mailCheck) {
			this.addField(new MailAddressField("mailAddressCheck")).addValidator(new RequiredValidator());
		} /*else {
			this.addField(new MailAddressField("mailAddressCheck")).addValidator(new RequiredValidator());
		}*/
		this.addField(table.getPasswordField());
		this.addField(new PasswordField("passwordCheck"));

		// ユーザ追加情報テーブルのフィールドを追加します。
		FieldList flist = UserAdditionalInfoTableUtil.getFieldList();
		if (flist != null) {
			this.addFieldList(flist);
		}

	}

	/**
	 * ユーザ有効化ページのパスを取得します。
	 * @return ユーザ有効化ページ。
	 */
	public static String getUserEnablePage() {
		return userEnablePage;
	}

	/**
	 * ユーザ有効化ページのパスを設定します。
	 * @param userEnablePage ユーザ有効化ページ。
	 */
	public static void setUserEnablePage(final String userEnablePage) {
		UserRegistForm.userEnablePage = userEnablePage;
	}


	/**
	 * 設定情報を取得します。
	 * @return 設定情報。
	 */
	public static Map<String, Object> getConfig() {
		return config;
	}

	/**
	 * 設定情報を設定します。
	 * @param config 設定情報。
	 */
	public static void setConfig(final Map<String, Object> config) {
		UserRegistForm.config = config;
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("userEnablePage", userEnablePage);
		ret.put("config", config);
		return ret;
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
	protected List<ValidationError> validateForm(final Map<String, Object> data) throws Exception {
		List<ValidationError> list = super.validateForm(data);
		if (list.size() == 0) {
			Boolean mailCheck = (Boolean) config.get("mailCheck");
			if (mailCheck) {
				String mailAddress = (String) data.get("mailAddress");
				String mailAddressCheck = (String) data.get("mailAddressCheck");
				if (!mailAddress.equals(mailAddressCheck)) {
					String msg = this.getPage().getMessage("error.mailaddressnotmatch");
					ValidationError err = new ValidationError("mailAddressCheck", msg);
					list.add(err);
				}
			}
			String password = (String) data.get("password");
			String passwordCheck = (String) data.get("passwordCheck");
			if (!password.equals(passwordCheck)) {
				String msg = this.getPage().getMessage("error.passwordnotmatch");
				ValidationError err = new ValidationError("passwordCheck", msg);
				list.add(err);
			}
			UserDao dao = new UserDao(this);
			if (dao.existLoginId(data, this.isUpdate(data))) {
				String msg = this.getPage().getMessage("error.duplicate");
				ValidationError err = new ValidationError(UserInfoTable.Entity.ID_LOGIN_ID, msg);
				list.add(err);
			}
		}
		return list;
	}

	/**
	 * 確認メール送信。
	 *
	 * @param data 登録データ。
	 * @throws Exception 例外。
	 */
	protected void sendMail(final Map<String, Object> data) throws Exception {
		String path = this.getAppropriatePath("/mailTemplate/userRegistMail.txt", this.getPage().getRequest());
		String text = this.getWebResource(path);
		log.debug("mailTemplate=" + text);
		MailTemplate template = new MailTemplate(text, null);
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		template.addToAddress(e.getMailAddress());
		template.setFrom(MailSender.getMailFrom());
		template.setReplyTo(MailSender.getMailFrom());
		template.setArg(UserInfoTable.Entity.ID_USER_NAME, e.getUserName());

		HttpServletRequest req = this.getPage().getRequest();
		String url = req.getRequestURL().toString();
		String uri = req.getRequestURI();
		url = url.replaceAll(uri, req.getContextPath()) + UserRegistForm.getUserEnablePage() +
				"." + WebComponent.getServlet().getPageExt();

		Map<String, Object> m = new HashMap<String, Object>();
		m.put(UserInfoTable.Entity.ID_USER_ID, data.get(UserInfoTable.Entity.ID_USER_ID));
//		m.put(UserInfoTable.Entity.ID_LOGIN_ID, data.get(UserInfoTable.Entity.ID_LOGIN_ID));
		m.put(UserInfoTable.Entity.ID_MAIL_ADDRESS, data.get(UserInfoTable.Entity.ID_MAIL_ADDRESS));
//		m.put(UserInfoTable.Entity.ID_USER_NAME, data.get(UserInfoTable.Entity.ID_USER_NAME));
		String json = JSON.encode(m);
		String key = CryptUtil.encrypt(json, DataFormsServlet.getQueryStringCryptPassword());
		String enckey = java.net.URLEncoder.encode(key, DataFormsServlet.getEncoding());
		url += "?key=" + enckey;
		template.setLink("enableUserPage", url, url);
		Session session = MailSender.getMailSession();
		MailSender sender = new MailSender();
		sender.send(template, session);
	}


	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		data.put(UserInfoTable.Entity.ID_EXTERNAL_USER_FLAG, "1");
		data.put(UserInfoTable.Entity.ID_DELETE_FLAG, "0");
		Boolean sendUserEnableMail = (Boolean) config.get("sendUserEnableMail");
		if (sendUserEnableMail) {
			data.put(UserInfoTable.Entity.ID_ENABLED_FLAG, "0");
		} else {
			data.put(UserInfoTable.Entity.ID_ENABLED_FLAG, "1");
		}
		List<Map<String, Object>> attTable = new ArrayList<Map<String, Object>>();
		UserAttributeTable.Entity e = new UserAttributeTable.Entity();
		e.setUserAttributeType("userLevel");
		e.setUserAttributeValue("user");
		this.setUserInfo(e.getMap());
		attTable.add(e.getMap());
		data.put("attTable", attTable);
		UserDao dao = new UserDao(this);
		dao.insertUser(data);
		if (sendUserEnableMail) {
			this.sendMail(data);
		}
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
	}
}
