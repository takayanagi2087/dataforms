package dataforms.app.dao.user;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;

/**
 * ユーザ属性テーブルの関係を定義するクラスです。
 *
 */
public class UserAttributeTableRelation extends TableRelation {
	
	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public UserAttributeTableRelation(final Table table) {
		super(table);
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 結合対象テーブルは以下の通りです。
	 * EnumOptionNameTable	(aliasが"nm"のもの)
	 * </pre>
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if ("nm".equals(alias)) {
			return 	(this.getTable().getLinkFieldCondition("userAttributeType", joinTable, alias, "enumTypeCode") + " and " +
					this.getTable().getLinkFieldCondition("userAttributeValue", joinTable, alias, "enumOptionCode"));
		}
		return null;
	}

}
