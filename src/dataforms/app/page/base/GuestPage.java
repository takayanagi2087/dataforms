package dataforms.app.page.base;

import java.util.Map;

/**
 * ゲスト向けページクラス。
 *
 */
public abstract class GuestPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public GuestPage() {
//		this.addPreloadJs(this.getClassScriptPath(GuestPage.class));

	}

	/**
	 * {@inheritDoc}
	 * 開発者、管理者、ユーザ、ゲストがログインしている場合表示可能です。
	 */
	@Override
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		return this.checkUserAttribute("userLevel", "admin")
				|| this.checkUserAttribute("userLevel", "developer")
				|| this.checkUserAttribute("userLevel", "user")
				|| this.checkUserAttribute("userLevel", "guest");
	}
}
