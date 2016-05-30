package dataforms.devtool.field.common;

import dataforms.field.sqltype.VarcharField;

/**
 * Javascriptクラス名。
 *
 *
 */
public class JavascriptClassField extends VarcharField {
	/**
	 * 項目帳。
	 */
	private static final int LENGTH = 256;

	/**
	 * コンストラクタ。
	 */
	public JavascriptClassField() {
		super(null, LENGTH);
	}

	/**
	 * フィールド名。
	 * @param id フィールドID。
	 */
	public JavascriptClassField(final String id) {
		super(id, LENGTH);
	}
}
