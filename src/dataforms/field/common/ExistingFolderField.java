package dataforms.field.common;

import dataforms.validator.ExistingFolderValidator;


/**
 * 存在するフォルダフィールドクラス。
 *
 */
public class ExistingFolderField extends FolderField {

	/**
	 * コンストラクタ。
	 */
	public ExistingFolderField() {
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public ExistingFolderField(final String id) {
		super(id);
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new ExistingFolderValidator());
	}
}
