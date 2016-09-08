package dataforms.app.dao.enumeration;

import dataforms.dao.SubQuery;
import dataforms.dao.Table;
import dataforms.dao.TableRelation;

/**
 * 列挙型グループテーブルの関係を定義するクラスです。
 *
 */
public class EnumGroupTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public EnumGroupTableRelation(final Table table) {
		super(table);
	}
	
	/**
	 * {@inheritDoc}
	 * <pre>
	 * 結合対象テーブルは以下の通りです。
	 * 	EnumTypeNameTable
	 * </pre>
	 * @see dataforms.dao.Table#getJoinCondition(dataforms.dao.Table, java.lang.String)
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String joinTableAlias) {
		if (joinTable instanceof EnumTypeNameTable || joinTable instanceof SubQuery) {
			return (
				this.getTable().getLinkFieldCondition(EnumGroupTable.Entity.ID_ENUM_TYPE_CODE, 
						joinTable, joinTableAlias, EnumTypeNameTable.Entity.ID_ENUM_TYPE_CODE)
			);
		}
		return null;
	}
}
