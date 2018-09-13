package dataforms.app.dao.func;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;


/**
 * 機能情報テーブルの関係を定義するクラスです。
 *
 */
public class FuncInfoTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table テーブル。
	 */
	public FuncInfoTableRelation(final Table table) {
		super(table);
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		return null;
	}
}
