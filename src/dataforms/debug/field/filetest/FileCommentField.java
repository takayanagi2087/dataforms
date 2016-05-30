package dataforms.debug.field.filetest;

import dataforms.debug.page.filetest.FileTestEditForm;
import dataforms.field.sqltype.VarcharField;
import dataforms.validator.RequiredValidator;

/**
 * FileCommentFieldフィールドクラス。
 *
 */
public class FileCommentField extends VarcharField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 64;
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "ファイルコメント";
	/**
	 * コンストラクタ。
	 */
	public FileCommentField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FileCommentField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		if (this.getParentForm() instanceof FileTestEditForm) {
			this.addValidator(new RequiredValidator());
		}
	}
}
