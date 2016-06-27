package dataforms.devtool.page.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.EditForm;
import dataforms.controller.JsonResponse;
import dataforms.dao.Table;
import dataforms.devtool.field.common.AliasNameField;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.QueryClassNameField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.common.FlagField;
import dataforms.htmltable.EditableHtmlTable;
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
	 * Joinテーブルクラス。
	 *
	 */
	private static class JoinHtmlTable extends EditableHtmlTable {
		/**
		 * コンストラクタ。
		 * @param id デーブルID。
		 */
		public JoinHtmlTable(final String id) {
			super(id);
			FieldList flist = new FieldList(
				new FunctionSelectField()
				, new PackageNameField()
				, new TableClassNameField()
				, new AliasNameField()
			);
			flist.get("tableClassName").setAutocomplete(true).setCalcEventField(true);
			this.setFieldList(flist);
		}
	};

	/**
	 * コンストラクタ。
	 */
	public QueryGeneratorEditForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new QueryClassNameField()).setAutocomplete(false).addValidator(new RequiredValidator());
		this.addField(new AliasNameField());

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
			ent.put("selectFieldId", f.getId());
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
		List<ValidationError> vlist = this.validate(param);
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
	
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		return null;
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
	private String getImportTableList(final List<Map<String, Object>> list) {
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
	private String getImportTables(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		String packageName = (String) data.get("mainTablePackageName");
		String mainTableClassName = (String) data.get("mainTableClassName");
		sb.append("import " + packageName + "." + mainTableClassName + ";\n");
		sb.append(this.getImportTableList((List<Map<String, Object>>) data.get("joinTableList")));
		sb.append(this.getImportTableList((List<Map<String, Object>>) data.get("leftJoinTableList")));
		sb.append(this.getImportTableList((List<Map<String, Object>>) data.get("rightJoinTableList")));
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
	private String getNewTableList(final List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m:list) {
			String tableClassName = (String) m.get("tableClassName");
			sb.append("\t\tTable " + this.getTableVariableName(tableClassName) + " = new " + tableClassName + "();\n");
		}
		return sb.toString();
	}
	

	/**
	 * テーブルクラスのインポート文を作成。
	 * @param data POSTされたデータ。
	 * @return インポート文。
	 */
	@SuppressWarnings("unchecked")
	private String getNewTables(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		String mainTableClassName = (String) data.get("mainTableClassName");
		sb.append("\t\tTable " + this.getTableVariableName(mainTableClassName) + " = new " + mainTableClassName + "();\n");
		sb.append(this.getNewTableList((List<Map<String, Object>>) data.get("joinTableList")));
 		sb.append(this.getNewTableList((List<Map<String, Object>>) data.get("leftJoinTableList")));
		sb.append(this.getNewTableList((List<Map<String, Object>>) data.get("rightJoinTableList")));
		return sb.toString();
	}

	/**
	 * 選択フィールド設定ソースを生成します。
	 * @param data POSTされたデータ。
	 * @return 選択フィールド設定ソース。
	 */
	private String getSelectFieldList(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("selectFieldList");
		for (Map<String, Object> m: list) {
			String selectFieldId = (String) m.get("selectFieldId");
			if (!StringUtil.isBlank(selectFieldId)) {
				if (sb.length() > 0) {
					sb.append("\t\t\t, ");
				} else {
					sb.append("\t\t\t");
				}
				String tableClassName = (String) m.get("selectTableClassName");
				log.debug("tableClassName=" + tableClassName);
				sb.append(this.getTableVariableName(tableClassName) + ".");
				sb.append("getFieldList().get(\"" + selectFieldId + "\")\n");
			}
		}
		return "\t\tthis.setFieldList(new FieldList(\n" + sb.toString() + "\t\t));";
	}

	/**
	 * テーブルのJOIN設定ソースを生成します。
	 * @param list JOINテーブルリスト。
	 * @return テーブルのJOIN設定ソース。
	 */
	private String getJoinTableList(final List<Map<String, Object>> list) {
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
	private String getJoinTables(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		String join = this.getJoinTableList((List<Map<String, Object>>) data.get("joinTableList"));
		if (!StringUtil.isBlank(join)) {
			sb.append("\t\tthis.setJoinTableList(" + join + ");\n");
		}
		String leftJoin = this.getJoinTableList((List<Map<String, Object>>) data.get("leftJoinTableList"));
		if (!StringUtil.isBlank(leftJoin)) {
			sb.append("\t\tthis.setLeftJoinTableList(" + leftJoin + ");\n");
		}
		String rightJoin = this.getJoinTableList((List<Map<String, Object>>) data.get("rightJoinTableList"));
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
		javasrc = javasrc.replaceAll("\\$\\{importTables\\}", this.getImportTables(data));
		javasrc = javasrc.replaceAll("\\$\\{newTables\\}", this.getNewTables(data));
		javasrc = javasrc.replaceAll("\\$\\{selectFields\\}", this.getSelectFieldList(data));
		String mainTableClassName = (String) data.get("mainTableClassName");
		javasrc = javasrc.replaceAll("\\$\\{mainTable\\}", this.getTableVariableName(mainTableClassName));
		String distinctFlag = (String) data.get("distinctFlag");
		if ("1".equals(distinctFlag)) {
			javasrc = javasrc.replaceAll("\\$\\{distinctFlag\\}", "true");
		} else {
			javasrc = javasrc.replaceAll("\\$\\{distinctFlag\\}", "false");
		}
		javasrc = javasrc.replaceAll("\\$\\{joinTables\\}", this.getJoinTables(data));
		
		log.debug("javasrc=\n" + javasrc);
		throw new Exception("a");
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
	}

}
