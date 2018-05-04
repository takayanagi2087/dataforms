package dataforms.debug.report;

import dataforms.debug.dao.alltype.AllTypeAttachFileTable;
import dataforms.debug.dao.alltype.AllTypeTable;
import dataforms.field.common.WebResourceImageField;
import dataforms.field.sqltype.IntegerField;
import dataforms.report.ExcelReport;
import dataforms.report.ReportTable;


/**
 * Excelレポート出力テスト。
 *
 */
public class AlltypeExcelReport extends ExcelReport {
	/**
	 * コンストラクタ。
	 * @param path テンプレートパス。
	 */
	public AlltypeExcelReport(final String path) {
		this.setTemplatePath(path);

		this.addTableFields(new AllTypeTable());
		this.addField(new WebResourceImageField("webImage", "/frame/default/image/menu.png"));

		AllTypeAttachFileTable aft = new AllTypeAttachFileTable();
		aft.getFieldList().addField(new IntegerField("no"));
//		aft.getFieldList().addField(new ImageField("barcode"));
		aft.getFieldList().addField(new WebResourceImageField("barcode", "/frame/default/image/menu.png"));
		ReportTable htmltable = new ReportTable("attachFileTable", aft.getFieldList());
		this.addReportTable(htmltable);

		this.setRowsParPage(10);
		this.setMainTableId(htmltable.getId());
		this.addBreakField(htmltable.getFieldList().get("fileComment"));
	}

/*	@Override
	protected Workbook getTamplate() throws Exception {
		Workbook ret = super.getTamplate();
		ret.setForceFormulaRecalculation(true);
		return ret;
	}*/

	
	@Override
	public byte[] getReport() throws Exception {
//		Workbook wb = this.getWorkbook();
//		XSSFSheet sh = (XSSFSheet) wb.getSheetAt(0);
//		this.lockSheet(sh, "password");
		return super.getReport();
	}


}
