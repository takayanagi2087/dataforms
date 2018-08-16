package dataforms.app.page.login;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.app.dao.user.UserDao;
import dataforms.app.field.user.LoginIdField;
import dataforms.app.field.user.PasswordField;
import dataforms.controller.Form;
import dataforms.controller.JsonResponse;
import dataforms.field.common.FlagField;
import dataforms.util.AutoLoginCookie;
import dataforms.util.StringUtil;
import dataforms.validator.ValidationError;

/**
 * ログインフォームクラス。
 *
 */
public class LoginForm extends Form {
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(LoginForm.class.getName());

	/**
	 * ユーザ登録ページのアドレス。
	 */
	private static String passwordResetMailPage = null;

	
	/**
	 * コンストラクタ。
	 */
	public LoginForm() {
		super("loginForm");
		this.addField(new LoginIdField());
		PasswordField pw = new PasswordField();
		this.addField(pw);
		this.addField(new FlagField(AutoLoginCookie.ID_KEEP_LOGIN));
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData(AutoLoginCookie.ID_KEEP_LOGIN, AutoLoginCookie.getKeepLoginFlag(this.getPage()));
	}
	
	/**
	 * 自動ログイン処理を行います。
	 * @throws Exception 例外。
	 */
	public void autoLogin() throws Exception {
		AutoLoginCookie.autoLogin(this.getPage());
	}
	
	
	/**
	 * ログインの処理を行います。
	 * @param params パラメータ。
	 * @return ログイン結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse login(final Map<String, Object> params) throws Exception {
		// ログからパスワードを削除
		Map<String, Object> nopass = new HashMap<String, Object>();
		nopass.putAll(params);
		nopass.remove("password");
		this.methodStartLog(logger, nopass);
		JsonResponse ret = null;
		List<ValidationError> elist = this.validate(params);
		if (elist.size() > 0) {
			ret = new JsonResponse(JsonResponse.INVALID, elist);
			logger.warn("login fail");
		} else {
			UserDao dao = new UserDao(this);
			Map<String, Object> userInfo = dao.login(params);
			HttpSession session = this.getPage().getRequest().getSession();
			session.setAttribute("userInfo", userInfo);
			logger.info("login success=" + userInfo.get("loginId") + "(" + userInfo.get("userId") + ")");
			AutoLoginCookie.setAutoLoginCookie(this.getPage(), params);
			ret = new JsonResponse(JsonResponse.SUCCESS, "");
		}
		this.methodFinishLog(logger, ret);
		return ret;
	}
	
	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		if (!StringUtil.isBlank(LoginForm.getPasswordResetMailPage())) {
			ret.put("passwordResetMailPage", LoginForm.getPasswordResetMailPage() + "." + this.getPage().getPageExt());
		}
		ret.put("autoLogin", AutoLoginCookie.isAutoLogin());
		return ret;
	}
	
	/**
	 * パスワードリセットメール送信ページを取得します。
	 * @return パスワードリセットメール送信ページ。
	 */
	public static String getPasswordResetMailPage() {
		return passwordResetMailPage;
	}

	/**
	 * パスワードリセットメール送信ページを設定します。
	 * @param passwordResetMailPage パスワードリセットメール送信ページ。
	 * 
	 */
	public static void setPasswordResetMailPage(final String passwordResetMailPage) {
		LoginForm.passwordResetMailPage = passwordResetMailPage;
	}

};

