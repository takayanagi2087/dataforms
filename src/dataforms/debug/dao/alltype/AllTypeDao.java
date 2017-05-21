package dataforms.debug.dao.alltype;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.controller.ApplicationException;
import dataforms.dao.Dao;
import dataforms.dao.Index;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.Query;
import dataforms.dao.SubQuery;
import dataforms.dao.Table;
import dataforms.dao.TableList;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.sqlfunc.AliasField;
import dataforms.field.sqlfunc.AvgField;
import dataforms.field.sqlfunc.CountField;
import dataforms.field.sqlfunc.MaxField;
import dataforms.field.sqlfunc.MinField;
import dataforms.field.sqlfunc.SqlField;
import dataforms.field.sqlfunc.SumField;
import dataforms.field.sqltype.NumericField;
import net.arnx.jsonic.JSON;

/**
 * 全データタイプデータアクセスオブジェクトクラス。
 *
 */
public class AllTypeDao extends Dao {

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(AllTypeDao.class.getName());


	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public AllTypeDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
		AllTypeTable tbl = new AllTypeTable();
		List<Index> ilist = tbl.getIndexList();
		for (Index index: ilist) {
			log.debug("index=" + index.getClass().getName());
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
		AllTypeTable tbl = new AllTypeTable();
		Query query = new Query();
		query.setFieldList(new FieldList(
				tbl.getField("recordIdField")
				, tbl.getField("charField")
				, tbl.getField("varcharField")
				, tbl.getField("numericField")
				, new SqlField(new NumericField("sqlField", 10, 3), "numeric_field * 100")
				));
		query.setMainTable(tbl);
		query.setQueryFormFieldList(flist);
		query.setQueryFormData(data);

		String sortOrder = (String) data.get("sortOrder");
		log.debug("sortOrder=" + sortOrder);
		FieldList sflist = tbl.getFieldList().getOrderByFieldList(sortOrder);
		log.debug("sflist.size()=" + sflist.size());
		if (sflist.size() == 0) {
			query.setOrderByFieldList(new FieldList(tbl.getField("recordIdField").setSortOrder(Field.SortOrder.DESC)));
		} else {
			query.setOrderByFieldList(sflist);
		}
		//
		Map<String, Object> ret = executePageQuery(query);
		return ret;
	}

	/**
	 * 選択したデータを取得します。
	 * @param data パラメータ。
	 * @param flist フィールドリスト。
	 * @return 取得結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> getSelectedData(final Map<String, Object> data, final FieldList flist) throws Exception {
		AllTypeTable table = new AllTypeTable();
		Query query = new Query();
		query.setFieldList(table.getFieldList());
		query.setMainTable(table);
		query.setQueryFormFieldList(flist);
		query.setQueryFormData(data);
		Map<String, Object> rec = this.executeRecordQuery(query);
		List<Map<String, Object>> list = this.getAttachFileList(data, flist);
		rec.put("attachFileTable", list);
		return rec;
	}


	/**
	 * 添付ファイルリストを取得します。
	 * @param data データ。
	 * @param flist フィールドリスト。
	 * @return 添付ファイルリスト。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> getAttachFileList(final Map<String, Object> data, final FieldList flist)
			throws Exception {
		AllTypeAttachFileTable atable = new AllTypeAttachFileTable();
		Query aquery = new Query();
		aquery.setFieldList(atable.getFieldList());
		aquery.setMainTable(atable);
		aquery.setQueryFormFieldList(flist);
		aquery.setQueryFormData(data);
		aquery.setOrderByFieldList(new FieldList(atable.getField("sortOrder")));
		List<Map<String, Object>> list = this.executeQuery(aquery);
		return list;
	}

	/**
	 * 選択したデータを参照登録用として取得します。
	 * @param data パラメータ。
	 * @param flist フィールドリスト。
	 * @return 取得結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> getReferData(final Map<String, Object> data, final FieldList flist) throws Exception {
		Map<String, Object> rec = this.getSelectedData(data, flist);
		// キー情報は取得しない.
		rec.remove("recordIdField");
		// ファイルアップロード系のフィールドは削除.
		rec.remove("uploadBlobData");
		rec.remove("uploadFileData");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> table = (List<Map<String, Object>>) rec.get("attachFileTable");
		for (Map<String, Object> m: table) {
			m.remove("recordIdField");
			m.remove("blobData");
			m.remove("fileData");
		}
		return rec;
	}

	/**
	 * テーブルに対する主キーの設定を行綯います。
	 * @param data 更新データ。
	 */
	private void setTablePrimaryKey(final Map<String, Object> data) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attachFileTable");
		if (list != null) {
			for (Map<String, Object> m: list) {
				m.put("recordIdField", data.get("recordIdField"));
			}
		}
	}

	/**
	 * AllTypeに対する登録を行ないます。
	 * @param data パラメータ。
	 * @throws Exception 例外。
	 */
	public void insertAllType(final Map<String, Object> data) throws Exception {
		AllTypeTable table = new AllTypeTable();
		this.executeInsert(table, data);
		this.setTablePrimaryKey(data);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attachFileTable");
		if (list != null) {
			AllTypeAttachFileTable aftable = new AllTypeAttachFileTable();
			this.saveTable(aftable, list, data);
/*			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				m.put("fileKey", Integer.valueOf(i));
			}
			this.executeInsert(aftable, list);*/
		}
	}

	/**
	 * 更新可能かどうかを判定します。
	 * <pre>
	 * 楽観ロック用のチェックを実装します。
	 * </pre>
	 * @param data パラメータ。
	 * @return 更新可能な場合true。
	 * @throws Exception 例外。
	 */
	private boolean isUpdatableAllType(final Map<String, Object> data) throws Exception {
		AllTypeTable tbl = new AllTypeTable();
		boolean ret = this.isUpdatable(tbl, data);
		if (ret) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attachFileTable");
			if (list != null) {
				AllTypeAttachFileTable ftbl = new AllTypeAttachFileTable();
				for (Map<String, Object> m: list) {
					if (m.get("fileKey") != null) {
						if (!this.isUpdatable(ftbl, m)) {
							ret = false;
							break;
						}
					}
				}
			}
		}
		return ret;
	}




	/**
	 * AllTypeに対する更新を行います。
	 * @param data パラメータ。
	 * @throws Exception 例外。
	 */
	public void updateAllType(final Map<String, Object> data) throws Exception {
		this.setTablePrimaryKey(data);
		if (!this.isUpdatableAllType(data)) {
			throw new ApplicationException(this.getPage(), "error.notupdatable");
		}
		AllTypeTable table = new AllTypeTable();
		this.executeUpdate(table, data);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attachFileTable");
		if (list != null) {
			AllTypeAttachFileTable aftable = new AllTypeAttachFileTable();
			this.saveTable(aftable, list, data);
		}
	}


	/**
	 * AllTypeTableのデータを削除します。
	 * @param data パラメータ。
	 * @throws Exception 例外。
	 */
	public void deleteAllType(final Map<String, Object> data) throws Exception {
		this.setTablePrimaryKey(data);
		log.debug("delete data=" + JSON.encode(data, true));
		AllTypeTable tbl = new AllTypeTable();
		this.executeDelete(tbl, data);
//		this.executeRemove(tbl, data);
		AllTypeAttachFileTable atbl = new AllTypeAttachFileTable();
		List<Map<String, Object>> list = this.getAttachFileList(data, tbl.getPkFieldList());
		this.executeDelete(atbl, list);
//		this.executeRemove(atbl, tbl.getPkFieldList(), data, false);
	}

	/**
	 * AliasField,SqlFieldのテスト問い合わせ。
	 *
	 */
	public static class AliasQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public AliasQuery() {
			AllTypeTable table = new AllTypeTable();
			this.setFieldList(new FieldList(
				table.getField("recordIdField")
				, new AliasField("vcalias", table.getField("varcharField"))
				, new SqlField(table.getField("numericField"), "m.numeric_field * 100")));
			this.setMainTable(table);
		}
	}

	/**
	 * SumField,AvarageFieldのテスト問い合わせ。
	 *
	 */
	public static class SumQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public SumQuery() {
			AllTypeAttachFileTable table = new AllTypeAttachFileTable();
			this.setFieldList(new FieldList(
				table.getField("recordIdField")
				, new SumField("sumSortOrder", table.getField("sortOrder"))
				, new AvgField("avgSortOrder", table.getField("sortOrder"))
				));
			this.setMainTable(table);
		}
	}

	/**
	 * CountFieldのテスト問い合わせ。
	 *
	 */
	private static class CountQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public CountQuery() {
			AllTypeAttachFileTable table = new AllTypeAttachFileTable();
			this.setFieldList(new FieldList(
				table.getField("recordIdField")
				, new CountField("recCount", table.getField("fileKey"))
				));
			this.setMainTable(table);
		}
	}

	/**
	 * MinField, MaxFieldのテスト問い合わせ。
	 *
	 */
	private static class MinMaxQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public MinMaxQuery() {
			AllTypeAttachFileTable table = new AllTypeAttachFileTable();
			this.setFieldList(new FieldList(
				table.getField("recordIdField")
				, new MinField("minComment", table.getField("fileComment"))
				, new MaxField("maxComment", table.getField("fileComment"))
				));
			this.setMainTable(table);
		}
	}

	/**
	 * テスト問い合わせ。
	 *
	 */
	private static class TestQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public TestQuery() {
			SubQuery aliasSq = new SubQuery(new AliasQuery()) {
				public String getJoinCondition(final Table joinTable, final String alias) {
					return this.getLinkFieldCondition("recordIdField", joinTable, alias, "recordIdField");
				};
			};
			aliasSq.setAlias("msub");
			SubQuery sumSq = new SubQuery(new SumQuery());
			SubQuery countSq = new SubQuery(new CountQuery());
			SubQuery minMaxSq = new SubQuery(new MinMaxQuery());
			AllTypeAttachFileTable atable = new AllTypeAttachFileTable();
			String sql = "select record_id_field, file_comment from all_type_attach_file";
			SubQuery sqlSq = new SubQuery(new FieldList(atable.getField("recordIdField"), atable.getField("fileComment")), sql);
			FieldList flist = new FieldList();
			flist.addAll(aliasSq.getFieldList());
			flist.marge(sumSq.getFieldList());
			flist.marge(countSq.getFieldList());
			flist.marge(minMaxSq.getFieldList());
			flist.marge(sqlSq.getFieldList());
			this.setFieldList(flist);
			this.setMainTable(aliasSq);
			this.setLeftJoinTableList(new TableList(sumSq, countSq, minMaxSq, sqlSq));
		}
	}

	/**
	 * 各種問い合わせのテストメソッド。
	 * @throws Exception 例外。
	 */
	public void subQueryTest() throws Exception {
		TestQuery query = new TestQuery();
		List<Map<String, Object>> list = this.executeQuery(query);
		String json = JSON.encode(list, true);
		log.debug("testQueryResult=" + json);
	}
}
