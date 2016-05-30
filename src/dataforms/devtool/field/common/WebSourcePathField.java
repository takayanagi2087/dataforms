package dataforms.devtool.field.common;

import dataforms.field.common.ExistingFolderField;
import dataforms.validator.RequiredValidator;

/**
 * HTMLやjavascript等のWebリソースを出力するパスフィールドクラス。
 *
 *
 */
public class WebSourcePathField extends ExistingFolderField {
	/**
	 * コンストラクタ。
	 */
	public WebSourcePathField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public WebSourcePathField(final String id) {
		super(id);
	}


	@Override
	protected void onBind() {
		super.onBind();
		this.setReadonly(true);
		this.addValidator(new RequiredValidator());
	}
}
