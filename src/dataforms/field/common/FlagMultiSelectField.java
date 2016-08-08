package dataforms.field.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.util.MessagesUtil;

/**
 * フラグマルチ選択フィールドクラス(検索条件用)。
 * 
 *
 */
public class FlagMultiSelectField extends MultiSelectField<String> {
	
	/**
	 * フラグの表示名に対応したしたメッセージキー。
	 */
	private String flagKey = "onoroff";
	
	/**
	 * フラグの表示名に対応したしたメッセージキーを取得します。
	 * @return フラグの表示名に対応したしたメッセージキー。
	 */
	public String getFlagKey() {
		return flagKey;
	}

	/**
	 * フラグの表示名に対応したしたメッセージキーを設定します。
	 * @param flagKey フラグの表示名に対応したしたメッセージキー。
	 */
	public void setFlagKey(final String flagKey) {
		this.flagKey = flagKey;
	}

	/**
	 * コンストラクタ。
	 */
	public FlagMultiSelectField() {
		super(null, 1);
		this.setMatchType(MatchType.IN);
		this.setHtmlFieldType(HtmlFieldType.CHECKBOX);
	}
	
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FlagMultiSelectField(final String id) {
		super(id, 1);
		this.setMatchType(MatchType.IN);
		this.setHtmlFieldType(HtmlFieldType.CHECKBOX);
	}

	
	@Override
	public void init() throws Exception {
		super.init();
		String key = this.getFlagKey();
		List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
		{
			String v0 = MessagesUtil.getMessage(this.getPage(), key + ".0");
			Map<String, Object> m0 = new HashMap<String, Object>();
			m0.put("name", v0);
			m0.put("value", "0");
			options.add(m0);
		}
		{
			String v1 = MessagesUtil.getMessage(this.getPage(), key + ".1");
			Map<String, Object> m1 = new HashMap<String, Object>();
			m1.put("name", v1);
			m1.put("value", "1");
			options.add(m1);
		}
		this.setOptionList(options);
	}
}
