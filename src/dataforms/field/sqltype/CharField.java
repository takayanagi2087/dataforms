package dataforms.field.sqltype;

import dataforms.dao.sqldatatype.SqlChar;
import dataforms.field.base.Field;
import dataforms.field.base.TextField;
import dataforms.validator.MaxLengthValidator;



/**
 * CHARフィールドクラス。
 *
 */
public class CharField extends TextField implements SqlChar {
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 * @param length フィールド長。
	 */
	public CharField(final String fieldId, final int length) {
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
