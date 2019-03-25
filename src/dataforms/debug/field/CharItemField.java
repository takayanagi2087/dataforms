package dataforms.debug.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataforms.field.common.CharSingleSelectField;
import dataforms.field.common.SelectField;
import dataforms.validator.MaxLengthValidator;


/**
 * CharItemFieldフィールドクラス。
 *
 */
public class CharItemField extends CharSingleSelectField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 64;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "Charフィールド";
	/**
	 * コンストラクタ。
	 */
	public CharItemField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CharItemField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setOptionList(this.queryOptionList(), true);
	}

	/**
	 * 名前リスト。
	 */
	private static String[] optionName = {
		"Char0", "Char1", "Char2", "Char3", "Char4"
	};


	/**
	 * オプションリスト。
	 * @return オプションリストを取得します。
	 */
	private List<Map<String, Object>> queryOptionList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < optionName.length; i++) {
			SelectField.OptionEntity e = new SelectField.OptionEntity();
			e.setValue(optionName[i]);
			e.setName(optionName[i]);
			list.add(e.getMap());
		}
		return list;
	}
}
