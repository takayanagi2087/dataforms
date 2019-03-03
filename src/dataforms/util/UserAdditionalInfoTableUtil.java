package dataforms.util;

import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.app.field.user.UserIdField;
import dataforms.controller.Page;
import dataforms.dao.Dao;
import dataforms.dao.Query;
import dataforms.dao.Table;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.common.CreateTimestampField;
import dataforms.field.common.CreateUserIdField;
import dataforms.field.common.UpdateTimestampField;
import dataforms.field.common.UpdateUserIdField;
import dataforms.servlet.DataFormsServlet;

/**
 * ユーザの追加情報ユーティリティ。
 *
 */
public class UserAdditionalInfoTableUtil {

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(UserAdditionalInfoTableUtil.class);

	/**
	 * コンストラクタ。
	 */
	private UserAdditionalInfoTableUtil() {

	}

	/**
	 * web.xmlのuser-additional-info-tableに指定されたテーブルのクラスを取得します。
	 * @return テーブルクラス。
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends Table> getUserAdditionalInfoTable() {
		Class<? extends Table> table = null;
		try {
			DataFormsServlet servlet = Page.getServlet();
			String className = servlet.getServletContext().getInitParameter("user-additional-info-table");
			if (className != null) {
				className = className.trim();
				if (className.length() != 0) {
					table = (Class<? extends Table>) Class.forName(className);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return table;
	}

	/**
	 * 除外フィールドかどうかを確認します。
	 * @param f 除外フィールド。
	 * @return 除外フィールドの場合true。
	 */
	public static boolean isExcludedField(final Field<?> f) {
		boolean ret = false;
		if (f instanceof UserIdField) {
			ret = true;
		}
		if (f instanceof CreateUserIdField) {
			ret = true;
		}
		if (f instanceof CreateTimestampField) {
			ret = true;
		}
		if (f instanceof UpdateUserIdField) {
			ret = true;
		}
		if (f instanceof UpdateTimestampField) {
			ret = true;
		}
		return ret;
	}


	/**
	 * ユーザ追加情報テーブルのフィールドリストを取得します。
	 * @return ユーザ追加情報テーブルのフィールドリスト。
	 */
	public static FieldList getFieldList() {
		FieldList flist = null;
		try {
			Class<? extends Table> clazz = UserAdditionalInfoTableUtil.getUserAdditionalInfoTable();
			if (clazz != null) {
				flist = new FieldList();
				Table table = clazz.newInstance();
				for (Field<?> f: table.getFieldList()) {
					if (UserAdditionalInfoTableUtil.isExcludedField(f)) {
						continue;
					}
					flist.add(f);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return flist;
	}

	/**
	 * ユーザ追加情報テーブルを読み込みます。
	 * @param dao Daoのインスタンス。
	 * @param data  data PKの情報を含んだマップ。
	 * @return 読み込み結果のマップ。
	 * @throws Exception 例外。
	 */
	public static Map<String, Object> query(final Dao dao, final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = null;
		Class<? extends Table> clazz = UserAdditionalInfoTableUtil.getUserAdditionalInfoTable();
		if (clazz != null) {
			Table table = clazz.newInstance();
			if (dao.tableExists(table.getTableName())) {
				Query query = new Query();
				query.setFieldList(table.getFieldList());
				query.setMainTable(table);
				query.setQueryFormFieldList(table.getPkFieldList());
				query.setQueryFormData(data);
				ret = dao.executeRecordQuery(query);
			}
		}
		return ret;
	}


	/**
	 * ユーザ追加情報テーブルを読み込みます。
	 * @param dao Daoのインスタンス。
	 * @param data PKの情報を含んだマップ。読み込んだデータをこのマップに追加する。
	 * @throws Exception 例外。
	 */
	public static void read(final Dao dao, final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = query(dao, data);
		if (ret != null) {
			FieldList flist = getFieldList();
			for (Field<?> f: flist) {
				if (UserAdditionalInfoTableUtil.isExcludedField(f)) {
					continue;
				}
				data.put(f.getId(), ret.get(f.getId()));
			}
		}
	}

	/**
	 * ユーザ追加情報テーブルを読み込みます。
	 * @param dao Daoのインスタンス。
	 * @param data 書き込みデータ。
	 * @throws Exception 例外。
	 */
	public static void write(final Dao dao, final Map<String, Object> data) throws Exception {
		Class<? extends Table> clazz = UserAdditionalInfoTableUtil.getUserAdditionalInfoTable();
		if (clazz != null) {
			Map<String, Object> ret = query(dao, data);
			if (ret != null) {
				Table table = clazz.newInstance();
				dao.executeUpdate(table, data);
			} else {
				Table table = clazz.newInstance();
				dao.executeInsert(table, data);
			}
		}
	}


	/**
	 * ユーザ追加情報テーブルを削除します。
	 * @param dao Daoのインスタンス。
	 * @param data 書き込みデータ。
	 * @throws Exception 例外。
	 */
	public static void delete(final Dao dao, final Map<String, Object> data) throws Exception {
		Class<? extends Table> clazz = UserAdditionalInfoTableUtil.getUserAdditionalInfoTable();
		if (clazz != null) {
			Table atable = clazz.newInstance();
			dao.executeDelete(atable, data);
		}
	}
}
