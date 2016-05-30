package dataforms.dao.sqldatatype;

import java.util.Map;

/**
 * blob型インターフェース。
 * <pre>
 * このインターフェースを実装したフィールドは基本的にblob型となります。
 * </pre>
 */
public interface SqlBlob {
	/**
	 * フィールドIDを取得します。
	 * @return フィールドID。
	 */
	String getId();
	
	/**
	 * ダウンロードパラメータを取得します。
	 * @param m データマップ。
	 * @return ダウンロードパラメータ。
	 */
	String getBlobDownloadParameter(final Map<String, Object> m);
}
