package dataforms.debug.dao.alltype;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;

/**
 * 全項目タイプテーブルの関係を定義するクラスです。
 *
 */
public class AllTypeTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table テーブル。
	 */
	public AllTypeTableRelation(final Table table) {
		super(table);
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof AllTypeAttachFileTable) {
			return this.getTable().getLinkFieldCondition("recordIdField", joinTable, alias, "recordIdField");
		}
		return null;
	}
}
