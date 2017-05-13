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
	 * @param unique ユニークフラグ。
	 * @param table 対象テーブル。
	 * @param fieldList フィールドリスト。
	 */
	public Index(final boolean unique, final Table table, final FieldList fieldList) {
		this.unique = unique;
		this.table = table;
		this.fieldList = fieldList;
	}


	/**
	 * 対象テーブルのインスタンスを取得します。
	 * @return 対象テーブル。
	 */
	public Table getTable() {
		return table;
	}


	/**
	 * ユニークフラグを取得します。
	 * @return ユニークフラグ。
	 */
	public boolean isUnique() {
		return unique;
	}


	/**
	 * フィールドリストを取得します。
	 * @return フィールドリスト。
	 */
	public FieldList getFieldList() {
		return fieldList;
	}
	
	
	
}
