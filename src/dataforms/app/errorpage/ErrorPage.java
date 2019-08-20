package dataforms.app.errorpage;

import java.util.Map;

import dataforms.app.page.base.BasePage;

/**
 * エラー表示ページクラス。
 */
public class ErrorPage extends BasePage {

	/**
	 * エラーメッセージのセッションID。
	 */
	public static final String ID_ERROR_MESSAGE = "errorMessage";

	/**
	 * コンストラクタ。
	 */
	public ErrorPage() {
		this.setMenuItem(false);
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> prop = super.getProperties();
		String errorMessage = (String) this.getRequest().getSession().getAttribute(ID_ERROR_MESSAGE);
		prop.put(ID_ERROR_MESSAGE, errorMessage);
		this.getRequest().getSession().removeAttribute(ID_ERROR_MESSAGE);
		return prop;
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
