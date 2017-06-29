package dataforms.app.page.enumeration;

import dataforms.app.dao.enumeration.EnumGroupDao;
import dataforms.app.dao.enumeration.EnumGroupTable;
import dataforms.app.page.base.AdminPage;
import dataforms.dao.Dao;
import dataforms.dao.Table;


/**
 * ページクラス。
 */
public class EnumGroupPage extends AdminPage {
	/**
	 * コンストラクタ。
	 */
	public EnumGroupPage() {
		this.addForm(new EnumGroupQueryForm());
		this.addForm(new EnumGroupQueryResultForm());
		this.addForm(new EnumGroupEditForm());

	}

	/**
	 * Pageが配置されるパスを返します。
	 *
	 * @return Pageが配置されるパス。
	 */
	public String getFunctionPath() {
		return "/dataforms/app";
	}

	/**
	 * 操作対象テーブルクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成用のメソッドです。
	 * </pre>
	 * @return 操作対象テーブル。
	 */
	public Class<? extends  Table> getTableClass() {
		return EnumGroupTable.class;
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return EnumGroupDao.class;
	}

}
