package dataforms.devtool.page.db;

import dataforms.controller.Dialog;

/**
 * インポートダイアログクラス。
 * <pre>
  * インポートデータが存在するフォルダを指定するフォームです。
* </pre>
 *
 */
public class ImportDataDialog extends Dialog {
	/**
	 * コンストラクタ。
	 */
	public ImportDataDialog() {
		super("importDataDialog");
		this.addForm(new ImportDataForm());
	}
}
