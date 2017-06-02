package dataforms.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.util.StringUtil;


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
	 * NonUniqueフラグを取得します。
	 * @param iflist インデックスフィールドリスト。
	 * @return NonUniqueフラグ。
	 */
	private boolean getNonUnique(final List<Map<String, Object>> iflist) {
		Object o = iflist.get(0).get("nonUnique");
		if (o instanceof BigDecimal) {
			// for oracle
			BigDecimal v = (BigDecimal) o;
			if (v.intValue() == 1) {
				return true;
			} else {
				return false;
			}
		} else {
			Boolean b = (Boolean) o;
			return b.booleanValue();
		}
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
			boolean nonUnique = this.getNonUnique(iflist);
			if (this.isUnique() == nonUnique) {
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
