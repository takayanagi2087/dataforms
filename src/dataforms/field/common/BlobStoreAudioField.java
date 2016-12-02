package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlBlob;

/**
 * BLOB保存音声ファイルフィールドクラス。
 *
 */
public class BlobStoreAudioField extends AudioField implements SqlBlob {
	/**
	 * Log.
	 */
//	private Logger log = Logger.getLogger(BlobStoreImageField.class);

	/**
	 * コンストラクタ。
	 */
	public BlobStoreAudioField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public BlobStoreAudioField(final String id) {
		super(id);
	}
}
