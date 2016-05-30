package dataforms.devtool.field.common;

import dataforms.field.common.ExistingFolderField;
import dataforms.validator.RequiredValidator;

/**
 * Javaソースファイルを出力するパスフィールドクラス。
 *
 *
 */
public class JavaSourcePathField extends ExistingFolderField {
	/**
	 * コンストラクタ。
	 */
	public JavaSourcePathField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public JavaSourcePathField(final String id) {
		super(id);
	}


	@Override
	protected void onBind() {
		super.onBind();
		this.setReadonly(true);
		this.addValidator(new RequiredValidator());
	}
}
