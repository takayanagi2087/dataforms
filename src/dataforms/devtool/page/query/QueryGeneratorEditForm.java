package dataforms.devtool.page.query;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.EditForm;
import dataforms.controller.JsonResponse;
import dataforms.dao.Query;
import dataforms.dao.Table;
import dataforms.dao.TableList;
import dataforms.devtool.field.common.AliasNameField;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.JavaSourcePathField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.QueryClassNameField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.common.FlagField;
import dataforms.htmltable.EditableHtmlTable;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.FileUtil;
import dataforms.util.MessagesUtil;
import dataforms.util.StringUtil;
import dataforms.validator.RequiredValidator;
import dataforms.validator.ValidationError;

/**
 * 問合せJavaクラス作成編集フォーム。
 *
 */
public class QueryGeneratorEditForm extends EditForm {

	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(QueryGeneratorEditForm.class);
	

	/**
	 * コンストラクタ。
	 */
	public QueryGeneratorEditForm() {
		this.addField(new JavaSourcePathField());
		((FunctionSelectField) this.addField(new FunctionSelectField())).setPackageOption("dao");
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new QueryClassNameField()).setAutocomplete(false).addValidator(new RequiredValidator());
		this.addField(new AliasNameField()).setCalcEventField(true);

		this.addField(new FlagField("distinctFlag"));
		this.addField(new FlagField("forceOverwrite"));
		
		this.addField((new FunctionSelectField("mainTableFunctionSelect")).setPackageFieldId("mainTablePackageName")).setComment("主テーブルの機能");
		this.addField(new PackageNameField("mainTablePackageName")).setComment("主テーブルのパッケージ").addValidator(new RequiredValidator());
		this.addField((new TableClassNameField("mainTableClassName")).setPackageNameFieldId("mainTablePackageName"))
			.setAutocomplete(true).setComment("主テーブルクラス名").addValidator(new RequiredValidator()).setCalcEventField(true);
		
		EditableHtmlTable joinTableList = new JoinHtmlTable("joinTableList");
		joinTableList.setCaption("JOINするテーブルリスト");
		this.addHtmlTable(joinTableList);
		EditableHtmlTable leftJoinTableList = new JoinHtmlTable("leftJoinTableList");
		leftJoinTableList.setCaption("LEFT JOINするテーブルリスト");
		this.addHtmlTable(leftJoinTableList);
		EditableHtmlTable rightJoinTableList = new JoinHtmlTable("rightJoinTableList");
		rightJoinTableList.setCaption("RIGHT JOINするテーブルリスト");
		this.addHtmlTable(rightJoinTableList);
		SelectFieldHtmlTable slectFieldList = new SelectFieldHtmlTable("selectFieldList");
		slectFieldList.setCaption("選択フィールドリスト");
		this.addHtmlTable(slectFieldList);
		
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("javaSourcePath", DeveloperPage.getJavaSourcePath());
	}
	
	/**
	 * JOINテーブルリストのデータを取得します。
	 * @param list JOINテーブルリスト。
	 * @return JOINテーブルリストのデータ。
	 */
	private List<Map<String, Object>> getJoinTableData(final TableList list) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		if (list != null) {
			for (Table t: list) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("packageName", t.getClass().getPackage().getName());
				m.put("tableClassName", t.getClass().getSimpleName());
				m.put("aliasName", t.getAlias());
				ret.add(m);
			}
		}
		return ret;
	}
	
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		String packageName = (String) data.get("packageName");
		String queryClassName = (String) data.get("queryClassName");
		log.debug("packageName=" + packageName + ",queryClassName=" + queryClassName);
		@SuppressWarnings("unchecked")
		Class<? extends Query> clazz = (Class<? extends Query>) Class.forName(packageName + "." + queryClassName);
		Query q = clazz.newInstance();
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("javaSourcePath", DeveloperPage.getJavaSourcePath());
		ret.put("packageName", packageName);
		ret.put("queryClassName", queryClassName);
		if (q.isDistinct()) {
			ret.put("distinctFlag", "1");
		} else {
			ret.put("distinctFlag", "0");
		}
		ret.put("forceOverwrite", "0");
		ret.put("mainTablePackageName", q.getMainTable().getClass().getPackage().getName());
		ret.put("mainTableClassName", q.getMainTable().getClass().getSimpleName());
		ret.put("aliasName", q.getMainTable().getAlias());
		ret.put("joinTableList", this.getJoinTableData(q.getJoinTableList()));
		ret.put("leftJoinTableList", this.getJoinTableData(q.getLeftJoinTableList()));
		ret.put("rightJoinTableList", this.getJoinTableData(q.getRightJoinTableList()));
		ret.put("rightJoinTableList", this.getJoinTableData(q.getRightJoinTableList()));
		List<Map<String, Object>> flist = this.queryTableFieldList(ret);
		FieldList qfl = q.getFieldList();
		for (Map<String, Object> m: flist) {
			String fid = (String) m.get("fieldId");
			String tclass = (String) m.get("selectTableClass");
			Field<?> qfield = qfl.get(fid);
			if (qfield != null) {
				log.debug(qfield.getId() + ":" + fid + "," + qfield.getTable().getClass().getName() + ":" + tclass);
				if (qfield.getTable().getClass().getName().equals(tclass)) {
					m.put("sel", "1");
				} else {
					m.put("sel", "0");
				}
			} else {
				m.put("sel", "0");
			}
		}
		ret.put("selectFieldList", flist);
		return ret;
	}

	
	/**
	 * クラスの存在チェック。
	 * @param name クラス名。
	 * @return 存在する場合true。
	 */
	private boolean classExists(final String name) {
		try {
			Class.forName(name);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * 各種JOINテーブルのバリデーションを行います。
	 * @param id テーブルID。
	 * @param list テーブルの入力内容リスト。
	 * @return バリデーション結果。
	 */
	private List<ValidationError> validateJoinTable(final String id, final List<Map<String, Object>> list) {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> ent = list.get(i);
			String packageName = (String) ent.get("packageName");
			String className = (String) ent.get("tableClassName");
			String tClass = packageName + "." + className;
			if (!this.classExists(tClass)) {
				ret.add(new ValidationError(id + "[" + i + "].tableClassName", MessagesUtil.getMessage(this.getPage(), "error.tableclassnotfound", "{0}", tClass)));
			}
		}
		return ret;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<ValidationError> validateForm(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		String mtClass = (String) data.get("mainTablePackageName") + "." + (String) data.get("mainTableClassName");
		if (!this.classExists(mtClass)) {
			ret.add(new ValidationError("mainTableClassName", MessagesUtil.getMessage(this.getPage(), "error.tableclassnotfound", "{0}", mtClass)));
		}
		ret.addAll(this.validateJoinTable("joinTableList", (List<Map<String, Object>>) data.get("joinTableList")));
		ret.addAll(this.validateJoinTable("leftJoinTableList", (List<Map<String, Object>>) data.get("leftJoinTableList")));
		ret.addAll(this.validateJoinTable("rightJoinTableList", (List<Map<String, Object>>) data.get("rightJoinTableList")));
		
		String packageName = (String) data.get("packageName");
		String queryClassName = (String) data.get("queryClassName");
		String javaSrc = (String) data.get("javaSourcePath");
		String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
		String query = srcPath + "/" + queryClassName + ".java";
		String forceOverwrite = (String) data.get("forceOverwrite");
		if (!"1".equals(forceOverwrite)) {
			File f = new File(query);
			if (f.exists()) {
				ret.add(new ValidationError("queryClassName", this.getPage().getMessage("error.sourcefileexist", queryClassName + ".java")));
			}
		}
		return ret;
	}
	
	/**
	 * 1テーブル中のフィールドリストを取得します。
	 * @param tableClass テーブルクラス名。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryTableFieldList(final String tableClass) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		Class<? extends Table> clazz = (Class<? extends Table>) Class.forName(tableClass);
		Table table = clazz.newInstance();
		FieldList flist = table.getFieldList();
		for (Field<?> f: flist) {
			Map<String, Object> ent = new HashMap<String, Object>();
			ent.put("selectTableClass", table.getClass().getName());
			ent.put("tableClassName", table.getClass().getName());
			ent.put("selectTableClassName", table.getClass().getSimpleName());
			ent.put("sel", "0");
			ent.put("fieldId", f.getId());
			ent.put("fieldClassName", f.getClass().getName());
			ent.put("comment", f.getComment());
			ent.put("tableClassName", table.getClass().getSimpleName());
			ret.add(ent);
		}
		return ret;
		
	}

	/**
	 * JOINテーブルのフィールドリストを取得します。
	 * @param list JOINテーブルリスト。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryJoinTableFieldList(final List<Map<String, Object>> list) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> ent = list.get(i);
			String packageName = (String) ent.get("packageName");
			String className = (String) ent.get("tableClassName");
			String tClass = packageName + "." + className;
			ret.addAll(this.queryTableFieldList(tClass));
		}
		return ret;
	}


	/**
	 * 問合せ対象のフィールドリストを取得します。
	 * @param data フォームデータ。
	 * @return 問合せ対象のフィールドリスト
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> queryTableFieldList(final Map<String, Object> data) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		String mtClass = (String) data.get("mainTablePackageName") + "." + (String) data.get("mainTableClassName");
		log.debug("mainTable=" + mtClass);
		ret.addAll(this.queryTableFieldList(mtClass));
		ret.addAll(this.queryJoinTableFieldList((List<Map<String, Object>>) data.get("joinTableList")));
		ret.addAll(this.queryJoinTableFieldList((List<Map<String, Object>>) data.get("leftJoinTableList")));
		ret.addAll(this.queryJoinTableFieldList((List<Map<String, Object>>) data.get("rightJoinTableList")));

		return ret;
		
	}
	
	/**
	 * テーブルのフィールドリストを取得します。
	 * @param param パラメータ。
	 * @return フィールドリスト。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getFieldList(final Map<String, Object> param) throws Exception {
		this.methodStartLog(log, param);
		JsonResponse ret = null;
		param.put("forceOverwrite", "1");
		Map<String, Object> p = new HashMap<String, Object>();
		p.putAll(param);
		p.put("forceOverwrite", "1");
		List<ValidationError> vlist = this.validate(p);
		if (vlist.size() == 0) {
			Map<String, Object> data = this.convertToServerData(param);
			List<Map<String, Object>> list = this.queryTableFieldList(data);
			ret = new JsonResponse(JsonResponse.SUCCESS, list);
		} else {
			ret = new JsonResponse(JsonResponse.INVALID, vlist);
		}
		this.methodFinishLog(log, ret);
		return ret;
	}
	
	/**
	 * 各JOINテーブルの結合条件を取得します。
	 * @param mainTable 主テーブルのインスタンス。
	 * @param list JOINテーブルリスト。
	 * @param defaultAlias デフォルト別名。
	 * @return 結合条件。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> queryJoinCondition(final Table mainTable, final List<Map<String, Object>> list, final String defaultAlias) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> m =list.get(i);
			String packageName = (String) m.get("packageName");
			String className = (String) m.get("tableClassName");
			String alias = (String) m.get("aliasName");
			if (StringUtil.isBlank(alias)) {
				alias = defaultAlias + i;
			}
			String tcn = packageName + "." + className;
			Class<? extends Table> tc = (Class<? extends Table>) Class.forName(tcn);
			Table jt = tc.newInstance();
			String joinCondition = mainTable.getJoinCondition(jt, alias);
			log.debug("joinConditon=" + joinCondition);
			Map<String, Object> rec = new HashMap<String, Object>();
			if (StringUtil.isBlank(joinCondition)) {
				joinCondition = MessagesUtil.getMessage(this.getPage(), "message.joinconditionnotfound");
			}
			rec.put("joinCondition", joinCondition);
			ret.add(rec);
		}
		return ret;
	}
	
	/**
	 * 各テーブルのJOIN条件式を取得します。
	 * @param data POSTされたデータ。
	 * @return JOIN条件式が設定されたリスト。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> queryJoinCondition(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		String packageName = (String) data.get("mainTablePackageName");
		String mainTableClassName = (String) data.get("mainTableClassName");
		String aliasName = (String) data.get("aliasName");
		if (StringUtil.isBlank(aliasName)) {
			aliasName = "m";
		}
		Class<? extends Table> clazz = (Class<? extends Table>) Class.forName(packageName + "." + mainTableClassName);
		Table mainTable = clazz.newInstance();
		mainTable.setAlias(aliasName);
		List<Map<String, Object>> join = (List<Map<String, Object>>) data.get("joinTableList");
		ret.put("joinTableList", this.queryJoinCondition(mainTable, join, "i"));
		List<Map<String, Object>> leftJoin = (List<Map<String, Object>>) data.get("leftJoinTableList");
		ret.put("leftJoinTableList", this.queryJoinCondition(mainTable, leftJoin, "l"));
		List<Map<String, Object>> rightJoin = (List<Map<String, Object>>) data.get("rightJoinTableList");
		ret.put("rightJoinTableList", this.queryJoinCondition(mainTable, rightJoin, "r"));
		return ret;
	}
	
	/**
	 * 各テーブルのJOIN条件式を取得します。
	 * @param param POSTされたデータ。
	 * @return JOIN条件式が設定されたリスト。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getJoinCondition(final Map<String, Object> param) throws Exception {
		this.methodStartLog(log, param);
		JsonResponse ret = null;
		param.put("forceOverwrite", "1");
		Map<String, Object> p = new HashMap<String, Object>();
		p.putAll(param);
		p.put("forceOverwrite", "1");
		List<ValidationError> vlist = this.validate(p);
		if (vlist.size() == 0) {
			Map<String, Object> data = this.convertToServerData(param);
			Map<String, Object> cond = this.queryJoinCondition(data);
			ret = new JsonResponse(JsonResponse.SUCCESS, cond);
		}
		this.methodFinishLog(log, ret);
		return ret;
		
	}
	
	
	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
	}


	/**
	 * 各JOINリストのインポート文を作成します。
	 * @param list  POSTされたデータ。
	 * @return インポート文。
	 */
	private String generateImportTableList(final List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m:list) {
			String packageName = (String) m.get("packageName");
			String tableClassName = (String) m.get("tableClassName");
			sb.append("import " + packageName + "." + tableClassName + ";\n");
		}
		return sb.toString();
	}
	
	/**
	 * テーブルクラスのインポート文を作成。
	 * @param data POSTされたデータ。
	 * @return インポート文。
	 */
	@SuppressWarnings("unchecked")
	private String generateImportTables(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		String packageName = (String) data.get("mainTablePackageName");
		String mainTableClassName = (String) data.get("mainTableClassName");
		sb.append("import " + packageName + "." + mainTableClassName + ";\n");
		sb.append(this.generateImportTableList((List<Map<String, Object>>) data.get("joinTableList")));
		sb.append(this.generateImportTableList((List<Map<String, Object>>) data.get("leftJoinTableList")));
		sb.append(this.generateImportTableList((List<Map<String, Object>>) data.get("rightJoinTableList")));
		return sb.toString();
	}

	/**
	 * テーブルクラスのインスタンス変数名を取得します。
	 * @param classname クラス名。
	 * @return テーブルクラスのインスタンス変数名。
	 */
	private String getTableVariableName(final String classname) {
		String ret = classname.substring(0, 1).toLowerCase() + classname.substring(1);
		return ret;
	}
	
	/**
	 * 各JOINリストのインポート文を作成します。
	 * @param list  POSTされたデータ。
	 * @return インポート文。
	 */
	private String generateNewTableList(final List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m:list) {
			String tableClassName = (String) m.get("tableClassName");
			sb.append("\t\tTable " + this.getTableVariableName(tableClassName) + " = new " + tableClassName + "();\n");
			String aliasName = (String) m.get("aliasName");
			if (!StringUtil.isBlank(aliasName)) {
				sb.append("\t\t" + this.getTableVariableName(tableClassName) + ".setAlias(\"" + aliasName + "\");\n"); 
			}
		}
		return sb.toString();
	}
	

	/**
	 * テーブルクラスのインポート文を作成。
	 * @param data POSTされたデータ。
	 * @return インポート文。
	 */
	@SuppressWarnings("unchecked")
	private String generateNewTables(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		String mainTableClassName = (String) data.get("mainTableClassName");
		sb.append("\t\tTable " + this.getTableVariableName(mainTableClassName) + " = new " + mainTableClassName + "();\n");
		String aliasName = (String) data.get("aliasName");
		if (!StringUtil.isBlank(aliasName)) {
			sb.append("\t\t" + this.getTableVariableName(mainTableClassName) + ".setAlias(\"" + aliasName + "\");\n"); 
		}
		sb.append(this.generateNewTableList((List<Map<String, Object>>) data.get("joinTableList")));
 		sb.append(this.generateNewTableList((List<Map<String, Object>>) data.get("leftJoinTableList")));
		sb.append(this.generateNewTableList((List<Map<String, Object>>) data.get("rightJoinTableList")));
		return sb.toString();
	}

	/**
	 * 選択フィールド設定ソースを生成します。
	 * @param data POSTされたデータ。
	 * @return 選択フィールド設定ソース。
	 */
	private String generateSelectFieldList(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("selectFieldList");
		for (Map<String, Object> m: list) {
			String sel = (String) m.get("sel");
			if ("1".equals(sel)) {
				if (sb.length() > 0) {
					sb.append("\t\t\t, ");
				} else {
					sb.append("\t\t\t");
				}
				String tableClassName = (String) m.get("selectTableClassName");
				log.debug("tableClassName=" + tableClassName);
				String fieldId = (String) m.get("fieldId");
				sb.append(this.getTableVariableName(tableClassName) + ".");
				sb.append("getFieldList().get(\"" + fieldId + "\")\n");
			}
		}
		return "\t\tthis.setFieldList(new FieldList(\n" + sb.toString() + "\t\t));";
	}

	/**
	 * テーブルのJOIN設定ソースを生成します。
	 * @param list JOINテーブルリスト。
	 * @return テーブルのJOIN設定ソース。
	 */
	private String generateJoinTableList(final List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		if (list != null) {
			for (Map<String, Object> m: list) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				String tableClassName = (String) m.get("tableClassName");
				sb.append(this.getTableVariableName(tableClassName));
			}
		}
		if (sb.length() > 0) {
			return "new TableList(" + sb.toString() + ")";
		} else {
			return "";
		}
	}

	/**
	 * JOINの設定ソースを生成します。
	 * @param data POSTされたデータ。
	 * @return JOINの設定ソース。
	 */
	@SuppressWarnings("unchecked")
	private String generateJoinTables(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		String join = this.generateJoinTableList((List<Map<String, Object>>) data.get("joinTableList"));
		if (!StringUtil.isBlank(join)) {
			sb.append("\t\tthis.setJoinTableList(" + join + ");\n");
		}
		String leftJoin = this.generateJoinTableList((List<Map<String, Object>>) data.get("leftJoinTableList"));
		if (!StringUtil.isBlank(leftJoin)) {
			sb.append("\t\tthis.setLeftJoinTableList(" + leftJoin + ");\n");
		}
		String rightJoin = this.generateJoinTableList((List<Map<String, Object>>) data.get("rightJoinTableList"));
		if (!StringUtil.isBlank(rightJoin)) {
			sb.append("\t\tthis.setRightJoinTableList(" + rightJoin + ");\n");
		}
		return sb.toString();
	}

	
	
	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		String javasrc = this.getStringResourse("template/Query.java.template");
		String packageName = (String) data.get("packageName");
		String queryClassName = (String) data.get("queryClassName");
		javasrc = javasrc.replaceAll("\\$\\{packageName\\}", packageName);
		javasrc = javasrc.replaceAll("\\$\\{queryClassName\\}", queryClassName);
		javasrc = javasrc.replaceAll("\\$\\{importTables\\}", this.generateImportTables(data));
		javasrc = javasrc.replaceAll("\\$\\{newTables\\}", this.generateNewTables(data));
		javasrc = javasrc.replaceAll("\\$\\{selectFields\\}", this.generateSelectFieldList(data));
		String mainTableClassName = (String) data.get("mainTableClassName");
		javasrc = javasrc.replaceAll("\\$\\{mainTable\\}", this.getTableVariableName(mainTableClassName));
		String distinctFlag = (String) data.get("distinctFlag");
		if ("1".equals(distinctFlag)) {
			javasrc = javasrc.replaceAll("\\$\\{distinctFlag\\}", "true");
		} else {
			javasrc = javasrc.replaceAll("\\$\\{distinctFlag\\}", "false");
		}
		javasrc = javasrc.replaceAll("\\$\\{joinTables\\}", this.generateJoinTables(data));
		String javaSrc = (String) data.get("javaSourcePath");
		String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
		String query = srcPath + "/" + queryClassName + ".java";
		FileUtil.writeTextFileWithBackup(query, javasrc, DataFormsServlet.getEncoding());
		log.debug("javasrc=\n" + javasrc);
	}
	
	@Override
	protected String getSavedMessage(final Map<String, Object> data) {
		return MessagesUtil.getMessage(this.getPage(), "message.javasourcecreated");
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		// 何もしない
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		//　何もしない。
	}

}
