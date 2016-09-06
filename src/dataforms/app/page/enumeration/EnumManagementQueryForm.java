package dataforms.app.page.enumeration;

import dataforms.app.dao.enumeration.EnumOptionTable;
import dataforms.app.dao.enumeration.EnumTypeNameTable;
import dataforms.controller.QueryForm;
import dataforms.field.base.Field.MatchType;
import dataforms.validator.RequiredValidator;

/**
 * 列挙型管理問い合わせフォームクラス。
 */
public class EnumManagementQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public EnumManagementQueryForm() {
		EnumOptionTable table = new EnumOptionTable();
		this.addField(table.getEnumTypeCodeField()).removeRequiredValidator().setMatchType(MatchType.PART);
		EnumTypeNameTable nameTable = new EnumTypeNameTable();
		this.addField(nameTable.getEnumTypeNameField()).removeRequiredValidator().setMatchType(MatchType.PART);
		this.addField(nameTable.getLangCodeField()).addValidator(new RequiredValidator());
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData(EnumTypeNameTable.Entity.ID_LANG_CODE, "default");
	}
}
