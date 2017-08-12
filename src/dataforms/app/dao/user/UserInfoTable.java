package dataforms.app.dao.user;

import java.util.Map;
import dataforms.dao.Table;
import dataforms.app.field.user.UserIdField;
import dataforms.app.field.user.LoginIdField;
import dataforms.app.field.user.PasswordField;
import dataforms.app.field.user.UserNameField;
import dataforms.app.field.user.MailAddressField;
import dataforms.app.field.user.ExternalUserFlagField;
import dataforms.app.field.user.EnabledFlagField;
import dataforms.field.common.DeleteFlagField;


/**
 * ユーザ情報テーブルクラス。
 * <pre>
 * 必要最小限のユーザ情報を記録します。
 * 追加項目が必要な場合、別テーブルを作成してください。
 * </pre>
 *
 */
public class UserInfoTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public UserInfoTable() {
		this.setAutoIncrementId(true);
		this.setComment("ユーザ情報テーブル");
		this.addPkField(new UserIdField()); //ユーザを示すID。
		this.addField(new LoginIdField()); //ログインID.
		this.addField(new PasswordField()); //パスワード
		this.addField(new UserNameField()); //氏名
		this.addField(new MailAddressField()); //メールアドレス
		this.addField(new ExternalUserFlagField()); //外部ユーザフラグ
		this.addField(new EnabledFlagField()); //ユーザ有効フラグ
		this.addField(new DeleteFlagField()); //削除フラグ

		this.addUpdateInfoFields();
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		UserInfoTableRelation r = new UserInfoTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** ユーザを示すID。のフィールドID。 */
		public static final String ID_USER_ID = "userId";
		/** ログインID.のフィールドID。 */
		public static final String ID_LOGIN_ID = "loginId";
		/** パスワードのフィールドID。 */
		public static final String ID_PASSWORD = "password";
		/** 氏名のフィールドID。 */
		public static final String ID_USER_NAME = "userName";
		/** メールアドレスのフィールドID。 */
		public static final String ID_MAIL_ADDRESS = "mailAddress";
		/** 外部ユーザフラグのフィールドID。 */
		public static final String ID_EXTERNAL_USER_FLAG = "externalUserFlag";
		/** ユーザ有効フラグのフィールドID。 */
		public static final String ID_ENABLED_FLAG = "enabledFlag";
		/** 削除フラグのフィールドID。 */
		public static final String ID_DELETE_FLAG = "deleteFlag";

		/**
		 * コンストラクタ。
		 */
		public Entity() {
			
		}
		/**
		 * コンストラクタ。
		 * @param map 操作対象マップ。
		 */
		public Entity(final Map<String, Object> map) {
			super(map);
		}
		/**
		 * ユーザを示すID。を取得します。
		 * @return ユーザを示すID。。
		 */
		public java.lang.Long getUserId() {
			return (java.lang.Long) this.getMap().get(Entity.ID_USER_ID);
		}

		/**
		 * ユーザを示すID。を設定します。
		 * @param userId ユーザを示すID。。
		 */
		public void setUserId(final java.lang.Long userId) {
			this.getMap().put(Entity.ID_USER_ID, userId);
		}

		/**
		 * ログインID.を取得します。
		 * @return ログインID.。
		 */
		public java.lang.String getLoginId() {
			return (java.lang.String) this.getMap().get(Entity.ID_LOGIN_ID);
		}

		/**
		 * ログインID.を設定します。
		 * @param loginId ログインID.。
		 */
		public void setLoginId(final java.lang.String loginId) {
			this.getMap().put(Entity.ID_LOGIN_ID, loginId);
		}

		/**
		 * パスワードを取得します。
		 * @return パスワード。
		 */
		public java.lang.String getPassword() {
			return (java.lang.String) this.getMap().get(Entity.ID_PASSWORD);
		}

		/**
		 * パスワードを設定します。
		 * @param password パスワード。
		 */
		public void setPassword(final java.lang.String password) {
			this.getMap().put(Entity.ID_PASSWORD, password);
		}

		/**
		 * 氏名を取得します。
		 * @return 氏名。
		 */
		public java.lang.String getUserName() {
			return (java.lang.String) this.getMap().get(Entity.ID_USER_NAME);
		}

		/**
		 * 氏名を設定します。
		 * @param userName 氏名。
		 */
		public void setUserName(final java.lang.String userName) {
			this.getMap().put(Entity.ID_USER_NAME, userName);
		}

		/**
		 * メールアドレスを取得します。
		 * @return メールアドレス。
		 */
		public java.lang.String getMailAddress() {
			return (java.lang.String) this.getMap().get(Entity.ID_MAIL_ADDRESS);
		}

		/**
		 * メールアドレスを設定します。
		 * @param mailAddress メールアドレス。
		 */
		public void setMailAddress(final java.lang.String mailAddress) {
			this.getMap().put(Entity.ID_MAIL_ADDRESS, mailAddress);
		}

		/**
		 * 外部ユーザフラグを取得します。
		 * @return 外部ユーザフラグ。
		 */
		public java.lang.String getExternalUserFlag() {
			return (java.lang.String) this.getMap().get(Entity.ID_EXTERNAL_USER_FLAG);
		}

		/**
		 * 外部ユーザフラグを設定します。
		 * @param externalUserFlag 外部ユーザフラグ。
		 */
		public void setExternalUserFlag(final java.lang.String externalUserFlag) {
			this.getMap().put(Entity.ID_EXTERNAL_USER_FLAG, externalUserFlag);
		}

		/**
		 * ユーザ有効フラグを取得します。
		 * @return ユーザ有効フラグ。
		 */
		public java.lang.String getEnabledFlag() {
			return (java.lang.String) this.getMap().get(Entity.ID_ENABLED_FLAG);
		}

		/**
		 * ユーザ有効フラグを設定します。
		 * @param enabledFlag ユーザ有効フラグ。
		 */
		public void setEnabledFlag(final java.lang.String enabledFlag) {
			this.getMap().put(Entity.ID_ENABLED_FLAG, enabledFlag);
		}

		/**
		 * 削除フラグを取得します。
		 * @return 削除フラグ。
		 */
		public java.lang.String getDeleteFlag() {
			return (java.lang.String) this.getMap().get(Entity.ID_DELETE_FLAG);
		}

		/**
		 * 削除フラグを設定します。
		 * @param deleteFlag 削除フラグ。
		 */
		public void setDeleteFlag(final java.lang.String deleteFlag) {
			this.getMap().put(Entity.ID_DELETE_FLAG, deleteFlag);
		}


	}
	/**
	 * ユーザを示すID。フィールドを取得します。
	 * @return ユーザを示すID。フィールド。
	 */
	public UserIdField getUserIdField() {
		return (UserIdField) this.getField(Entity.ID_USER_ID);
	}

	/**
	 * ログインID.フィールドを取得します。
	 * @return ログインID.フィールド。
	 */
	public LoginIdField getLoginIdField() {
		return (LoginIdField) this.getField(Entity.ID_LOGIN_ID);
	}

	/**
	 * パスワードフィールドを取得します。
	 * @return パスワードフィールド。
	 */
	public PasswordField getPasswordField() {
		return (PasswordField) this.getField(Entity.ID_PASSWORD);
	}

	/**
	 * 氏名フィールドを取得します。
	 * @return 氏名フィールド。
	 */
	public UserNameField getUserNameField() {
		return (UserNameField) this.getField(Entity.ID_USER_NAME);
	}

	/**
	 * メールアドレスフィールドを取得します。
	 * @return メールアドレスフィールド。
	 */
	public MailAddressField getMailAddressField() {
		return (MailAddressField) this.getField(Entity.ID_MAIL_ADDRESS);
	}

	/**
	 * 外部ユーザフラグフィールドを取得します。
	 * @return 外部ユーザフラグフィールド。
	 */
	public ExternalUserFlagField getExternalUserFlagField() {
		return (ExternalUserFlagField) this.getField(Entity.ID_EXTERNAL_USER_FLAG);
	}

	/**
	 * ユーザ有効フラグフィールドを取得します。
	 * @return ユーザ有効フラグフィールド。
	 */
	public EnabledFlagField getEnabledFlagField() {
		return (EnabledFlagField) this.getField(Entity.ID_ENABLED_FLAG);
	}

	/**
	 * 削除フラグフィールドを取得します。
	 * @return 削除フラグフィールド。
	 */
	public DeleteFlagField getDeleteFlagField() {
		return (DeleteFlagField) this.getField(Entity.ID_DELETE_FLAG);
	}


}
