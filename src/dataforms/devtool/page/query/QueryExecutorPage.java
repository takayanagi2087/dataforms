package dataforms.devtool.page.query;

import dataforms.dao.Dao;
import dataforms.dao.Table;
import dataforms.devtool.page.base.DeveloperPage;


/**
 * ページクラス。
 */
public class QueryExecutorPage extends DeveloperPage {
	/**
	 * コンストラクタ。
	 */
	public QueryExecutorPage() {
		this.addForm(new QueryExecutorQueryForm());
		this.addForm(new QueryExecutorQueryResultForm());

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
	 * ページjavaクラス作成で用のメソッドです。
	 * </pre>
	 * @return 操作対象テーブル。
	 */
	public Class<? extends  Table> getTableClass() {
		return Table.class;
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成で用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return Dao.class;
	}

}
