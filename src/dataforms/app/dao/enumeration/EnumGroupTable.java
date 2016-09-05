package dataforms.app.dao.enumeration;

import java.util.Map;

import dataforms.app.field.enumeration.EnumGroupCodeField;
import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.dao.Table;
import dataforms.dao.TableRelation;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.SortOrderField;

/**
 * 列挙型グループテーブルクラス。
 * <pre>
 * 列挙型のグルーブを作るためのテーブルです。
 * </pre>
 *
 */
public class EnumGroupTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public EnumGroupTable() {
		this.setComment("列挙型グループテーブル");
		this.addPkField(new EnumGroupCodeField());
		this.addPkField(new EnumTypeCodeField());
		this.addField(new SortOrderField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
	}

	/**
	 * 列挙型グループコードフィールドを取得します。
	 * @return 列挙型グループコードフィールド。
	 */
	public EnumGroupCodeField getEnumGroupCodeField() {
		return (EnumGroupCodeField) this.getField(Entity.ID_ENUM_GROUP_CODE);
	}
	
	/**
	 * 列挙型コードを取得します。
	 * @return 列挙型コード。
	 */
	public EnumTypeCodeField getEnumTypeCodeField() {
		return (EnumTypeCodeField) this.getField(Entity.ID_ENUM_TYPE_CODE);
	}
	
	/**
	 * ソート順を取得します。
	 * @return ソート順。
	 */
	public SortOrderField getSortOrderField() {
		return (SortOrderField) this.getField(Entity.ID_SORT_ORDER);
	}
	
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		TableRelation r = new EnumGroupTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** 列挙型グループコードのフィールドID。 */
		public static final String ID_ENUM_GROUP_CODE = "enumGroupCode";
		/** 列挙型コードのフィールドID。 */
		public static final String ID_ENUM_TYPE_CODE = "enumTypeCode";
		/** ソート順のフィールドID。 */
		public static final String ID_SORT_ORDER = "sortOrder";
		/**
		 * コンストラクタ。
		 * @param map 操作対象マップ。
		 */
		public Entity(final Map<String, Object> map) {
			super(map);
		}
		
		/**
		 * 列挙型グループコードを取得します。
		 * @return 列挙型グループコード。
		 */
		public String getEnumGroupCode() {
			return (String) this.getMap().get(ID_ENUM_GROUP_CODE);
		}
		
		/**
		 * 列挙型グループコードを設定します。
		 * @param enumGroupCode 列挙型グループコード。
		 */
		public void setEnumGroupCode(final String enumGroupCode) {
			this.getMap().put(ID_ENUM_GROUP_CODE, enumGroupCode);
		}
		
		
		/**
		 * 列挙型コードを取得します。
		 * @return 列挙型コード。
		 */
		public String getEnumTypeCode() {
			return (String) this.getMap().get(ID_ENUM_TYPE_CODE);
		}
		
		/**
		 * 列挙型コードを設定します。
		 * @param enumTypeCode 列挙型コード。
		 */
		public void setEnumTypeCode(final String enumTypeCode) {
			this.getMap().put(ID_ENUM_TYPE_CODE, enumTypeCode);
		}
		
		/**
		 * ソート順を取得します。
		 * @return ソート順。
		 */
		public Short getSortOrder() {
			return (Short) this.getMap().get(ID_SORT_ORDER);
		}

		/**
		 * ソート順を設定します。
		 * @param sortOrder ソート順。
		 */
		public void setSortOrder(final Short sortOrder) {
			this.getMap().put(ID_SORT_ORDER, sortOrder);
		}
		

	}
}
