package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlVarchar;

/**
 * フォルダ保存画像ファイルフィールドクラス。
 */
public class FolderStoreFileField extends FileObjectField implements SqlVarchar {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 1024;

	/**
	 * コンストラクタ。
	 */
	public FolderStoreFileField() {
		super(null);
		this.setLength(LENGTH);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FolderStoreFileField(final String id) {
		super(id);
		this.setLength(LENGTH);
	}
}
