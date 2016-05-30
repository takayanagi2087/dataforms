package dataforms.app.dao.user;

import dataforms.app.field.user.UserAttributeTypeField;
import dataforms.app.field.user.UserAttributeValueField;
import dataforms.app.field.user.UserIdField;
import dataforms.dao.Table;

/**
 * ユーザ属性テーブルクラス。
 */
public class UserAttributeTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public UserAttributeTable() {
		this.addPkField(new UserIdField());
		this.addPkField(new UserAttributeTypeField());
		this.addPkField(new UserAttributeValueField());
		this.addUpdateInfoFields();
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
			return 	(this.getLinkFieldCondition("userAttributeType", joinTable, alias, "enumTypeCode") + " and " +
					this.getLinkFieldCondition("userAttributeValue", joinTable, alias, "enumOptionCode"));
		}
		return null;
	}
}
