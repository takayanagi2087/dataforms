package dataforms.app.dao.enumeration;

import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.app.field.enumeration.EnumTypeNameField;
import dataforms.dao.Table;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.LangCodeField;

/**
 * 列挙型名称テーブルクラス。
 * <pre>
 * &lt;select&gt;やラジオボタンの選択肢を管理するテーブルです。
 * </pre>
 *
 */
public class EnumTypeNameTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public EnumTypeNameTable() {
		this.addPkField(new EnumTypeCodeField());
		this.addPkField(new LangCodeField());
		this.addField(new EnumTypeNameField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 結合対象テーブルは以下の通りです。
	 * 　EnumOptionTable
	 * </pre>
	 * @see dataforms.dao.Table#getJoinCondition(dataforms.dao.Table, java.lang.String)
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof EnumOptionTable) {
			return (
				this.getLinkFieldCondition("enumTypeCode", joinTable, alias, "enumTypeCode")
				+ " and " + this.getLinkFieldCondition("langCode", joinTable, alias, "langCode")
			);
		}
		return null;
	}
}
