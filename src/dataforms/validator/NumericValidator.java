package dataforms.validator;

import java.util.Map;

import dataforms.util.MessagesUtil;

/**
 * DBのNuemric型のバリデータクラス。
 * <pre>
 * 数値の有効桁数と小数点以下の桁数のチェックを行ないます。
 * </pre>
 *
 */
public class  NumericValidator extends FieldValidator {
	/**
	 * 有効桁数。
	 */
	private int precision = 10;
	/**
	 * 少数点以下の桁数。
	 */
	private int scale = 0;

	/**
	 * コンストラクタ。
	 * @param precision 長さ。
	 * @param scale 小数点の桁数。
	 */
	public NumericValidator(final int precision, final int scale) {
		super("error.numeric");
		this.precision = precision;
		this.scale = scale;
	}

	@Override
	public boolean validate(final Object value) {
		if (this.isBlank(value)) {
			return true;
		}
		String str = ((String) value).replaceAll(",", "");
		int sc = 0;
		String abs = str.replaceAll("[\\-\\+]", "");
		int sidx = abs.indexOf('.');
		int ln = abs.length();
		if (sidx >= 0) {
			sc = abs.length() - sidx - 1;
			ln = sidx;
		}
		if (ln > this.precision - this.scale) {
			return false;
		}
		if (sc > this.scale) {
			return false;
		}
		return true;
	}

	@Override
	public String getMessage() {
		return MessagesUtil.getMessage(this.getPage(), this.getMessageKey(), "{0}", Integer.toString(this.precision), Integer.toString(this.scale));
	}

	/**
	 * 有効桁数を取得します。
	 * @return 有効桁数.
	 */
	public final int getPrecision() {
		return precision;
	}

	/**
	 * 有効桁数を設定します。
	 * @param precision 有効桁数。
	 */
	public final void setPrecision(final int precision) {
		this.precision = precision;
	}


	/**
	 * 小数点以下の桁数を取得します。
	 * @return 小数点以下の桁数.
	 */
	public final int getScale() {
		return scale;
	}

	/**
	 * 小数点以下の桁数を設定します。
	 * @param scale 小数点以下の桁数.
	 */
	public final void setScale(final int scale) {
		this.scale = scale;
	}

	/**
	 * Clientの制御情報を取得します。
	 * <pre>
	 * 数値チェックに必要な情報を返します。
	 * </pre>
	 * @return Client制御情報。
	 * @throws Exception 例外。
	 */
	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> info = super.getClassInfo();
		info.put("precision", this.getPrecision());
		info.put("scale", this.getScale());
		return info;
	}
}

