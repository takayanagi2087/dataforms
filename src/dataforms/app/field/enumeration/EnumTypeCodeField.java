package dataforms.app.field.enumeration;

import dataforms.controller.EditForm;
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
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドのID。
	 */
	public EnumTypeCodeField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		if (this.getParentForm() instanceof EditForm) {
			this.addValidator(new RequiredValidator());
			this.addValidator(new MaxLengthValidator(LENGTH));
		}
	}
}
