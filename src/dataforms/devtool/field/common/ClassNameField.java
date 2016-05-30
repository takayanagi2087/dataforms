package dataforms.devtool.field.common;

import dataforms.field.sqltype.VarcharField;

/**
 * クラス名フィールドクラス。
 *
 */
public class ClassNameField extends VarcharField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "クラス名";
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 256;


	/**
	 * コンストラクタ。
	 */
	public ClassNameField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public ClassNameField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

}
