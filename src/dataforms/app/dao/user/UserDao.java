package dataforms.app.dao.user;

import java.util.List;
import java.util.Map;

import dataforms.controller.ApplicationException;
import dataforms.dao.Dao;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.Query;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.field.base.FieldList;
import dataforms.util.CryptUtil;

/**
 *
 * ユーザ関連テーブルアクセスクラス。
 *
 */
public class UserDao extends Dao {

    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(UserDao.class.getName());


	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public UserDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}

	/**
	 * ユーザの問い合わせを行います。
	 * @param flist 問い合わせフォームのフィールドリスト。
	 * @param data 問い合わせフォームのフィール入力データ。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 *
	 */
	public Map<String, Object> queryUser(final FieldList flist, final Map<String, Object> data) throws Exception {
//		AQuery query = new AQuery(new UserQuery(flist, data));
//		query.setQueryFormFieldList(flist);
//		query.setQueryFormData(data);
		Query query = new UserQuery(flist, data);
		String sortOrder = (String) data.get("sortOrder");
		FieldList sflist = query.getFieldList().getOrderByFieldList(sortOrder);
		if (sflist.size() == 0) {
			query.setOrderByFieldList(query.getMainTable().getPkFieldList());
		} else {
			query.setOrderByFieldList(sflist);
		}
		Map<String, Object> ret = executePageQuery(query);
		return ret;
	}


	/**
	 * ユーザの問い合わせを行ないます。
	 * @param flist フィールドリスト。
	 * @param data パラメータ。
	 * @return 検索結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryUserList(final FieldList flist, final Map<String, Object> data) throws Exception {
		UserQuery query = new UserQuery(flist, data);
		return this.executeQuery(query);
	}


	/**
	 * テーブルに対する主キーの設定を行います。
	 *
	 * @param data 更新データ。
	 */
	private void setTablePrimaryKey(final Map<String, Object> data) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attTable");
		if (list != null) {
			for (Map<String, Object> m: list) {
				m.put("userId", data.get("userId"));
			}
		}
	}


	/**
	 * ユーザ情報を新規登録します。
	 * @param data ユーザ情報。
	 * @throws Exception 例外。
	 */
	public void insertUser(final Map<String, Object> data) throws Exception {
		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		UserInfoTable table = new UserInfoTable();
//		Long pk = this.getNewRecordId(table);
//		data.put("userId", pk);
		this.executeInsert(table, data);
		this.setTablePrimaryKey(data);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attTable");
		if (list != null) {
			UserAttributeTable aftable = new UserAttributeTable();
			this.executeInsert(aftable, list);
		}
	}

	/**
	 * ユーザIDの存在チェックを行います。
	 * @param data データ。
	 * @param forUpdate 更新用の存在チェックの場合true。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	public boolean existLoginId(final Map<String, Object> data, final boolean forUpdate) throws Exception {
		UserInfoTable table = new UserInfoTable();
		return this.existRecord(table, new FieldList(table.getField("loginId")), data, forUpdate);
	}

	/**
	 * 指定されたユーザIDのユーザ情報を取得する問い合わせクラスです。
	 */
	private static class GetUserIdQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data フォームから入力されたデータ。
		 */
		public GetUserIdQuery(final Map<String, Object> data) {
			UserInfoTable tbl = new UserInfoTable();
			this.setFieldList(tbl.getFieldList());
			this.setMainTable(tbl);
			this.setQueryFormFieldList(new FieldList(tbl.getField("userId")));
			this.setQueryFormData(data);
		}
	}

	/**
	 * 指定されたユーザIDのユーザ情報を取得する問い合わせクラスです。
	 */
	private static class GetLoginIdQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data フォームから入力されたデータ。
		 */
		public GetLoginIdQuery(final Map<String, Object> data) {
			UserInfoTable tbl = new UserInfoTable();
			this.setFieldList(tbl.getFieldList());
			this.setMainTable(tbl);
			this.setQueryFormFieldList(new FieldList(tbl.getField("loginId"), tbl.getField("password")));
			this.setQueryFormData(data);
		}
	}


	/**
	 *
	 * 指定されたユーザIDのユーザ属性情報を取得する問い合わせクラスです。
	 */
	private static class GetUserAttributeQuery extends Query {
		/**
		 * コンストラクタ。
		 * @param data フォームから入力されたデータ。
		 */
		public GetUserAttributeQuery(final Map<String, Object> data) {
			UserAttributeTable tbl = new UserAttributeTable();
			this.setFieldList(tbl.getFieldList());
			this.setMainTable(tbl);
			this.setQueryFormFieldList(new FieldList(tbl.getField("userId")));
			this.setQueryFormData(data);
		}
	}

	/**
	 * 選択されたユーザ情報を取得します。
	 * @param data フォームから入力されたデータ。
	 * @return ユーザ情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> getSelectedData(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = this.executeRecordQuery(new GetUserIdQuery(data));
		List<Map<String, Object>> attTable = this.executeQuery(new GetUserAttributeQuery(data));
		ret.put("attTable", attTable);
		ret.put("password", CryptUtil.decrypt((String) ret.get("password")));
		ret.put("passwordCheck", ret.get("password"));
		return ret;
	}

	/**
	 * 更新可能かどうかを判定します。
	 * @param data パラメータ。
	 * @return 更新可能な場合true。
	 * @throws Exception 例外。
	 */
	private boolean isUpdatableUser(final Map<String, Object> data) throws Exception {
		UserInfoTable tbl = new UserInfoTable();
		boolean ret = this.isUpdatable(tbl, data);
		if (ret) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attTable");
			if (list != null) {
				UserAttributeTable ftbl = new UserAttributeTable();
				for (Map<String, Object> m: list) {
					if (m.get("userId") != null) {
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
	 * ユーザ情報の更新を行ないます。
	 * @param data フォームデータ。
	 * @throws Exception 例外。
	 */
	public void updateUser(final Map<String, Object> data) throws Exception {
		if (!this.isUpdatableUser(data)) {
			throw new ApplicationException(this.getPage(), "error.notupdatable");
		}
		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		SqlGenerator gen = this.getSqlGenerator();
		UserInfoTable tbl = new UserInfoTable();
		String sql = gen.generateUpdateSql(tbl);
		this.executeUpdate(sql, data);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attTable");
		if (list != null) {
			UserAttributeTable atbl = new UserAttributeTable();
			String delsql = gen.generateDeleteSql(atbl, new FieldList(atbl.getField("userId")));
			this.executeUpdate(delsql, data);
			for (Map<String, Object> rec: list) {
				rec.put("userId", data.get("userId"));
			}
			this.executeInsert(atbl, list);
		}
	}

	/**
	 * パスワードを更新します。
	 * @param data 更新データ。
	 * @throws Exception 例外。
	 */
	public void updatePassword(final Map<String, Object> data) throws Exception {
		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		UserInfoTable tbl = new UserInfoTable();
		this.executeUpdate(tbl,
				new FieldList(
					tbl.getField("password")
					, tbl.getField("updateUserId")
					, tbl.getField("updateTimestamp")
				),
				new FieldList(tbl.getField("userId")), data, true);
	}


	/**
	 * ユーザ自身が更新できる項目を更新します。
	 * @param data 更新データ。
	 * @throws Exception 例外。
	 */
	public void updateSelfUser(final Map<String, Object> data) throws Exception {
		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		UserInfoTable tbl = new UserInfoTable();
		this.executeUpdate(tbl,
				new FieldList(
					tbl.getField("loginId")
					, tbl.getField("userName")
					, tbl.getField("mailAddress")
					, tbl.getField("updateUserId")
					, tbl.getField("updateTimestamp")
				),
				new FieldList(tbl.getField("userId")), data, true);
	}

	/**
	 * ユーザの削除処理を行います。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	public void deleteUser(final Map<String, Object> data) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		UserInfoTable tbl = new UserInfoTable();
		String sql = gen.generateDeleteSql(tbl, new FieldList(tbl.getField("userId")));
		this.executeUpdate(sql, data);
		UserAttributeTable atbl = new UserAttributeTable();
		String asql = gen.generateDeleteSql(atbl, new FieldList(atbl.getField("userId")));
		this.executeUpdate(asql, data);
	}

	/**
	 * ログインチェックを行ないます。
	 * @param data データ。
	 * @return ユーザ情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> login(final Map<String, Object> data) throws Exception {
		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		GetLoginIdQuery query = new GetLoginIdQuery(data);
		Map<String, Object> rec = this.executeRecordQuery(query);
		if (rec != null) {
			data.put("userId", rec.get("userId"));
			List<Map<String, Object>> attTable = this.executeQuery(new GetUserAttributeQuery(data));
			rec.put("attTable", attTable);
		} else {
			throw new ApplicationException(this.getPage(), "error.invaliduserid");
		}
		return rec;
	}
}
