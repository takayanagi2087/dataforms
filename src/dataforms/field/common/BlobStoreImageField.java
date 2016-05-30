package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlBlob;

/**
 * BLOB保存画像ファイルフィールドクラス。
 *
 */
public class BlobStoreImageField extends ImageField implements SqlBlob {
	/**
	 * Log.
	 */
//	private Logger log = Logger.getLogger(BlobStoreImageField.class);

	/**
	 * コンストラクタ。
	 */
	public BlobStoreImageField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BlobStoreImageField(final String id) {
		super(id);
	}
}
