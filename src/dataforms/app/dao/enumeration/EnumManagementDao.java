package dataforms.app.dao.enumeration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.controller.ApplicationException;
import dataforms.dao.Dao;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.Query;
import dataforms.dao.Table;
import dataforms.dao.TableList;
import dataforms.field.base.FieldList;
import dataforms.field.common.RowNoField;

/**
 * 列挙型管理Daoクラス。
 *
 */
public class EnumManagementDao extends Dao {
	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public EnumManagementDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}

	/**
	 * QueryFormで指定した条件で行う問い合わせクラスです。
	 */
	public static class EnumOptionTableQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public EnumOptionTableQuery() {
			this.setDistinct(true);
			EnumOptionTable table = new EnumOptionTable();
			EnumTypeNameTable nameTable = new EnumTypeNameTable();
			this.setFieldList(new FieldList(
				table.getEnumTypeCodeField()
				, nameTable.getEnumTypeNameField()
			));
			this.setMainTable(table);
			this.setLeftJoinTableList(new TableList(nameTable));
			this.setOrderByFieldList(new FieldList(table.getEnumTypeCodeField()));
		}
	}

	/**
	 * 問い合わせ結果フォームのフィールドリストを取得します。
	 * @return 問い合わせ結果フォームのフィールドリスト。
	 */
	public static FieldList getQueryResultFieldList() {
		Query query = new EnumOptionTableQuery();
		FieldList list = new FieldList();
		list.addField(new RowNoField());
		list.addAll(query.getFieldList());
		return list;
	}

	/**
	 * QueryFormから入力された条件から、テーブルを検索し、指定されたページの情報を返します。
	 * @param data 条件データ。
	 * @param flist 条件フィールドリスト。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {
		Query query = new EnumOptionTableQuery();
		query.setQueryFormFieldList(flist);
		query.setQueryFormData(data);
		return this.executePageQuery(query);
	}

	/**
	 * QueryFormから入力された条件から、テーブルを検索し、マッチするすべてのデータを返します。
	 * @param data 条件データ。
	 * @param flist 条件フィールドリスト。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> query(final Map<String, Object> data, final FieldList flist) throws Exception {
		// 今回は使用しない。
/*		Query query = new EnumOptionTableQuery();
		query.setQueryFormFieldList(flist);
		query.setQueryFormData(data);
		return this.executeQuery(query);
*/		return null;
	}

	/**
	 * 列挙型タイプ名称リストの問い合わせ。
	 *
	 */
	private class EnumTypeNameQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data フォームデータ。
		 */
		public EnumTypeNameQuery(final Map<String, Object> data) {
			EnumTypeNameTable table = new EnumTypeNameTable();
			this.setFieldList(table.getFieldList());
			this.setMainTable(table);
			this.setQueryFormFieldList(new FieldList(table.getEnumTypeCodeField()));
			this.setQueryFormData(data);
			this.setOrderByFieldList(new FieldList(table.getLangCodeField()));
		}
	}

	/**
	 * 列挙型タイプ名称リストの問い合わせ。
	 *
	 */
	private class EnumOptionNameQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data フォームデータ。
		 */
		public EnumOptionNameQuery(final Map<String, Object> data) {
			EnumOptionTable table = new EnumOptionTable();
			EnumOptionNameTable ntable = new EnumOptionNameTable();
			FieldList flist = new FieldList();
			flist.addAll(table.getFieldList());
			flist.add(ntable.getLangCodeField());
			flist.add(ntable.getEnumOptionNameField());
			this.setFieldList(flist);
			this.setMainTable(table);
			this.setJoinTableList(new TableList(ntable));
			this.setQueryFormFieldList(new FieldList(table.getEnumTypeCodeField()));
			this.setQueryFormData(data);
			this.setOrderByFieldList(new FieldList(table.getSortOrderField(), ntable.getLangCodeField()));
		}
	}


	/**
	 * PKでレコードを限定し、データを取得します。
	 * @param data 条件データ PKの情報をすべて含むマップ。
	 * @return ヒットするレコード。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> query(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		//ret.put("enumTypeCode", data.get("enumTypeCode"));
		EnumOptionTable.Entity de = new EnumOptionTable.Entity(data);
		EnumOptionTable.Entity e = new EnumOptionTable.Entity(ret);
		e.setEnumTypeCode(de.getEnumTypeCode());
		List<Map<String, Object>> typeNameList = this.executeQuery(new EnumTypeNameQuery(data));
		ret.put("typeNameList", typeNameList);
		List<Map<String, Object>> optionNameList = this.executeQuery(new EnumOptionNameQuery(data));
		ret.put("optionNameList", optionNameList);
		return ret;
	}

	/**
	 * データを追加します。
	 * @param data データ。
	 * @return 追加件数。
	 * @throws Exception 例外。
	 */
/*	public int insert(final Map<String, Object> data) throws Exception {
		return this.executeInsert(new EnumOptionTable(), data);
	}
*/



	/**
	 * データを更新します。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	public void update(final Map<String, Object> data) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> typeNameList = (List<Map<String, Object>>) data.get("typeNameList");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> optionNameList = (List<Map<String, Object>>) data.get("optionNameList");
		Table table = new EnumOptionTable();
		// 楽観ロックチェック
		for (Map<String, Object> m: optionNameList) {
			boolean ret = this.isUpdatable(table, m);
			if (!ret) {
				throw new ApplicationException(this.getPage(), "error.notupdatable");
			}
		}
		this.delete(data);
		// データ更新
		this.executeInsert(new EnumTypeNameTable(), typeNameList);
		this.executeInsert(new EnumOptionNameTable(), optionNameList);
		short sortOrder = 0;
		for (Map<String, Object> m: optionNameList) {
			EnumOptionNameTable.Entity e = new EnumOptionNameTable.Entity(m);
			String lang = e.getLangCode(); //(String) m.get("langCode");
			if ("default".equals(lang)) {
				EnumOptionTable.Entity oe = new EnumOptionTable.Entity(m);
				//m.put("sortOrder", Short.valueOf(sortOrder++));
				oe.setSortOrder(Short.valueOf(sortOrder++));
				this.executeInsert(new EnumOptionTable(), m);
			}
		}
	}

	/**
	 * テーブル単位のデータ削除。
	 * @param table 削除対応テーブル。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	private void deleteEnumTable(final Table table, final Map<String, Object> data) throws Exception {
		this.executeDelete(table, new FieldList(table.getField(EnumOptionTable.Entity.ID_ENUM_TYPE_CODE)), data, true);
	}

	/**
	 * データを削除します。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	public void delete(final Map<String, Object> data) throws Exception {
		this.deleteEnumTable(new EnumTypeNameTable(), data); // レコードの物理削除
		this.deleteEnumTable(new EnumOptionNameTable(), data); // レコードの物理削除
		this.deleteEnumTable(new EnumOptionTable(), data); // レコードの物理削除
	}
}
