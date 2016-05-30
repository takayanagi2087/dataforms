package dataforms.app.dialog.user;

import dataforms.app.page.user.UserQueryForm;
import dataforms.app.page.user.UserQueryResultForm;
import dataforms.controller.Dialog;

/**
 * ユーザ問い合わせダイアログクラス。
 * <pre>
 * ユーザを検索し、入力するためのダイアログです。
 * </pre>
 *
 */
public class UserQueryDialog extends Dialog {

	/**
	 * コンストラクタ。
	 */
	public UserQueryDialog() {
		super("userQueryDialog");
		this.addForm(new UserQueryForm());
		this.addForm(new UserQueryResultForm());
	}
}
