package dataforms.devtool.page.version;

import dataforms.app.page.base.BasePage;

/**
 * バージョン情報ページクラスです。
 *
 */
public class VersionInfoPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public VersionInfoPage() {
		this.addForm(new VersionInfoForm());
	}
}
