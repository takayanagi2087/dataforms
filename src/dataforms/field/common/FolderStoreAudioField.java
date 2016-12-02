package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlVarchar;

/**
 * フォルダ保存動画ファイルフィールドクラス。
 */
public class FolderStoreAudioField extends AudioField implements SqlVarchar {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 1024;

	/**
	 * コンストラクタ。
	 */
	public FolderStoreAudioField() {
		super(null);
		this.setLength(LENGTH);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FolderStoreAudioField(final String id) {
		super(id);
		this.setLength(LENGTH);
	}
	
}
