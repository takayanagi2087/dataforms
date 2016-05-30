package dataforms.dao.file;

/**
 * Webリソースインターフェース。
 * <pre>
 * このインターフェースを実装したフィールドは画像等のWebリソースを処理するフィールドになります。
 * </pre>
 */
public interface WebResource {
	/**
	 * URLを取得します。
	 * @return URL。
	 */
	String getUrl();
	/**
	 * URLを設定します。
	 * @param url URL。
	 */
	void setUrl(final String url);
}
