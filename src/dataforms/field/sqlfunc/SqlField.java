package dataforms.field.sqlfunc;

import dataforms.field.base.Field;

/**
 * SQLフィールドクラス。
 * <pre>
 * QueryのfieldListにnew SqlField("yyy", "to_char(x.fuge_hoge)")を追加すると、以下のようなsqlを作成します。
 * select to_char(x.fuge_hoge) as yyy ...
 * 任意の関数やサブクエリでフィールド値を取得するときに使用します。
 * </pre>
 */
public class SqlField extends Field<Object> {
	/**
	 * SQL。
	 */
	private String sql = null;

	/**
	 * ターゲットフィールド。
	 */
	private Field<?> targetField = null;

	/**
	 * コンストラクタ。
	 * @param field フィールド。
	 * @param sql フィールド値を取得するSQL。
	 */
	public SqlField(final Field<?> field, final String sql) {
		super(field.getId());
		this.sql = sql;
		this.targetField = field;
	}

	/**
	 * SQLを取得します。
	 * @return SQL。
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * SQLを設定します。
	 * @param sql SQL。
	 * @return 設定したフィールド。
	 */
	public SqlField setSql(final String sql) {
		this.sql = sql;
		return this;
	}

	@Override
	public void setClientValue(final Object v) {
		// 何もしない.
	}

	@Override
	public Field<?> cloneForSubQuery() {
		Field<?> ret =  this.targetField.clone();
		ret.setId(this.getId());
		return ret;
	}

}
