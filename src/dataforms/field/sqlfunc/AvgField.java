package dataforms.field.sqlfunc;

import dataforms.field.base.Field;

/**
 * 平均値値フィールドクラス。
 * <pre>
 * QueryのfieldListにnew AvgField("yyy", xxxField)を追加すると、以下のようなsqlを作成します。
 * select avg(m.xxx) as yyy ...  from main_table m  ...　group by ...
 * </pre>
 */
public class AvgField extends GroupSummaryField<Object> {

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param field 集計対象フィールド。
	 */
	public AvgField(final String id, final Field<?> field) {
		super(id, field);
	}

	@Override
	public String getFunctionName() {
		return "avg";
	}

}
