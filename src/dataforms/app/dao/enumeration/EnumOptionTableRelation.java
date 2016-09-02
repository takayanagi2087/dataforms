package dataforms.app.dao.enumeration;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;

/**
 * 列挙型選択肢テーブルの関係を定義するクラスです。
 *
 */
public class EnumOptionTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public EnumOptionTableRelation(final Table table) {
		super(table);
	}
	
	/**
	 * {@inheritDoc}
	 * <pre>
	 * 結合対象テーブルは以下の通りです。
	 * 　EnumOptionNameTable
	 * </pre>
	 * @see dataforms.dao.Table#getJoinCondition(dataforms.dao.Table, java.lang.String)
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof EnumOptionNameTable) {
			return (
				this.getTable().getLinkFieldCondition("enumTypeCode", joinTable, alias, "enumTypeCode") + " and " +
						this.getTable().getLinkFieldCondition("enumOptionCode", joinTable, alias, "enumOptionCode")
			);
		} else if (joinTable instanceof EnumTypeNameTable) {
			return (this.getTable().getLinkFieldCondition("enumTypeCode", joinTable, alias, "enumTypeCode"));
		}
		return null;
	}

}
