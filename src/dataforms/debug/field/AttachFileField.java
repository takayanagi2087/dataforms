package dataforms.debug.field;

import dataforms.field.common.BlobStoreFileField;


/**
 * AttachFileFieldフィールドクラス。
 *
 */
public class AttachFileField extends BlobStoreFileField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "添付ファイル";
	/**
	 * コンストラクタ。
	 */
	public AttachFileField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public AttachFileField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}
}
