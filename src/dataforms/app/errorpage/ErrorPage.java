package dataforms.app.errorpage;

import java.util.Map;

import dataforms.app.page.base.BasePage;

/**
 * エラー表示ページクラス。
 */
public class ErrorPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public ErrorPage() {
		this.setMenuItem(false);
	}

	/**
	 * {@inheritDoc}
	 * 常にtrueを返します。
	 */
	@Override
	public boolean isAuthenticated(final Map<String, Object> param) throws Exception {
		return true; // 常にOK
	}
}
