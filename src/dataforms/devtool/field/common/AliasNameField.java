package dataforms.devtool.field.common;

import dataforms.field.sqltype.VarcharField;

/**
 * 別名フィールドクラス。
 *
 */
public class AliasNameField extends VarcharField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "別名";
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 256;


	/**
	 * コンストラクタ。
	 */
	public AliasNameField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public AliasNameField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

}
