package dataforms.field.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.field.base.Field;

/**
 * 選択肢フィールドクラス。
 * <pre>
 * 各種選択肢フィールドクラスの基本クラスです。
 * </pre>
 *
 * @param <TYPE> 取り扱うタイプ。
 */
public abstract class SelectField<TYPE> extends Field<TYPE> {

	/**
	 * 選択肢。
	 */
	private List<Map<String, Object>> optionList = null;


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SelectField(final String id) {
		super(id);
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param len 長さ。
	 */
	public SelectField(final String id, final int len) {
		super(id);
		this.setLength(len);
	}

	/**
	 * 選択肢のリストを取得します。
	 * @return 選択肢のリスト。
	 */
	public List<Map<String, Object>> getOptionList() {
		return optionList;
	}

	/**
	 * 選択肢のリストを設定します。
	 * @param optionList 選択肢のリスト。
	 * @param addBlankOption 空白オプションを追加する。
	 * @return 設定したフィールド。
	 */
	public SelectField<TYPE> setOptionList(final List<Map<String, Object>> optionList, final boolean addBlankOption) {
		ArrayList<Map<String, Object>> olist = new ArrayList<Map<String, Object>>();
		if (addBlankOption) {
			HashMap<String, Object> bm = new HashMap<String, Object>();
			bm.put("value", "");
			bm.put("name", "");
			olist.add(bm);
		}
		olist.addAll(optionList);
		this.optionList = olist;
		return this;
	}

	/**
	 * 選択肢のリストを設定します。
	 * @param optionList 選択肢のリスト。
	 * @return 設定したフィールド。
	 */
	public SelectField<TYPE> setOptionList(final List<Map<String, Object>> optionList) {
//		this.optionList = optionList;
		this.setOptionList(optionList, false);
		return this;
	}



	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> ret = super.getClassInfo();
		ret.put("optionList", this.optionList);
		return ret;
	}

}
