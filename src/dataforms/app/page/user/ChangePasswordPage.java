package dataforms.app.page.user;

import dataforms.app.page.base.UserPage;

/**
 * パスワード変更ページ。
 *
 */
public class ChangePasswordPage extends UserPage {
	/**
	 * コンストラクタ。
	 */
	public ChangePasswordPage() {
		this.addForm(new ChangePasswordForm(false));
	}
}
