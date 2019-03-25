package dataforms.debug.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataforms.field.common.IntegerSingleSelectField;
import dataforms.field.common.SelectField;


/**
 * IntegerIetmFieldフィールドクラス。
 *
 */
public class IntegerIetmField extends IntegerSingleSelectField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "Integerフィールド";
	/**
	 * コンストラクタ。
	 */
	public IntegerIetmField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public IntegerIetmField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}


	@Override
	public void init() throws Exception {
		super.init();
		this.setOptionList(this.queryOptionList(), true);
	}

	/**
	 * 値リスト。
	 */
	private static Integer[] optionValue = {
		0, 1, 2, 3, 4
	};

	/**
	 * 名前リスト。
	 */
	private static String[] optionName = {
		"Integer0", "Integer1", "Integer2", "Integer3", "Integer4"
	};


	/**
	 * オプションリスト。
	 * @return オプションリストを取得します。
	 */
	private List<Map<String, Object>> queryOptionList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < optionValue.length; i++) {
			SelectField.OptionEntity e = new SelectField.OptionEntity();
			e.setValue(optionValue[i].toString());
			e.setName(optionName[i].toString());
			list.add(e.getMap());
		}
		return list;
	}
}
