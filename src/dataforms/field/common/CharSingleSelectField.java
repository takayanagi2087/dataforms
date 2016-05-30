package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlChar;

/**
 * CHAR型の単一選択フィールドクラス。
 * <pre>
 * optionList中の選択肢を単一選択の&lt;select&gt;や&lt;input type=&quot;radio&quot; ...&gt;に
 * 表示し、選択するためのフィールドです。
 * </pre>
 *
 */
public class CharSingleSelectField extends SingleSelectField<String> implements SqlChar {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param length 長さ。
	 */
	public CharSingleSelectField(final String id, final int length) {
		super(id, length);
	}
};
