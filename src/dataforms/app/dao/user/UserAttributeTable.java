package dataforms.app.dao.user;

import java.util.Map;

import dataforms.app.field.user.UserAttributeTypeField;
import dataforms.app.field.user.UserAttributeValueField;
import dataforms.app.field.user.UserIdField;
import dataforms.dao.Table;

/**
 * ユーザ属性テーブルクラス。
 */
public class UserAttributeTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public UserAttributeTable() {
		this.setComment("ユーザ属性テーブル");
		this.addPkField(new UserIdField());
		this.addPkField(new UserAttributeTypeField());
		this.addPkField(new UserAttributeValueField());
		this.addUpdateInfoFields();
	}


	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		UserAttributeTableRelation r = new UserAttributeTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** ユーザIDのフィールドID。 */
		public static final String ID_USER_ID = "userId";
		/** ユーザ属性タイプフィールドID。 */
		public static final String ID_USER_ATTRIBUTE_TYPE = "userAttributeType";
		/** ユーザ属性値フィールID。 */
		public static final String ID_USER_ATTRIBUTE_VALUE = "userAttributeValue";
		/**
		 * コンストラクタ。
		 * @param map 操作対象マップ。
		 */
		public Entity(final Map<String, Object> map) {
			super(map);
		}
		
		/**
		 * ユーザIDを取得します。
		 * @return ユーザID。
		 */
		public Long getUserId() {
			return (Long) this.getMap().get(ID_USER_ID);
		}
		
		/**
		 * ユーザIDを設定します。
		 * @param userId ユーザID。
		 */
		public void setUserId(final Long userId) {
			this.getMap().put(ID_USER_ID, userId);
		}
		
		/**
		 * ユーザ属性タイプを取得します。
		 * @return ユーザ属性タイプ。
		 */
		public String getUserAttributeType() {
			return (String) this.getMap().get(ID_USER_ATTRIBUTE_TYPE);
		}
		
		/**
		 * ユーザ属性タイプを設定します。
		 * @param userAttributeType ユーザ属性タイプ。
		 */
		public void setUserAttributeType(final String userAttributeType) {
			this.getMap().put(ID_USER_ATTRIBUTE_TYPE, userAttributeType);
		}

		
		/**
		 * ユーザ属性値を取得します。
		 * @return ユーザ属性値。
		 */
		public String getUserAttributeValue() {
			return (String) this.getMap().get(ID_USER_ATTRIBUTE_VALUE);
		}
		
		/**
		 * ユーザ属性値を設定します。
		 * @param userAttributeValue ユーザ属性値。
		 */
		public void setUserAttributeValue(final String userAttributeValue) {
			this.getMap().put(ID_USER_ATTRIBUTE_TYPE, userAttributeValue);
		}
	}
	
	/**
	 * ユーザIDフィールドを作成します。
	 * @return  ユーザIDフィールド。
	 */
	public UserIdField getUserIdField() {
		return (UserIdField) this.getField(Entity.ID_USER_ID);
	}
	
	/**
	 * ユーザ属性タイプフィールドを取得します。
	 * @return ユーザ属性タイプフィールド。
	 */
	public UserAttributeTypeField getUserAttributeTypeField() {
		return (UserAttributeTypeField) this.getField(Entity.ID_USER_ATTRIBUTE_TYPE);
	}
	
	/**
	 * ユーザ属性値フィールドを取得します。
	 * @return ユーザ属性値フィールド。
	 */
	public UserAttributeTypeField getUserAttributeTypeValue() {
		return (UserAttributeTypeField) this.getField(Entity.ID_USER_ATTRIBUTE_VALUE);
	}

}
