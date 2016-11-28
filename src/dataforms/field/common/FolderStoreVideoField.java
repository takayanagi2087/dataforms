package dataforms.field.common;

import dataforms.dao.file.FileObject;
import dataforms.dao.file.ImageData;
import dataforms.dao.sqldatatype.SqlVarchar;

/**
 * フォルダ保存動画ファイルフィールドクラス。
 */
public class FolderStoreVideoField extends VideoField implements SqlVarchar {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 1024;

	/**
	 * コンストラクタ。
	 */
	public FolderStoreVideoField() {
		super(null);
		this.setLength(LENGTH);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FolderStoreVideoField(final String id) {
		super(id);
		this.setLength(LENGTH);
	}

	
	@Override
	protected FileObject newFileObject() {
		return new ImageData();
	}
}
