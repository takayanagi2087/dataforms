package dataforms.devtool.page.webres;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataforms.app.dao.enumeration.EnumDao;
import dataforms.controller.QueryForm;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.PageClassNameField;
import dataforms.devtool.field.common.WebComponentTypeListField;
import dataforms.devtool.field.common.WebSourcePathField;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.common.FlagField;
import dataforms.validator.RequiredValidator;

/**
 * 問い合わせフォームクラス。
 */
public class WebResourceQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public WebResourceQueryForm() {
		this.addField(new WebSourcePathField()).setReadonly(true);
		FunctionSelectField funcsel = new FunctionSelectField();
		funcsel.setPackageOption("page");
		this.addField(funcsel);
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new PageClassNameField()).addValidator(new RequiredValidator());
		this.addField(new WebComponentTypeListField());
		this.addField(new FlagField("generatableOnly"));
		this.addField(new ClassNameField());
	}

	@Override
	public void init() throws Exception {
		super.init();
		EnumDao dao = new EnumDao(this);
		List<Map<String, Object>> list = dao.getOptionList("webCompType", this.getPage().getRequest().getLocale().getLanguage());
		List<String> tlist = new ArrayList<String>();
		for (Map<String, Object> m: list) {
			tlist.add((String) m.get("value"));
		}
		this.setFormData("webSourcePath", DeveloperPage.getWebSourcePath());
		this.setFormData("webComponentTypeList", tlist);
		this.setFormData("generatableOnly", "0");
	}
}
