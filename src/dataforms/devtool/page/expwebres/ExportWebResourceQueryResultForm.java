package dataforms.devtool.page.expwebres;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.JsonResponse;
import dataforms.controller.QueryResultForm;
import dataforms.devtool.field.common.WebSourcePathField;
import dataforms.devtool.page.base.DeveloperPage;
import dataforms.field.base.FieldList;
import dataforms.field.common.FlagField;
import dataforms.field.common.MultiSelectField;
import dataforms.field.common.PresenceField;
import dataforms.field.common.RowNoField;
import dataforms.field.sqltype.VarcharField;
import dataforms.htmltable.HtmlTable;
import dataforms.util.ClassFinder;
import dataforms.util.FileUtil;
import dataforms.util.StringUtil;

/**
 *
 * Webリソースの問い合わせ結果フォームクラス。
 *
 */
public class ExportWebResourceQueryResultForm extends QueryResultForm {

	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(ExportWebResourceQueryResultForm.class);

	/**
	 * コンストラクタ。
	 */
	public ExportWebResourceQueryResultForm() {
		this.addField(new WebSourcePathField()).setReadonly(true);
		this.addField(new FlagField("selAll"));
		this.addField(new MultiSelectField<String>("sel", 1024));
		HtmlTable tbl = new HtmlTable(
				"queryResult"
				, new RowNoField()
				, new VarcharField("sel", 1024)
				, new VarcharField("fileName", 1024)
				, new PresenceField("existFlag"));
		tbl.getFieldList().get("sel").setComment("選択");
		tbl.getFieldList().get("rowNo").setComment("No.").setReadonly(true);
		tbl.getFieldList().get("fileName").setComment("ファイル名").setReadonly(true);
		tbl.getFieldList().get("existFlag").setComment("ファイル有無").setReadonly(true);
		this.addHtmlTable(tbl);
	}

	/**
	 * ファイルが条件にマッチするかどうかを確認します。
	 * @param filename ファイル名。
	 * @param pattern ファイル名のパターン。
	 * @param regexp 正規表現フラグ。
	 * @return 条件にマッチした場合true.
	 */
	private boolean isMatchFile(final String filename, final String pattern, final boolean regexp) {
		if (StringUtil.isBlank(pattern)) {
			return true;
		} else {
			if (regexp) {
				Pattern p = Pattern.compile(pattern);
				Matcher m = p.matcher(filename);
				return m.find();
			} else {
				return (filename.indexOf(pattern) > 0);
			}
		}
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("webSourcePath", DeveloperPage.getWebSourcePath());
		List<Map<String, Object>> queryResult = new ArrayList<Map<String, Object>>();
		String fileNamePattern = (String) data.get("fileName");
		String regexp = (String) data.get("regexpFlag");
		ClassFinder cf = new ClassFinder();
		List<String> flist = cf.findResource("/META-INF/resources");
		int rowNo = 1;
		for (String res: flist) {
			String respath = res.replaceAll("/META-INF/resources", "");
			if (respath.indexOf("/doc/") == 0) {
				continue;
			}
			if (this.isMatchFile(respath, fileNamePattern, "1".equals(regexp))) {
				log.debug("res=" + respath);
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("rowNo", Integer.valueOf(rowNo));
				m.put("sel", respath);
				m.put("fileName", respath);
				File f = new File(DeveloperPage.getWebSourcePath() + respath);
				m.put("existFlag", (f.exists() ? "1" : "0"));
				queryResult.add(m);
				rowNo++;
			}
		}
		ret.put("queryResult", queryResult);
		return ret;
	}

	/**
	 * Webリソースのエクスポートを行います。
	 * @param params パラメータ。
	 * @return 実行結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse exportWebResource(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log,  params);
		Map<String, Object> data = this.convertToServerData(params);
		String webSourcePath = (String) data.get("webSourcePath");
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) data.get("sel");
		for (String r: list) {
			log.debug("r=" + r);
			this.exportResourceFile(webSourcePath, r);
		}
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, "エクスポートしました。");
		this.methodFinishLog(log, ret);
		return ret;
	}



	/**
	 * リソースファイルのエクスポート。
	 * @param webResourcePath Webリソースのパス。
	 * @param path リソースファイルのパス。
	 * @throws Exception 例外。
	 */
	private void exportResourceFile(final String webResourcePath, final String path) throws Exception {
		byte[] res = this.getBinaryWebResource(path);
		File file = new File(webResourcePath + "/" + path);
		log.debug("outpath=" + file.getAbsolutePath());
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileUtil.backup(webResourcePath + "/" + path);
		FileOutputStream os = new FileOutputStream(webResourcePath + "/" + path);
		try {
			FileUtil.writeOutputStream(res, os);
		} finally {
			os.close();
		}
	}

}

