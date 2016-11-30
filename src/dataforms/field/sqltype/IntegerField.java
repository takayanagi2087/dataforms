package dataforms.field.sqltype;

import dataforms.dao.sqldatatype.SqlInteger;
import dataforms.field.base.NumberField;
import dataforms.util.NumberUtil;
import dataforms.util.StringUtil;
import dataforms.validator.NumberRangeValidator;


/**
 * 32 ビットの符号付き整数値フィールドクラス。
 *
 */
public class IntegerField extends NumberField<Integer> implements SqlInteger {
	/**
	 * コンストラクタ。
	 * @param id フィールドID.
	 */
	public IntegerField(final String id) {
		super(id, false);
		this.addValidator(new NumberRangeValidator(Integer.MIN_VALUE, Integer.MAX_VALUE));
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param comma カンマ編集フラグ。
	 */
	public IntegerField(final String id, final boolean comma) {
		super(id, comma);
		this.addValidator(new NumberRangeValidator(Integer.MIN_VALUE, Integer.MAX_VALUE));
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 入力された文字列をIntegerに変換します。
	 * </pre>
	 */
	@Override
	public void setClientValue(final Object v) {
		if (!StringUtil.isBlank(v)) {
			this.setValue(Integer.parseInt(((String) v).replaceAll(",", "")));
		} else {
			this.setValue(null);
		}
	}


	@Override
	public void setDBValue(final Object value) {
		this.setValue(NumberUtil.integerValueObject(value));
	}


}
