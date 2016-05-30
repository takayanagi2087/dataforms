package dataforms.field.sqlfunc;

import java.math.BigDecimal;

import dataforms.field.base.Field;

/**
 * 合計フィールドクラス。
 *
 * <pre>
 * QueryのfieldListにnew SumField("yyy", xxxField)を追加すると、以下のようなsqlを作成します。
 * select sum(m.xxx) as yyy ...  from main_table m  ...　group by ...
 * </pre>
 */
public class SumField extends GroupSummaryField<BigDecimal> {

	/**
	 * コンストラクタ.
	 * @param id フィールドID.
	 * @param field 集計対象フィールド.
	 */
	public SumField(final String id, final Field<?> field) {
		super(id, field);
	}

	@Override
	public String getFunctionName() {
		return "sum";
	}

}
