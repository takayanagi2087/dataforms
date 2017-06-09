package dataforms.app.page.enumeration;

import java.util.Map;

import dataforms.annotation.WebMethod;
import dataforms.app.dao.enumeration.EnumOptionTable;
import dataforms.app.dao.enumeration.EnumTypeNameTable;
import dataforms.controller.JsonResponse;
import dataforms.controller.QueryForm;
import dataforms.devtool.dao.db.TableManagerDao;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.base.Field.MatchType;
import dataforms.util.MessagesUtil;
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

	/**
	 * 列挙型関連テーブルのエクスポートを行います。
	 * @param p パラメータ。
	 * @return Json形式のエクスポート。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse export(final Map<String, Object> p) throws Exception {
		JsonResponse ret = null;
		if (this.getPage().checkUserAttribute("userLevel", "developer")) {
			TableManagerDao dao = new TableManagerDao(this);
			String initialDataPath = DeveloperPage.getWebSourcePath() + "/WEB-INF/initialdata";
			dao.exportData("dataforms.app.dao.enumeration.EnumOptionTable", initialDataPath);
			dao.exportData("dataforms.app.dao.enumeration.EnumTypeNameTable", initialDataPath);
			dao.exportData("dataforms.app.dao.enumeration.EnumOptionNameTable", initialDataPath);
			ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.initializationdatacreated"));
		} else {
			ret = new JsonResponse(JsonResponse.APPLICATION_EXCEPTION, MessagesUtil.getMessage(this.getPage(), "error.permission"));
		}
		return ret;
	}
}
