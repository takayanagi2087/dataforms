package dataforms.devtool.page.db;

import dataforms.controller.Dialog;

/**
 * テーブル情報ダイアログクラス。
 */
public class TableInfoDialog extends Dialog {

	/**
	 * コンストラクタ。
	 */
	public TableInfoDialog() {
		super(null);
		this.addForm(new TableInfoForm());
	}

}
