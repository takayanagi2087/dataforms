package dataforms.app.dao.user;

import java.util.ArrayList;
import java.util.List;

import dataforms.app.dao.enumeration.EnumOptionTable;
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
	 * 外部キーリスト。
	 */
	private static List<ForeignKey> foreignKeyList = null;
	
	/**
	 * 外部キーリストの作成。
	 */
	static {
		UserAttributeTableRelation.foreignKeyList = new ArrayList<ForeignKey>();
		{
			String[] flist = {UserAttributeTable.Entity.ID_USER_ATTRIBUTE_TYPE, UserAttributeTable.Entity.ID_USER_ATTRIBUTE_VALUE};
			String[] rflist = {EnumOptionTable.Entity.ID_ENUM_TYPE_CODE, EnumOptionTable.Entity.ID_ENUM_OPTION_CODE};
			ForeignKey fk = new ForeignKey("fkUserAttribute000", flist, EnumOptionTable.class, rflist);
			UserAttributeTableRelation.foreignKeyList.add(fk);
		}
		{
			ForeignKey fk = new ForeignKey("fkUserAttribute001", UserAttributeTable.Entity.ID_USER_ID, UserInfoTable.class, UserInfoTable.Entity.ID_USER_ID);
			UserAttributeTableRelation.foreignKeyList.add(fk);
		}
	}
	
	@Override
	public List<ForeignKey> getForeignKeyList() {
		return UserAttributeTableRelation.foreignKeyList;
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
			return 	(this.getTable().getLinkFieldCondition(UserAttributeTable.Entity.ID_USER_ATTRIBUTE_TYPE, joinTable, alias, EnumOptionTable.Entity.ID_ENUM_TYPE_CODE) + " and " +
					this.getTable().getLinkFieldCondition(UserAttributeTable.Entity.ID_USER_ATTRIBUTE_VALUE, joinTable, alias, EnumOptionTable.Entity.ID_ENUM_OPTION_CODE));
		}
		return null;
	}

}
