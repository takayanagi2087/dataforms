package dataforms.app.page.base;

import java.util.Map;

/**
 * ユーザ向けページクラス。
 *
 */
public abstract class UserPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public UserPage() {
//		this.addPreloadJs(this.getClassScriptPath(UserPage.class));

	}

	/**
	 * {@inheritDoc}
	 * 開発者、管理者、ユーザがログインしている場合表示可能です。
	 */
	@Override
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		return this.checkUserAttribute("userLevel", "admin")
				|| this.checkUserAttribute("userLevel", "developer")
				|| this.checkUserAttribute("userLevel", "user");
	}
}
