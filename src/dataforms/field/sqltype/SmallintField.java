package dataforms.field.sqltype;

import dataforms.dao.sqldatatype.SqlSmallint;
import dataforms.field.base.NumberField;
import dataforms.util.NumberUtil;
import dataforms.util.StringUtil;
import dataforms.validator.NumberRangeValidator;


/**
 *  16ビット符号付き整数値フィールド。
 *
 */
public class SmallintField extends NumberField<Short> implements SqlSmallint {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SmallintField(final String id) {
		super(id, false);
		this.addValidator(new NumberRangeValidator(Short.MIN_VALUE, Short.MAX_VALUE));
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param comma カンマ編集フラグ。
	 */
	public SmallintField(final String id, final boolean comma) {
		super(id, comma);
		this.addValidator(new NumberRangeValidator(Short.MIN_VALUE, Short.MAX_VALUE));
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 入力された文字列をShortに変換します。
	 * </pre>
	 */
	@Override
	public void setClientValue(final Object v) {
		if (!StringUtil.isBlank(v)) {
			this.setValue(Short.parseShort(((String) v).replaceAll(",", "")));
		} else {
			this.setValue(null);
		}
	}

	@Override
	public void setDBValue(final Object value) {
		this.setValue(NumberUtil.shortValueObject(value));
	}

}
