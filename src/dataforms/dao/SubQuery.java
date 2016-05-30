package dataforms.dao;

import dataforms.field.base.Field;
import dataforms.field.base.FieldList;

/**
 * 副問い合わせクラス。
 * <pre>
 *  (select ...)のような副問い合わせを実現するためのクラスです。
 *  コンストラクタに渡されたQueryやsqlをTableと同様に
 *  処理できるようにするためのクラスです。
 * </pre>
 */
public class SubQuery extends Table {
	/**
	 * 問い合わせ。
	 */
	private Query query = null;
	/**
	 * SQL。
	 */
	private String sql = null;


	/**
	 * コンストラクタ。
	 */
	public SubQuery() {
		this.query = null;
		this.sql = null;
	}

	/**
	 * コンストラクタ。
	 * @param query 問い合わせ。
	 */
	public SubQuery(final Query query) {
		this.query = query;
		this.sql = null;
		FieldList flist = cloneFieldList(query.getFieldList());
		this.setFieldList(flist);
	}

	/**
	 * Fieldリストのコピーを作成します。
	 * @param fieldList コピー元のフィールドリスト。
	 * @return コピー結果。
	 */
	private FieldList cloneFieldList(final FieldList fieldList) {
		FieldList flist = new FieldList();
		for (Field<?> f : fieldList) {
			Field<?> nf = f.cloneForSubQuery();
			nf.setTable(this);
			flist.add(nf);
/*			if (f instanceof GroupSummaryField) {
				GroupSummaryField<?> gsf = (GroupSummaryField<?>) f;
				if (f instanceof SumField || f instanceof AvgField) {
					Field<?> sf = gsf.getTargetField().clone();
					sf.setTable(this);
					flist.add(sf);
				} else if (f instanceof MinField || f instanceof MaxField) {
					Field<?> sf = gsf.getTargetField().clone();
					sf.setTable(this);
					flist.add(sf);
				} else if (f instanceof CountField) {
					Field<?> sf = new BigintField(gsf.getId());
					sf.setTable(this);
					flist.add(sf);
				}
			} else {
				Field<?> nf = f.clone();
				nf.setTable(this);
				flist.add(nf);
			}*/
		}
		return flist;
	}

	/**
	 * コンストラクタ。
	 * @param flist フィールドリスト。
	 * @param sql SQL。
	 */
	public SubQuery(final FieldList flist, final String sql) {
		this.query = null;
		this.sql = sql;
		this.setFieldList(this.cloneFieldList(flist));
	}

	/**
	 * 問い合わせを取得します。
	 * @return 問い合わせ。
	 */
	public Query getQuery() {
		return query;
	}
	/**
	 * 問い合わせを設定します。
	 * @param query 問い合わせ。
	 */
	public void setQuery(final Query query) {
		this.query = query;
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
	 */
	public void setSql(final String sql) {
		this.sql = sql;
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * "(select ...)"の形式の文字列を返します。
	 * </pre>
	 */
	@Override
	public String getTableName() {
		if (this.sql != null) {
			return "(" + this.sql + ")";
		} else {
			return null;
		}
	}
}
