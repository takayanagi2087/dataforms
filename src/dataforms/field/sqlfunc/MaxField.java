package dataforms.field.sqlfunc;

import dataforms.field.base.Field;

/**
 * 最大値フィールドクラス。
 * <pre>
 * QueryのfieldListにnew MaxField("yyy", xxxField)を追加すると、以下のようなsqlを作成します。
 * select max(m.xxx) as yyy ...  from main_table m  ...　group by ...
 * </pre>
 */
public class MaxField extends GroupSummaryField<Object> {

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param field 集計対象フィールド。
	 */
	public MaxField(final String id, final Field<?> field) {
		super(id, field);
	}

	@Override
	public String getFunctionName() {
		return "max";
	}
}
