package dataforms.field.common;

import dataforms.field.sqltype.SmallintField;

/**
 * ソート順フィールドクラス。
 * <pre>
 * 各種データのソート順を記録するフィールドです。
 * SortOrderFieldをEdiatableHtmlTableクラスに配置し、各行をドラッグし順序を変更した場合、
 * 自動的に更新し、順序を記録します。
 * </pre>
 */
public class SortOrderField extends SmallintField {
	/**
	 * コンストラクタ。
	 */
	public SortOrderField() {
		super(null);
		this.setHidden(true);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SortOrderField(final String id) {
		super(id);
		this.setHidden(true);
	}
}
