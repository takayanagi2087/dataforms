package dataforms.devtool.field.common;

import dataforms.field.sqltype.VarcharField;

/**
 * パッケージ名フィールドクラス。
 *
 */
public class PackageNameField extends VarcharField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "パッケージ名";

	/**
	 * コンストラクタ。
	 */
	public PackageNameField() {
		super(null, 256);
		this.setComment(COMMENT);
	}


	/**
	 * コンストラクタ。
	 * @param id パッケージID。
	 */
	public PackageNameField(final String id) {
		super(id, 256);
		this.setComment(COMMENT);
	}
}
