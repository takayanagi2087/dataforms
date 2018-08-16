package dataforms.app.page.top;

import java.util.Map;

import dataforms.annotation.WebMethod;
import dataforms.app.page.base.BasePage;
import dataforms.app.page.login.LoginForm;
import dataforms.controller.RedirectResponse;
import dataforms.controller.Response;
import dataforms.devtool.dao.db.TableManagerDao;

/**
 * トップページクラス。
 * <pre>
 * アプリケーション、ユーザの状態に応じて、適切なページにリダイレクトします。
 * </pre>
 *
 */
public class TopPage extends BasePage {

    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(TopPage.class.getName());
	/**
	 * コンストラクタ。
	 */
	public TopPage() {
		this.setMenuItem(false);
		//this.addForm(new LoginForm());
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * ページは表示せず、状態に応じてリダイレクションを行ないます。
	 * DBの初期化前	... DB初期化ページ。
	 * ログイン前 ... ログインページ。
	 * ログイン後 ... サイトマップページ。
	 * </pre>
	 */
	@WebMethod
	@Override
	public Response getHtml(final Map<String, Object> params) throws Exception {
		String context = this.getRequest().getContextPath();
		TableManagerDao dao = new TableManagerDao(this);
		if (dao.isDatabaseInitialized()) {
			LoginForm loginForm = new LoginForm();
			loginForm.autoLogin();
			if (this.getUserInfo() == null) {
				return new RedirectResponse(context + "/dataforms/app/page/login/LoginPage." + this.getPageExt());
			} else {
				return new RedirectResponse(context + "/dataforms/app/page/sitemap/SiteMapPage." + this.getPageExt());
			}
		} else {
			return new RedirectResponse(context + "/dataforms/devtool/page/db/InitializeDatabasePage." + this.getPageExt());
		}
	}
}
