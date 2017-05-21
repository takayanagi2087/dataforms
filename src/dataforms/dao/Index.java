package dataforms.dao;

import dataforms.field.base.FieldList;

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

	
}
