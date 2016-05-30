package dataforms.util;

/**
 * 数値変換ユーティリティクラス。
 * <pre>
 * DBからの数値項目がDBベンダによって異なる場合の対応用の変換処理です。
 * </pre>
 *
 */
public final class NumberUtil {
	/**
	 * コンストラクタ.
	 */
	private NumberUtil() {

	}

	/**
	 * byte値として取得します。
	 * @param v 数値。
	 * @return byte値。
	 */
	public static byte byteValue(final Object v)  {
		byte ret = 0;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.byteValue();
		}
		return ret;
	}


	/**
	 * double値として取得します。
	 * @param v 数値。
	 * @return double値。
	 */
	public static double doubleValue(final Object v)  {
		double ret = 0.0;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.doubleValue();
		}
		return ret;
	}

	/**
	 * float値として取得します。
	 * @param v 数値。
	 * @return float値。
	 */
	public static float floatValue(final Object v)  {
		float ret = (float) 0.0;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.floatValue();
		}
		return ret;
	}


	/**
	 * int値として取得します。
	 * @param v 数値。
	 * @return int値。
	 */
	public static int intValue(final Object v)  {
		int ret = 0;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.intValue();
		}
		return ret;
	}


	/**
	 * long値として取得します。
	 * @param v 数値。
	 * @return lomg値。
	 */
	public static long longValue(final Object v)  {
		long ret = 0;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.longValue();
		}
		return ret;
	}

	/**
	 * short値として取得します。
	 * @param v 数値。
	 * @return lomg値。
	 */
	public static short shortValue(final Object v)  {
		short ret = 0;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.shortValue();
		}
		return ret;
	}


	/**
	 * byte値として取得します。
	 * @param v 数値。
	 * @return byte値。
	 */
	public static Byte byteValueObject(final Object v)  {
		Byte ret = null;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.byteValue();
		}
		return ret;
	}


	/**
	 * double値として取得します。
	 * @param v 数値。
	 * @return double値。
	 */
	public static Double doubleValueObject(final Object v)  {
		Double ret = null;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.doubleValue();
		}
		return ret;
	}

	/**
	 * float値として取得します。
	 * @param v 数値。
	 * @return float値。
	 */
	public static Float floatValueObject(final Object v)  {
		Float ret = null;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.floatValue();
		}
		return ret;
	}


	/**
	 * int値として取得します。
	 * @param v 数値。
	 * @return int値。
	 */
	public static Integer integerValueObject(final Object v)  {
		Integer ret = null;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.intValue();
		}
		return ret;
	}


	/**
	 * long値として取得します。
	 * @param v 数値。
	 * @return lomg値。
	 */
	public static Long longValueObject(final Object v)  {
		Long ret = null;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.longValue();
		}
		return ret;
	}

	/**
	 * short値として取得します。
	 * @param v 数値。
	 * @return lomg値。
	 */
	public static Short shortValueObject(final Object v)  {
		Short ret = null;
		if (v instanceof Number) {
			Number iv = (Number) v;
			ret = iv.shortValue();
		}
		return ret;
	}


}
