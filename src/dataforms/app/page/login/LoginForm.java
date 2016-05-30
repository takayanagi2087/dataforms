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

};

