package dataforms.devtool.field.common;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dataforms.field.base.Field;
import dataforms.field.sqltype.VarcharField;
import dataforms.util.ClassFinder;
import dataforms.util.StringUtil;

/**
 * パッケージ名を除いたクラス名フィールドクラス。
 *
 */
public class SimpleClassNameField extends VarcharField {

	/**
	 * フィールドコメント。
	 *
	 */
	private static final String COMMENT = "単純クラス名";
	/**
	 * 項目名。
	 */
	private static final int LENGTH = 256;

	/**
	 * autocompleで検索する際の基本クラス。
	 */
	private Class<?> baseClass = null;

	/**
	 * パッケージ名フィールドID。
	 */
	private String packageNameFieldId = "packageName";

	/**
	 * 例外パターンのリスト。
	 */
	private List<String> exceptionPatternList = new ArrayList<String>();


	/**
	 * コンストラクタ。
	 */
	public SimpleClassNameField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
		this.setAutocomplete(true);
		this.setAjaxParameter(AjaxParameter.FORM);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SimpleClassNameField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
		this.setAutocomplete(true);
		this.setAjaxParameter(AjaxParameter.FORM);
	}

	/**
	 * autocomplete検索時のベースクラスを取得します。
	 * @return autocomplete検索時のベースクラス。
	 */
	public Class<?> getBaseClass() {
		return baseClass;
	}

	/**
	 * autocomplete検索時のベースクラスを設定します。
	 * @param baseClass autocomplete検索時のベースクラス。
	 * @return 設定したフィールド。
	 */
	public Field<?> setBaseClass(final Class<?> baseClass) {
		this.baseClass = baseClass;
		return this;
	}



	/**
	 * パッケージ名フィールドのIDを取得します。
	 * @return パッケージ名フィールドのID。
	 */
	public String getPackageNameFieldId() {
		return packageNameFieldId;
	}

	/**
	 * パッケージ名フィールドIDを設定します。
	 * @param packageNameFieldId パッケージ名フィールドID。
	 * @return 設定されたフィールド。
	 */
	public SimpleClassNameField setPackageNameFieldId(final String packageNameFieldId) {
		this.packageNameFieldId = packageNameFieldId;
		return this;
	}

	/**
	 * 例外パターンを追加します。
	 * <pre>
	 * 指定された例外パターンを含むクラス名はautocompleteリストから除外されます。
	 * </pre>
	 * @param pattern 例外パターン(正規表現文字列)。
	 */
	protected void addExceptionPattern(final String pattern) {
		this.exceptionPatternList.add(pattern);
	}

	/**
	 * 例外パターンのチェック。
	 * @param classname クラス名。
	 * @return クラス名に例外パターンを含む場合true。
	 * @throws Exception 例外。
	 */
	private boolean isExcetionClass(final String classname) throws Exception {
		boolean ret = false;
		for (String regex: this.exceptionPatternList) {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(classname);
			if (m.find()) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	@Override
	protected List<Map<String, Object>> queryAutocompleteSourceList(final 	Map<String, Object> data) throws Exception {
		String id = (String) data.get("currentFieldId");
		String rowid = this.getHtmlTableRowId(id);
		String packageName = (String) data.get(StringUtil.isBlank(rowid) ? this.getPackageNameFieldId() : rowid + "." + this.getPackageNameFieldId());
		String simpleClassName = (String) data.get(id);
		ClassFinder finder = new ClassFinder();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (StringUtil.isBlank(packageName)) {
			return result;
		}
		List<Class<?>> list = finder.findClasses(packageName, this.baseClass);
		for (Class<?> c: list) {
			if (this.isExcetionClass(c.getName())) {
				continue;
			}
			String name = c.getSimpleName();
			if (name.toLowerCase().indexOf(simpleClassName.toLowerCase()) >= 0) {
				if (!Modifier.isAbstract(c.getModifiers())) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("className", c.getSimpleName());
					m.put(this.getPackageNameFieldId(), c.getPackage().getName());
					result.add(m);
				}
			}
		}
		return this.convertToAutocompleteList(rowid, result, "className", "className", this.getPackageNameFieldId());
	}
}

