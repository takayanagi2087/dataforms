package dataforms.debug.page.alltype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.BinaryResponse;
import dataforms.controller.EditForm;
import dataforms.controller.Response;
import dataforms.dao.file.ImageData;
import dataforms.debug.dao.alltype.AllTypeAttachFileTable;
import dataforms.debug.dao.alltype.AllTypeDao;
import dataforms.debug.dao.alltype.AllTypeTable;
import dataforms.debug.report.AlltypeExcelReport;
import dataforms.htmltable.EditableHtmlTable;
import dataforms.util.StringUtil;
import dataforms.validator.FileSizeValidator;
import dataforms.validator.RequiredValidator;
import net.arnx.jsonic.JSON;

/**
 * 全データタイプの入力テスト用フォームクラス。
 *
 */
public class AllTypeEditForm extends EditForm {
    /**
     * Logger.
     */
	private static Logger log = Logger.getLogger(AllTypeEditForm.class.getName());

	/**
	 * コンストラクタ。
	 */
	public AllTypeEditForm() {
		this.addTableFields(new AllTypeTable());
		this.getFieldList().get("radioField").addValidator(new RequiredValidator());
		this.getFieldList().get("checkboxField").addValidator(new RequiredValidator());

		this.getFieldList().get("uploadBlobData").addValidator(new FileSizeValidator(10 * 1024 * 1024));
		AllTypeAttachFileTable aft = new AllTypeAttachFileTable();
//		aft.getField("fileComment").setSortable(true, Field.SortOrder.DESC);
		EditableHtmlTable tbl = new EditableHtmlTable("attachFileTable", aft.getFieldList());
		tbl.setSortable(true);
		tbl.setFixedColumns(5);
//		tbl.setSortableSwitching(true);
		this.addHtmlTable(tbl);

		this.getFieldList().get("charField").addValidator(new RequiredValidator());
		
		
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("attachFileTable", new ArrayList<HashMap<String, Object>>());
		
//		EnumDao dao = new EnumDao(this);
//		log.debug("optionName=" + dao.getOptionName("userLevel", "admin", Locale.JAPAN.getLanguage()));
	}


	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		AllTypeDao dao = new AllTypeDao(this);
		AllTypeTable tbl = new AllTypeTable();
		Map<String, Object> ret = dao.getSelectedData(data, tbl.getPkFieldList());
		return ret;
	}

	@Override
	protected Map<String, Object> queryReferData(final Map<String, Object> data)
			throws Exception {
		AllTypeDao dao = new AllTypeDao(this);
		AllTypeTable tbl = new AllTypeTable();
		Map<String, Object> ret = dao.getReferData(data, tbl.getPkFieldList());
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * 主キーとなるrecordIdFieldが入力されていた場合、更新モードと判断します。
	 */
	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		Long userIdField = (Long) data.get("recordIdField");
		return (!StringUtil.isBlank(userIdField));
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		AllTypeDao dao = new AllTypeDao(this);
		this.setUserInfo(data);
		dao.insertAllType(data);

	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		AllTypeDao dao = new AllTypeDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.updateAllType(data);
	}


	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		AllTypeDao dao = new AllTypeDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.deleteAllType(data);
	}


	/**
	 * バーコードイメージを取得します。
	 * @param text バーコードの文字列。
	 * @return バーコードイメージ。
	 * @throws Exception 例外。
	 */
	public ImageData getBarcodeImage(final String text) throws Exception {
		// zxing-core.jar zxing-javase.jarを使用したバーコード生成
/*		String contents = text;
		BarcodeFormat format = BarcodeFormat.CODE_39;
		int width = 200;
		int height = 50;
		Code39Writer writer = new Code39Writer();
		BitMatrix bitMatrix = writer.encode(contents, format, width, height);
		BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", os);
		} finally {
			os.close();
		}
		ImageData ret = new ImageData();
		ret.setFileName("barcode.png");
		ret.setContents(os.toByteArray());
		log.debug("Image conetnt-type=" + ret.getContentType());
		return ret;*/
		return null;
	}


	/**
	 * 印刷処理を行います。
	 * @param param パラメータ。
	 * @return 応答。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response print(final Map<String, Object> param) throws Exception {
		this.methodStartLog(log, param);
		Map<String, Object> data = this.convertToServerData(param);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("attachFileTable");
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> m = list.get(i);
			m.put("no", Integer.valueOf(i + 1));
			m.put("barcode", this.getBarcodeImage(Integer.toString(123450000 + i)));
		}
		log.debug("data=" + JSON.encode(data, true));
		String template = AllTypeEditForm.getServlet().getServletContext().getRealPath("/exceltemplate/alltypeExcelTempl.xlsx");
		log.debug("template=" + template);
		AlltypeExcelReport rep = new AlltypeExcelReport(template);
		byte[] excel = rep.print(data);
		BinaryResponse ret = new BinaryResponse(excel);
		ret.setFileName("test001.xlsx");
		this.methodFinishLog(log, ret);
		return ret;
	}

}
