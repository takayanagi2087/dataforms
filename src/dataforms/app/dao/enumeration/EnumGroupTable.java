package dataforms.app.dao.enumeration;

import dataforms.app.field.enumeration.EnumGroupCodeField;
import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.dao.SubQuery;
import dataforms.dao.Table;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.SortOrderField;

/**
 * 列挙型グループテーブルクラス。
 * <pre>
 * 列挙型のグルーブを作るためのテーブルです。
 * </pre>
 *
 */
public class EnumGroupTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public EnumGroupTable() {
		this.setComment("列挙型グループテーブル");
		this.addPkField(new EnumGroupCodeField());
		this.addPkField(new EnumTypeCodeField());
		this.addField(new SortOrderField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
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
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof EnumTypeNameTable || joinTable instanceof SubQuery) {
			return (
				this.getLinkFieldCondition("enumTypeCode", joinTable, alias, "enumTypeCode")
			);
		}
		return null;
	}
}
