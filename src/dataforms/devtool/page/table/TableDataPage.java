package dataforms.devtool.page.table;

import dataforms.app.page.base.BasePage;
import dataforms.dao.Dao;
import dataforms.dao.Table;
import dataforms.dao.TableDao;


/**
 * ページクラス。
 */
public class TableDataPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public TableDataPage() {
		this.addForm(new TableDataQueryForm());
		this.addForm(new TableDataQueryResultForm());
		this.addForm(new TableDataEditForm());
		this.setMenuItem(false);
	}

	/**
	 * Pageが配置されるパスを返します。
	 *
	 * @return Pageが配置されるパス。
	 */
	public String getFunctionPath() {
		return "/dataforms/devtool";
	}

	/**
	 * 操作対象テーブルクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成用のメソッドです。
	 * </pre>
	 * @return 操作対象テーブル。
	 */
	public Class<? extends  Table> getTableClass() {
		return Table.class;
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return TableDao.class;
	}

}
