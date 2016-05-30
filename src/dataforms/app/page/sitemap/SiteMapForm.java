package dataforms.app.page.sitemap;

import dataforms.app.form.MenuForm;
import dataforms.menu.Menu;

/**
 * サイドメニューフォームクラス。
 *
 */
public class SiteMapForm extends MenuForm {
	/**
	 * コンストラクタ。
	 */
	public SiteMapForm() {
		super(null);
	}

	@Override
	protected Menu newMenuComponent() {
		Menu menu = new Menu();
		return menu;
	}
}
