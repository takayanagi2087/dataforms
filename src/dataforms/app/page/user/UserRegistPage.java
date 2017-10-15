package dataforms.app.page.user;

import dataforms.app.page.base.BasePage;

/**
 * 外部ユーザ登録ページ。
 *
 */
public class UserRegistPage extends BasePage {
	
	
	/**
	 * コンストラクタ。
	 */
	public UserRegistPage() {
		this.addForm(new UserRegistForm());
		this.setMenuItem(true);
	}
	
}
