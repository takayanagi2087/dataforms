package dataforms.app.dao.enumeration;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;


/**
 * 列挙型名称テーブルの関係を定義するクラスです。
 *
 */
public class EnumTypeNameTableRelation extends TableRelation {
	
	/**
	 * コンストラクタ。
	 * @param table  対象テーブル。
	 */
	public EnumTypeNameTableRelation(final Table table) {
		super(table);
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 結合対象テーブルは以下の通りです。
	 * 　EnumOptionTable
	 * </pre>
	 */

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
/*		if (joinTable instanceof EnumOptionTable) {
			return (
				this.getTable().getLinkFieldCondition("enumTypeCode", joinTable, alias, "enumTypeCode")
				+ " and " + this.getTable().getLinkFieldCondition("langCode", joinTable, alias, "langCode")
			);
		}*/
		return null;
	}

}
