package dataforms.app.page.base;

import java.util.Map;

/**
 * 監理者向けページクラス。
 *
 */
public abstract class AdminPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public AdminPage() {
//		this.addPreloadJs(this.getClassScriptPath(AdminPage.class));

	}

	/**
	 * {@inheritDoc}
	 * 開発者、監理者権限を持つユーザのみ表示可能です。
	 */
	@Override
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		return this.checkUserAttribute("userLevel", "admin") || this.checkUserAttribute("userLevel", "developer");
	}
}
