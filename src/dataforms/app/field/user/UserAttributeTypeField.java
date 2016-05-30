package dataforms.app.field.user;

import dataforms.app.dao.enumeration.EnumDao;
import dataforms.app.page.user.UserEditForm;
import dataforms.controller.Form;
import dataforms.field.common.EnumTypeSingleSelectField;
import dataforms.validator.RequiredValidator;

/**
 * ユーザ属性フィールドクラス。
 *
 */
public class UserAttributeTypeField extends EnumTypeSingleSelectField {
	/**
	 * コンストラクタ。
	 */
	public UserAttributeTypeField() {
		super(null, "userAttribute");
		this.setComment("ユーザ属性");
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public UserAttributeTypeField(final String id) {
		super(id, "userAttribute");
		this.setComment("ユーザ属性");
	}

	@Override
	protected void onBind() {
		Form form = this.getParentForm();
		if (form instanceof UserEditForm) {
			this.addValidator(new RequiredValidator());
		}
	}

	@Override
	public void init() throws Exception {
		super.init();
		EnumDao dao = new EnumDao(this);
		String lang = this.getPage().getCurrentLanguage();
		this.setOptionList(dao.getTypeList(this.getEnumGroupCode(), lang), true);
	}

}
