package dataforms.field.sqltype;

import java.math.BigDecimal;

import dataforms.dao.sqldatatype.SqlNumeric;
import dataforms.field.base.Field;
import dataforms.field.base.NumberField;
import dataforms.util.StringUtil;
import dataforms.validator.NumericValidator;

/**
 * 数値(NUMERIC)フィールドクラス。
 *
 */
public class NumericField extends NumberField<BigDecimal> implements SqlNumeric {

	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 * @param precision 有効桁数。
	 * @param scale 小数点以下桁数。
	 */
	public NumericField(final String fieldId, final int precision, final int scale) {
		super(fieldId, false);
		this.setPrecision(precision);
		this.setScale(scale);
		this.addValidator(new NumericValidator(precision, scale));
	}

	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 * @param precision 有効桁数。
	 * @param scale 小数点以下桁数。
	 * @param comma カンマ編集フラグ。
	 */
	public NumericField(final String fieldId, final int precision, final int scale, final boolean comma) {
		super(fieldId, comma);
		this.setPrecision(precision);
		this.setScale(scale);
		this.addValidator(new NumericValidator(precision, scale));
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 入力された文字列をBigDecimalに変換します。
	 * </pre>
	 */
	@Override
	public void setClientValue(final Object v) {
		if (!StringUtil.isBlank(v)) {
			this.setValue(BigDecimal.valueOf(Double.parseDouble(((String) v).replaceAll(",", ""))));
		} else {
			this.setValue(null);
		}
	}

	@Override
	public String getFieldOption() {
		if (Field.hasLengthParameter(this.getClass().getSuperclass())) {
			return this.getPrecision() + "," + this.getScale();
		} else {
			return null;
		}
	}


	@Override
	protected String getLengthParameterSample() throws Exception {
		if (this.getPrecision() == 0) {
			return "10,2";
		} else {
			return this.getPrecision() + ", " + this.getScale();
		}
	}

	@Override
	public String getLengthParameterPattern() throws Exception {
		return "^[0-9]+,[0-9]+$";
	}


}
