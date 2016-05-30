package dataforms.app.page.user;

import dataforms.app.page.base.BasePage;

/**
 * ユーザ情報入力ページクラス。
 *
 */
public class RegistUserPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public RegistUserPage() {
		this.addForm(new UserEditForm(false));
		this.setMenuItem(false);
	}
}
