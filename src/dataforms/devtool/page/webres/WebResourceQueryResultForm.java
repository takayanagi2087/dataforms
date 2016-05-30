package dataforms.devtool.page.webres;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import dataforms.controller.Dialog;
import dataforms.controller.Form;
import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.controller.WebComponent;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.JavascriptClassField;
import dataforms.devtool.field.common.WebComponentTypeField;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.common.MultiSelectField;
import dataforms.field.common.PresenceField;
import dataforms.field.common.RowNoField;
import dataforms.htmltable.HtmlTable;
import dataforms.util.StringUtil;
import dataforms.validator.FieldValidator;

/**
 * 問い合わせ結果フォームクラス。
 */
public class WebResourceQueryResultForm extends QueryResultForm {

	/**
	 * Logger.
	 */
//s	private static Logger log = Logger.getLogger(WebResourceQueryResultForm.class.getName());


	/**
	 * コンストラクタ。
	 */
	public WebResourceQueryResultForm() {
		this.addField(new ClassNameField());
		this.addField(new MultiSelectField<String>("checkedClass", 256));
		HtmlTable htmltbl = new HtmlTable(Page.ID_QUERY_RESULT
			, new RowNoField()
			, new ClassNameField()
			, new WebComponentTypeField()
			, new PresenceField("htmlStatus")
			, new PresenceField("javascriptStatus")
			, new JavascriptClassField()
		);
		htmltbl.getFieldList().get("webComponentType").setReadonly(true);
		htmltbl.getFieldList().get("htmlStatus").setReadonly(true);
		htmltbl.getFieldList().get("javascriptStatus").setReadonly(true);
		htmltbl.getFieldList().get("javascriptClass").setReadonly(true);
		this.addHtmlTable(htmltbl);
	}

	/**
	 * 同一のクラス名を場外するためのSet.
	 */
	private HashSet<String> classNameSet = null;

	/**
	 * 保持するコンポーネントの一覧を取得します。
	 * @param result コンポーネントの一覧。
	 * @param comp コンポーネント。
	 * @throws Exception 例外。
	 */
	private void getComponentList(final List<Map<String, Object>> result, final WebComponent comp) throws Exception {
		Map<String, WebComponent> wcmap = comp.getComponentMap();
		for (String key: wcmap.keySet()) {
			WebComponent c = wcmap.get(key);
			if (!this.classNameSet.contains(c.getClass().getName())) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("checkedClass", c.getClass().getName());
				m.put("className", c.getClass().getName());
				result.add(m);
				this.getComponentList(result, c);
				this.classNameSet.add(c.getClass().getName());
			}
		}
	}


	/**
	 * コンポーネントタイプのチェックを行います。
	 * @param cls クラス。
	 * @param tlist コンポーネントタイプリスト。
	 * @return マッチする場合true。
	 * @throws Exception 例外。
	 */
	private boolean checkTypeCondition(final Class<?> cls, final List<String> tlist) throws Exception {
		boolean ret = false;
		for (String t: tlist) {
			Class<?> baseclass = WebComponent.class;
			if ("page".equals(t)) {
				baseclass = Page.class;
			} else if ("dialog".equals(t)) {
				baseclass = Dialog.class;
			} else if ("form".equals(t)) {
				baseclass = Form.class;
			} else if ("table".equals(t)) {
				baseclass = HtmlTable.class;
			} else if ("field".equals(t)) {
				baseclass = Field.class;
			} else if ("validator".equals(t)) {
				baseclass = FieldValidator.class;
			}
			if (baseclass.isAssignableFrom(cls)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * コンポーネントタイプ名称を取得します。
	 * @param c コンポーネントクラス。
	 * @return コンポーネントタイプ名称。
	 * @throws Exception 例外。
	 */
	private String getComponentTypeName(final Class<?> c) throws Exception {
		String type = null;
		if (Page.class.isAssignableFrom(c)) {
			type = "page";
		} else if (Dialog.class.isAssignableFrom(c)) {
			type = "dialog";
		} else if (Form.class.isAssignableFrom(c)) {
			type = "form";
		} else if (HtmlTable.class.isAssignableFrom(c)) {
			type = "table";
		} else if (Field.class.isAssignableFrom(c)) {
			type = "field";
		} else if (FieldValidator.class.isAssignableFrom(c)) {
			type = "validator";
		}
		return type;
	}

	/**
	 * HTMLの状態を取得します。
	 * @param c クラス。
	 * @param webResourcePath Webリソース保存先フォルダ。
	 * @return 状態。
	 * @throws Exception 例外。
	 */
	private String getHtmlStatus(final Class<?> c, final String webResourcePath) throws Exception {
		if (Page.class.isAssignableFrom(c) || Dialog.class.isAssignableFrom(c)) {
			String respath =  "/" + this.getWebResourcePath(c) + ".html";
			String ret = this.getWebResource(respath);
			File resfile = new File(webResourcePath + respath);
			if ((!resfile.exists()) && StringUtil.isBlank(ret)) {
				return "0";
			} else {
				return "1";
			}
		} else {
			return null;
		}
	}

	/**
	 * Javascriptの状態を取得します。
	 * @param c クラス。
	 * @return javasciptの状態。
	 * @param webResourcePath Webリソース保存先フォルダ。
	 * @throws Exception 例外。
	 */
	private String getJavascriptStatus(final Class<?> c, final String webResourcePath) throws Exception {
		String respath = "/" + this.getWebResourcePath(c) + ".js";
		File resfile = new File(webResourcePath + respath);
		String ret = this.getWebResource(respath);
		if ((!resfile.exists()) && StringUtil.isBlank(ret)) {
			return "0";
		} else {
			return "1";
		}
	}

	/**
	 * スクリプトが存在する親クラス名を取得します。
	 * @param c クラス。
	 * @return スクリプトが存在する親クラス名。
	 * @throws Exception 例外。
	 */
	private String getJavascriptClass(final Class<?> c) throws Exception {
		Class<?> sc = c;
		String classname = sc.getSimpleName();
		String respath = "/" + this.getWebResourcePath(sc) + ".js";
		String ret = this.getWebResource(respath);
		while (StringUtil.isBlank(ret)) {
			sc = sc.getSuperclass();
			respath = "/" + this.getWebResourcePath(sc) + ".js";
			ret = this.getWebResource(respath);
			classname = sc.getSimpleName();
		}
		return classname;
	}


	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		String webResourcePath = (String) data.get("webSourcePath");
		String className = (String) data.get("className");
		Map<String, Object> ret = new HashMap<String, Object>();
		String packageName = (String) data.get("packageName");
		String pageClassName = (String) data.get("pageClassName");
		String generatableOnly = (String) data.get("generatableOnly");

		@SuppressWarnings("unchecked")
		Class<? extends Page> cls = (Class<? extends Page>) Class.forName(packageName + "." + pageClassName);
		Page p = cls.newInstance();
		this.classNameSet = new HashSet<String>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> r = new HashMap<String, Object>();
		r.put("checkedClass", p.getClass().getName());
		r.put("className", p.getClass().getName());
		result.add(r);
		this.getComponentList(result, p);

		List<Map<String, Object>> queryResult = new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		List<String> tlist = (List<String>) data.get("webComponentTypeList");
		int no = 1;
		for (Map<String, Object> m: result) {
			String classname = (String) m.get("className");
			if ("dataforms.app.form.SideMenuForm".equals(classname)
				|| "dataforms.app.form.LoginInfoForm".equals(classname)
				|| "dataforms.app.field.user.LoginIdField".equals(classname)
				|| "dataforms.app.field.user.UserNameField".equals(classname)) {
				continue;
			}
			if (!StringUtil.isBlank(className)) {
				if (classname.indexOf(className) < 0) {
					continue;
				}
			}
			Class<?> c = Class.forName(classname);
			if (this.checkTypeCondition(c, tlist)) {
				String htmlStatus = this.getHtmlStatus(c, webResourcePath);
				String javascriptStatus = this.getJavascriptStatus(c, webResourcePath);
				if ("1".equals(generatableOnly)) {
					if (("1".equals(htmlStatus) || StringUtil.isBlank(htmlStatus)) && "1".equals(javascriptStatus)) {
						continue;
					}
				}
				m.put("rowNo", Integer.valueOf(no++));
				m.put("webComponentType", this.getComponentTypeName(c));
				m.put("htmlStatus", htmlStatus);
				m.put("javascriptStatus", javascriptStatus);
				m.put("javascriptClass", this.getJavascriptClass(c));
				queryResult.add(m);
			}
		}
		ret.put("queryResult", queryResult);
		return ret;
	}
}
