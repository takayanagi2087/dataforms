package dataforms.app.dao.enumeration;

import dataforms.app.field.enumeration.EnumOptionCodeField;
import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.dao.Table;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.SortOrderField;

/**
 * 列挙型選択肢テーブルクラス。
 * <pre>
 * &lt;select&gt;やラジオボタンの選択肢を管理するテーブルです。
 * </pre>
 *
 */
public class EnumOptionTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public EnumOptionTable() {
		this.setComment("列挙型オプションテーブル");
		this.addPkField(new EnumTypeCodeField());
		this.addPkField(new EnumOptionCodeField());
		this.addField(new SortOrderField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
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
				this.getLinkFieldCondition("enumTypeCode", joinTable, alias, "enumTypeCode") + " and " +
				this.getLinkFieldCondition("enumOptionCode", joinTable, alias, "enumOptionCode")
			);
		} else if (joinTable instanceof EnumTypeNameTable) {
			return (this.getLinkFieldCondition("enumTypeCode", joinTable, alias, "enumTypeCode"));
		}
		return null;
	}
}
