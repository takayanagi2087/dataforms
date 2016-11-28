package dataforms.debug.dao.filetest;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;

/**
 * FileFieldTestTableの関係を定義するクラスです。
 *
 */
public class FileFieldTestTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public FileFieldTestTableRelation(final Table table) {
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
