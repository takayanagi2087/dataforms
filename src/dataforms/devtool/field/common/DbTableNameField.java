package dataforms.devtool.field.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.dao.Dao;
import dataforms.field.base.TextField;

/**
 * DB中のテーブルを検索するフィールド。
 *
 */
public class DbTableNameField extends TextField {

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(DbTableNameField.class);

	/**
	 * コンストラクタ。
	 */
	public DbTableNameField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールド。
	 */
	public DbTableNameField(final String id) {
		super(id);
	}


	@Override
	protected List<Map<String, Object>> queryAutocompleteSourceList(Map<String, Object> data) throws Exception {
		String id = (String) data.get("currentFieldId");
		String rowid = this.getHtmlTableRowId(id);
		String colid = this.getHtmlTableColumnId(id);
		String tblname = (String) data.get(id);
		Dao dao = new Dao(this);
		List<Map<String, Object>> tableList = dao.queryTableInfo();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: tableList) {
			Dao.TableInfoEntity e = new Dao.TableInfoEntity(m);
			String name = e.getTableName();
			logger.debug("name=" + name);
			if (name.toLowerCase().indexOf(tblname.toLowerCase()) >= 0) {
				Map<String, Object> rm = new HashMap<String, Object>();
				rm.put(colid, name);
				rm.put("label", name + ":" + (e.getRemarks() == null ? "" : e.getRemarks()));
//				rm.put("tableComment", e.getRemarks());
				result.add(rm);
			}
		}
		return this.convertToAutocompleteList(rowid, result, colid, "label"/*, "tableComment"*/);
	}
}
