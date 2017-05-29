package dataforms.dao;

import java.util.List;
import java.util.Map;

import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.util.StringUtil;

// TODO:インデックスの生成機能を実装する。

/**
 * インデックス。
 *
 */
public class Index {
	/**
	 * テーブルクラス。
	 */
	private Table table = null;
	
	/**
	 * ユニークフラグ。
	 */
	private boolean unique  = false;
	
	/**
	 * フィールドリスト。
	 */
	private FieldList fieldList = null;


	/**
	 * コンストラクタ。
	 */
	public Index() {
		this.unique = false;
		this.table = null;
		this.fieldList = null;
	}



	/**
	 * 対象テーブル取得します。
	 * @return 対象テーブル。
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * 対象テーブルを設定します。
	 * @param table 対象テーブル。
	 */
	public void setTable(final Table table) {
		this.table = table;
	}


	/**
	 * ユニークフラグを取得します。
	 * @return ユニークフラグ。
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * 一意フラグを設定します。
	 * @param unique 一意フラグ。
	 */
	public void setUnique(final boolean unique) {
		this.unique = unique;
	}


	/**
	 * フィールドリストを取得します。
	 * @return フィールドリスト。
	 */
	public FieldList getFieldList() {
		return fieldList;
	}
	
	/**
	 * フィールドリストを設定します。
	 * @param fieldList フィールドリスト。 
	 */
	public void setFieldList(final FieldList fieldList) {
		this.fieldList = fieldList;
	}

	/**
	 * DB用のインデックス名称を取得します。
	 * @return DB用のインデックス名称。
	 */
	public String getIndexName() {
		String clsname = this.getClass().getSimpleName();
		return StringUtil.camelToSnake(clsname);
	}
	
	/**
	 * インデックスの構造差があるかチェックします。
	 * @param iflist インデックスフィールドリスト。
	 * @return 一致する場合true。
	 */
	public boolean structureAccords(final List<Map<String, Object>> iflist) {
		if (iflist.size() == 0) {
			return false;
		} else {
			Boolean nonUnique = (Boolean) iflist.get(0).get("nonUnique");
			if (this.isUnique() == nonUnique.booleanValue()) {
				return false;
			} else {
				if (iflist.size() != this.getFieldList().size()) {
					return false;
				} else {
					for (int i = 0; i < iflist.size(); i++) {
						Map<String, Object> m = iflist.get(i);
						String idxname = StringUtil.snakeToCamel(((String) m.get("columnName")).toLowerCase());
						Field<?> f = this.getFieldList().get(i);
						if (!f.getId().equals(idxname)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
}
