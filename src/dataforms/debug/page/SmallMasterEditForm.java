package dataforms.debug.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.controller.MultiRecordEditForm;
import dataforms.debug.dao.SmallMasterDao;
import dataforms.debug.dao.SmallMasterTable;
import dataforms.field.base.FieldList;
import dataforms.htmltable.EditableHtmlTable;

/**
 * 小規模マスタの編集フォーム。
 *
 */
public class SmallMasterEditForm extends MultiRecordEditForm {

	/**
	 * コンストラクタ。
	 */
	public SmallMasterEditForm() {
		SmallMasterTable table = new SmallMasterTable();
		EditableHtmlTable htmlTable = new EditableHtmlTable(MultiRecordEditForm.ID_LIST, table.getFieldList());
		this.addHtmlTable(htmlTable);
	}

	
	
	@Override
	public void init() throws Exception {
		super.init();
		this.setFormDataMap(this.queryData(new HashMap<String, Object>()));
	}


	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		SmallMasterDao dao = new SmallMasterDao(this);
		List<Map<String, Object>> list = dao.query(new HashMap<String, Object>(), new FieldList());
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(MultiRecordEditForm.ID_LIST, list);
		return ret;
	}

	
	@Override
	protected void saveTable(final Map<String, Object> data) throws Exception {
		SmallMasterTable table = new SmallMasterTable();
		SmallMasterDao dao = new SmallMasterDao(this);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(MultiRecordEditForm.ID_LIST);
		this.setUserInfo(list);
		dao.saveTable(table, list);
		
	}
}
