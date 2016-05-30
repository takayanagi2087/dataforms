package dataforms.debug.page.filetest;

import dataforms.app.page.base.AdminPage;
import dataforms.dialog.image.ImageDialog;

/**
 * ページクラス。
 */
public class FileTestPage extends AdminPage {
	/**
	 * コンストラクタ。
	 */
	public FileTestPage() {
		this.addForm(new FileTestQueryForm());
		this.addForm(new FileTestQueryResultForm());
		this.addForm(new FileTestEditForm());
		this.addDialog(new ImageDialog());
	}
}
