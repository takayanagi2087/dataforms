package dataforms.devtool.page.table;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.BinaryResponse;
import dataforms.controller.EditForm;
import dataforms.controller.JsonResponse;
import dataforms.controller.Response;
import dataforms.dao.Dao;
import dataforms.dao.Table;
import dataforms.dao.file.FileObject;
import dataforms.dao.file.ImageData;
import dataforms.dao.file.VideoData;
import dataforms.dao.sqldatatype.SqlBigint;
import dataforms.dao.sqldatatype.SqlChar;
import dataforms.dao.sqldatatype.SqlDate;
import dataforms.dao.sqldatatype.SqlDouble;
import dataforms.dao.sqldatatype.SqlInteger;
import dataforms.dao.sqldatatype.SqlNumeric;
import dataforms.dao.sqldatatype.SqlSmallint;
import dataforms.dao.sqldatatype.SqlTime;
import dataforms.dao.sqldatatype.SqlTimestamp;
import dataforms.dao.sqldatatype.SqlVarchar;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.JavaSourcePathField;
import dataforms.devtool.field.common.OverwriteModeField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.base.Field;
import dataforms.field.common.CharSingleSelectField;
import dataforms.field.common.CreateTimestampField;
import dataforms.field.common.CreateUserIdField;
import dataforms.field.common.FileObjectField;
import dataforms.field.common.FlagField;
import dataforms.field.common.ImageField;
import dataforms.field.common.MultiSelectField;
import dataforms.field.common.UpdateTimestampField;
import dataforms.field.common.UpdateUserIdField;
import dataforms.field.common.VideoField;
import dataforms.field.sqltype.BigintField;
import dataforms.field.sqltype.CharField;
import dataforms.field.sqltype.ClobField;
import dataforms.field.sqltype.DateField;
import dataforms.field.sqltype.IntegerField;
import dataforms.field.sqltype.NumericField;
import dataforms.field.sqltype.SmallintField;
import dataforms.field.sqltype.TimeField;
import dataforms.field.sqltype.TimestampField;
import dataforms.field.sqltype.VarcharField;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.ClassNameUtil;
import dataforms.util.FileUtil;
import dataforms.util.MessagesUtil;
import dataforms.util.StringUtil;
import dataforms.validator.RequiredValidator;
import dataforms.validator.ValidationError;
import net.arnx.jsonic.JSON;

/**
 * 編集フォームクラス。
 *
 */
public class TableGeneratorEditForm extends EditForm {

	/**
	 * Logger.
	 */
	private Logger logger = Logger.getLogger(TableGeneratorEditForm .class);

	/**
	 * コンストラクタ。
	 */
	public TableGeneratorEditForm() {
		this.addField(new JavaSourcePathField());
//		this.addField(new ExistingFolderField("javaSourcePath")).setReadonly(true).addValidator(new RequiredValidator());
		this.addField((new FunctionSelectField()).setPackageOption("dao"));
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new VarcharField("tableComment", 256));
		this.addField(new TableClassNameField()).setComment("テーブルクラス名").setAutocomplete(false).addValidator(new RequiredValidator());
		this.addField(new OverwriteModeField());
		this.addField(new FlagField("autoIncrementId")).setComment("主キー自動生成フラグ");
		FieldListHtmlTable htmltbl = new FieldListHtmlTable();
		this.addHtmlTable(htmltbl);
		this.setFormData(htmltbl.getId(), new ArrayList<Map<String, Object>>());
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("overwriteMode", OverwriteModeField.ERROR);
		this.setFormData("javaSourcePath", DeveloperPage.getJavaSourcePath());
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.putAll(data);
		String packageName = (String) data.get("packageName");
		String tableClassName = (String) data.get("tableClassName");
		String fullClassName = packageName + "." + tableClassName;
		ret.put("overwriteMode", OverwriteModeField.ERROR);
		Class<?> cls = Class.forName(fullClassName);
		Table tbl = (Table) cls.newInstance();
		if (tbl.isAutoIncrementId()) {
			ret.put("autoIncrementId", "1");
		} else {
			ret.put("autoIncrementId", "0");
		}
		ret.put("tableComment", tbl.getComment());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int no = 1;
		for (Field<?> f: tbl.getFieldList()) {
			if (f instanceof CreateUserIdField
				|| f instanceof CreateTimestampField
				|| f instanceof UpdateUserIdField
				|| f instanceof UpdateTimestampField) {
				continue;
			}
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("no", Integer.valueOf(no++));
			String className = f.getClass().getName();
			m.put("packageName", ClassNameUtil.getPackageName(className));
			m.put("fieldClassName", ClassNameUtil.getSimpleClassName(className));
			String baseClassName = f.getClass().getSuperclass().getName();
			m.put("superPackageName", ClassNameUtil.getPackageName(baseClassName));
			m.put("superSimpleClassName", ClassNameUtil.getSimpleClassName(baseClassName));
			String pkflg = "0";
			if (tbl.getPkFieldList().get(f.getId()) != null) {
				pkflg = "1";
			}
			m.put("isDataformsField", (Field.isDataformsField(className) ? "1" : "0"));
			m.put("pkFlag", pkflg);
			m.put("fieldLength", f.getLengthParameter());
			if (!f.idIsDefault()) {
				m.put("fieldId", f.getId());
			} else {
				m.put("fieldId", "");
			}
			m.put("comment", f.getComment());
			m.put("overwriteMode", OverwriteModeField.ERROR);
			list.add(m);
		}
		ret.put("fieldList", list);
		ret.put("javaSourcePath", DeveloperPage.getJavaSourcePath());

		return ret;
	}


	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {

	}

	/**
	 * 引数の数が一番少ないコンストラクタを取得します。
	 * @param cls フィールドクラス。
	 * @return 引数の数が一番少ないコンストラクタ。
	 */
	private Constructor<?> getDefaultConstructor(final Class<?> cls) {
		Constructor<?>[] clist = cls.getConstructors();
		Constructor<?> cns = clist[0];
		for (Constructor<?> c: clist) {
			if (cns.getParameterCount() > c.getParameterCount()) {
				cns = c;
			}
		}
		return cns;
	}

	/**
	 * 取り敢えずコンストラクタに渡さすことが可能なパラメータのインスタンスを作成します。
	 * @param pcls パラメータクラス。
	 * @return パラメータのインスタンス。
	 * @throws Exception 例外。
	 */
	private Object newParameterInstance(final Class<?> pcls) throws Exception {
		Object ret = null;
		String classname = pcls.getName();
		if ("byte".equals(classname)) {
			ret = Byte.valueOf((byte) 0x00);
		} else if ("short".equals(classname)) {
			ret = Short.valueOf((short) 0x00);
		} else if ("int".equals(classname)) {
			ret = Integer.valueOf(0);
		} else if ("long".equals(classname)) {
			ret = Long.valueOf(0);
		} else if ("boolean".equals(classname)) {
			ret = Boolean.FALSE;
		} else if ("float".equals(classname)) {
			ret = Float.valueOf(0);
		} else if ("double".equals(classname)) {
			ret = Double.valueOf(0);
		} else if ("char".equals(classname)) {
			ret = Double.valueOf(0x00);
		} else {
			ret = pcls.newInstance();
		}
		return ret;

	}

	/**
	 * 指定されたフィールドクラスのインスタンスを作成する。
	 * @param cls フィールドクラス。
	 * @return フィールドクラスのインスタンス。
	 * @throws Exception 例外。
	 */
	private Field<?> newFieldInstance(final Class<?> cls) throws Exception {
		Constructor<?> cns = getDefaultConstructor(cls);
		Class<?>[] ptypes = cns.getParameterTypes();
		Object[] p = new Object[ptypes.length];
		for (int i = 0; i < p.length; i++) {
			logger.debug("parameter class = " + ptypes[i].getName());
			p[i] = this.newParameterInstance(ptypes[i]);
			logger.debug("parameter value = " + p[i].toString());
		}
		return (Field<?>) cns.newInstance(p);
	}


	/**
	 * 指定されたフィールドクラスの情報を返します。
	 * @param param パラメータ。
	 * @return 判定結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getFieldClassInfo(final Map<String, Object> param) throws Exception {
		this.methodStartLog(logger, param);
		try {
			String classname = (String) param.get("classname");
			Map<String, Object> ret = new HashMap<String, Object>();
			Boolean isDataformsField = Field.isDataformsField(classname);
			ret.put("isDataformsField", (isDataformsField ? "1" : "0"));
			Class<?> cls = Class.forName(classname);
			Class<?> scls = cls.getSuperclass();
			String superClassPackage = scls.getPackage().getName();
			String superClassSimpleName = scls.getSimpleName();
			ret.put("superClassPackage", superClassPackage);
			ret.put("superClassSimpleName", superClassSimpleName);
			Field<?> field = this.newFieldInstance(cls);
			// dataforms.jarの提供するFieldクラスを直接指定する場合。
			ret.put("fieldLength", field.getLengthParameter());
			ret.put("fieldComment", field.getComment());
			JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, ret);
			this.methodFinishLog(logger, result);
			return result;
		} catch (ClassNotFoundException ex) {
			// 未定義のフィールドの場合。
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("isDataformsField", "0");
			JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, ret);
			this.methodFinishLog(logger, result);
			return result;
		}
	}


	//
	/**
	 * 指定されたフィールドの親クラスの情報を取得します。
	 * @param param パラメータ。
	 * @return 判定結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getSuperFieldClassInfo(final Map<String, Object> param) throws Exception {
		this.methodStartLog(logger, param);
		JsonResponse result = null;
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			String classname = (String) param.get("superclassname");
			Class<?> cls = Class.forName(classname);
			Field<?> field = this.newFieldInstance(cls);
			ret.put("fieldLength", field.getLengthParameter());
			result = new JsonResponse(JsonResponse.SUCCESS, ret);
		} catch (Exception ex) {
			result = new JsonResponse(JsonResponse.SUCCESS, ret);
		}
		this.methodFinishLog(logger, result);
		return result;
	}

	/**
	 * ソースファイルの存在チェック。
	 * @param data データ。
	 * @return チェック結果。
	 * @throws Exception 例外。
	 */
	private List<ValidationError> validateSourceExistence(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		logger.debug("data=\n" + JSON.encode(data, true));
		String packageName = (String) data.get("packageName");
		String javaSrc = (String) data.get("javaSourcePath");
		{
			String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
			String tableClassName = (String) data.get("tableClassName");
			String overwriteMode = (String) data.get("overwriteMode");
			if (OverwriteModeField.ERROR.equals(overwriteMode)) {
				File tbl = new File(srcPath + "/" + tableClassName + ".java");
				if (tbl.exists()) {
					ret.add(new ValidationError("tableClassName", this.getPage().getMessage("error.sourcefileexist", tableClassName + ".java")));
				}
			}
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> fieldList = (List<Map<String, Object>>) data.get("fieldList");
//		for (Map<String, Object> m: fieldList) {
		for (int i = 0; i < fieldList.size(); i++) {
			Map<String, Object> m = fieldList.get(i);
			String isDataformsField = (String) m.get("isDataformsField");
			if (!"1".equals(isDataformsField)) {
				String overwriteMode = (String) m.get("overwriteMode");
				if (OverwriteModeField.ERROR.equals(overwriteMode)) {
					String fieldPackageName = (String) m.get("packageName");
					String fieldClassName = (String) m.get("fieldClassName");
					String fldSrcPath = javaSrc + "/" + fieldPackageName.replaceAll("\\.", "/");
					File fld = new File(fldSrcPath + "/" + fieldClassName + ".java");
					if (fld.exists()) {
						ret.add(new ValidationError("fieldList[" + i + "].fieldClassName", this.getPage().getMessage("error.sourcefileexist", fieldClassName + ".java")));
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 長さパラメータのチェックパターンを取得します。
	 * @param className クラス名。
	 * @return チェックパターン。
	 * @throws Exception 例外。
	 */
	private String getLengthParameterPattern(final String className) throws Exception {
		String pat = null;
		Class<?> fcls = Class.forName(className);
		if ((fcls.getModifiers() & Modifier.ABSTRACT) == 0) {
			Field<?> field = this.newFieldInstance(fcls);
			// アプリケーション用のフィールドを更新
			if (Field.hasLengthParameter(fcls)) {
				pat = field.getLengthParameterPattern();
			}
		}
		return pat;
	}

	/**
	 * フィールドのオプションパラメータのチェックを行います。
	 * @param data フォームデータ。
	 * @return チェック結果。
	 * @throws Exception 例外。
	 */
	private List<ValidationError> validateFieldOption(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> fieldList = (List<Map<String, Object>>) data.get("fieldList");
		for (int i = 0; i < fieldList.size(); i++) {
			Map<String, Object> m = (Map<String, Object>) fieldList.get(i);
			String pat = null;
			String isDataformsField = (String) m.get("isDataformsField");
			if ("1".equals(isDataformsField)) {
				String fieldPackageName = (String) m.get("packageName");
				String fieldClassSimpleName = (String) m.get("fieldClassName");
				String className = fieldPackageName + "." + fieldClassSimpleName;
				pat = this.getLengthParameterPattern(className);
			} else {
				// アプリケーション用フィールドの新規作成時の対応。
				// 親クラスに長さパラメータがある場合チェックパターンを取得する。
				String superPackageName = (String) m.get("superPackageName");
				String superSimpleClassName = (String) m.get("superSimpleClassName");
				String className = superPackageName + "." + superSimpleClassName;
				pat = this.getLengthParameterPattern(className);
			}
			logger.debug("pattern=" + pat);
			if (!StringUtil.isBlank(pat)) {
				String fieldLength = (String) m.get("fieldLength");
				if (!Pattern.matches(pat, fieldLength)) {
					ret.add(new ValidationError("fieldList[" + i + "].fieldLength", this.getPage().getMessage("error.regexp")));
				}
			} else {
				String fieldLength = (String) m.get("fieldLength");
				if (!StringUtil.isBlank(fieldLength)) {
					ret.add(new ValidationError("fieldList[" + i + "].fieldLength", this.getPage().getMessage("error.regexp")));
				}
			}
		}
		return ret;
	}

	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> ret = super.validate(param);
		if (ret.size() == 0) {
			// 追加のバリデーション。
			Map<String, Object> data = this.convertToServerData(param);
			ret.addAll(this.validateFieldOption(data));
			if (ret.size() == 0) {
				ret.addAll(this.validateSourceExistence(data));

			}
		}
		return ret;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
	}

	/**
	 * テンプレートファイルを取得します。
	 * @param name リソース名。
	 * @return テンプレートの内容。
	 * @throws Exception 例外。
	 */
	private String getTemplate(final String name) throws Exception {
		String ret = "";
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream(name);
		if (is != null) {
			try {
				ret = new String(FileUtil.readInputStream(is), DataFormsServlet.getEncoding());
			} finally {
				is.close();
			}
		}
		return ret;
	}

	/**
	 * フィールドクラスに応じたテンプレートを取得します。
	 * @param superClassName スーパークラス名。
	 * @return テンプレート。
	 * @throws Exception 例外。
	 */
	private String getFieldTemplate(final String superClassName) throws Exception {
		Class<?> c = Class.forName(superClassName);
		String ret = "";
		while (!"WebComponent".equals(c.getSimpleName())) {
			ret = this.getTemplate("template/" + c.getSimpleName() + ".java.template");
			if (!StringUtil.isBlank(ret)) {
				break;
			}
			c = c.getSuperclass();
		}
		return ret;
	}

	/**
	 * フィールドクラスの作成を行います。
	 * @param m フィールド情報。
	 * @return 生成されたソース。
	 * @throws Exception 例外。
	 *
	 */
	private String generateFieldClass(final Map<String, Object> m) throws Exception {
//		String fsrc = this.getTemplate("template/Field.java.template");
		String superPackageName = (String) m.get("superPackageName");
		String superSimpleClassName = (String) m.get("superSimpleClassName");
		String fsrc = this.getFieldTemplate(superPackageName + "." + superSimpleClassName);
		String fieldPackageName = (String) m.get("packageName");
		String fieldClassSimpleName = (String) m.get("fieldClassName");
		String fieldLength = (String) m.get("fieldLength");
		String importList = "";
		String constList = "";
		String validators = "";
		if (!StringUtil.isBlank(fieldLength)) {
			if (fieldLength.indexOf(",") < 0) {
				constList = "\t/**\n";
				constList += "\t * フィールド長。\n";
				constList += "\t */\n";
				constList += "\tprivate static final int LENGTH = " + fieldLength + ";\n";
				validators = "\t\tthis.addValidator(new MaxLengthValidator(this.getLength()));\n";
				importList = "import dataforms.validator.MaxLengthValidator;\n";
				fieldLength = ", LENGTH";
			} else {
				fieldLength = ", " + fieldLength.replaceAll(",", ", ");
			}
		}
		String fieldComment = (String) m.get("comment");
		String superClassName = superPackageName + "." + superSimpleClassName;
		String isDataformsField = (String) m.get("isDataformsField");
		if (!"1".equals(isDataformsField)) {
			fsrc = fsrc.replaceAll("\\$\\{fieldPackageName\\}", fieldPackageName);
			fsrc = fsrc.replaceAll("\\$\\{superClassName\\}", superClassName);
			fsrc = fsrc.replaceAll("\\$\\{importList\\}", importList);
			fsrc = fsrc.replaceAll("\\$\\{constList\\}", constList);
			fsrc = fsrc.replaceAll("\\$\\{fieldClassSimpleName\\}", fieldClassSimpleName);
			fsrc = fsrc.replaceAll("\\$\\{superSimpleClassName\\}", superSimpleClassName);
			fsrc = fsrc.replaceAll("\\$\\{fieldComment\\}", fieldComment);
			fsrc = fsrc.replaceAll("\\$\\{fieldLength\\}", fieldLength);
			fsrc = fsrc.replaceAll("\\$\\{validators\\}", validators);
			logger.debug("fsrc=\n" + fsrc);
		} else {
			fsrc = null;
		}
		return fsrc;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		logger.debug("data=" + JSON.encode(data, true));
		this.writeTableJavaSource(data);
	}

	/**
	 * フィールドIDを取得します。
	 * @param m フィールドリスト。
	 * @return フィールドID。
	 */
	private String getFieldId(final Map<String, Object> m) {
		String fieldId = (String) m.get("fieldId");
		if (StringUtil.isBlank(fieldId)) {
			String fclass = (String) m.get("fieldClassName");
			StringBuilder fsb = new StringBuilder(fclass.replaceAll("Field$", ""));
			char c = fsb.charAt(0);
			fsb.setCharAt(0, Character.toLowerCase(c));
			fieldId = fsb.toString();
		}
		return fieldId;
	}


	/**
	 * フィールドIdの定数を展開します。
	 * @param list フィールドリスト。
	 * @return フィールドIDの定数値。
	 */
	private String generateFieldIdConstant(final List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m: list) {
			String fieldId = getFieldId(m);
			String comment = (String) m.get("comment");
			sb.append("\t\t/** " + comment + "のフィールドID。 */\n");
			sb.append("\t\tpublic static final String ID_");
			sb.append(StringUtil.camelToUpperCaseSnake(fieldId));
			sb.append(" = \"");
			sb.append(fieldId);
			sb.append("\";\n");
		}
		return sb.toString();
	}

	/**
	 * フィールドに対応する値の型を返します。
	 * @param field フィールド。
	 * @return 値の型。
	 */
	private Class<?> getFieldValueType(final Class<?> field) {
		logger.debug("field class=" + field.getName());
		Class<?> ret = Object.class;
		if (FileObjectField.class.isAssignableFrom(field)) {
			ret = FileObject.class;
		} else if (ImageField.class.isAssignableFrom(field)) {
			ret = ImageData.class;
		} else if (VideoField.class.isAssignableFrom(field)) {
			ret = VideoData.class;
		} else 	if (SqlVarchar.class.isAssignableFrom(field)
			|| SqlChar.class.isAssignableFrom(field)
			|| CharField.class.isAssignableFrom(field)
			|| VarcharField.class.isAssignableFrom(field)
			|| ClobField.class.isAssignableFrom(field)
			|| CharSingleSelectField.class.isAssignableFrom(field)) {
			ret = String.class;
		} else if (DateField.class.isAssignableFrom(field)) {
			ret = java.sql.Date.class;
		} else if (TimeField.class.isAssignableFrom(field)) {
			ret = java.sql.Time.class;
		} else if (TimestampField.class.isAssignableFrom(field)) {
			ret = java.sql.Timestamp.class;
		} else if (SmallintField.class.isAssignableFrom(field)) {
			ret = Short.class;
		} else if (IntegerField.class.isAssignableFrom(field)) {
			ret = Integer.class;
		} else if (BigintField.class.isAssignableFrom(field)) {
			ret = Long.class;
		} else if (NumericField.class.isAssignableFrom(field)) {
			ret = BigDecimal.class;
		} else if (MultiSelectField.class.isAssignableFrom(field)) {
			ret = List.class;

		} else if (SqlBigint.class.isAssignableFrom(field)) { //
			ret = Long.class;
		} else if (SqlInteger.class.isAssignableFrom(field)) {
			ret = Integer.class;
		} else if (SqlSmallint.class.isAssignableFrom(field)) {
			ret = Short.class;
		} else if (SqlDouble.class.isAssignableFrom(field)) {
			ret = Double.class;
		} else if (SqlNumeric.class.isAssignableFrom(field)) {
			ret = BigDecimal.class;
		} else if (SqlDate.class.isAssignableFrom(field)) {
			ret = java.sql.Date.class;
		} else if (SqlTime.class.isAssignableFrom(field)) {
			ret = java.sql.Time.class;
		} else if (SqlTimestamp.class.isAssignableFrom(field)) {
			ret = java.sql.Timestamp.class;
		}
		return ret;
	}

	/**
	 * フィールドIdの定数を展開します。
	 * @param list フィールドリスト。
	 * @return フィールドIDの定数値。
	 * @throws Exception 例外。
	 */
	private String generateFieldValueGetterSetter(final List<Map<String, Object>> list) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m: list) {
/*			String superPackageName = (String) m.get("superPackageName");
			String superSimpleClassName = (String) m.get("superSimpleClassName");
			Class<?> cls = Class.forName(superPackageName + "." + superSimpleClassName);*/

			String packageName = (String) m.get("packageName");
			String fieldClassName = (String) m.get("fieldClassName");
			Class<?> cls = Class.forName(packageName + "." + fieldClassName);
			Class<?> valueType = this.getFieldValueType(cls);
			logger.debug(packageName + ":" + valueType);

			String fieldId = getFieldId(m);
			String uFieldId = StringUtil.firstLetterToUpperCase(fieldId);
			String comment = (String) m.get("comment");
			sb.append("\t\t/**\n");
			sb.append("\t\t * " + comment + "を取得します。\n");
			sb.append("\t\t * @return " + comment + "。\n");
			sb.append("\t\t */\n");
			sb.append("\t\tpublic " + valueType.getName() + " get" + uFieldId+ "() {\n");
			sb.append("\t\t\treturn (" + valueType.getName() + ") this.getMap().get(Entity.ID_" + StringUtil.camelToUpperCaseSnake(fieldId) + ");\n");
			sb.append("\t\t}\n\n");

			sb.append("\t\t/**\n");
			sb.append("\t\t * " + comment + "を設定します。\n");
			sb.append("\t\t * @param " + fieldId + " " + comment + "。\n");
			sb.append("\t\t */\n");
			sb.append("\t\tpublic void set" + uFieldId+ "(final " + valueType.getName() + " " + fieldId + ") {\n");
			sb.append("\t\t\tthis.getMap().put(Entity.ID_" + StringUtil.camelToUpperCaseSnake(fieldId) + ", " + fieldId + ");\n");
			sb.append("\t\t}\n\n");
}
		return sb.toString();
	}


	/**
	 * フィールドIdの定数を展開します。
	 * @param list フィールドリスト。
	 * @return フィールドIDの定数値。
	 */
	private String generateFieldGetter(final List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m: list) {
			String fieldId = getFieldId(m);
			String uFieldId = StringUtil.firstLetterToUpperCase(fieldId);
			String fieldClassSimpleName = (String) m.get("fieldClassName");
			String comment = (String) m.get("comment");
			sb.append("\t/**\n");
			sb.append("\t * " + comment + "フィールドを取得します。\n");
			sb.append("\t * @return " + comment + "フィールド。\n");
			sb.append("\t */\n");
			sb.append("\tpublic " + fieldClassSimpleName + " get" + uFieldId + "Field() {\n");
			sb.append("\t\treturn (" + fieldClassSimpleName + ") this.getField(Entity.ID_" + StringUtil.camelToUpperCaseSnake(fieldId) + ");\n");
			sb.append("\t}\n\n");
		}
		return sb.toString();
	}



	/**
	 * Table.javaのソースを作成します。
	 * @param data POSTされたデータ。
	 * @throws Exception 例外。
	 */
	public void writeTableJavaSource(final Map<String, Object> data) throws Exception {
		String tsrc = this.getTemplate("template/Table.java.template");
		String tableOverwriteMode = (String) data.get("overwriteMode");
		String packageName = (String) data.get("packageName");
		String tableClassName = (String) data.get("tableClassName");
		String autoIncrementId = (String) data.get("autoIncrementId");
		String tableComment = (String) data.get("tableComment");
		tsrc = tsrc.replaceAll("\\$\\{packageName\\}", packageName);
		tsrc = tsrc.replaceAll("\\$\\{tableComment\\}", tableComment);
		tsrc = tsrc.replaceAll("\\$\\{TableClassShortName\\}", tableClassName);
		StringBuilder implist = new StringBuilder();
		StringBuilder constructor = new StringBuilder();
		if ("1".equals(autoIncrementId)) {
			constructor.append("\t\tthis.setAutoIncrementId(true);\n");
		}
		constructor.append("\t\tthis.setComment(\"" + tableComment + "\");\n");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> fieldList = (List<Map<String, Object>>) data.get("fieldList");
		Map<String, String> srcmap = new HashMap<String, String>();
		Set<String> fieldSet = new HashSet<String>();
		for (Map<String, Object> m: fieldList) {
			String isDataformsField = (String) m.get("isDataformsField");
			if (!"1".equals(isDataformsField)) {
				String overwriteMode = (String) m.get("overwriteMode");
				if (!OverwriteModeField.SKIP.equals(overwriteMode)) {
					String fldsrc = this.generateFieldClass(m);
					String fieldPackageName = (String) m.get("packageName");
					String fieldClassSimpleName = (String) m.get("fieldClassName");
					srcmap.put(fieldPackageName + "." + fieldClassSimpleName, fldsrc);
				}
			}
			String fpackage = (String) m.get("packageName");
			String fclass = (String) m.get("fieldClassName");
			String pkFlag = (String) m.get("pkFlag");
			String fieldId = (String) m.get("fieldId");
			String fieldLength = (String) m.get("fieldLength");
			if (!fieldSet.contains(fpackage + "." + fclass)) {
				implist.append("import ");
				implist.append(fpackage);
				implist.append(".");
				implist.append(fclass);
				implist.append(";\n");
				fieldSet.add(fpackage + "." + fclass);
			}
			if ("1".equals(pkFlag)) {
				constructor.append("\t\tthis.addPkField(new ");
			} else {
				constructor.append("\t\tthis.addField(new ");
			}
			constructor.append(fclass);
			constructor.append("(");
			if (!StringUtil.isBlank(fieldId)) {
				constructor.append("\"" + fieldId + "\"");
			}
			if ("1".equals(isDataformsField)) {
				if (!StringUtil.isBlank(fieldLength)) {
					constructor.append(", ");
					constructor.append(fieldLength);
				}
			}
			String comment = (String) m.get("comment");
			constructor.append(")); //" + comment + "\n");
		}
		tsrc = tsrc.replaceAll("\\$\\{importList\\}", implist.toString());
		tsrc = tsrc.replaceAll("\\$\\{constructor\\}", constructor.toString());
		tsrc = tsrc.replaceAll("\\$\\{idConstants\\}", this.generateFieldIdConstant(fieldList));
		tsrc = tsrc.replaceAll("\\$\\{valueGetterSetter\\}", this.generateFieldValueGetterSetter(fieldList));
		tsrc = tsrc.replaceAll("\\$\\{fieldGetter\\}", this.generateFieldGetter(fieldList));
		logger.debug("tsrc=\n" + tsrc);
		if (!OverwriteModeField.SKIP.equals(tableOverwriteMode)) {
			srcmap.put(packageName + "." + tableClassName, tsrc);
		}
		String rclass = packageName + "." + tableClassName + "Relation";

		String path = (String) data.get("javaSourcePath");
		File relationFile = new File(path + "/" + rclass.replaceAll("\\.", "/") + ".java");
		if (!relationFile.exists()) {
			srcmap.put(rclass, this.generateRelationJavaSource(data));
		}
		logger.debug("srcmap=" + JSON.encode(srcmap));
		this.writeJavaSource(path, srcmap);
	}


	/**
	 * Relation.javaのソースを作成します。
	 * @param data データ。
	 * @return Relation.javaのソース。
	 * @throws Exception 例外。
	 */
	private String generateRelationJavaSource(final Map<String, Object> data) throws Exception {
		String rsrc = this.getTemplate("template/Relation.java.template");
		String packageName = (String) data.get("packageName");
		String tableClassName = (String) data.get("tableClassName");
		rsrc = rsrc.replaceAll("\\$\\{packageName\\}", packageName);
		rsrc = rsrc.replaceAll("\\$\\{TableClassShortName\\}", tableClassName);
		return rsrc;
	}


	/**
	 * javaソース出力。
	 * @param path javaソースパス。
	 * @param srcmap ソースのマップ情報。
	 * @throws Exception 例外。
	 */
	private void writeJavaSource(final String path, final Map<String, String> srcmap) throws Exception {
		for (String key: srcmap.keySet()) {
			String srcPath = path + "/" + key.replaceAll("\\.", "/") + ".java";
			FileUtil.writeTextFileWithBackup(srcPath, srcmap.get(key), DataFormsServlet.getEncoding());
		}
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {

	}

	@Override
	protected String getSavedMessage(final Map<String, Object> data) {
		return MessagesUtil.getMessage(this.getPage(), "message.javasourcecreated");
	}

	/**
	 * テーブル定義書を作成します。
	 * @param param パラメータ。
	 * @return テーブル定義書Excelイメージ。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response print(final Map<String, Object> param) throws Exception {
		this.methodStartLog(logger, param);
		Response ret = null;
		File template = TableReport.makeTemplate(this);
		try {
			logger.debug("template path=" + template.getAbsolutePath());
			TableReport rep = new TableReport(template.getAbsolutePath(), 0);
			rep.removeSheet(0);
			rep.setSheetName(0, ((String) param.get("tableClassName")));
			rep.setSystemHeader(MessagesUtil.getMessage(this.getPage(), "message.systemname"));
			Map<String, Object> spec = rep.getTableSpec(param, new Dao(this));
			if (spec != null) {
				ret = new BinaryResponse(rep.print(spec), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ((String) param.get("tableClassName")) +".xlsx");
			} else {
				ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.classnotfound"));
			}
		} finally {
			template.delete();
		}
		this.methodFinishLog(logger, ret);
		return ret;
	}

}
