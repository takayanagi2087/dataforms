package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlBlob;


/**
 * BLOB保存ファイルフィールドクラス。
 *
 */
public class BlobStoreFileField extends FileObjectField implements SqlBlob {
	/**
	 * Log.
	 */
//	private Logger log = Logger.getLogger(BlobStoreFileField.class);

	/**
	 * コンストラクタ。
	 */
	public BlobStoreFileField() {
		super(null);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BlobStoreFileField(final String id) {
		super(id);
	}

}
