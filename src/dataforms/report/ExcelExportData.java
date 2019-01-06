package dataforms.report;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.base.NumberField;
import dataforms.util.NumberUtil;

/**
 * Excel形式のエクスポートデータクラスです。
 *
 */
public class ExcelExportData implements ExportData {

	/**
	 * ダウンロードファイル名。
	 */
	private String fileName = "export.xlsx";

	/**
	 * コンストラクタ。
	 */
	public ExcelExportData() {

	}

	/**
	 * ファイル名を設定します。
	 * @param fileName ファイル名。
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}


	/**
	 * ファイル名を取得します。
	 * @return ファイル名。
	 */
	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public String getContentType() {
		return "application/vnd.ms-excel";
	}

	/**
	 * ヘッダの行インデックス。
	 */
	private int headerRowIndex = 0;

	/**
	 * ヘッダの行インデックスを取得します。
	 * <pre>
	 * デフォルトは0です。
	 * </pre>
	 * @return ヘッダの行インデックス。
	 */
	public int getHeaderRowIndex() {
		return headerRowIndex;
	}

	/**
	 * ヘッダの行インデックスを設定します。
	 * @param headerRowIndex ヘッダの行インデックス。
	 */
	public void setHeaderRowIndex(final int headerRowIndex) {
		this.headerRowIndex = headerRowIndex;
	}

	/**
	 * データの開始行インデックス。
	 */
	private int dataRowIndex = 1;

	/**
	 * データの開始行インデックスを取得します。
	 * <pre>
	 * デフォルトは1です。
	 * </pre>
	 * @return データの開始行インデックス。
	 */
	public int getDataRowIndex() {
		return dataRowIndex;
	}

	/**
	 * データの開始行インデックスを設定します。
	 * @param dataRowIndex データの開始行インデックス。
	 */
	public void setDataRowIndex(final int dataRowIndex) {
		this.dataRowIndex = dataRowIndex;
	}

	/**
     * ダウンロードするExcelのワークブックを取得します。
     * <pre>
     * 空のExcelファイルを用意します。
     * テンプレートExcelファイルを用意する場合は、このメソッドをオーバーライドしてください。
     * </pre>
     * @return ダウンロードするExcelのワークブック。
     * @throws Exception 例外。
     */
    protected Workbook getDownloadWorkbook() throws Exception {
    	XSSFWorkbook wb = new XSSFWorkbook();
    	wb.createSheet("download");
    	return wb;
    }

    /**
     * ダウンロードするExcelのヘッダを設定します。
     * @param sheet シート。
     * @param rowidx ヘッダの行インデックス。
     * @param flist ヘッダフィールドリスト。
     * @throws Exception 例外。
     */
    protected void setHeader(final Sheet sheet, final int rowidx, final FieldList flist) throws Exception {
    	Row row = sheet.createRow(rowidx);
    	int idx = 0;
    	for (Field<?> f: flist) {
    		Cell cell = row.createCell(idx);
    		cell.setCellValue(f.getComment());
    		idx++;
    	}
    }

    /**
     * エクスポートデータを設定します。
     * @param sheet シート。
     * @param rowidx 行インデックス。
     * @param list エクスポートデータ。
     * @param flist フィールドリスト。
     * @throws Exception 例外。
     */
    protected void setExportData(final Sheet sheet, final int rowidx, final List<Map<String, Object>> list, final FieldList flist) throws Exception {
       	int ridx = rowidx;
       	for (Map<String, Object> m: list) {
       	   	Row row = sheet.createRow(ridx);
       	    for (int c = 0; c < flist.size(); c++) {
       	    	Cell cell = row.createCell(c);
       	    	Field<?> f = flist.get(c);
       	    	if (f instanceof NumberField) {
           	    	Object v = m.get(f.getId());
           	    	if (v != null) {
           	    		double dv =NumberUtil.doubleValue(v);
           	    		cell.setCellValue(dv);
           	    	}
       	    	} else {
           	    	Object v = m.get(f.getId());
           	    	if (v != null) {
               	    	cell.setCellValue(v.toString());
           	    	}
       	    	}
       	    }
       	    ridx++;
       	}

   }

    /**
     * エクスポートデータをExcelに設定します。
     * @param list エクスポートデータ。
     * @param flist フィールドリスト。
     * @param wb ワークブック。
     * @throws Exception 例外。
     */
    protected void setExportData(final List<Map<String, Object>> list, final FieldList flist, final Workbook wb) throws Exception {
    	Sheet sh = wb.getSheetAt(0);
    	this.setHeader(sh, this.getHeaderRowIndex(), flist);
    	this.setExportData(sh, this.getDataRowIndex(), list, flist);

    }

    /**
     * Excel形式のエクスポートデータを取得します。
     * @param list エクスポートデータ。
     * @param flist フィールドリスト。
     * @return Excelデータのエクスポートデータ。
     * @throws Exception 例外。
     */
	protected byte[] getExcelExportData(final List<Map<String, Object>> list, final FieldList flist) throws Exception {
		Workbook wb = this.getDownloadWorkbook();
		this.setExportData(list, flist, wb);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			wb.write(os);
		} finally {
			os.close();
		}
		byte[] exceldata = os.toByteArray();
		return exceldata;
	}



	@Override
	public byte[] getExportData(final List<Map<String, Object>> list, final FieldList flist) throws Exception {
		return this.getExcelExportData(list, flist);
	}


}
