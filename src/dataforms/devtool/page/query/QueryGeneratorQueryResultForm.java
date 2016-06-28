package dataforms.devtool.page.query;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.dao.Query;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.QueryClassNameField;
import dataforms.field.base.FieldList;
import dataforms.field.common.RowNoField;
import dataforms.htmltable.HtmlTable;
import dataforms.util.ClassFinder;
import dataforms.util.StringUtil;


/**
 * 問い合わせ結果フォームクラス。
 */
public class QueryGeneratorQueryResultForm extends QueryResultForm {
	/**
	 * コンストラクタ。
	 */
	public QueryGeneratorQueryResultForm() {
		HtmlTable htmltbl = new HtmlTable(Page.ID_QUERY_RESULT
				, (new RowNoField()).setSpanField(true)
				, (new PackageNameField()).setHidden(true)
				, (new QueryClassNameField()).setHidden(true)
				, (new ClassNameField("fullClassName")).setSpanField(true).setComment("問合せクラス名")
			);
			this.addHtmlTable(htmltbl);
			this.addPkField(htmltbl.getFieldList().get("packageName"));
			this.addPkField(htmltbl.getFieldList().get("queryClassName"));
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		String packageName = (String) data.get("packageName");
		String className = (String) data.get("queryClassName");
		ClassFinder finder = new ClassFinder();
		List<Class<?>> queryList = finder.findClasses(packageName, Query.class);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int no = 1;
		for (Class<?> querycls : queryList) {
			Map<String, Object> m = new HashMap<String, Object>();
			if (Page.class.getName().equals(querycls.getName())) {
				continue;
			}
			if (!StringUtil.isBlank(className)) {
				if (querycls.getName().indexOf(className) < 0) {
					continue;
				}
			}
			if ((querycls.getModifiers() & Modifier.ABSTRACT) != 0) {
				continue;
			}
			if (querycls.getName().indexOf("$") > 0) {
				// インナークラスは除外。
				continue;
			}
			try {
				querycls.getConstructor();
			} catch (NoSuchMethodException e) {
				// デフォルトコンストラクタが存在しない場合はヒットさせない。
				continue;
			}
			m.put("rowNo", Integer.valueOf(no));
			m.put("packageName", querycls.getPackage().getName());
			m.put("queryClassName", querycls.getSimpleName());
			m.put("fullClassName", querycls.getName());
			result.add(m);
			no++;
		}
		ret.put("queryResult", result); // とりあえず空のリストを返信。 
		return ret;
	}

	@Override
	protected void deleteData(final Map<String, Object> data) throws Exception {
		// 何もしない。
	}
}
