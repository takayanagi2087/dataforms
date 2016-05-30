package dataforms.devtool.page.version;

import dataforms.controller.Form;
import dataforms.controller.Page;
import dataforms.field.sqltype.VarcharField;

/**
 * バージョン情報フォームクラス。
 *
 */
public class VersionInfoForm extends Form {
	/**
	 * コンストラクタ。
	 */
	public VersionInfoForm() {
		super(null);
		this.addField(new VarcharField("version", 1024)).setComment("バージョン").setReadonly(true);
		this.addField(new VarcharField("vendor", 1024)).setComment("作成者").setReadonly(true);
		this.addField(new VarcharField("createDate", 1024)).setComment("作成日").setReadonly(true);
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("version", Page.getDataformsVersion());
		this.setFormData("vendor", Page.getDataformsVendor());
		this.setFormData("createDate", Page.getDataformsCreateDate());
	}
}
