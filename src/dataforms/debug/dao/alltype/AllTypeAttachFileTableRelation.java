package dataforms.debug.dao.alltype;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;

/**
 * 全項目タイプ添付ファイルテーブルの関係を定義するクラスです。
 *
 */
public class AllTypeAttachFileTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table テーブル。
	 */
	public AllTypeAttachFileTableRelation(final Table table) {
		super(table);
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		return null;
	}
}
