package dataforms.debug.page;

import dataforms.controller.QueryForm;
import dataforms.debug.dao.SingleSelectTable;
import dataforms.field.base.Field.MatchType;



/**
 * 問い合わせフォームクラス。
 */
public class SelectItemQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public SelectItemQueryForm() {
		SingleSelectTable table = new SingleSelectTable();
		this.addField(table.getCharItemField()).setMatchType(MatchType.FULL);
		this.addField(table.getVarcharItemField()).setMatchType(MatchType.FULL);
		this.addField(table.getSmallintItemField()).setMatchType(MatchType.FULL);
		this.addField(table.getIntegerIetmField()).setMatchType(MatchType.FULL);
		this.addField(table.getBigintItemField()).setMatchType(MatchType.FULL);

	}
}
