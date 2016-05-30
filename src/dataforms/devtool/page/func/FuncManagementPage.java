package dataforms.devtool.page.func;

import dataforms.devtool.page.base.DeveloperPage;

/**
 * 機能管理ページクラス。
 *
 */
public class FuncManagementPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public FuncManagementPage() {
		this.addForm(new FuncEditForm());
	}
}
