package dataforms.field.sqlfunc;

import dataforms.field.base.Field;
import dataforms.field.sqltype.BigintField;

/**
 * カウントフィールドクラス。
 * <pre>
 * QueryのfieldListにnew CountField("yyy", xxxField)を追加すると、以下のようなsqlを作成します。
 * select count(m.xxx) as yyy ...  from main_table m  ...　group by ...
 * </pre>
 */
public class CountField extends GroupSummaryField<Long> {

	/**
	 * DISTINCTフラグ。
	 */
	private boolean distinct = false;

	/**
	 * コンストラクタ.
	 * @param id フィールドID.
	 * @param field 集計対象フィールド.
	 */
	public CountField(final String id, final Field<?> field) {
		super(id, field);
		this.distinct = false;
	}

	/**
	 * コンストラクタ.
	 * @param id フィールドID.
	 * @param distinct DISTINCTフラグ.
	 * @param field 集計対象フィールド.
	 */
	public CountField(final String id, final Field<?> field, final boolean distinct) {
		super(id, field);
		this.distinct = distinct;
	}

	/**
	 * DISTINCTフラグを取得します。
	 * @return DISTINCTフラグ。
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * DISTINCTフラグを設定します。
	 * @param distinct DISTINCTフラグ。
	 */
	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	@Override
	public String getFunctionName() {
		return "count";
	}

	@Override
	public Field<?> cloneForSubQuery() {
		Field<?> ret = new BigintField(this.getId());
		return ret;
	}
}
