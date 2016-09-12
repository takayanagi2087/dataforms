package dataforms.devtool.page.pageform;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.controller.ApplicationException;
import dataforms.controller.EditForm;
import dataforms.controller.Page;
import dataforms.controller.QueryForm;
import dataforms.controller.QueryResultForm;
import dataforms.controller.WebComponent;
import dataforms.dao.Dao;
import dataforms.dao.Table;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.JavaSourcePathField;
import dataforms.devtool.field.common.OverwriteModeField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.PageClassNameField;
import dataforms.devtool.field.common.PageNameField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.devtool.field.pagegen.EditFormClassNameField;
import dataforms.devtool.field.pagegen.QueryFormClassNameField;
import dataforms.devtool.field.pagegen.QueryResultFormClassNameField;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.base.Field;
import dataforms.field.base.Field.MatchType;
import dataforms.field.common.CreateTimestampField;
import dataforms.field.common.CreateUserIdField;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.FlagField;
import dataforms.field.common.PresenceField;
import dataforms.field.common.UpdateTimestampField;
import dataforms.field.common.UpdateUserIdField;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.FileUtil;
import dataforms.util.MessagesUtil;
import dataforms.util.SequentialProperties;
import dataforms.util.StringUtil;
import dataforms.validator.RequiredValidator;
import dataforms.validator.ValidationError;

/**
 * ページ作成フォームクラス。
 *
 */
public class PageGeneratorEditForm extends EditForm {
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(PageGeneratorEditForm.class.getName());

	/**
	 * コンストラクタ。
	 */
	public PageGeneratorEditForm() {
		this.addField(new JavaSourcePathField());
//		this.addField(new ExistingFolderField("javaSourcePath")).setReadonly(true);
		FunctionSelectField funcField = new FunctionSelectField();
		funcField.setPackageOption("page");
		funcField.setCalcEventField(true);
		this.addField(funcField);

		this.addField(new PageNameField()).addValidator(new RequiredValidator());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new PageClassNameField())
			.addValidator(new RequiredValidator())
			.setCalcEventField(true)
			.setAutocomplete(false);
		this.addField(new OverwriteModeField("pageClassOverwriteMode"));
		this.addField(new PresenceField("updateTable"));

		this.addField(new PackageNameField("tablePackageName"));
		TableClassNameField tblcls = new TableClassNameField();
		tblcls.setCalcEventField(true);
		tblcls.setAutocomplete(true);
		tblcls.setPackageNameFieldId("tablePackageName");
		this.addField(tblcls);
		this.addField(new ClassNameField("daoClassName")).setCalcEventField(true);
		this.addField(new OverwriteModeField("daoClassOverwriteMode"));

		this.addField(new QueryFormClassNameField());
		this.addField(new FlagField("queryFormClassFlag"));
		this.addField(new OverwriteModeField("queryFormClassOverwriteMode"));
		this.addField(new QueryResultFormClassNameField());
		this.addField(new FlagField("queryResultFormClassFlag"));
		this.addField(new OverwriteModeField("queryResultFormClassOverwriteMode"));
		this.addField(new EditFormClassNameField());
		this.addField(new OverwriteModeField("editFormClassOverwriteMode"));
		this.addField(new FlagField("editFormClassFlag"));
//		this.addField(new FlagField("forceOverwrite"));
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("javaSourcePath", DeveloperPage.getJavaSourcePath());
		this.setFormData("updateTable", "1");
		this.setFormData("pageClassOverwriteMode", OverwriteModeField.ERROR);
		this.setFormData("daoClassOverwriteMode", OverwriteModeField.ERROR);
		this.setFormData("queryFormClassFlag", "1");
		this.setFormData("queryFormClassOverwriteMode", OverwriteModeField.ERROR);
		this.setFormData("queryResultFormClassFlag", "1");
		this.setFormData("queryResultFormClassOverwriteMode", OverwriteModeField.ERROR);
		this.setFormData("editFormClassFlag", "1");
		this.setFormData("editFormClassOverwriteMode", OverwriteModeField.ERROR);

	}


	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		log.debug("data=" + data);
		String pkg = (String) data.get("packageName");
		String cls = (String) data.get("pageClassName");
		String classname = pkg + "." + cls;
		Class<?> clazz = Class.forName(classname);
		Page p = (Page) clazz.newInstance();
		PageClassInfo pi = new PageClassInfo(p);
		Class<? extends Table> tblcls = pi.getTableClass();
		Class<? extends Dao> daocls = pi.getDaoClass();
		String functionPath = pi.getFunctionPath();
		if (tblcls == null || daocls == null || functionPath == null) {
			throw new ApplicationException(this.getPage(), "error.notgeneratedpage");
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.putAll(data);
		if (daocls.getName().equals(dataforms.dao.Dao.class.getName())) {
			ret.put("updateTable", "0");
			ret.put("tablePackageName", "");
			ret.put("tableClassName", "");
			ret.put("daoClassName", "");
		} else {
			ret.put("updateTable", "1");
			ret.put("tablePackageName", tblcls.getPackage().getName());
			ret.put("tableClassName", tblcls.getSimpleName());
			ret.put("daoClassName", daocls.getSimpleName());
		}
		ret.put("pageClassOverwriteMode", OverwriteModeField.ERROR);
		ret.put("daoClassOverwriteMode", OverwriteModeField.ERROR);
		ret.put("functionSelect", functionPath);


		String funcprop = this.getFunctionPropertiesPath(functionPath);
		SequentialProperties prop = this.readFunctionProperties(funcprop);
		String name = (String) prop.get(pkg + "." + cls);
		ret.put("pageName", name);

		Map<String, WebComponent> fm = p.getFormMap();
		ret.put("queryFormClassFlag", "0");
		ret.put("queryResultFormClassFlag", "0");
		ret.put("editFormClassFlag", "0");
		for (String key: fm.keySet()) {
			log.debug("*** key=" + key);
			WebComponent cmp = fm.get(key);
			if (cmp != null) {
				if (cmp instanceof QueryForm) {
					ret.put("queryFormClassName", cmp.getClass().getSimpleName());
					ret.put("queryFormClassFlag", "1");
					ret.put("queryFormClassOverwriteMode", OverwriteModeField.ERROR);
				} else if (cmp instanceof QueryResultForm) {
					ret.put("queryResultFormClassName", cmp.getClass().getSimpleName());
					ret.put("queryResultFormClassFlag", "1");
					ret.put("queryResultFormClassOverwriteMode", OverwriteModeField.ERROR);
				} else if (cmp instanceof EditForm) {
					ret.put("editFormClassName", cmp.getClass().getSimpleName());
					ret.put("editFormClassFlag", "1");
					ret.put("editFormClassOverwriteMode", OverwriteModeField.ERROR);
				}
			}
		}
		ret.put("javaSourcePath", DeveloperPage.getJavaSourcePath());
		return ret;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
	}


	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> ret =  super.validate(param);
		if (ret.size() == 0) {
			Map<String, Object> data = this.convertToServerData(param);
			String javaSrc = (String) data.get("javaSourcePath");
			String packageName = (String) data.get("packageName");
			String queryFormClassName = (String) data.get("queryFormClassName");
			String daoClassName = (String) data.get("daoClassName");
			String queryResultFormClassName = (String) data.get("queryResultFormClassName");
			String editFormClassName = (String) data.get("editFormClassName");
			String pageClassName = (String) data.get("pageClassName");
			String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
			String pageClassOverwriteMode = (String) data.get("pageClassOverwriteMode");
			File f3 = new File(srcPath + "/" + pageClassName + ".java");
			if (OverwriteModeField.ERROR.equals(pageClassOverwriteMode)) {
				if (f3.exists()) {
					ret.add(new ValidationError("pageClassName", this.getPage().getMessage("error.sourcefileexist", pageClassName + ".java")));
				}
			}

			if (this.isUpdateTable(data)) {
				String tablePackageName = (String) data.get("tablePackageName");
				String daoSrcPath = javaSrc + "/" + tablePackageName.replaceAll("\\.", "/");
				String daoClassOverwriteMode = (String) data.get("daoClassOverwriteMode");
				if (OverwriteModeField.ERROR.equals(daoClassOverwriteMode)) {
					File f0 = new File(daoSrcPath + "/" + daoClassName + ".java");
					if (f0.exists()) {
						ret.add(new ValidationError("daoClassName", this.getPage().getMessage("error.sourcefileexist", queryFormClassName + ".java")));
					}
				}
			}

			String queryFormClassOverwriteMode = (String) data.get("queryFormClassOverwriteMode");
			if (OverwriteModeField.ERROR.equals(queryFormClassOverwriteMode)) {
				File f0 = new File(srcPath + "/" + queryFormClassName + ".java");
				if (f0.exists()) {
					ret.add(new ValidationError("queryFormClassName", this.getPage().getMessage("error.sourcefileexist", queryFormClassName + ".java")));
				}
			}
			String queryResultFormClassOverwriteMode = (String) data.get("queryResultFormClassOverwriteMode");
			if (OverwriteModeField.ERROR.equals(queryResultFormClassOverwriteMode)) {
				File f1 = new File(srcPath + "/" + queryResultFormClassName + ".java");
				if (f1.exists()) {
					ret.add(new ValidationError("queryResultFormClassName", this.getPage().getMessage("error.sourcefileexist", queryResultFormClassName + ".java")));
				}
			}
			String editFormClassOverwriteMode = (String) data.get("editFormClassOverwriteMode");
			if (OverwriteModeField.ERROR.equals(editFormClassOverwriteMode)) {
				File f2 = new File(srcPath + "/" + editFormClassName + ".java");
				if (f2.exists()) {
					ret.add(new ValidationError("editFormClassName", this.getPage().getMessage("error.sourcefileexist", editFormClassName + ".java")));
				}
			}
		}
		return ret;

	}

	/**
	 * 問い合わせフォームのフィールドリストソースの作成。
	 * @param table テーブル。
	 * @param implist フィールドのインポートリスト。
	 * @return 問い合わせフォームのフィールドリストソース。
	 * @throws Exception 例外。
	 */
	private String getQueryFieldList(final String table, final StringBuilder implist) throws Exception {
		implist.append("import dataforms.field.base.Field.MatchType;\n");
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		Class<? extends Table> tcls = (Class<? extends Table>) Class.forName(table);
		String scn = tcls.getSimpleName();
		Table tbl = tcls.newInstance();
		for (Field<?> f: tbl.getFieldList()) {
			if (tbl.getPkFieldList().get(f.getId()) != null) {
				continue;
			}
			if (f instanceof DeleteFlagField
				|| f instanceof CreateUserIdField
				|| f instanceof CreateTimestampField
				|| f instanceof UpdateUserIdField
				|| f instanceof UpdateTimestampField
				) {
				;
			} else {
				MatchType dt = f.getDefaultMatchType();
				if (dt == MatchType.NONE) {
					;
				} else if (dt == MatchType.RANGE_FROM) {
					String id = f.getId();
					String cname = f.getClass().getSimpleName();
					String cmt = f.getComment();
					sb.append("\t\tthis.addField(new " + cname + "(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(id) + " + \"From\")).setMatchType(MatchType.RANGE_FROM).setComment(\"" + cmt + "(from)\");\n");
					sb.append("\t\tthis.addField(new " + cname + "(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(id) + " + \"To\")).setMatchType(MatchType.RANGE_TO).setComment(\"" + cmt + "(to)\");\n");
					implist.append("import " + f.getClass().getName() + ";\n");
				} else {
					String id = f.getId();
					String mt = dt.name();
					sb.append("\t\tthis.addField(table.get" + StringUtil.firstLetterToUpperCase(id) + "Field()).setMatchType(MatchType." + mt + ");\n");
				}
			}
		}
		return sb.toString();
	}


	/**
	 * テンプレートのパスを取得します。
	 * @param data POSTされたデータ。
	 * @return テンプレートのパス。
	 */
	private String getTemplatePath(final Map<String, Object> data) {
		return (this.isUpdateTable(data) ? "template/" : "notabletemplate/");
	}


	/**
	 * 問い合わせ結果フォームのソートカラム指定コードを生成します。
	 * @param table テーブルのクラス名。
	 * @return 生成されたコード。
	 * @throws Exception 例外。
	 */
	private String getQueryResultFieldSetting(final String table) throws Exception {
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		Class<? extends Table> tcls = (Class<? extends Table>) Class.forName(table);
		String scn = tcls.getSimpleName();
		Table tbl = tcls.newInstance();
		for (Field<?> f: tbl.getFieldList()) {
			if (f.isHidden() || f instanceof DeleteFlagField) {
				continue;
			}
			// 		htmltable.getFieldList().get("sampleText").setSortable(true);
			sb.append("\t\thtmltable.getFieldList().get(" + scn + ".Entity.ID_" + StringUtil.camelToUpperCaseSnake(f.getId()) + ").setSortable(true);\n");
		}
		return sb.toString();
	}

	/**
	 * フォームクラスの生成処理。
	 * @param data 入力データ。
	 * @param packageNameId パッケージ名フィールドのID。
	 * @param formFieldId フォーム名称のフィールドID。
	 * @param template テンプレートリソース。
	 * @param overWriteMode 上書きモード。
	 * @return 作成されたクラス名。
	 * @throws Exception 例外。
	 *
	 */
	private String generateFormClass(final Map<String, Object> data, final String packageNameId, final String formFieldId, final String template, final String overWriteMode) throws Exception {
		String javaSrc = (String) data.get("javaSourcePath");
		String tablePackageName = (String) data.get("tablePackageName");
		String tableClassName = (String) data.get("tableClassName");
		String daoClassName = (String) data.get("daoClassName");
		String packageName = (String) data.get(packageNameId);
		String formClassName = (String) data.get(formFieldId);
		if (!StringUtil.isBlank(formClassName)) {
			String javasrc = this.getStringResourse(this.getTemplatePath(data) + template);
			javasrc = javasrc.replaceAll("\\$\\{packageName\\}", packageName);
			javasrc = javasrc.replaceAll("\\$\\{tableClassFullName\\}", tablePackageName + "." + tableClassName);
			javasrc = javasrc.replaceAll("\\$\\{daoClassFullName\\}", tablePackageName + "." + daoClassName);
			javasrc = javasrc.replaceAll("\\$\\{" + formFieldId + "\\}", formClassName);
			javasrc = javasrc.replaceAll("\\$\\{tableClassName\\}", tableClassName);
			javasrc = javasrc.replaceAll("\\$\\{daoClassName\\}", daoClassName);
			if (this.isUpdateTable(data)) {
				StringBuilder implist = new StringBuilder();
				javasrc = javasrc.replaceAll("\\$\\{queryFormFieldList\\}", this.getQueryFieldList(tablePackageName + "." + tableClassName, implist));
				javasrc = javasrc.replaceAll("\\$\\{queryResultFieldSetting\\}", this.getQueryResultFieldSetting(tablePackageName + "." + tableClassName));
				javasrc = javasrc.replaceAll("\\$\\{fieldImportList\\}", implist.toString());
			}
			String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
			File dir = new File(srcPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String srcfile = srcPath + "/" + formClassName + ".java";
			log.debug("srcpath=" + srcfile);
			log.debug("src=" + javasrc);
			if (!OverwriteModeField.SKIP.equals(overWriteMode)) {
				FileUtil.writeTextFileWithBackup(srcfile, javasrc, DataFormsServlet.getEncoding());
			}
		}
		return formClassName;
	}

	/**
	 * Daoクラスの生成処理。
	 * @param data 入力データ。
	 * @param overWriteMode 上書きモード。
	 * @return 作成されたクラス名。
	 * @throws Exception 例外。
	 */
	private String generateDaoClass(final Map<String, Object> data, final String overWriteMode) throws Exception {
		String javaSrc = (String) data.get("javaSourcePath");
		String packageName = (String) data.get("tablePackageName");
		String tableClassName = (String) data.get("tableClassName");
		String daoClassName = (String) data.get("daoClassName");
		String javasrc = this.getStringResourse("template/Dao.java.template");
		javasrc = javasrc.replaceAll("\\$\\{packageName\\}", packageName);
		javasrc = javasrc.replaceAll("\\$\\{daoClassName\\}", daoClassName);
		javasrc = javasrc.replaceAll("\\$\\{tableClassName\\}", tableClassName);
		String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
		File dir = new File(srcPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String srcfile = srcPath + "/" + daoClassName + ".java";
		log.debug("srcpath=" + srcfile);
		log.debug("src=" + javasrc);
		if (!OverwriteModeField.SKIP.equals(overWriteMode)) {
			FileUtil.writeTextFileWithBackup(srcfile, javasrc, DataFormsServlet.getEncoding());
		}
		return daoClassName;
	}

	/**
	 * テーブル更新処理生成フラグを取得する。
	 * @param data POSTされたデータ。
	 * @return テーブル操作を行う場合true。
	 */
	private boolean isUpdateTable(final Map<String, Object> data) {
		String updateTable = (String) data.get("updateTable");
		return "1".equals(updateTable);
	}


	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		String javaSrc = (String) data.get("javaSourcePath");
		String functionPath = (String) data.get("functionSelect");
		String packageName = (String) data.get("packageName");
		String pageClassName = (String) data.get("pageClassName");
		String tablePackageName = (String) data.get("tablePackageName");
		String tableClassName = (String) data.get("tableClassName");
		String daoClassName = (String) data.get("daoClassName");

		String daoClassOverwriteMode = (String) data.get("daoClassOverwriteMode"); 
		String queryFormClassOverwriteMode = (String) data.get("queryFormClassOverwriteMode");
		String queryResultFormClassOverwriteMode = (String) data.get("queryResultFormClassOverwriteMode");
		String editFormClassOverwriteMode = (String) data.get("editFormClassOverwriteMode");
		String pageClassOverwriteMode = (String) data.get("pageClassOverwriteMode");

		
		String queryFormClass = this.generateFormClass(data, "packageName", "queryFormClassName", "QueryForm.java.template", queryFormClassOverwriteMode);
		String queryResultFormClass = this.generateFormClass(data, "packageName", "queryResultFormClassName", "QueryResultForm.java.template", queryResultFormClassOverwriteMode);
		String editFormClass = this.generateFormClass(data, "packageName", "editFormClassName", "EditForm.java.template", editFormClassOverwriteMode);

		if (this.isUpdateTable(data)) {
			this.generateDaoClass(data, daoClassOverwriteMode);
		}

		StringBuilder sb = new StringBuilder();
		if (!StringUtil.isBlank(queryFormClass)) {
			sb.append("\t\tthis.addForm(new " + queryFormClass + "());\n");
		}
		if (!StringUtil.isBlank(queryResultFormClass)) {
			sb.append("\t\tthis.addForm(new " + queryResultFormClass + "());\n");
		}
		if (!StringUtil.isBlank(editFormClass)) {
			sb.append("\t\tthis.addForm(new " + editFormClass + "());\n");
		}

		String javasrc = this.getStringResourse(this.getTemplatePath(data) + "Page.java.template");
		javasrc = javasrc.replaceAll("\\$\\{packageName\\}", packageName);
		javasrc = javasrc.replaceAll("\\$\\{tableClassFullName\\}", tablePackageName + "." + tableClassName);
		javasrc = javasrc.replaceAll("\\$\\{daoClassFullName\\}", tablePackageName + "." + daoClassName);
		javasrc = javasrc.replaceAll("\\$\\{pageClassName\\}", pageClassName);
		javasrc = javasrc.replaceAll("\\$\\{formList\\}", sb.toString());
		javasrc = javasrc.replaceAll("\\$\\{tableClass\\}", tableClassName);
		javasrc = javasrc.replaceAll("\\$\\{daoClass\\}", daoClassName);
		javasrc = javasrc.replaceAll("\\$\\{functionPath\\}", functionPath);
		String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
		String srcfile = srcPath + "/" + pageClassName + ".java";
//		log.debug("srcpath=" + srcfile);
//		log.debug("src=" + javasrc);
		if (!OverwriteModeField.SKIP.equals(pageClassOverwriteMode)) {
			FileUtil.writeTextFileWithBackup(srcfile, javasrc, DataFormsServlet.getEncoding());
		}
		String pageName = (String) data.get("pageName");
		this.updatePageName(functionPath, packageName, pageClassName, pageName);

	}

	/**
	 * ページ名の更新。
	 * @param functionPath 機能パス。
	 * @param packageName パッケージ名。
	 * @param pageClassName ページクラス名。
	 * @param pageName ページ名。
	 * @throws Exception 例外。
	 */
	private void updatePageName(final String functionPath, final String packageName, final String pageClassName, final String pageName) throws Exception {
		String funcprop = this.getFunctionPropertiesPath(functionPath);
		SequentialProperties prop = this.readFunctionProperties(funcprop);
		prop.put(packageName + "." + pageClassName, pageName);
		String str = prop.getSaveText();
		log.debug("str=" + str);
		FileUtil.writeTextFileWithBackup(funcprop, str, DataFormsServlet.getEncoding());

	}

	/**
	 * Function.propertiesのパスを取得します。
	 * @param functionPath 機能のパス。
	 * @return Function.propertiesのパス。
	 * @throws Exception 例外。
	 */
	private String getFunctionPropertiesPath(final String functionPath) throws Exception {
		String webResourcePath = DeveloperPage.getWebSourcePath();
		String funcprop = this.getPage().getAppropriatePath(functionPath + "/Function.properties", this.getPage().getRequest());
		funcprop = webResourcePath + funcprop;
		return funcprop;
	}

	/**
	 * Function.propertiesを読み込みます。
	 * @param funcprop Function.propertiesのパス。
	 * @return 読み込んだ内容。
	 * @throws Exception 例外。
	 */
	private SequentialProperties readFunctionProperties(final String funcprop) throws Exception {
		String text = "";
		File propfile = new File(funcprop);
		if (propfile.exists()) {
			text = FileUtil.readTextFile(funcprop, DataFormsServlet.getEncoding());
		}
		SequentialProperties prop = new SequentialProperties();
		prop.loadText(text);
		return prop;
	}


	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		// 何もしない
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		// 何もしない
	}


	@Override
	protected String getSavedMessage(final Map<String, Object> data) {
		return MessagesUtil.getMessage(this.getPage(), "message.javasourcecreated");
	}
}
