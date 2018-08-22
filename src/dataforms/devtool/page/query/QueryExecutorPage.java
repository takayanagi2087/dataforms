package dataforms.devtool.page.query;

import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.Response;
import dataforms.dao.Dao;
import dataforms.dao.Table;
import dataforms.devtool.page.base.DeveloperPage;


/**
 * ページクラス。
 */
public class QueryExecutorPage extends DeveloperPage {
	
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(QueryExecutorPage.class);

	/**
	 * テーブル名のセッションID。
	 */
	public static final String ID_TABLE_NAME = "tableName";

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

	@WebMethod
	@Override
	public Response getHtml(final Map<String, Object> params) throws Exception {
		String t = (String) params.get("t");
		logger.debug("tableName=" + t);
		this.getRequest().getSession().setAttribute(ID_TABLE_NAME, t);
		return super.getHtml(params);
	}
}
