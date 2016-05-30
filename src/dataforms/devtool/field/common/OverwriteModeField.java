package dataforms.devtool.field.common;

import dataforms.field.common.EnumOptionSingleSelectField;

/**
 * 上書きモードフィールドクラス。
 *
 */
public class OverwriteModeField extends EnumOptionSingleSelectField {
	/**
	 * コンストラクタ。
	 */
	public OverwriteModeField() {
		super(null, "overwriteMode");
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public OverwriteModeField(final String id) {
		super(id, "overwriteMode");
	}
}
