package dataforms.controller;


/**
 * ファイル応答情報クラス。
 * <pre>
 * ファイルダウンロード応答の基本クラスです。
 * </pre>
 *
 */
public abstract class FileResponse extends Response {

	/**
	 * ファイル名。
	 */
	private String fileName = null;

	/**
	 * ファイル名を取得します。
	 * @return ファイル名。
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * ファイル名を設定します。
	 * @param fileName ファイル名。
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

}
