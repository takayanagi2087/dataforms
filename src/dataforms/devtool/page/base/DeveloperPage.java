package dataforms.devtool.page.base;

import java.util.Map;

import dataforms.app.page.base.BasePage;
import dataforms.servlet.DataFormsServlet;

/**
 * 開発者向けページのベースクラス.
 *
 */
public abstract class DeveloperPage extends BasePage {

	/**
	 * Log.
	 */
//	private static Logger log = Logger.getLogger(DeveloperPage.class);


	/**
	 * Javaソースの出力パス。
	 */
	private static String javaSourcePath = null;

	/**
	 * webソースの出力パス。
	 */
	private static String webSourcePath = null;

	/**
	 * コンストラクタ。
	 */
	public DeveloperPage() {
	}

	/**
	 * Javaソースの出力パスを取得します。
	 * @return Javaソースの出力パス。
	 */
	public static String getJavaSourcePath() {
		return javaSourcePath;
	}

	/**
	 * Javaソースの出力パスを設定します。
	 * @param javaSourcePath Javaソースの出力パス。
	 */
	public static void setJavaSourcePath(final String javaSourcePath) {
		DeveloperPage.javaSourcePath = javaSourcePath;
	}

	/**
	 * Webソースの出力パスを取得します。
	 * @return Webソースの出力パス。
	 */
	public static String getWebSourcePath() {
		return webSourcePath;
	}

	/**
	 * Webソースの出力パスを設定します。
	 * @param webSourcePath Webソースの出力パス。
	 */
	public static void setWebSourcePath(final String webSourcePath) {
		DeveloperPage.webSourcePath = webSourcePath;
	}

	/**
	 * {@inheritDoc}
	 * ユーザレベルがdeveloperのユーザのみアクセス可能です。
	 */
	@Override
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		if (DataFormsServlet.isDisableDeveloperTools()) {
			return false;
		}
		/*		log.debug("getRequestURL=" + this.getRequest().getRemoteHost());
		String requrl = this.getRequest().getRequestURL().toString();
		if (!Pattern.matches("^(http|https)\\://localhost.*$", requrl)) {
			return false;
		}*/
		return checkUserAttribute("userLevel", "developer");
	}
}
