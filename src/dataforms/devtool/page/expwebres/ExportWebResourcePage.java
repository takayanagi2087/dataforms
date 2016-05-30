package dataforms.devtool.page.expwebres;

import dataforms.devtool.page.base.DeveloperPage;

/**
 * webリソースエクスポートページクラス。
 *
 */
public class ExportWebResourcePage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public ExportWebResourcePage() {
		this.addForm(new ExportWebResourceQueryForm());
		this.addForm(new ExportWebResourceQueryResultForm());
	}
}

