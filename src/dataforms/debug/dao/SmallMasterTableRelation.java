package dataforms.debug.dao;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;

/**
 * SmallMasterTableの関係を定義するクラスです。
 *
 */
public class SmallMasterTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public SmallMasterTableRelation(final Table table) {
		super(table);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		return null;
	}
}
