package dataforms.debug.page.alltype;

import java.util.List;
import java.util.Map;

import dataforms.controller.QueryForm;
import dataforms.dao.Table;
import dataforms.debug.dao.alltype.AllTypeDao;
import dataforms.debug.dao.alltype.AllTypeTable;
import dataforms.field.base.Field;
import dataforms.field.base.Field.MatchType;
import dataforms.field.common.MultiSelectField;
import dataforms.field.sqltype.DateField;
import dataforms.field.sqltype.NumericField;

/**
 * 全データタイプの問い合わせフォームクラス。
 *
 */
public class AllTypeQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public AllTypeQueryForm() {
		Table tbl = new AllTypeTable();
		this.addField(tbl.getField("charField")).setCaseInsensitive(true).setMatchType(MatchType.PART);
		this.addField(tbl.getField("varcharField").setMatchType(Field.MatchType.PART)).setCaseInsensitive(true);
		NumericField nf = (NumericField) tbl.getField("numericField");
		nf.setCommaFormat(true);
		this.addField(nf);
		DateField from = new DateField("dateFieldFrom");
		from.setComment("日付範囲(From)");
		this.addField(from.setMatchType(Field.MatchType.RANGE_FROM));
		DateField to = new DateField("dateFieldTo");
		to.setComment("日付範囲(To)");
		this.addField(to.setMatchType(Field.MatchType.RANGE_TO));
		MultiSelectField<String> dropdownCondField = new MultiSelectField<String>("dropdownField", 1);
		dropdownCondField.setOptionList(AllTypeTable.getOptionList());
		this.addField(dropdownCondField).setMatchType(Field.MatchType.IN);

	}

	@Override
	protected List<Map<String, Object>> queryExportData(Map<String, Object> data) throws Exception {
		AllTypeDao dao = new AllTypeDao(this);
		List<Map<String, Object>> list = dao.queryList(data, this.getFieldList());
		int rowNo = 1;
		for (Map<String, Object> m: list) {
			m.put("rowNo", Integer.valueOf(rowNo++));
		}
		return list;
	}
}
