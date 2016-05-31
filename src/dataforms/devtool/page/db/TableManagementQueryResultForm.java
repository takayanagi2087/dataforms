package dataforms.devtool.page.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.ApplicationException;
import dataforms.controller.JsonResponse;
import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.devtool.dao.db.TableManagerDao;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.base.FieldList;
import dataforms.field.common.MultiSelectField;
import dataforms.field.common.PresenceField;
import dataforms.field.common.RowNoField;
import dataforms.field.sqltype.IntegerField;
import dataforms.field.sqltype.VarcharField;
import dataforms.htmltable.HtmlTable;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.StringUtil;

/**
 * DB管理ページの検索結果フォームクラス。
 *
 *TODO:テーブルのインポート時にcreateUserId,createTimestap,updateUserId,updateTimestampがなくてもインポートできるようにする。
 */
public class TableManagementQueryResultForm extends QueryResultForm {
	/**
	 * Log.
	 */
	private static Logger log = Logger.getLogger(TableManagementQueryResultForm.class.getName());
	/**
	 * コンストラクタ。
	 */
	public TableManagementQueryResultForm() {
		this.addField(new VarcharField("className", 256));
		this.addField(new MultiSelectField<String>("checkedClass", 256));
		HtmlTable htmltbl = new HtmlTable(Page.ID_QUERY_RESULT
			, new ClassNameField("checkedClass")
			, new RowNoField()
			, (new ClassNameField()).setSortable(true)
			, (new VarcharField("tableName", 64)).setSortable(true)
			, new PresenceField("status").setSortable(true)
			, new PresenceField("statusVal")
			, new PresenceField("sequenceGeneration").setSortable(true)
			, new PresenceField("difference").setSortable(true)
			, new PresenceField("differenceVal")
			, new IntegerField("recordCount").setSortable(true)
		);
		this.addHtmlTable(htmltbl);
	}

	@Override
	public void init() throws Exception {
		super.init();
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {
    	this.methodStartLog(log, data);
    	TableManagerDao dao = new TableManagerDao(this);
    	List<Map<String, Object>> queryResult = dao.queryTableClass(data);
    	List<String> clslist = new ArrayList<String>();
    	for (Map<String, Object> r: queryResult) {
    		String statusVal = (String) r.get("statusVal");
    		if ("0".equals(statusVal)) {
        		clslist.add((String) r.get("className"));
    		}
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("checkedClass", clslist);
    	result.put("queryResult", queryResult);
    	this.methodFinishLog(log, result);
		return result;
	}

	/**
	 * テーブル情報を取得します。
	 * @param params パラメータ。
	 * @return SQL。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getTableInfo(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log,  params);
		TableManagerDao dao = new TableManagerDao(this);
		Map<String, Object> p = this.convertToServerData(params);
		String classname = (String) p.get("className");
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, dao.getTableInfo(classname));
		this.methodFinishLog(log, ret);
		return ret;
	}

	/**
	 * テーブルの初期化処理を行います。
	 * @param params パラメータ。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse initTable(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log,  params);
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls : classlist) {
			dao.initTable(cls);
		}
		List<Map<String, Object>> result = dao.getTableInfoList(classlist);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, result);
		this.methodFinishLog(log, ret);
		return ret;
	}

	/**
	 * テーブルの更新処理を行います。
	 * @param params パラメータ。
	 * @return 更新結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse updateTable(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log,  params);
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls: classlist) {
			dao.updateTable(cls);
		}
		List<Map<String, Object>> result = dao.getTableInfoList(classlist);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, result);
		this.methodFinishLog(log, ret);
		return ret;
	}

	/**
	 * テーブルを削除します。
	 * @param params パラメータ。
	 * @return 各テーブル情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse dropTable(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log,  params);
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls : classlist) {
			dao.moveToBackupTable(cls);
		}
		List<Map<String, Object>> result = dao.getTableInfoList(classlist);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, result);
		this.methodFinishLog(log, ret);
		return ret;
	}

	/**
	 * テーブルのデータをエクスポートします。
	 * @param params パラメータ。
	 * @return 出力パス情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse exportTable(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log,  params);
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls : classlist) {
			dao.exportData(cls, DataFormsServlet.getExportImportDir());
		}
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, DataFormsServlet.getExportImportDir());
		this.methodFinishLog(log, ret);
		return ret;
	}
	
	/**
	 * 選択されたテーブルの初期化データを作成します。
	 * @param params パラメータ。
	 * @return 出力パス情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse exportTableAsInitialData(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log,  params);
		Map<String, Object> p = this.convertToServerData(params);

		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		if (StringUtil.isBlank(DeveloperPage.getWebSourcePath())) {
			throw new ApplicationException(this.getPage(), "error.webresourcepathnotfound");
		}
		String initialDataPath = DeveloperPage.getWebSourcePath() + "/WEB-INF/initialdata";
		for (String cls : classlist) {
			dao.exportData(cls, initialDataPath);
		}
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, initialDataPath);
		this.methodFinishLog(log, ret);
		return ret;
	}

	/**
	 * テーブルのデータをインポートします。
	 * @param params パラメータ。
	 * @return 各テーブル情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse importTable(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log,  params);
		Map<String, Object> p = this.convertToServerData(params);
		String datapath = (String) params.get("datapath");
		TableManagerDao dao = new TableManagerDao(this);
		@SuppressWarnings("unchecked")
		List<String> classlist = (List<String>) p.get("checkedClass");
		for (String cls : classlist) {
			dao.importData(cls, datapath);
		}
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, DataFormsServlet.getExportImportDir());
		this.methodFinishLog(log, ret);
		return ret;
	}
}
