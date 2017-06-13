package dataforms.devtool.page.func;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.app.dao.func.FuncInfoDao;
import dataforms.app.dao.func.FuncInfoTable;
import dataforms.app.field.func.FuncNameField;
import dataforms.controller.EditForm;
import dataforms.controller.JsonResponse;
import dataforms.controller.Page;
import dataforms.devtool.dao.db.TableManagerDao;
import dataforms.devtool.field.common.WebSourcePathField;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.base.FieldList;
import dataforms.htmltable.EditableHtmlTable;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.FileUtil;
import dataforms.util.MessagesUtil;
import dataforms.util.SequentialProperties;

/**
 *
 * 機能編集フォームクラス。
 * <pre>
 * func_infoを編集するためのフォームです。
 * </pre>
 */
public class FuncEditForm extends EditForm {

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(FuncEditForm.class.getName());

	/**
	 * コンストラクタ。
	 */
	public FuncEditForm() {
		this.addField(new WebSourcePathField());
		FuncInfoTable tbl = new FuncInfoTable();
		FieldList flist = new FieldList();
		flist.addAll(tbl.getFieldList());
		flist.add(new FuncNameField());
		EditableHtmlTable funcTable = new EditableHtmlTable("funcTable", flist);
		this.addHtmlTable(funcTable);
	}


	@Override
	public void init() throws Exception {
		super.init();
		this.setFormDataMap(this.queryData(null));
		this.setFormData("webSourcePath", DeveloperPage.getWebSourcePath());
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		FuncInfoDao dao = new FuncInfoDao(this);
		List<Map<String, Object>> list = dao.queryFuncList(true);
		for (Map<String, Object> m: list) {
			FuncInfoTable.Entity e = new FuncInfoTable.Entity(m);
			String funcPath = e.getFuncPath(); // (String) m.get("funcPath");
			String propFile = funcPath + "/Function";
			String key = funcPath.substring(1).replaceAll("/", ".");
			log.debug("key=" + key);
			SequentialProperties prop = MessagesUtil.getProperties(this.getPage(), propFile);
			String name = prop.getProperty(key);
			m.put("funcName", name);
		}
		ret.put("funcTable", list);
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 常に更新モードで動作させる。
	 * </pre>
	 */
	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return true;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		// 何もしない
	}

	/**
	 * Function.propertiesのパスを新規作成します。
	 * @param path 機能のパス。
	 * @return 言語に応じたFunction.propertiesのバス。
	 * @throws Exception 例外。
	 */
	private String getFunctionPropertiesPath(final String path) throws Exception {
		String ret = path + "/Function.properties";
/*		String lang = this.getPage().getRequest().getLocale().getLanguage();
		String langlist = DataFormsServlet.getSupportLanguage();
		log.debug("langlist=" + langlist + "," + lang);
		if (langlist.indexOf(lang) >= 0) {
			ret = ret.replaceAll("\\.properties$", "_" + lang + ".properties");
		}*/
		return ret;
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		Page page = this.getPage();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("funcTable");
		FuncInfoDao dao = new FuncInfoDao(this);
		dao.saveFuncList(list);
		String webResourcePath = (String) data.get("webSourcePath");
		for (Map<String, Object> m: list) {
			FuncInfoTable.Entity e = new FuncInfoTable.Entity(m);
			String path = e.getFuncPath(); //(String) m.get("funcPath");
			String name = (String) m.get("funcName");
			if (path.indexOf("/dataforms") != 0) {
				String funcprop = page.getAppropriatePath(path + "/Function.properties", page.getRequest());
				if (funcprop == null) {
					funcprop = this.getFunctionPropertiesPath(path);
				}
				funcprop = webResourcePath + funcprop;
				String text = "";
				File propfile = new File(funcprop);
				if (propfile.exists()) {
					text = FileUtil.readTextFile(funcprop, DataFormsServlet.getEncoding());
				}
				log.debug("funcprop=" + funcprop);
				SequentialProperties prop = new SequentialProperties();
				prop.loadText(text);
				prop.put(path.substring(1).replaceAll("/", "."), name);
				String str = prop.getSaveText();
				log.debug("str=" + str);
				FileUtil.writeTextFileWithBackup(funcprop, str, DataFormsServlet.getEncoding());
			}
		}
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		// 何もしない
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
			dao.exportData("dataforms.app.dao.func.FuncInfoTable", initialDataPath);
			ret = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.initializationdatacreated"));
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.permission"));
		}
		return ret;
	}

}
