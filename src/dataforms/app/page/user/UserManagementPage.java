package dataforms.app.page.user;

import dataforms.app.page.base.AdminPage;

/**
 * ユーザ管理ページクラス。
 *
 */
public class UserManagementPage extends AdminPage {
	/**
	 * コンストラクタ。
	 */
	public UserManagementPage() {
		this.addForm(new UserQueryForm());
		this.addForm(new UserQueryResultForm());
		this.addForm(new UserEditForm(true));
	}
}
