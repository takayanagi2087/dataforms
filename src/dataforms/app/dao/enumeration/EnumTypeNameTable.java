package dataforms.app.dao.enumeration;

import java.util.Map;

import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.app.field.enumeration.EnumTypeNameField;
import dataforms.dao.Table;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.LangCodeField;

/**
 * 列挙型名称テーブルクラス。
 * <pre>
 * &lt;select&gt;やラジオボタンの選択肢を管理するテーブルです。
 * </pre>
 *
 */
public class EnumTypeNameTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public EnumTypeNameTable() {
		this.setComment("列挙型名称テーブル");
		this.addPkField(new EnumTypeCodeField());
		this.addPkField(new LangCodeField());
		this.addField(new EnumTypeNameField());
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
	 * 言語コードフィールドを取得します。
	 * @return 言語コードフィールド。
	 */
	public LangCodeField getLangCodeField() {
		return (LangCodeField) this.getField(Entity.ID_LANG_CODE);
	}
	
	/**
	 * 列挙型名称フィールドを取得します。
	 * @return 列挙型名称フィールド。
	 */
	public EnumTypeNameField getEnumTypeNameField() {
		return (EnumTypeNameField) this.getField(Entity.ID_ENUM_TYPE_NAME);
	}
	
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		EnumTypeNameTableRelation r = new EnumTypeNameTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** 列挙型コードのフィールドID。 */
		public static final String ID_ENUM_TYPE_CODE = "enumTypeCode";
		/** 言語コードのフィールドID。 */
		public static final String ID_LANG_CODE = "langCode";
		/** 列挙型名称フィールドID。 */
		public static final String ID_ENUM_TYPE_NAME = "enumTypeName";
		
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
		 * 言語コードを取得します。
		 * @return 言語コード。
		 */
		public String getLangCode() {
			return (String) this.getMap().get(ID_LANG_CODE);
		}
		
		/**
		 * 言語コードを設定します。
		 * @param langCode 列挙型コード。
		 */
		public void setLangCode(final String langCode) {
			this.getMap().put(ID_LANG_CODE, langCode);
		}

		/**
		 * 列挙型名称を取得します。
		 * @return 列挙型名称。
		 */
		public String getEnumTypeName() {
			return (String) this.getMap().get(ID_ENUM_TYPE_NAME);
		}
		
		/**
		 * 列挙型名称を設定します。
		 * @param enumTypeName 列挙型名称。
		 */
		public void setEnumTypeName(final String enumTypeName) {
			this.getMap().put(ID_ENUM_TYPE_NAME, enumTypeName);
		}

	}
}
