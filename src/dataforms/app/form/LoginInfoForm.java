package dataforms.app.form;

import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.app.field.user.LoginIdField;
import dataforms.app.field.user.UserNameField;
import dataforms.controller.Form;
import dataforms.controller.JsonResponse;
import dataforms.util.AutoLoginCookie;

/**
 * ログイン情報フォームクラス。
 * <pre>
 * ユーザのログイン状況を表示するフォームです。
 * フレーム中に表示されます。
 * </pre>
 */
public class LoginInfoForm extends Form {

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(LoginInfoForm.class.getName());

	/**
	 * ユーザ登録ページのアドレス。
	 */
	private static String userRegistPage = null;

	/**
	 * コンストラクタ。
	 */
	public LoginInfoForm() {
		super("loginInfoForm");
		this.addField(new LoginIdField());
		this.addField(new UserNameField());
	}


	/**
	 * ユーザ登録ページのアドレスを取得します。
	 * @return ユーザ登録ページのアドレス
	 */
	public static String getUserRegistPage() {
		return userRegistPage;
	}

	/**
	 * ユーザ登録ページのアドレスを設定します。
	 * @param userRegistPage ユーザ登録ページのアドレス。
	 */
	public static void setUserRegistPage(final String userRegistPage) {
		LoginInfoForm.userRegistPage = userRegistPage;
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.getUserInfo();
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * ログイン情報フォームの場合アプリケーションによってデザインを変更できるように
	 * フレームフォルダを参照するように変更します。
	 * </pre>
	 * @param cls パスを取得するクラス。
	 * @return デフォルトのhtml,jsパス。
	 */
	@Override
	public String getWebResourcePath(final Class<?> cls) {
//		return Page.getFramePath() + "/" + cls.getSimpleName();
		if (cls.getName().equals(this.getClass().getName())) {
			return this.getPage().getPageFramePath().substring(1) + "/" + cls.getSimpleName();
		} else {
			return super.getWebResourcePath(cls);
		}
	}

	/**
	 * ログイン中のユーザ情報を取得します。
	 */
	private void getUserInfo() {
    	@SuppressWarnings("unchecked")
		Map<String, Object> userInfo = (Map<String, Object>) this.getPage().getRequest().getSession().getAttribute("userInfo");
    	if (userInfo != null) {
        	this.setFormData("loginId", userInfo.get("loginId"));
        	this.setFormData("userName", userInfo.get("userName"));
    	} else {
        	this.setFormData("loginId", null);
        	this.setFormData("userName", null);
    	}
	}

	/**
	 * ログインユーザ情報を取得します。
	 * @param param パラメータ。
	 * @return ログインユーザ情報。
	 * @throws Exception 例外。
	 */
    @WebMethod(everyone = true)
	public JsonResponse getUserInfo(final Map<String, Object> param) throws Exception {
    	this.getUserInfo();
    	JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, this.getFormDataMap());
    	return ret;
    }

	/**
	 * ログアウト処理をないます。
	 * @param param パラメータ。
	 * @return 実行結果。
	 * @throws Exception 例外。
	 */
    @WebMethod
	public JsonResponse logout(final Map<String, Object> param) throws Exception {
		this.methodStartLog(log, param);
		Map<String, Object> userInfo = this.getPage().getUserInfo();
		log.info("logout success=" + userInfo.get("loginId") + "(" + userInfo.get("userId") + ")");
		this.getPage().getRequest().getSession().setAttribute("userInfo", null);
		AutoLoginCookie.clearAutoLoginCookie(this.getPage());
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, "");
		this.methodFinishLog(log, ret);
		return ret;
    }


    @Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("userRegistPage", LoginInfoForm.getUserRegistPage());
		return ret;
	}
}
