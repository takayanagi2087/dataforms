package dataforms.debug.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.controller.MultiRecordEditForm;
import dataforms.controller.Page;
import dataforms.controller.QueryForm;
import dataforms.debug.dao.SmallMasterDao;
import dataforms.debug.dao.SmallMasterTable;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.htmltable.EditableHtmlTable;
import net.arnx.jsonic.JSON;

/**
 * 小規模マスタの編集フォーム。
 *
 */
public class SmallMasterEditForm extends MultiRecordEditForm {
	
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(SmallMasterEditForm.class);

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
		// this.setFormDataMap(this.queryData(new HashMap<String, Object>()));
	}


	@Override
	protected Map<String, Object> queryDataByQueryFormCondition(final Map<String, Object> data) throws Exception {
		SmallMasterDao dao = new SmallMasterDao(this);
		QueryForm qf = (QueryForm) this.getPage().getComponent(Page.ID_QUERY_FORM);
		FieldList flist = qf.getFieldList();
		for (Field<?> f: flist) {
			logger.debug("f.id=" + f.getId());
		}
		logger.debug("data=" + JSON.encode(data));
		List<Map<String, Object>> list = dao.query(data, flist);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(MultiRecordEditForm.ID_LIST, list);
		return ret;
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
