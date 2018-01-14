package dataforms.app.page.user;

import dataforms.app.page.base.BasePage;

/**
 * パスワードリセットメール送信フォームクラス。
 *
 */
public class PasswordResetMailPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public PasswordResetMailPage() {
		this.addForm(new PasswordResetMailForm());
	}
}
