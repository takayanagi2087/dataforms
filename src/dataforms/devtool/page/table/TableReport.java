package dataforms.devtool.page.table;

import dataforms.devtool.field.common.OverwriteModeField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.field.base.FieldList;
import dataforms.field.common.FlagField;
import dataforms.field.sqltype.VarcharField;
import dataforms.report.ExcelReport;
import dataforms.report.ReportTable;
import dataforms.validator.RequiredValidator;

/**
 * テーブル定義書レポートクラスです。
 *
 */
public class TableReport extends ExcelReport {
	/**
	 * コンストラクタ。
	 * @param templatePath テンプレートファイルパス。
	 */
	public TableReport(final String templatePath) {
		this.setTemplatePath(templatePath);
		this.addField(new VarcharField("tableName", 1024));
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new TableClassNameField()).setComment("テーブルクラス名").setAutocomplete(false).addValidator(new RequiredValidator());
		this.addField(new OverwriteModeField());
		this.addField(new FlagField("autoIncrementId")).setComment("主キー自動生成フラグ");
		FieldListHtmlTable htmltbl = new FieldListHtmlTable();
		FieldList flist = new FieldList();
		flist.addAll(htmltbl.getFieldList());
		flist.addField(new VarcharField("columnName", 1024));
		flist.addField(new VarcharField("dataType", 1024));
		ReportTable rtbl = new ReportTable("fieldList", flist);
		this.addReportTable(rtbl);
		this.setMainTableId("fieldList");
		this.setRowsParPage(30);
	}
	
}
