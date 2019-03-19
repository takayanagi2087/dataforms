package dataforms.app.page.enumeration;

import java.util.Map;

import dataforms.app.dao.enumeration.EnumOptionTable;
import dataforms.controller.DataForms;

/**
 * 特定の列挙型の選択肢を編集するフォーム。
 *
 */
public class EnumMasterEditForm extends EnumManagementEditForm {

	/**
	 * 列挙型コード。
	 */
	private String enumTypeCode = null;

	/**
	 * 多言語対応。
	 */
	private Boolean multiLanguage = false;

	/**
	 * コンストラクタ。
	 * @param enumTypeCode 列挙型コード。
	 */
	public EnumMasterEditForm(final String enumTypeCode) {
		this(DataForms.ID_EDIT_FORM, enumTypeCode, false);
	}

	/**
	 * コンストラクタ。
	 * @param enumTypeCode 列挙型コード。
	 * @param multiLang 多言語フラグ。
	 */
	public EnumMasterEditForm(final String enumTypeCode,  final boolean multiLang) {
		this(DataForms.ID_EDIT_FORM, enumTypeCode, multiLang);
	}

	/**
	 * コンストラクタ。
	 * @param id フォームID。
	 * @param enumTypeCode 列挙型コード。
	 * @param multiLang 多言語フラグ。
	 */
	public EnumMasterEditForm(final String id, final String enumTypeCode, final boolean multiLang) {
		super(id);
		this.enumTypeCode = enumTypeCode;
		this.multiLanguage = multiLang;
/*		HtmlTable table = (HtmlTable) this.getComponent(ID_OPTION_NAME_LIST);
		table.getFieldList().get(EnumOptionNameTable.Entity.ID_ENUM_OPTION_CODE).setCalcEventField(true);
		table.getFieldList().get(EnumOptionNameTable.Entity.ID_ENUM_OPTION_NAME).setCalcEventField(true);*/

	}


	/**
	 * 列挙型タイプコードを取得します。
	 * @return 列挙型タイプコード。
	 */
	public String getEnumTypeCode() {
		return enumTypeCode;
	}

	/**
	 * 列挙型タイプコードを設定します。
	 * @param enumTypeCode 列挙型タイプコード。
	 */
	public void setEnumTypeCode(final String enumTypeCode) {
		this.enumTypeCode = enumTypeCode;
	}

	@Override
	public void init() throws Exception {
		super.init();
		EnumOptionTable.Entity e = new EnumOptionTable.Entity();
		e.setEnumTypeCode(this.enumTypeCode);
		Map<String, Object> data = this.queryData(e.getMap());
		this.setFormDataMap(data);
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> prop = super.getProperties();
		prop.put("multiLanguage", this.multiLanguage);
		return prop;
	}
}
