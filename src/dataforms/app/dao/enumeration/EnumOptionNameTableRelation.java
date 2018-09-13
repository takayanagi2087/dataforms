package dataforms.app.dao.enumeration;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;

/**
 * 列挙型選択肢名称テーブルの関係を定義するクラスです。
 *
 */
public class EnumOptionNameTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table テーブル。
	 */
	public EnumOptionNameTableRelation(final Table table) {
		super(table);
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		return null;
	}
}
