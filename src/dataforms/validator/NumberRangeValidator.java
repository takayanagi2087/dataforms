package dataforms.validator;

import java.util.Map;

import org.apache.commons.validator.GenericValidator;

import dataforms.util.MessagesUtil;

/**
 * 数値範囲バリデータクラス。
 *
 */
public class  NumberRangeValidator extends FieldValidator {
	/**
	 * 最小値。
	 */
	private double min = Double.MIN_VALUE;


	/**
	 * 最大値。
	 */
	private double max = Double.MAX_VALUE;

	/**
	 * コンストラクタ。
	 * @param min 最小値。
	 * @param max 最大値。
	 */
	public NumberRangeValidator(final double min, final double max) {
		super("error.numberrange");
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean validate(final Object value) {
		if (this.isBlank(value)) {
			return true;
		}
		String strval = (String) value;
		double v = Double.parseDouble(strval.replaceAll(",",  ""));
		if (GenericValidator.isInRange(v, min, max)) {
			return true;
		}
		return false;
	}

	@Override
	public String getMessage() {
		return MessagesUtil.getMessage(this.getPage(), this.getMessageKey(), "{0}", Double.toString(this.min), Double.toString(this.max));
	}

	/**
	 * 最小値を取得します。
	 * @return 最小値。
	 */
	public final double getMin() {
		return min;
	}

	/**
	 * 最小値を取得します。
	 * @param min 最小値。
	 */
	public final void setMin(final double min) {
		this.min = min;
	}

	/**
	 * 最大値を取得します。
	 * @return 最大値。
	 */
	public final double getMax() {
		return max;
	}

	/**
	 * 最大値を設定します。
	 * @param max 最大値。
	 */
	public final void setMax(final double max) {
		this.max = max;
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
		info.put("min", this.getMin());
		info.put("max", this.getMax());
		return info;
	}
}

