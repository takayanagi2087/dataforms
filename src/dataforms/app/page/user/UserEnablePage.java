package dataforms.app.page.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.app.dao.user.UserInfoTable;
import dataforms.app.page.base.BasePage;
import dataforms.controller.Response;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.CryptUtil;
import dataforms.util.NumberUtil;
import net.arnx.jsonic.JSON;

/**
 * 外部ユーザ登録ページ。
 *
 */
public class UserEnablePage extends BasePage {

	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(UserEnablePage.class);
	
	/**
	 * コンストラクタ。
	 */
	public UserEnablePage() {
		this.addForm(new UserEnableForm());
		this.setMenuItem(false);
	}
	
	@Override
	@WebMethod
	public Response getHtml(final Map<String, Object> params) throws Exception {
		String key = (String) params.get("key");
		String json = CryptUtil.decrypt(key, DataFormsServlet.getQueryStringCryptPassword());
		@SuppressWarnings("unchecked")
		Map<String, Object> m = (Map<String, Object>) JSON.decode(json, HashMap.class);
		Long userId = NumberUtil.longValueObject(m.get(UserInfoTable.Entity.ID_USER_ID));
		String mailAddress = (String) m.get(UserInfoTable.Entity.ID_MAIL_ADDRESS);
		log.debug("userId=" + userId + ",mailAddress=" + mailAddress);
		HttpSession session = this.getPage().getRequest().getSession();
		session.setAttribute(UserInfoTable.Entity.ID_USER_ID, userId);
		session.setAttribute(UserInfoTable.Entity.ID_MAIL_ADDRESS, mailAddress);
		return super.getHtml(params);
	}
}
