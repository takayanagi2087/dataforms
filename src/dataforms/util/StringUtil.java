package dataforms.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文字列処理ユーティリティクラス。
 *
 */
public final class StringUtil {
	

	/**
	 * コンストラクタ。
	 */
	private StringUtil() {

	}

	/**
	 * 指定された値がブランクかどうかを確認します。
	 * @param value フィールドの値。
	 * @return ブランクの場合true。
	 */
	public static boolean isBlank(final Object value) {
		if (value == null) {
			return true;
		}
		if (value instanceof String) {
			if (((String) value).length() == 0) {
				return true;
			}
		}
		/* else if (value instanceof Set) {
			@SuppressWarnings("unchecked")
			Set<String> set = (Set<String>) value;
			if (set.size() == 0) {
				return true;
			}
		}*/
		return false;
	}

	/**
	 * キャメル形式をスネーク形式(小文字)に変換します。
	 * @param camelStr キャメル形式文字列。
	 * @return スネーク形式文字列(小文字)。
	 */
	public static String camelToSnake(final String camelStr) {
		String ret = camelStr
				.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
				.replaceAll("([0-9a-z])([A-Z])", "$1_$2");
		return ret.toLowerCase();
	}
	
	
	/**
	 * キャメル形式をスネーク形式(大文字)に変換します。
	 * @param camelStr キャメル形式文字列。
	 * @return スネーク形式文字列(大文字)。
	 */
	public static String camelToUpperCaseSnake(final String camelStr) {
		String ret = camelToSnake(camelStr);
		return ret.toUpperCase();
	}
	
	

	/**
	 * スネーク形式の文字列をキャメル形式の文字列に変換します。
	 * @param snakeStr スネーク形式の文字列。
	 * @return キャメル形式の文字列。
	 */
	public static String snakeToCamel(final String snakeStr) {
		Pattern p = Pattern.compile("_([a-z])");
		Matcher m = p.matcher(snakeStr.toLowerCase());

		StringBuffer sb = new StringBuffer(snakeStr.length());
		while (m.find()) {
			m.appendReplacement(sb, m.group(1).toUpperCase());
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 先頭文字を大文字に変換します。
	 * @param str 変換元の文字。
	 * @return 変換結果。
	 */
	public static String firstLetterToUpperCase(final String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * テスト。
	 * @param args
	 */
/*	public static void main(String[] args) {
		String snake = StringUtil.camelToSnake("test01Field");
		System.out.println("snake=" + snake);
		String camel = StringUtil.snakeToCamel(snake);
		System.out.println("camel=" + camel);
		
	}*/

}
