package dataforms.app.dao.enumeration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.app.field.enumeration.EnumTypeNameField;
import dataforms.controller.ApplicationException;
import dataforms.dao.Dao;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.Query;
import dataforms.dao.Table;
import dataforms.dao.TableList;
import dataforms.field.base.FieldList;
import dataforms.field.common.RowNoField;
import dataforms.field.sqlfunc.CountField;
import dataforms.field.sqlfunc.SqlField;

/**
 * Daoクラス。
 *
 */
public class EnumGroupDao extends Dao {
	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public EnumGroupDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}

	/**
	 * QueryFormで指定した条件で行う問い合わせクラスです。
	 */
	public static class EnumGroupListQuery extends Query {
		/**
		 * コンストラクタ。
		 */
		public EnumGroupListQuery() {
			EnumGroupTable table = new EnumGroupTable();
			this.setFieldList(new FieldList(
				table.getEnumGroupCodeField(),
				new CountField("codeCount", table.getEnumTypeCodeField())
			));
			this.setMainTable(table);
		}
	}

	
	/**
	 * 問い合わせ結果フォームのフィールドリストを取得します。
	 * @return 問い合わせ結果フォームのフィールドリスト。
	 */
	public static FieldList getQueryResultFieldList() {
		EnumGroupListQuery query = new EnumGroupListQuery();
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
		EnumGroupListQuery query = new EnumGroupListQuery();
		query.setQueryFormFieldList(flist);
		query.setQueryFormData(data);
		String sortOrder = (String) data.get("sortOrder");
		FieldList sflist = query.getFieldList().getOrderByFieldList(sortOrder);
		if (sflist.size() == 0) {
			query.setOrderByFieldList(new FieldList(query.getMainTable().getField(EnumGroupTable.Entity.ID_ENUM_GROUP_CODE)));
		} else {
			query.setOrderByFieldList(sflist);
		}
		return this.executePageQuery(query);
	}

	/**
	 * QueryFormで指定した条件で行う問い合わせクラスです。
	 */
	public static class EnumGroupTableQuery extends Query {
		
		
		/**
		 * コンストラクタ。
		 */
		public EnumGroupTableQuery() {
			EnumTypeNameTable dt = new EnumTypeNameTable();
			dt.setAlias("dt");
			EnumTypeNameTable ct = new EnumTypeNameTable();
			ct.setAlias("ct");
			EnumGroupTable table = new EnumGroupTable() {
				@Override
				public String getJoinCondition(final Table joinTable, final String alias) {
					if ("dt".equals(alias)) {
						return this.getLinkFieldCondition(EnumGroupTable.Entity.ID_ENUM_TYPE_CODE, joinTable, alias, EnumTypeNameTable.Entity.ID_ENUM_TYPE_CODE)
								+ " and dt.lang_code='default'";
					}
					if ("ct".equals(alias)) {
						return this.getLinkFieldCondition(EnumGroupTable.Entity.ID_ENUM_TYPE_CODE, joinTable, alias, EnumTypeNameTable.Entity.ID_ENUM_TYPE_CODE)
								+ " and ct.lang_code=:lang_code";
					}
					return super.getJoinCondition(joinTable, alias);
				}
			};
			FieldList flist = new FieldList();
			flist.addAll(table.getFieldList());
			flist.add(new SqlField(new EnumTypeNameField(), "(case when ct.enum_type_name is not null then ct.enum_type_name else dt.enum_type_name end)"));
			this.setFieldList(flist);
			this.setMainTable(table);
			this.setLeftJoinTableList(new TableList(dt, ct));
			this.setCondition(
				"m.enum_group_code=:enum_group_code"
			);
		}
	}


	/**
	 * QueryFormから入力された条件から、テーブルを検索し、マッチするすべてのデータを返します。
	 * @param data 条件データ。
	 * @param flist 条件フィールドリスト。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> query(final Map<String, Object> data, final FieldList flist) throws Exception {
		EnumGroupTableQuery query = new EnumGroupTableQuery();
		query.setQueryFormFieldList(flist);
		query.setQueryFormData(data);
		query.setOrderByFieldList(new FieldList(query.getMainTable().getField(EnumGroupTable.Entity.ID_SORT_ORDER)));
		return this.executeQuery(query);
	}


	/**
	 * PKでレコードを限定し、データを取得します。
	 * @param data 条件データ PKの情報をすべて含むマップ。
	 * @return ヒットしたレコード。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> query(final Map<String, Object> data) throws Exception {
		EnumGroupTableQuery query = new EnumGroupTableQuery();
		query.setOrderByFieldList(new FieldList(query.getFieldList().get(EnumGroupTable.Entity.ID_SORT_ORDER)));
		query.setQueryFormData(data);
		Map<String, Object> ret = new HashMap<String, Object>();
		List<Map<String, Object>> codeList = this.executeQuery(query);
		ret.put("codeList", codeList);
		if (codeList.size() > 0) {
			EnumGroupTable.Entity e = new EnumGroupTable.Entity(codeList.get(0));
			ret.put("enumGroupCode", e.getEnumGroupCode());
		}
		return ret;
	}

	/**
	 * データを追加します。
	 * @param data データ。
	 * @return 追加件数。
	 * @throws Exception 例外。
	 */
	public int insert(final Map<String, Object> data) throws Exception {
		return this.executeInsert(new EnumGroupTable(), data);
	}


	/**
	 * データを更新します。
	 * @param list データ。
	 * @return 更新件数。
	 * @throws Exception 例外。
	 */
	public int update(final List<Map<String, Object>> list) throws Exception {
		if (list.size() > 0) {
			// 楽観ロックチェック
			for (Map<String, Object> m: list) {
				Table table = new EnumGroupTable();
				boolean ret = this.isUpdatable(table, m);
				if (!ret) {
					throw new ApplicationException(this.getPage(), "error.notupdatable");
				}
			}
			this.delete(list.get(0));
			this.executeInsert(new EnumGroupTable(), list);
		}
		return list.size();
	}

	/**
	 * データを削除します。
	 * @param data データ。
	 * @return 削除件数。
	 * @throws Exception 例外。
	 */
	public int delete(final Map<String, Object> data) throws Exception {
		EnumGroupTable table = new EnumGroupTable();
		return this.executeDelete(table, new FieldList(table.getEnumGroupCodeField()), data, true); // レコードの物理削除
	}
}
