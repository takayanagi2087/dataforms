package dataforms.app.field.enumeration;

import dataforms.field.sqltype.VarcharField;
import dataforms.validator.MaxLengthValidator;
import dataforms.validator.RequiredValidator;

/**
 * 列挙型コードクラス。
 *
 */
public class EnumTypeCodeField extends VarcharField {

	/**
	 * フィールド長。
	 */
	public static final int LENGTH = 16;

	/**
	 * コメント。
	 */
	private static final String COMMENT = "列挙型コード.";

	/**
	 * コンストラクタ。
	 */
	public EnumTypeCodeField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new RequiredValidator());
		this.addValidator(new MaxLengthValidator(LENGTH));
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドのID。
	 */
	public EnumTypeCodeField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new RequiredValidator());
		this.addValidator(new MaxLengthValidator(LENGTH));
	}
}
