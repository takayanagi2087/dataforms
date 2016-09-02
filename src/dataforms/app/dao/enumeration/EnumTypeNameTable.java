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
		this.setComment("列挙型名称テーブル");
		this.addPkField(new EnumTypeCodeField());
		this.addPkField(new LangCodeField());
		this.addField(new EnumTypeNameField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
	}

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		EnumTypeNameTableRelation r = new EnumTypeNameTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
}
