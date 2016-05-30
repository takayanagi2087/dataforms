package dataforms.debug.dao.filetest;

import dataforms.dao.Table;
import dataforms.debug.field.filetest.FileCommentField;
import dataforms.field.common.BlobStoreFileField;
import dataforms.field.common.BlobStoreImageField;
import dataforms.field.common.FolderStoreFileField;
import dataforms.field.common.FolderStoreImageField;
import dataforms.field.common.RecordIdField;


/**
 * FileFieldTestTableクラス。
 *
 */
public class FileFieldTestTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public FileFieldTestTable() {
		this.setAutoIncrementId(true);
		this.addPkField(new RecordIdField());
		this.addField(new FileCommentField());
		this.addField(new BlobStoreFileField("blobFile")).setComment("BLOB保存ファイル");
		this.addField(new FolderStoreFileField("folderFile")).setComment("フォルダ保存ファイル");;
		this.addField(new BlobStoreImageField("blobImage")).setComment("BLOB保存画像ファイル");
		this.addField(new FolderStoreImageField("folderImage")).setComment("フォルダ保存画像ファイル");;
		this.addUpdateInfoFields();
	}
}
