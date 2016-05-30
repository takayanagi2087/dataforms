package dataforms.util;

/**
 * クラス名関連ユーティリティクラス。
 */
public final class ClassNameUtil {

	/**
	 * コンストラクタ。
	 */
	private ClassNameUtil() {

	}

	/**
	 * パッケージ名を取得します。
	 * @param fullClassName 完全なクラス名。
	 * @return パッケージ名。
	 */
	public static String getPackageName(final String fullClassName) {
		String ret = fullClassName;
		int idx = fullClassName.lastIndexOf(".");
		if (idx > 0) {
			ret = fullClassName.substring(0, idx);
		}
		return ret;
	}

	/**
	 * 単純クラス名を取得します。
	 * @param fullClassName 完全クラス名。
	 * @return 単純クラス名。
	 */
	public static String getSimpleClassName(final String fullClassName) {
		String ret = fullClassName;
		int idx = fullClassName.lastIndexOf(".");
		if (idx > 0) {
			ret = fullClassName.substring(idx + 1);
		}
		return ret;
	}
}
