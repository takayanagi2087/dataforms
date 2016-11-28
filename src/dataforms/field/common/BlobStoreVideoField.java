package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlBlob;

/**
 * BLOB保存動画ファイルフィールドクラス。
 *
 */
public class BlobStoreVideoField extends VideoField implements SqlBlob {
	/**
	 * Log.
	 */
//	private Logger log = Logger.getLogger(BlobStoreImageField.class);

	/**
	 * コンストラクタ。
	 */
	public BlobStoreVideoField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BlobStoreVideoField(final String id) {
		super(id);
	}
}
