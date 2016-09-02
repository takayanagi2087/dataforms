package dataforms.app.dao.user;

import dataforms.dao.Table;
import dataforms.dao.TableRelation;

/**
 * ユーザテーブルの関係を定義するクラスです。
 *
 */
public class UserInfoTableRelation extends TableRelation {
	/**
	 * コンストラクタ。
	 * @param table 対象テーブル。
	 */
	public UserInfoTableRelation(final Table table) {
		super(table);
	}
	
	/**
	 * {@inheritDoc}
	 * <pre>
	 * 結合対象テーブルは以下の通りです。
	 * 	UserAttributeTable
	 *  UserAttributeQueryのサブクエリ　alias="ua"のサブクエリ
	 *  alias="ul"のサブクエリ
	 * </pre>
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof UserAttributeTable
				|| "ua".equals(alias) || "ul".equals(alias)) {
			return (
					this.getTable().getLinkFieldCondition("userId", joinTable, alias, "userId")
			);
		}
		return null;
	}

}
