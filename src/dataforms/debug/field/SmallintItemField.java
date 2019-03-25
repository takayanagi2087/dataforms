package dataforms.debug.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataforms.field.common.SelectField;
import dataforms.field.common.SmallintSingleSelectField;


/**
 * SmallintItemFieldフィールドクラス。
 *
 */
public class SmallintItemField extends SmallintSingleSelectField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "Smallintフィールド";
	/**
	 * コンストラクタ。
	 */
	public SmallintItemField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SmallintItemField(final String id) {
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
		"Smallint0", "Smallint1", "Smallint2", "Smallint3", "Smallint4"
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
