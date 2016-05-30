package dataforms.field.base;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dataforms.validator.NumberValidator;




/**
 * 数値フィールドの基本クラス。
 * @param <TYPE> データ型。
 *
 */
public abstract class NumberField<TYPE> extends Field<TYPE> {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(TextField.class.getName());


	/**
	 * 有効桁数。
	 *
	 */
	private int precision = 10;

	/**
	 * 小数点以下桁数。
	 */
	private int scale = 0;

	/**
	 * カンマフォーマットフラグ。
	 */
	private boolean commaFormat = false;


	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 */
	public NumberField(final String fieldId) {
		super(fieldId);
		this.addValidator(new NumberValidator());
	}


	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 * @param comma カンマフォーマットフラグ。
	 */
	public NumberField(final String fieldId, final boolean comma) {
		super(fieldId);
		this.setCommaFormat(comma);
		this.addValidator(new NumberValidator());
	}

	/**
	 * 有効桁数を取得します。
	 * @return 有効桁数。
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * 有効桁数を設定します。
	 * @param precision 有効桁数。
	 * @return 変更されたフィールド。
	 */
	public Field<?> setPrecision(final int precision) {
		this.precision = precision;
		return this;
	}

	/**
	 * 小数点以下桁数を取得します。
	 * @return 小数点以下桁数。
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * 小数点以下桁数を設定します。
	 * @param scale 小数点以下桁数。
	 * @return 変更されたフィールド。
	 */
	public Field<?> setScale(final int scale) {
		this.scale = scale;
		return this;
	}

	/**
	 * カンマフォーマットフラグを取得取得します。
	 * @return カンマフォーマットフラグ。
	 */
	public boolean isCommaFormat() {
		return commaFormat;
	}


	/**
	 * カンマフォーマットフラグを設定します。
	 * <pre>
	 * trueの場合、','区切りのフォーマットで表示します。
	 * </pre>
	 * @param commaFormat カンマフォーマットフラグ。
	 * @return 変更されたフィールド。
	 */
	public Field<?> setCommaFormat(final boolean commaFormat) {
		this.commaFormat = commaFormat;
		return this;
	}


	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> ret =  super.getClassInfo();
		ret.put("precision", this.precision);
		ret.put("scale", this.scale);
		ret.put("commaFormat", this.commaFormat);
		return ret;
	}


	/**
	 * カンマを削除します。
	 * @param v 削除する文字列。
	 * @return 削除結果。
	 */
	public String delComma(final String v) {
		return v.replaceAll(",",  "");
	}

	/**
	 * 3桁毎にカンマ追加します。
	 * @param v 追加対象の文字列。
	 * @return 追加結果。
	 */
	public String addComma(final String v) {
		String value = this.delComma(v);
		Pattern p = Pattern.compile("^([+-]?\\d+)(\\d{3})");
		Matcher m = p.matcher(value);
    	while (value != (value = m.replaceFirst("$1,$2"))) {
			m = p.matcher(value);
    	}
    	return value;
	}

	@Override
	public Object getClientValue() {
		String value = null;
		if (super.getClientValue() != null) {
			value = super.getClientValue().toString();
		}
		if (value != null) {
			if (this.commaFormat) {
				value = this.addComma(value);
			}
			value = adjustScale(value);
		}
		return value;
	}

	/**
	 * 小数点以下の桁数を指定の値に揃えます。
	 * @param v 値。
	 * @return 小数点以下の桁数を調整した文字列。
	 */
	private String adjustScale(final String v) {
		String value = v;
		if (this.scale > 0) {
			if (value.indexOf(".") < 0) {
				value += ".";
			}
			for (int i = 0; i < this.scale; i++) {
				value += "0";
			}
			int pp = value.indexOf(".");
			if (pp >= 0) {
				value = value.substring(0, pp + this.scale + 1);
			}
		} else {
			int pp = value.indexOf(".");
			if (pp >= 0) {
				value = value.substring(0, pp);
			}
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 検索条件フィールドを生成する際は範囲条件とします。
	 * </pre>
	 */
	@Override
	public dataforms.field.base.Field.MatchType getDefaultMatchType() {
		return MatchType.RANGE_FROM;
	}
}
