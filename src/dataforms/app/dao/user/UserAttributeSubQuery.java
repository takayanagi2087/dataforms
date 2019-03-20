package dataforms.app.dao.user;

import dataforms.app.dao.enumeration.EnumOptionNameSubQuery;
import dataforms.app.dao.enumeration.EnumOptionNameTable;
import dataforms.dao.Query;
import dataforms.dao.SubQuery;
import dataforms.dao.Table;
import dataforms.field.base.FieldList;

/**
 * 特定のユーザ属性のサブクエリ。
 */
public class UserAttributeSubQuery extends SubQuery {
	/**
	 * QueryFormで指定した条件で行う問い合わせクラスです。
	 */
	private static class EnumOptionTableQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param enumTypeCode 列挙型コード。
		 */
		public EnumOptionTableQuery(final String enumTypeCode) {
			UserAttributeTable table = new UserAttributeTable();
			FieldList flist = new FieldList();
			flist.addAll(table.getFieldList());
			this.setFieldList(flist);
			this.setMainTable(table);
			this.setCondition("m.user_attribute_type='" + enumTypeCode + "'");
		}
	}

	/**
	 * コンストラクタ。
	 * @param enumTypeCode ユーザ属性を示す列挙型コード。
	 */
	public UserAttributeSubQuery(final String enumTypeCode) {
		super(new EnumOptionTableQuery(enumTypeCode));
	}

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof EnumOptionNameSubQuery) {
			return this.getLinkFieldCondition(UserAttributeTable.Entity.ID_USER_ATTRIBUTE_VALUE, joinTable, alias, EnumOptionNameTable.Entity.ID_ENUM_OPTION_CODE);
		}
		return super.getJoinCondition(joinTable, alias);
	}
}
