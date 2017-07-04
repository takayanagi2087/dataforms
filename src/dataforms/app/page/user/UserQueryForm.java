package dataforms.app.page.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataforms.annotation.WebMethod;
import dataforms.app.field.user.LoginIdField;
import dataforms.app.field.user.UserAttributeTypeField;
import dataforms.app.field.user.UserAttributeValueField;
import dataforms.app.field.user.UserNameField;
import dataforms.controller.JsonResponse;
import dataforms.controller.QueryForm;
import dataforms.devtool.dao.db.TableManagerDao;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.htmltable.EditableHtmlTable;
import dataforms.util.MessagesUtil;

/**
 * ユーザ検索フォームクラス。
 */
public class UserQueryForm extends QueryForm {

    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(UserQueryForm.class.getName());

	/**
	 * コンストラクタ。
	 */
	public UserQueryForm() {
		this.addField(new LoginIdField()).setMatchType(Field.MatchType.BEGIN);
		this.addField(new UserNameField());
		EditableHtmlTable at = new EditableHtmlTable("attTable",
			new FieldList(
				new UserAttributeTypeField(),
				new UserAttributeValueField()
			)
		);
		this.addHtmlTable(at);
	}

	@Override
	public void init() throws Exception {
		super.init();
		// 初期データ設定.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		this.setFormData("attTable", list);
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
			String initialDataPath =  DeveloperPage.getExportInitalDataPath(this.getPage()); // DeveloperPage.getWebSourcePath() + "/WEB-INF/initialdata";
			dao.exportData("dataforms.app.dao.user.UserInfoTable", initialDataPath);
			dao.exportData("dataforms.app.dao.user.UserAttributeTable", initialDataPath);
			ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.initializationdatacreated"));
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.permission"));
		}
		return ret;
	}
}
