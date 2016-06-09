package dataforms.devtool.field.common;

import dataforms.field.common.EnumOptionSingleSelectField;

/**
 * 上書きモードフィールドクラス。
 *
 */
public class OverwriteModeField extends EnumOptionSingleSelectField {

	/**
	 * エラー.
	 */
	public static final String ERROR = "error";
	/**
	 * 上書きしない.
	 */
	public static final String SKIP = "skip";
	/**
	 * 強制的に上書き.
	 */
	public static final String FORCE_OVERWRITE = "force";
	
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
