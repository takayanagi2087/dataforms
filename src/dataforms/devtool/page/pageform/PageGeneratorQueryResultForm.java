package dataforms.devtool.page.pageform;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.PageClassNameField;
import dataforms.field.base.FieldList;
import dataforms.field.common.RowNoField;
import dataforms.htmltable.HtmlTable;
import dataforms.util.ClassFinder;
import dataforms.util.StringUtil;

/**
 * Pageクラス問い合わせ結果フォーム。
 *
 */
public class PageGeneratorQueryResultForm extends QueryResultForm {

	/**
	 * Log.
	 */
	private Logger log = Logger.getLogger(PageGeneratorQueryResultForm.class);

	/**
	 * コンストラクタ。
	 */
	public PageGeneratorQueryResultForm() {
		HtmlTable htmltbl = new HtmlTable(Page.ID_QUERY_RESULT
			, (new RowNoField()).setSpanField(true)
			, (new PackageNameField()).setHidden(true)
			, (new PageClassNameField()).setHidden(true)
			, (new ClassNameField("fullClassName")).setSpanField(true)
			, (new ClassNameField("tableClassName")).setSpanField(true)
		);
		this.addHtmlTable(htmltbl);
		this.addPkField(htmltbl.getFieldList().get("packageName"));
		this.addPkField(htmltbl.getFieldList().get("pageClassName"));
	}


	/**
	 * ページクラスの一覧を取得します。
	 * @param data パラメータ。
	 * @return クエリ結果。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryPageClass(final Map<String, Object> data) throws Exception {
		String packageName = (String) data.get("packageName");
		String classname = (String) data.get("className");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		ClassFinder finder = new ClassFinder();
		List<Class<?>> pageList = finder.findClasses(packageName, Page.class);
		int no = 1;
		for (Class<?> pagecls : pageList) {
			Map<String, Object> m = new HashMap<String, Object>();
			if (Page.class.getName().equals(pagecls.getName())) {
				continue;
			}
			if (!StringUtil.isBlank(classname)) {
				if (pagecls.getName().indexOf(classname) < 0) {
					continue;
				}
			}
			if ((pagecls.getModifiers() & Modifier.ABSTRACT) != 0) {
				continue;
			}
			m.put("rowNo", Integer.valueOf(no));
			m.put("packageName", pagecls.getPackage().getName());
			m.put("pageClassName", pagecls.getSimpleName());
			m.put("fullClassName", pagecls.getName());
			result.add(m);
			no++;
		}
		return result;
	}


	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {
		this.methodStartLog(log, data);
		List<Map<String, Object>> queryResult = this.queryPageClass(data);
		for (Map<String, Object> r: queryResult) {
			String className = (String) r.get("fullClassName");
			Class<?> cls = Class.forName(className);
			Page p = (Page) cls.newInstance();
			PageClassInfo pi = new PageClassInfo(p);
			r.put("tableClassName", pi.getTableClass());
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("queryResult", queryResult);
		this.methodFinishLog(log, result);
		return result;
	}

}
