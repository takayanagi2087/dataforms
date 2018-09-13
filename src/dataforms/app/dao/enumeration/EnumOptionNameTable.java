package dataforms.app.dao.enumeration;

import java.util.Map;

import dataforms.app.field.enumeration.EnumOptionCodeField;
import dataforms.app.field.enumeration.EnumOptionNameField;
import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.dao.Table;
import dataforms.field.common.LangCodeField;

/**
 * 列挙型選択肢名称テーブルクラス。
 * <pre>
 * &lt;select&gt;やラジオボタンの選択肢を管理するテーブルです。
 * 言語ごとの表示名称を記録します。
 * </pre>
 *
 */
public class EnumOptionNameTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public EnumOptionNameTable() {
		this.setComment("列挙型オプション名称テーブル");
		this.addPkField(new EnumTypeCodeField());
		this.addPkField(new EnumOptionCodeField());
		this.addPkField(new LangCodeField());
		this.addField(new EnumOptionNameField());
		this.addUpdateInfoFields();
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		EnumOptionNameTableRelation rel = new EnumOptionNameTableRelation(this);
		return rel.getJoinCondition(joinTable, alias);
	}
	
	
	/**
	 * 列挙型コードフィールドを取得します。
	 * @return 列挙型コードフィールド。
	 */
	public EnumTypeCodeField getEnumTypeCodeField() {
		return (EnumTypeCodeField) this.getField(Entity.ID_ENUM_TYPE_CODE);
	}
	
	/**
	 * 列挙型選択肢コードフィールドを取得します。
	 * @return 列挙型選択肢コードフィールド。
	 */
	public EnumOptionCodeField getEnumOptionCodeField() {
		return (EnumOptionCodeField) this.getField(Entity.ID_ENUM_OPTION_CODE);
	}
	
	/**
	 * 言語コードフィールドを取得します。
	 * @return 言語コードフィールド。
	 */
	public LangCodeField getLangCodeField() {
		return (LangCodeField) this.getField(Entity.ID_LANG_CODE);
	}
	
	/**
	 * 列挙型選択肢名称フィールドを取得します。
	 * @return 列挙型選択肢名称フィールド。
	 */
	public EnumOptionNameField getEnumOptionNameField() {
		return (EnumOptionNameField) this.getField(Entity.ID_ENUM_OPTION_NAME);
	}

	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** 列挙型コードのフィールドID。 */
		public static final String ID_ENUM_TYPE_CODE = "enumTypeCode";
		/** 列挙型選択肢コードのフィールドID。 */
		public static final String ID_ENUM_OPTION_CODE = "enumOptionCode";
		/** 言語コードのフィールドID。 */
		public static final String ID_LANG_CODE = "langCode";
		/** 列挙型選択肢名称フィールドID。 */
		public static final String ID_ENUM_OPTION_NAME = "enumOptionName";
		
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
		 * 列挙型選択肢名称を取得します。
		 * @return 列挙型選択肢名称。
		 */
		public String getEnumOptionName() {
			return (String) this.getMap().get(ID_ENUM_OPTION_NAME);
		}
		
		/**
		 * 列挙型選択肢名称を設定します。
		 * @param langCode 列挙型選択肢名称。
		 */
		public void setEnumOptionName(final String langCode) {
			this.getMap().put(ID_ENUM_OPTION_NAME, langCode);
		}


	}
}
