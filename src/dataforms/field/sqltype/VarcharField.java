package dataforms.field.sqltype;

import dataforms.dao.sqldatatype.SqlVarchar;
import dataforms.field.base.Field;
import dataforms.field.base.TextField;
import dataforms.validator.MaxLengthValidator;


/**
 * VARCHARフィールドクラス。
 *
 */
public class VarcharField extends TextField implements SqlVarchar {
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 * @param length フィールド長。
	 */
	public VarcharField(final String fieldId, final int length) {
		super(fieldId, length);
	}

	@Override
	public String getFieldOption() {
		if (Field.hasLengthParameter(this.getClass().getSuperclass())) {
			return Integer.toString(this.getLength());
		} else {
			return null;
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));
	}
}
