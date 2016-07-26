package dataforms.devtool.page.table;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import dataforms.controller.Form;
import dataforms.dao.Dao;
import dataforms.dao.Table;
import dataforms.dao.sqlgen.SqlGenerator;
import dataforms.devtool.field.common.OverwriteModeField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.common.FlagField;
import dataforms.field.sqltype.VarcharField;
import dataforms.report.ExcelReport;
import dataforms.report.ReportTable;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.FileUtil;
import dataforms.validator.RequiredValidator;

/**
 * テーブル定義書レポートクラスです。
 *
 */
public class TableReport extends ExcelReport {
	/**
	 * コンストラクタ。
	 * @param templatePath テンプレートファイルパス。
	 * @param sheets 追加するSheet数。
	 * @throws Exception 例外。
	 */
	public TableReport(final String templatePath, final int sheets) throws Exception {
		this.setTemplatePath(templatePath);
		this.addField(new VarcharField("tableName", 1024));
		this.addField(new VarcharField("tableComment", 1024));
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
		this.addSheets(sheets);
	}
	
	
	/**
	 * テンプレートファイルを作成します。
	 * @param form フォーム。
	 * @return 作成されたテンプレートファイル。
	 * @throws Exception 例外。
	 */
	public static File makeTemplate(final Form form) throws Exception {
		//this.getServlet().getTempDir()
		File tmp = File.createTempFile("tableSpec", ".xlsx", new File(DataFormsServlet.getTempDir()));
		byte[] excel = form.getBinaryWebResource("/dataforms/devtool/exceltemplate/tableSpec.xlsx");
		FileOutputStream os = new FileOutputStream(tmp);
		try {
			FileUtil.writeOutputStream(excel, os);
		} finally {
			os.close();
		}
		return tmp;
	}

	
	/**
	 * 仕様書作成用の追加情報を設定する。
	 * @param data データ。
	 * @param dao データアクセスオブジェクト。
	 * @return テーブル仕様データ。
	 * @throws Exception 例外。
	 * 
	 */
	public Map<String, Object> getTableSpec(final Map<String, Object> data, final Dao dao) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			SqlGenerator gen = dao.getSqlGenerator();
			String packageName = (String) data.get("packageName");
			String tableClassName = (String) data.get("tableClassName");
			@SuppressWarnings("unchecked")
			Class<? extends Table> c = (Class<? extends Table>) Class.forName(packageName + "." + tableClassName);
			Table t = c.newInstance();
			ret.put("tableName", t.getTableName());
			ret.put("tableComment", t.getComment());
			ret.put("tableClassName", t.getClass().getName());
			List<Map<String, Object>> fieldList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < t.getFieldList().size(); i++) {
				Map<String, Object> m = new HashMap<String, Object>();
				Field<?> f = t.getFieldList().get(i);
				m.put("comment", f.getComment());
				m.put("columnName", f.getDbColumnName());
				if (t.getPkFieldList().get(f.getId()) != null) {
					m.put("pkFlag", "*");
				}
				m.put("dataType", gen.getDatabaseType(f));
				m.put("fieldId", f.getId());
				m.put("fieldClassName", f.getClass().getName());
				fieldList.add(m);
			}
			ret.put("fieldList", fieldList);
		} catch (ClassNotFoundException e) {
			return null;
		}
		return ret;
	}

	/**
	 * シートの削除を行います。
	 * @param idx シートのインデックス。
	 */
	public void removeSheet(final int idx) {
		this.getWorkbook().removeSheetAt(idx);
	}
	
	/**
	 * ヘッダーを設定します。
	 * @param header ヘッダー文字列。
	 * @throws Exception 例外。
	 */
	public void setSystemHeader(final String header) throws Exception {
		Workbook wb = this.getWorkbook();
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			Sheet sheet = wb.getSheetAt(i);	
			sheet.getHeader().setLeft(header);
		}
	}
}
