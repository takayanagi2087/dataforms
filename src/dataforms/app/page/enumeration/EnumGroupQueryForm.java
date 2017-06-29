package dataforms.app.page.enumeration;

import dataforms.app.dao.enumeration.EnumGroupTable;
import dataforms.controller.QueryForm;
import dataforms.field.base.Field.MatchType;



/**
 * 問い合わせフォームクラス。
 */
public class EnumGroupQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public EnumGroupQueryForm() {
		EnumGroupTable table = new EnumGroupTable();
		this.addField(table.getEnumGroupCodeField()).setMatchType(MatchType.PART);
	}
}
