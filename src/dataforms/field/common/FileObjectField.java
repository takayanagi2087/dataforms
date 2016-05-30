package dataforms.field.common;

import dataforms.dao.file.FileObject;

/**
 * ファイルフィールド。
 *
 */
public class FileObjectField extends FileField<FileObject> {
	/**
	 * コンストラクタ。
	 * @param id フィールドID.
	 */
	public FileObjectField(final String id) {
		super(id);
	}

	/**
	 * ファイルオブジェクトを作成します。
	 * @return ファイルオブジェクト。
	 */
	@Override
	protected FileObject newFileObject() {
		FileObject value = new FileObject();
		return value;
	}

}
