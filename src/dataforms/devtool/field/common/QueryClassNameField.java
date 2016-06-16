package dataforms.devtool.field.common;

import dataforms.dao.Query;

/**
 * 問合せクラス名フィールドクラス。
 *
 */
public class QueryClassNameField extends SimpleClassNameField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "問合せクラス名";
	/**
	 * コンストラクタ。
	 */
	public QueryClassNameField() {
		this.setBaseClass(Query.class);
		this.setComment(COMMENT);
		this.setAutocomplete(true);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public QueryClassNameField(final String id) {
		super(id);
		this.setBaseClass(Query.class);
		this.setComment(COMMENT);
		this.setAutocomplete(true);
	}
}
