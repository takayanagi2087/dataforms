package dataforms.debug.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataforms.field.common.SelectField;
import dataforms.field.common.VarcharSingleSelectField;
import dataforms.validator.MaxLengthValidator;


/**
 * VarcharItemFieldフィールドクラス。
 *
 */
public class VarcharItemField extends VarcharSingleSelectField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 64;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "Varcharフィールド";
	/**
	 * コンストラクタ。
	 */
	public VarcharItemField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public VarcharItemField(final String id) {
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
		"Varchar0", "Varchar1", "Varchar2", "Varchar3", "Varchar4"
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
