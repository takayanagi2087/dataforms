package dataforms.field.sqlfunc;

import dataforms.field.base.Field;

/**
 * 最小値フィールドクラス。
 * <pre>
 * QueryのfieldListにnew MinField("yyy", xxxField)を追加すると、以下のようなsqlを作成します。
 * select min(m.xxx) as yyy ...  from main_table m  ...　group by ...
 * </pre>
 */
public class MinField extends GroupSummaryField<Object> {

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param field 集計対象フィールド。
	 */
	public MinField(final String id, final Field<?> field) {
		super(id, field);
	}

	@Override
	public String getFunctionName() {
		return "min";
	}

}
