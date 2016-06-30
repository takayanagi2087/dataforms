package dataforms.devtool.field.common;

import dataforms.dao.Table;

/**
 * テーブルクラス名フィールドクラス。
 *
 */
public class TableClassNameField extends SimpleClassNameField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "テーブルクラス名";
	/**
	 * コンストラクタ。
	 */
	public TableClassNameField() {
		this.setBaseClass(Table.class);
		this.setComment(COMMENT);
		this.setAutocomplete(false);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public TableClassNameField(final String id) {
		super(id);
		this.setBaseClass(Table.class);
		this.setComment(COMMENT);
		this.setAutocomplete(false);
	}

}
