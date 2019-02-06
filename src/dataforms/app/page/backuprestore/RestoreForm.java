package dataforms.app.page.backuprestore;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.Form;
import dataforms.controller.JsonResponse;
import dataforms.controller.Response;
import dataforms.devtool.dao.db.TableManagerDao;
import dataforms.field.common.FlagField;
import dataforms.field.common.FolderStoreFileField;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.FileUtil;
import dataforms.util.MessagesUtil;
import dataforms.validator.RequiredValidator;
import dataforms.validator.ValidationError;

/**
 * リストアーフォーム。
 *
 */
public class RestoreForm extends Form {

	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(RestoreForm.class);
	/**
	 * コンストラクタ。
	 */
	public RestoreForm() {
		super(null);
		this.addField(new FolderStoreFileField("backupFile")).addValidator(new RequiredValidator());
		this.addField(new FlagField("deleteDataFlag"));
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("deleteDataFlag", "1");
	}

	/**
	 * バックアップファイルを解凍します。
	 * @param fileItem バックアップファイル。
	 * @return 展開されたディレクトリのパス。
	 * @throws Exception 例外。
	 */
	public String unpackRestoreFile(final FileItem fileItem) throws Exception {
		InputStream is = fileItem.getInputStream();
		String ret = null;
		try {
			File bkdir = new File(DataFormsServlet.getTempDir() + "/restore");
			if (!bkdir.exists()) {
				bkdir.mkdirs();
			}
			Path backup = FileUtil.createTempDirectory(bkdir.getAbsolutePath(), "restore");
			FileUtil.unpackZipFile(is, backup.toString());
			ret = backup.toString();
		} finally {
			is.close();
		}
		return ret;
	}

	/**
	 * リストアを行います。
	 * @param p パラメータ。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response restore(final Map<String, Object> p) throws Exception {
		this.methodStartLog(log, p);
		Response resp = null;
		List<ValidationError> list = this.validate(p);
		if (list.size() == 0) {
			String deleteDataFlag = (String) p.get("deleteDataFlag");
			FileItem fi = (FileItem) p.get("backupFile");
			String path = this.unpackRestoreFile(fi);
			TableManagerDao dao = new TableManagerDao(this);
			dao.dropAllForeignKeys(); // 全外部キーの削除
			List<String> flist = FileUtil.getFileList(path);
			for (String fn: flist) {
				if (Pattern.matches(".*\\.data\\.json$", fn)) {
					log.debug("fn=" + fn);
					String classname = fn.substring(path.length() + 1).replaceAll("[\\\\/]", ".").replaceAll("\\.data\\.json$", "");
					log.debug("classname=" + classname);
					if ("1".equals(deleteDataFlag)) {
						dao.deleteTableData(classname);
					}
					dao.importData(classname, path);
				}
			}
			dao.createAllForeignKeys(); // 全外部キーの作成
			resp = new JsonResponse(JsonResponse.SUCCESS, MessagesUtil.getMessage(this.getPage(), "message.restored"));
		} else {
			resp = new JsonResponse(JsonResponse.INVALID, list);
		}
		this.methodFinishLog(log, resp);
		return resp;
	}

}
