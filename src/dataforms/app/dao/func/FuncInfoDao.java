package dataforms.app.dao.func;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.dao.Dao;
import dataforms.dao.JDBCConnectableObject;

/**
 * 機能テーブルアクセスクラス。
 * <pre>
 * FuncInfoTableには、ページを配置するパスを記述します。
 *  /func01というパスを登録すると、パッケージ"func01"以下の
 *  Pageクラスをメニューに表示します。
 *  Pageクラスは/func01以下の対応するhtmlを読み込み表示します。
 * ページおよび機能の名称は/func01/Function.propertiesに記録します。
 * </pre>
 *
 */
public class FuncInfoDao extends Dao {
	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public FuncInfoDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}

	/**
	 * 機能リストを取得します。
	 * @param all 削除フラグを無視する場合true。
	 * @return 機能リスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryFuncList(final boolean all) throws Exception {
		FuncInfoQuery query = new FuncInfoQuery();
		query.setEffectivenessOfDeleteFlag(!all);
		List<Map<String, Object>> list = this.executeQuery(query);
		return list;
	}

	/**
	 * 機能テーブルを保存します。
	 * @param list 機能リスト。
	 * @throws Exception 例外。
	 */
	public void saveFuncList(final List<Map<String, Object>> list) throws Exception {
		FuncInfoTable tbl = new FuncInfoTable();
		String sql = "delete from " + tbl.getTableName();
		this.executeUpdate(sql, new HashMap<String, Object>());
		this.executeInsert(new FuncInfoTable(), list);
	}
}
