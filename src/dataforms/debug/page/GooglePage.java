package dataforms.debug.page;

import dataforms.app.page.base.BasePage;

/**
 * Googleを表示するページ。
 *
 */
public class GooglePage extends BasePage {

	/**
	 * コンストラクタ。
	 */
	public GooglePage() {

	}

	@Override
	public String getMenuUrl() {
		return "https://www.google.com";
	}

	@Override
	public String getMenuTarget() {
		return "_blank";
	}
}
