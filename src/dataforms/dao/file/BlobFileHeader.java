package dataforms.dao.file;

import java.io.Serializable;

/**
 * BLOB項目の先頭に保存する管理情報クラス。
 *
 */
public class BlobFileHeader implements Serializable {
	/**
	 * UID。
	 */
	private static final long serialVersionUID = -7222388177179295487L;
	/**
	 * ファイル名。
	 */
	private String fileName = null;
	/**
	 * ファイル長。
	 */
	private long length = 0L;

	/**
	 * コンストラクタ。
	 * @param name ファイル名。
	 * @param len ファイル長。
	 */
	public BlobFileHeader(final String name, final long len) {
		this.fileName = name;
		this.length = len;
	}

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

	/**
	 * ファイル長を取得します。
	 * @return ファイル長。
	 */
	public long getLength() {
		return length;
	}

	/**
	 * ファイル長を設定します。
	 * @param length ファイル長。
	 */
	public void setLength(final long length) {
		this.length = length;
	}

	/**
	 * ヘッダ情報に対応したFileObjectのインスタンスを作成します。
	 * @return ヘッダ情報に対応したFileObjectのインスタンス。
	 */
	public FileObject newFileObject() {
		FileObject fobj = new FileObject();
		fobj.setFileName(this.getFileName());
		fobj.setLength(this.getLength());
		return fobj;
	}
}
