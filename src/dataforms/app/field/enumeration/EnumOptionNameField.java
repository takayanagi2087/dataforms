package dataforms.app.field.enumeration;

import dataforms.field.sqltype.VarcharField;
import dataforms.validator.MaxLengthValidator;

/**
 * 列挙型名称フィールドクラスです。
 *
 */
public class EnumOptionNameField extends VarcharField {

	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 64;

	/**
	 * コメント.
	 */
	private static final String COMMENT = "列挙型の名称.";



	/**
	 * コンストラクタ。
	 */
	public EnumOptionNameField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new MaxLengthValidator(LENGTH));
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドのID。
	 */
	public EnumOptionNameField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}
}
