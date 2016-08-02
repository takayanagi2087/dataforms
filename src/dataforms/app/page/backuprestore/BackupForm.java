package dataforms.app.page.backuprestore;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.app.field.func.FunctionMultiSelectField;
import dataforms.controller.BinaryResponse;
import dataforms.controller.Form;
import dataforms.controller.JsonResponse;
import dataforms.controller.Response;
import dataforms.devtool.dao.db.TableManagerDao;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.FileUtil;
import dataforms.util.MessagesUtil;
import dataforms.validator.ValidationError;

/**
 * 問い合わせフォームクラス。
 */
public class BackupForm extends Form {
	
	
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(BackupForm.class);
	
	/**
	 * コンストラクタ。
	 */
	public BackupForm() {
		super(null);
		this.addField(new FunctionMultiSelectField());
	}

	@Override
	public void init() throws Exception {
		super.init();
		FunctionMultiSelectField fsel = (FunctionMultiSelectField) this.getFieldList().get("functionMultiSelect");
		List<Map<String, Object>> list = fsel.getOptionList();
		List<String> sel = new ArrayList<String>();
		for (Map<String, Object> m: list) {
			String v = (String) m.get("value");
			if ("/dataforms/app".equals(v) || "/dataforms/devtool".equals(v)) {
				continue;
			} else {
				sel.add(v);
			}
		}
		this.setFormData("functionMultiSelect", sel);
	}
	
	@Override
	protected List<ValidationError> validateForm(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = super.validateForm(data);
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) data.get("functionMultiSelect");
		if (list == null || list.size() == 0) {
			ret.add(new ValidationError("functionMultiSelect", MessagesUtil.getMessage(this.getPage(), "error.required")));
		}
		return ret;
	}
	
	
	/**
	 * データのバックアップを行います。
	 * @param p パラメータ。
	 * @return バックアップ結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response backup(final Map<String, Object> p) throws Exception {
		this.methodStartLog(log, p);
		Response resp = null;
		List<ValidationError> el = this.validate(p);
		if (el.size() == 0) {
			Map<String, Object> data = this.convertToServerData(p);
			log.debug("data=" + data);
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) data.get("functionMultiSelect");
			if (list.size() > 0) {
				TableManagerDao dao = new TableManagerDao(this);
				List<Map<String, Object>> tbllist = new ArrayList<Map<String, Object>>();
				for (String path: list) {
					String pkg = path.substring(1).replaceAll("/", ".");
					tbllist.addAll(dao.queryTableClass(pkg, ""));
				}
				File bkdir = new File(DataFormsServlet.getTempDir() + "/backup");
				if (!bkdir.exists()) {
					bkdir.mkdirs();
				}
				Path backup = FileUtil.createTempDirectory(bkdir.getAbsolutePath(), "backup");
				try {
					for (Map<String, Object> m: tbllist) {
						String bktbl = (String) m.get("className");
						dao.exportData(bktbl, backup.toString());
					}
					FileUtil.createZipFile(backup.toString() + ".zip", backup.toString());
					SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
					resp = new BinaryResponse(backup.toString() + ".zip", "application/zip", "backup" + fmt.format(new Date()) + ".zip");
					((BinaryResponse) resp).setTempFile(new File(backup.toString() + ".zip"));
				} finally {
					log.debug("delete:" + backup.toString());
					if (backup.toString().indexOf("backup") > 0) {
						FileUtil.deleteDirectory(backup.toString());
					}
				}
			}
		} else {
			resp = new JsonResponse(JsonResponse.INVALID, el);
		}
		this.methodFinishLog(log, resp);
		return resp;
	}
}
