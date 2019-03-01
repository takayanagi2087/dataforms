package dataforms.app.dao.user;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.controller.ApplicationException;
import dataforms.dao.Dao;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.Query;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.field.base.FieldList;
import dataforms.util.CryptUtil;
import dataforms.util.UserAdditionalInfoTableUtil;

/**
 *
 * ユーザ関連テーブルアクセスクラス。
 *
 */
public class UserDao extends Dao {

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(UserDao.class.getName());


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
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		if (list != null) {
			for (Map<String, Object> m: list) {
				UserAttributeTable.Entity a = new UserAttributeTable.Entity(m);
//				m.put("userId", data.get("userId"));
				a.setUserId(e.getUserId());
			}
		}
	}


	/**
	 * ユーザ情報を新規登録します。
	 * @param data ユーザ情報。
	 * @throws Exception 例外。
	 */
	public void insertUser(final Map<String, Object> data) throws Exception {
//		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		e.setPassword(CryptUtil.encrypt(e.getPassword()));

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
		UserAdditionalInfoTableUtil.write(this, data);
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
		return this.existRecord(table, new FieldList(table.getLoginIdField()), data, forUpdate);
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
			this.setQueryFormFieldList(new FieldList(tbl.getUserIdField()));
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
			this.setQueryFormFieldList(new FieldList(tbl.getLoginIdField(), tbl.getPasswordField()));
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
			this.setQueryFormFieldList(new FieldList(tbl.getUserIdField()));
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
		UserInfoTable.Entity e = new UserInfoTable.Entity(ret);
		List<Map<String, Object>> attTable = this.executeQuery(new GetUserAttributeQuery(data));
		ret.put("attTable", attTable);
//		ret.put("password", CryptUtil.decrypt((String) ret.get("password")));
		e.setPassword(CryptUtil.decrypt(e.getPassword()));
		ret.put("passwordCheck", e.getPassword());
		UserAdditionalInfoTableUtil.read(this, ret);
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
					UserAttributeTable.Entity r = new UserAttributeTable.Entity(m);
					if (r.getUserId() != null) {
						if (!this.isUpdatable(ftbl, m)) {
							ret = false;
							break;
						}
					}
				}
			}
		}
		log.debug("isUpdatableUser=" + ret);
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
//		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		e.setPassword(CryptUtil.encrypt(e.getPassword()));
		SqlGenerator gen = this.getSqlGenerator();
		UserInfoTable tbl = new UserInfoTable();
		String sql = gen.generateUpdateSql(tbl);
		this.executeUpdate(sql, data);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attTable");
		if (list != null) {
			UserAttributeTable atbl = new UserAttributeTable();
			String delsql = gen.generateDeleteSql(atbl, new FieldList(atbl.getUserIdField()));
			this.executeUpdate(delsql, data);
			for (Map<String, Object> rec: list) {
				UserAttributeTable.Entity r = new UserAttributeTable.Entity(rec);
//				rec.put("userId", data.get("userId"));
				r.setUserId(e.getUserId());
			}
			this.executeInsert(atbl, list);
		}
		UserAdditionalInfoTableUtil.write(this, data);
	}

	/**
	 * パスワードを更新します。
	 * @param data 更新データ。
	 * @throws Exception 例外。
	 */
	public void updatePassword(final Map<String, Object> data) throws Exception {
//		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		e.setPassword(CryptUtil.encrypt(e.getPassword()));
		UserInfoTable tbl = new UserInfoTable();
		this.executeUpdate(tbl,
			new FieldList(
				/*
				tbl.getField(UserInfoTable.Entity.ID_PASSWORD)
				, tbl.getField(UserInfoTable.Entity.ID_UPDATE_USER_ID)
				, tbl.getField(UserInfoTable.Entity.ID_UPDATE_TIMESTAMP)*/
				tbl.getPasswordField()
				, tbl.getUpdateUserIdField()
				, tbl.getUpdateTimestampField()
			),
			new FieldList(tbl.getField(UserInfoTable.Entity.ID_USER_ID)), data, true);
	}


	/**
	 * ユーザを有効にします。
	 * @param data POSTされたデータ。
	 * @throws Exception 例外。
	 */
	public void enableUser(final Map<String, Object> data) throws Exception {
		if (!this.isUpdatableUser(data)) {
			throw new ApplicationException(this.getPage(), "error.notupdatable");
		}
		UserInfoTable.Entity p = new UserInfoTable.Entity(data);
		UserInfoTable.Entity e = new UserInfoTable.Entity();
		e.setUserId(p.getUserId());
		e.setUpdateUserId(p.getUserId());
		e.setEnabledFlag("1");
		UserInfoTable tbl = new UserInfoTable();
		this.executeUpdate(tbl,
			new FieldList(
				tbl.getEnabledFlagField()
				, tbl.getUpdateUserIdField()
				, tbl.getUpdateTimestampField()
			),
			new FieldList(tbl.getUserIdField()), e.getMap(), true);
	}



	/**
	 * ユーザ自身が更新できる項目を更新します。
	 * @param data 更新データ。
	 * @throws Exception 例外。
	 */
	public void updateSelfUser(final Map<String, Object> data) throws Exception {
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
//		data.put("password", CryptUtil.encrypt((String) data.get("password")));
		e.setPassword(CryptUtil.encrypt(e.getPassword()));
		UserInfoTable tbl = new UserInfoTable();
		this.executeUpdate(tbl,
			new FieldList(
/*				tbl.getField(UserInfoTable.Entity.ID_LOGIN_ID)
				, tbl.getField(UserInfoTable.Entity.ID_USER_NAME)
				, tbl.getField(UserInfoTable.Entity.ID_MAIL_ADDRESS)
				, tbl.getField(UserInfoTable.Entity.ID_UPDATE_USER_ID)
				, tbl.getField(UserInfoTable.Entity.ID_UPDATE_TIMESTAMP)*/
				tbl.getLoginIdField()
				, tbl.getUserNameField()
				, tbl.getMailAddressField()
				, tbl.getUpdateUserIdField()
				, tbl.getUpdateTimestampField()
			),
			new FieldList(tbl.getField(UserInfoTable.Entity.ID_USER_ID)), data, true);
		UserAdditionalInfoTableUtil.write(this, data);
	}

	/**
	 * ユーザの削除処理を行います。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	public void deleteUser(final Map<String, Object> data) throws Exception {
		UserAdditionalInfoTableUtil.delete(this, data);
		SqlGenerator gen = this.getSqlGenerator();
		UserInfoTable tbl = new UserInfoTable();
		String sql = gen.generateDeleteSql(tbl, new FieldList(tbl.getField(UserInfoTable.Entity.ID_USER_ID)));
		this.executeUpdate(sql, data);
		UserAttributeTable atbl = new UserAttributeTable();
		String asql = gen.generateDeleteSql(atbl, new FieldList(atbl.getField(UserInfoTable.Entity.ID_USER_ID)));
		this.executeUpdate(asql, data);
	}

	/**
	 * ログインチェックを行ないます。
	 * @param data データ。
	 * @return ユーザ情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> login(final Map<String, Object> data) throws Exception {
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		e.setPassword(CryptUtil.encrypt(e.getPassword()));
		GetLoginIdQuery query = new GetLoginIdQuery(data);
		query.setCondition("m.enabled_flag='1'");
		Map<String, Object> rec = this.executeRecordQuery(query);
		if (rec != null) {
			data.put(UserInfoTable.Entity.ID_USER_ID, rec.get(UserInfoTable.Entity.ID_USER_ID));
			List<Map<String, Object>> attTable = this.executeQuery(new GetUserAttributeQuery(data));
			rec.put("attTable", attTable);
		} else {
			throw new ApplicationException(this.getPage(), "error.invaliduserid");
		}
		return rec;
	}

	/**
	 * userIdを指定して、そのユーザ情報を取得します。
	 * @param userId ユーザID。
	 * @return ユーザ情報マップ。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryUserInfo(final Long userId) throws Exception {
		//Map<String, Object> p = new HashMap<String, Object>();
		UserInfoTable.Entity p = new UserInfoTable.Entity();
		p.setUserId(userId);
		return this.getSelectedData(p.getMap());
	}

	/**
	 * loginIdを指定して、そのユーザ情報を取得します。
	 * @param loginId ユーザID。
	 * @return ユーザ情報マップ。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> queryUserInfo(final String loginId) throws Exception {
		UserInfoTable table = new UserInfoTable();
		FieldList flist = new FieldList(table.getLoginIdField());
		UserInfoTable.Entity e = new UserInfoTable.Entity();
		e.setMailAddress(loginId);
		List<Map<String, Object>> list =  this.queryUserList(flist, e.getMap());
		if (list.size() > 0) {
			return this.getSelectedData(list.get(0));
		} else {
			return null;
		}
	}


	/**
	 * メールアドレスを指定してユーザを検索します。
	 * @param mail メールアドレス。
	 * @return ユーザリスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryUserListByMail(final String mail) throws Exception {
		UserInfoTable table = new UserInfoTable();
		FieldList flist = new FieldList(table.getMailAddressField());
		UserInfoTable.Entity e = new UserInfoTable.Entity();
		e.setMailAddress(mail);
		return this.queryUserList(flist, e.getMap());
	}

}
