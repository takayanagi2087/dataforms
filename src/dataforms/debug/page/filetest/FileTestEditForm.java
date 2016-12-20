package dataforms.debug.page.filetest;

import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.controller.EditForm;
import dataforms.debug.dao.filetest.FileFieldTestDao;
import dataforms.debug.dao.filetest.FileFieldTestTable;
import dataforms.field.common.WebResourceImageField;
import dataforms.util.StringUtil;
import net.arnx.jsonic.JSON;

/**
 * 編集フォームクラス。
 */
public class FileTestEditForm extends EditForm {
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(FileTestEditForm.class);
	/**
	 * コンストラクタ。
	 */
	public FileTestEditForm() {
		FileFieldTestTable tbl = new FileFieldTestTable();
		this.addTableFields(tbl);
		this.addField(new WebResourceImageField("menuImage", "/frame/default/image/menu.png"));
	}

	@Override
	public void init() throws Exception {
		super.init();
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		FileFieldTestDao dao = new FileFieldTestDao(this);
		Map<String, Object> ret = dao.queryEditData(data);
		log.debug("formData=" + JSON.encode(ret, true));
		return ret;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		Long id = (Long) data.get("recordId");
		return (!StringUtil.isBlank(id));
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		log.debug("insert data=" + JSON.encode(data, true));
		FileFieldTestDao dao = new FileFieldTestDao(this);
		dao.executeInsert(new FileFieldTestTable(), data);
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		log.debug("updatet data=" + JSON.encode(data, true));
		FileFieldTestDao dao = new FileFieldTestDao(this);
		dao.executeUpdate(new FileFieldTestTable(), data);
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		FileFieldTestDao dao = new FileFieldTestDao(this);
		dao.executeDelete(new FileFieldTestTable(), data);
	}
}
