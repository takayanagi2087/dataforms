package dataforms.util;

import java.util.List;
import java.util.Map;

/**
 * マップ処理のユーティリティクラス。
 *
 */
public final class MapUtil {

    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(MapUtil.class.getName());

	/**
	 * コンストラクタ。
	 */
	private MapUtil() {

	}

	/**
	 * パラメータに対応した値を取得します。
	 * <pre>
	 * パラメータは"tablename[idx].columnname"の形式をサポートし、
	 * tablenameでリストを取得し、idx番目のマップのcolumnnameに対応した値を取得します。
	 * </pre>
	 * @param p パラメータ名。
	 * @param map パラメータマップ。
	 * @return 値。
	 */
	public static Object getValue(final String p, final Map<String, Object> map) {
		String [] sp = p.split("(\\[)|(\\]\\.)");
		if (sp.length > 1) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) map.get(sp[0]);
			int idx = Integer.parseInt(sp[1]);
			return list.get(idx).get(sp[2]);
		} else {
			return map.get(p);
		}
	}
}
