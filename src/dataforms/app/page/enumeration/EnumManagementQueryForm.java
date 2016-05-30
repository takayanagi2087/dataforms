package dataforms.app.page.enumeration;

import dataforms.app.dao.enumeration.EnumOptionTable;
import dataforms.app.dao.enumeration.EnumTypeNameTable;
import dataforms.controller.QueryForm;
import dataforms.dao.Table;
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
		Table table = new EnumOptionTable();
		this.addField(table.getField("enumTypeCode")).removeRequiredValidator().setMatchType(MatchType.PART);
		Table nameTable = new EnumTypeNameTable();
		this.addField(nameTable.getField("enumTypeName")).removeRequiredValidator().setMatchType(MatchType.PART);
		this.addField(nameTable.getField("langCode")).addValidator(new RequiredValidator());
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("langCode", "default");
	}
}
