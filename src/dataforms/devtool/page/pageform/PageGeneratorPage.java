package dataforms.devtool.page.pageform;

import dataforms.devtool.page.base.DeveloperPage;

/**
 * ページJavaクラス作成ページ。
 */
public class PageGeneratorPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public PageGeneratorPage() {
		this.addForm(new PageGeneratorQueryForm());
		this.addForm(new PageGeneratorQueryResultForm());
		this.addForm(new PageGeneratorEditForm());
	}
	
	
}
