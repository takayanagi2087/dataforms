package dataforms.app.field.enumeration;

import dataforms.field.sqltype.VarcharField;
import dataforms.validator.MaxLengthValidator;
import dataforms.validator.RequiredValidator;

/**
 * 列挙型名称クラス。
 *
 */
public class EnumTypeNameField extends VarcharField {

	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 64;

	/**
	 * コメント。
	 */
	private static final String COMMENT = "列挙型の名称.";



	/**
	 * コンストラクタ。
	 */
	public EnumTypeNameField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new RequiredValidator());
		this.addValidator(new MaxLengthValidator(LENGTH));
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public EnumTypeNameField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
		this.addValidator(new RequiredValidator());
		this.addValidator(new MaxLengthValidator(LENGTH));
	}
}
