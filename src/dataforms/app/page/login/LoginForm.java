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
	private static Logger log = Logger.getLogger(LoginForm.class.getName());

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
		this.methodStartLog(log, nopass);
		JsonResponse ret = null;
		List<ValidationError> elist = this.validate(params);
		if (elist.size() > 0) {
			ret = new JsonResponse(JsonResponse.INVALID, elist);
			log.warn("login fail");
		} else {
			UserDao dao = new UserDao(this);
			Map<String, Object> userInfo = dao.login(params);
			HttpSession session = this.getPage().getRequest().getSession();
			session.setAttribute("userInfo", userInfo);
			log.info("login success=" + userInfo.get("loginId") + "(" + userInfo.get("userId") + ")");
			ret = new JsonResponse(JsonResponse.SUCCESS, "");
		}
		this.methodFinishLog(log, ret);
		return ret;
	}
	
	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		if (!StringUtil.isBlank(LoginForm.getPasswordResetMailPage())) {
			ret.put("passwordResetMailPage", LoginForm.getPasswordResetMailPage() + "." + this.getPage().getPageExt());
		}
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

