package dataforms.field.sqltype;

import dataforms.dao.sqldatatype.SqlBigint;
import dataforms.field.base.NumberField;
import dataforms.util.NumberUtil;
import dataforms.util.StringUtil;
import dataforms.validator.NumberRangeValidator;


/**
 * 64 ビット符号付き整数フィールドクラス。
 *
 */
public class BigintField extends NumberField<Long> implements SqlBigint {

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BigintField(final String id) {
		super(id, false);
		this.addValidator(new NumberRangeValidator(Long.MIN_VALUE, Long.MAX_VALUE));
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param comma カンマ編集フラグ。
	 */
	public BigintField(final String id, final boolean comma) {
		super(id, comma);
		this.addValidator(new NumberRangeValidator(Long.MIN_VALUE, Long.MAX_VALUE));
	}

	@Override
	public void setClientValue(final Object v) {
		if (!StringUtil.isBlank(v)) {
			this.setValue(Long.parseLong(((String) v).replaceAll(",", "")));
		} else {
			this.setValue(null);
		}
	}

	@Override
	public void setDBValue(final Object value) {
		this.setValue(NumberUtil.longValueObject(value));
	}

}
