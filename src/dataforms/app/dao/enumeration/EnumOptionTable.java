package dataforms.app.dao.enumeration;

import java.util.Map;

import dataforms.app.field.enumeration.EnumOptionCodeField;
import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.dao.Table;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.SortOrderField;

/**
 * 列挙型選択肢テーブルクラス。
 * <pre>
 * &lt;select&gt;やラジオボタンの選択肢を管理するテーブルです。
 * </pre>
 *
 */
public class EnumOptionTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public EnumOptionTable() {
		this.setComment("列挙型オプションテーブル");
		this.addPkField(new EnumTypeCodeField());
		this.addPkField(new EnumOptionCodeField());
		this.addField(new SortOrderField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
	}
	
	/**
	 * 列挙型コードフィールドを取得します。
	 * @return 列挙型コードフィールド。
	 */
	public EnumTypeCodeField getEnumTypeCodeField() {
		return (EnumTypeCodeField) this.getField(Entity.ID_ENUM_TYPE_CODE);
	}
	
	/**
	 * 列挙型選択肢フィールドを取得します。
	 * @return 列挙型選択肢フィールド。
	 */
	public EnumOptionCodeField getEnumOptionCodeField() {
		return (EnumOptionCodeField) this.getField(Entity.ID_ENUM_OPTION_CODE);
	}

	/**
	 * ソート順フィールドを取得します。
	 * @return ソート順フィールド。
	 */
	public SortOrderField getSortOrderField() {
		return (SortOrderField) this.getField(Entity.ID_SORT_ORDER);
	}

	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		EnumOptionTableRelation r = new EnumOptionTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
	
	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** 列挙型コードのフィールドID。 */
		public static final String ID_ENUM_TYPE_CODE = "enumTypeCode";
		/** 列挙型選択肢コードのフィールドID。 */
		public static final String ID_ENUM_OPTION_CODE = "enumOptionCode";
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
		 * 列挙型選択肢コードを取得します。
		 * @return 列挙型選択肢コード。
		 */
		public String getEnumOptionCode() {
			return (String) this.getMap().get(ID_ENUM_OPTION_CODE);
		}
		
		/**
		 * 列挙型選択肢コードを設定します。
		 * @param enumOptionCode 列挙型選択肢コード。
		 */
		public void setEnumOptionCode(final String enumOptionCode) {
			this.getMap().put(ID_ENUM_OPTION_CODE, enumOptionCode);
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
