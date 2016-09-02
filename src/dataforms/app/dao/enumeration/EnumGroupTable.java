package dataforms.app.dao.enumeration;

import dataforms.app.field.enumeration.EnumGroupCodeField;
import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.dao.Table;
import dataforms.dao.TableRelation;
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

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		TableRelation r = new EnumGroupTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
}
