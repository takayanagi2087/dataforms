package dataforms.debug.report;

import dataforms.debug.dao.alltype.AllTypeAttachFileTable;
import dataforms.debug.dao.alltype.AllTypeTable;
import dataforms.field.common.WebResourceImageField;
import dataforms.field.sqltype.IntegerField;
import dataforms.report.ReportTable;
import dataforms.report.XslFoReport;


/**
 * XSL-FOレポート出力テスト。
 *
 */
public class AlltypeXslFoReport extends XslFoReport {
	/**
	 * コンストラクタ。
	 * @param path テンプレートパス。
	 */
	public AlltypeXslFoReport(final String path) {
		super(path);

		this.addTableFields(new AllTypeTable());
		this.addField(new WebResourceImageField("webImage", "/frame/default/image/menu.png"));

		AllTypeAttachFileTable aft = new AllTypeAttachFileTable();
		aft.getFieldList().addField(new IntegerField("no"));
//		aft.getFieldList().addField(new ImageField("barcode"));
		aft.getFieldList().addField(new WebResourceImageField("barcode", "/frame/default/image/menu.png"));
		ReportTable htmltable = new ReportTable("attachFileTable", aft.getFieldList());
		this.addReportTable(htmltable);

		this.setRowsParPage(9);
		this.setMainTableId(htmltable.getId());
		this.addBreakField(htmltable.getFieldList().get("fileComment"));
	}
}
