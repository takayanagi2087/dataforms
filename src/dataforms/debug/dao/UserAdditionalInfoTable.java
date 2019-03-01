package dataforms.debug.dao;

import java.util.Map;

import dataforms.app.field.user.UserIdField;
import dataforms.dao.Table;
import dataforms.debug.field.AddressField;
import dataforms.field.common.ZipCodeField;


/**
 * ユーザ追加情報クラス。
 *
 */
public class UserAdditionalInfoTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public UserAdditionalInfoTable() {
		this.setComment("ユーザ追加情報");
		this.addPkField(new UserIdField()); //ユーザを示すID。
		this.addField(new ZipCodeField()); //郵便番号
		this.addField(new AddressField()); //住所

		this.addUpdateInfoFields();
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		UserAdditionalInfoTableRelation r = new UserAdditionalInfoTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** ユーザを示すID。のフィールドID。 */
		public static final String ID_USER_ID = "userId";
		/** 郵便番号のフィールドID。 */
		public static final String ID_ZIP_CODE = "zipCode";
		/** 住所のフィールドID。 */
		public static final String ID_ADDRESS = "address";

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
		 * 郵便番号を取得します。
		 * @return 郵便番号。
		 */
		public java.lang.String getZipCode() {
			return (java.lang.String) this.getMap().get(Entity.ID_ZIP_CODE);
		}

		/**
		 * 郵便番号を設定します。
		 * @param zipCode 郵便番号。
		 */
		public void setZipCode(final java.lang.String zipCode) {
			this.getMap().put(Entity.ID_ZIP_CODE, zipCode);
		}

		/**
		 * 住所を取得します。
		 * @return 住所。
		 */
		public java.lang.String getAddress() {
			return (java.lang.String) this.getMap().get(Entity.ID_ADDRESS);
		}

		/**
		 * 住所を設定します。
		 * @param address 住所。
		 */
		public void setAddress(final java.lang.String address) {
			this.getMap().put(Entity.ID_ADDRESS, address);
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
	 * 郵便番号フィールドを取得します。
	 * @return 郵便番号フィールド。
	 */
	public ZipCodeField getZipCodeField() {
		return (ZipCodeField) this.getField(Entity.ID_ZIP_CODE);
	}

	/**
	 * 住所フィールドを取得します。
	 * @return 住所フィールド。
	 */
	public AddressField getAddressField() {
		return (AddressField) this.getField(Entity.ID_ADDRESS);
	}


}
