package dataforms.app.page.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.app.page.base.BasePage;
import dataforms.controller.ApplicationException;
import dataforms.controller.Response;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.CryptUtil;
import net.arnx.jsonic.JSON;

/**
 * パスワードリセットページ。
 *
 */
public class PasswordResetPage extends BasePage {
	/**
	 * パスワードリセット情報のキー。
	 */
	public static final String PASSWORD_RESET_INFO = "passwordResetInfo";
	
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(PasswordResetPage.class);
	
	/**
	 * コンストラクタ。
	 */
	public PasswordResetPage() {
		this.addForm(new ChangePasswordForm(true));
		this.setMenuItem(false);
	}
	
	
	@WebMethod
	@Override
	public Response getHtml(final Map<String, Object> p) throws Exception {
		String key = (String) p.get("key");
		if (key != null) {
			try {
				logger.debug("key=" + key);
				String json = CryptUtil.decrypt(key, DataFormsServlet.getQueryStringCryptPassword());
				logger.debug("json=" + json);
				@SuppressWarnings("unchecked")
				Map<String, Object> userInfo = (Map<String, Object>) JSON.decode(json, HashMap.class);
				logger.debug("userInfo=" + userInfo);
				this.getPage().getRequest().getSession().setAttribute(PASSWORD_RESET_INFO, userInfo);
			} catch (java.lang.IllegalArgumentException e) {
				throw new ApplicationException(this.getPage(), "error.auth");
			}
		} else {
			throw new ApplicationException(this.getPage(), "error.auth");
		}
		return super.getHtml(p);
	}
}
