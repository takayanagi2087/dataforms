package dataforms.debug.page;

import dataforms.app.page.base.AdminPage;
import dataforms.app.page.enumeration.EnumMasterEditForm;

/**
 * 列挙型マスタフォームデバッグページ。
 *
 */
public class EnumMasterPage extends AdminPage {
	/**
	 * コンストラクタ。
	 */
	public EnumMasterPage() {
		this.addForm(new EnumMasterEditForm("section", true));
//		this.addForm(new EnumMasterEditForm("test"));
	}
}
