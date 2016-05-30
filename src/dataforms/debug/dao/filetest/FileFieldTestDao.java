package dataforms.debug.dao.filetest;

import java.util.Map;

import dataforms.dao.Dao;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.Query;
import dataforms.field.base.FieldList;

/**
 * ファイルフィールドテスト用DAO。
 *
 */
public class FileFieldTestDao extends Dao {
	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public FileFieldTestDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}

	/**
	 * 問い合わせ。
	 */
	public static class MainQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public MainQuery() {
			FileFieldTestTable tbl = new FileFieldTestTable();
			this.setFieldList(new FieldList(
				tbl.getField("recordId")
				, tbl.getField("fileComment")
			));
			this.setMainTable(tbl);
			this.setOrderByFieldList(new FieldList(tbl.getField("recordId")));
		}
	}

	/**
	 * クエリを実行します。
	 * @param data パラメータ。
	 * @param flist フィールドリスト。
	 * @return クエリ結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> getQueryResult(final Map<String, Object> data, final FieldList flist) throws Exception {
		Query query = new MainQuery();
		query.setQueryFormFieldList(flist);
		query.setQueryFormData(data);
		//
		Map<String, Object> ret = executePageQuery(query);
		return ret;
	}


	/**
	 * 編集データ取得用問い合わせ。
	 *
	 */
	public static class EditDataQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public EditDataQuery() {
			FileFieldTestTable tbl = new FileFieldTestTable();
			this.setFieldList(tbl.getFieldList());
			this.setMainTable(tbl);
			this.setQueryFormFieldList(tbl.getPkFieldList());
		}
	}

	/**
	 * 編集データを取得するための問い合わせ。
	 * @param data パラメータ。
	 * @return 編集データ。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryEditData(final Map<String, Object> data) throws Exception {
		EditDataQuery query = new EditDataQuery();
		query.setQueryFormData(data);
		return this.executeRecordQuery(query);
	}
}
