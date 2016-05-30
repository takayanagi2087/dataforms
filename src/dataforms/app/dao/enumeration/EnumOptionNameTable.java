package dataforms.app.dao.enumeration;

import dataforms.app.field.enumeration.EnumOptionCodeField;
import dataforms.app.field.enumeration.EnumOptionNameField;
import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.dao.Table;
import dataforms.field.common.LangCodeField;

/**
 * 列挙型選択肢名称テーブルクラス。
 * <pre>
 * &lt;select&gt;やラジオボタンの選択肢を管理するテーブルです。
 * 言語ごとの表示名称を記録します。
 * </pre>
 *
 */
public class EnumOptionNameTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public EnumOptionNameTable() {
		this.addPkField(new EnumTypeCodeField());
		this.addPkField(new EnumOptionCodeField());
		this.addPkField(new LangCodeField());
		this.addField(new EnumOptionNameField());
		this.addUpdateInfoFields();
	}
}
