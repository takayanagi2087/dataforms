package dataforms.report;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dataforms.controller.Page;
import dataforms.dao.file.ImageData;
import dataforms.field.base.Field;
import dataforms.util.MapUtil;
import dataforms.util.StringUtil;
import net.arnx.jsonic.JSON;


/**
 * Excel形式のレポートクラス。
 *
 */
public class ExcelReport extends Report {

	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(ExcelReport.class);

	/**
	 * テンプレートファイルのパス。
	 */
	private String templatePath = null;

	/**
	 * ワークブック。
	 */
	private Workbook workbook = null;

	/**
	 * シート。
	 */
	private Sheet sheet = null;

	/**
	 * 画像描画領域。
	 */
	private Drawing<?> drawing = null;

	/**
	 * 出力するシートのインデックス。
	 */
	private int sheetIndex = 0;
	
	/**
	 * ワークブックを取得します。
	 * @return ワークブック。
	 */
	public Workbook getWorkbook() {
		return workbook;
	}

	/**
	 * ワークブックを設定します。
	 * @param workbook ワークブック。
	 */
	public void setWorkbook(final Workbook workbook) {
		this.workbook = workbook;
	}

	/**
	 * シートを取得します。
	 * @return シート。
	 */
	protected Sheet getSheet() {
		return sheet;
	}

	/**
	 * シートを設定します。
	 * @param sheet シート。
	 */
	protected void setSheet(final Sheet sheet) {
		this.sheet = sheet;
	}
	
	/**
	 * 出力するシートインデックスを取得します。
	 * @return シートインデックス。
	 */
	public int getSheetIndex() {
		return sheetIndex;
	}

	/**
	 * 出力するシートインデックスを取得します。
	 * @param sheetIndex シートインデックス。
	 */
	public void setSheetIndex(final int sheetIndex) {
		this.sheetIndex = sheetIndex;
		this.sheet = this.workbook.getSheetAt(sheetIndex);
	}

	/**
	 * 描画オブジェクトを取得します。
	 * @return 描画オブジェクト。
	 */
	protected Drawing<?> getDrawing() {
		return drawing;
	}
	/**
	 * 描画オブジェクトを設定します。
	 * @param drawing 描画オブジェクト。
	 */
	protected void setDrawing(final Drawing<?> drawing) {
		this.drawing = drawing;
	}

	/**
	 * セルの位置マップを取得します。
	 * @return セルの位置マップ。
	 */
	protected Map<String, CellPosition> getCellPositionMap() {
		return cellPositionMap;
	}

	/**
	 * セルの位置マップを設定します。
	 * @param cellPositionMap セルの位置マップ。
	 */
	protected void setCellPositionMap(final Map<String, CellPosition> cellPositionMap) {
		this.cellPositionMap = cellPositionMap;
	}

	/**
	 * エクセルの1ページ行数。
	 */
	private int rowsPerExcelPage = 0;

	/**
	 * 1ページの行数(Excelのセルベース)を取得します。
	 * @return 1ページの行数(Excelのセルベース)。
	 */
	protected int getRowsPerExcelPage() {
		return rowsPerExcelPage;
	}


	/**
	 * コンストラクタ.
	 */
	public ExcelReport() {

	}

	/**
	 * コンストラクタ。
	 * @param templatePath テンプレートのパス。
	 */
	public ExcelReport(final String templatePath) {
		this.setTemplatePath(templatePath);
	}

	
	/**
	 * テンプレートファイルのパスを取得する。
	 * @return テンプレートファイルのパス。
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * テンプレートファイルのパスを設定する。
	 * @param templatePath テンプレートファイルのパス。
	 */
	public void setTemplatePath(final String templatePath) {
		this.templatePath = templatePath;
		this.sheet = null;
	}

	/**
	 * テンプレートファイルを開く。
	 * @return テンプレートファイル。
	 * @throws Exception 例外。
	 */
	protected Workbook getTamplate() throws Exception {
		if (this.workbook == null) {
			FileInputStream is = new FileInputStream(this.templatePath);
			try {
				this.workbook = new XSSFWorkbook(is);
				this.workbook.setForceFormulaRecalculation(true);
			} finally {
				is.close();
			}
		}
		return this.workbook;
	}


	/**
	 * セルの数式をコピーします。
	 * @param srcCell コピー元
	 * @param destCell コピー先
	 */
	protected void copyCellFormula(final Cell srcCell, final Cell destCell) {
		String formula = srcCell.getCellFormula();
		EvaluationWorkbook ew = XSSFEvaluationWorkbook.create((XSSFWorkbook) workbook);
		Ptg[] ptgs = FormulaParser.parse(formula, (XSSFEvaluationWorkbook) ew, FormulaType.CELL, this.getSheetIndex());
		FormulaRenderingWorkbook rw = (XSSFEvaluationWorkbook) ew;
		for (Ptg ptg : ptgs) {
			int shiftRows = destCell.getRowIndex() - srcCell.getRowIndex();
			int shiftCols = destCell.getColumnIndex() - srcCell.getColumnIndex();
			if (ptg instanceof RefPtgBase) {
				RefPtgBase ref = (RefPtgBase) ptg;
				if (ref.isColRelative()) {
					ref.setColumn(ref.getColumn() + shiftCols);
				}
				if (ref.isRowRelative()) {
					ref.setRow(ref.getRow() + shiftRows);
				}
			} else if (ptg instanceof AreaPtg) {
				AreaPtg ref = (AreaPtg) ptg;
				if (ref.isFirstColRelative()) {
					ref.setFirstColumn(ref.getFirstColumn() + shiftCols);
				}
				if (ref.isLastColRelative()) {
					ref.setLastColumn(ref.getLastColumn() + shiftCols);
				}
				if (ref.isFirstRowRelative()) {
					ref.setFirstRow(ref.getFirstRow() + shiftRows);
				}
				if (ref.isLastRowRelative()) {
					ref.setLastRow(ref.getLastRow() + shiftRows);
				}
			}
		}
		destCell.setCellFormula(FormulaRenderer.toFormulaString(rw, ptgs));
	}

	/**
	 * 行内のセルをコピーする。
	 * @param fromRow コピー元。
	 * @param toRow コピー先。
	 *
	 */
	protected void copyCells(final Row fromRow, final Row toRow) {
		for (int j = 0; j < fromRow.getLastCellNum(); j++) {
			Cell cell = fromRow.getCell(j);
			if (cell != null) {
				Cell toCell = toRow.getCell(j);
				if (toCell == null) {
					toCell = toRow.createCell(j);
				}
				CellStyle cellstyle = cell.getCellStyle();
				toCell.setCellStyle(cellstyle);
				if  (cell.getCellTypeEnum() == CellType.STRING) {
					toCell.setCellValue(cell.getRichStringCellValue());
				} else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
					if (DateUtil.isCellDateFormatted(cell)) {
						toCell.setCellValue(cell.getDateCellValue());
					} else {
						toCell.setCellValue(cell.getNumericCellValue());
					}
				} else if (cell.getCellTypeEnum() == CellType.FORMULA) {
					//toCell.setCellFormula(cell.getCellFormula());
					this.copyCellFormula(cell, toCell);
				} else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
					toCell.setCellValue(cell.getBooleanCellValue());
				}
			}
		}
	}

	/**
	 * 1行コピーを行う。
	 * @param fromSheet シート。
	 * @param from コピー元行。
	 * @param toSheet シート。
	 * @param to コピー先行。
	 */
	protected void copyRow(final Sheet fromSheet, final int from, final Sheet toSheet, final int to) {
		Row fromRow = fromSheet.getRow(from);
		if (fromRow != null) {
			Row toRow = toSheet.getRow(to);
			if (toRow != null) {
				toSheet.removeRow(toRow);
			}
			toRow = toSheet.createRow(to);
			toRow.setHeight(fromRow.getHeight());
			this.copyCells(fromRow, toRow);
		}
	}


	
	/**
	 * 1行コピーを行う。
	 * @param sheet シート。
	 * @param from コピー元行。
	 * @param to コピー先行。
	 */
	protected void copyRow(final Sheet sheet, final int from, final int to) {
/*		Row fromRow = sheet.getRow(from);
		if (fromRow != null) {
			Row toRow = sheet.getRow(to);
			if (toRow != null) {
				sheet.removeRow(toRow);
			}
			toRow = sheet.createRow(to);
			toRow.setHeight(fromRow.getHeight());
			this.copyCells(fromRow, toRow);
		}*/
		this.copyRow(sheet, from, sheet, to);
	}

	/**
	 * 指定範囲の行をコピーします。
	 * @param sheet シート。
	 * @param from コピー元。
	 * @param rows コピー行数。
	 * @param to コピー先。
	 */
	protected void copyRows(final Sheet sheet, final int from, final int rows, final int to) {
		for (int i = from; i < rows; i++) {
			this.copyRow(sheet, from + i,  to + i);
		}
		// 結合情報をコピーする。
		int cnt = sheet.getNumMergedRegions();
		for (int i = 0; i < cnt; i++) {
			CellRangeAddress cra = sheet.getMergedRegion(i);
			if (from <= cra.getFirstRow() && cra.getFirstRow() < from + rows) {
				cra.setFirstRow(cra.getFirstRow() + to);
				cra.setLastRow(cra.getLastRow() + to);
				sheet.addMergedRegion(cra);
			}
		}
	}

	/**
	 * セルの位置情報。
	 *
	 */
	protected static class CellPosition {
		/**
		 * セルの行。
		 */
		private int row = -1;
		/**
		 * セルの列。
		 */
		private int col = -1;
		/**
		 * コンストラクタ。
		 *
		 * @param row セルの行。
		 * @param col セルの列。
		 */
		public CellPosition(final int row, final int col) {
			this.row = row;
			this.col = col;
		}


		/**
		 * 行を取得します。
		 * @return 行。
		 */
		public int getRow() {
			return row;
		}
		/**
		 * 列を取得します。
		 * @return 列。
		 *
		 */
		public int getCol() {
			return col;
		}

		/**
		 * 行数。
		 */
		private int rows = 1;
		/**
		 * カラム数。
		 */
		private int columns = 1;
		/**
		 * 左オフセット。
		 */
		private int dx1 = 2;
		/**
		 * 上オフセット。
		 */
		private int dy1 = 2;
		/**
		 * 右オフセット。
		 */
		private int dx2 = -2;
		/**
		 * 下オフセット。
		 */
		private int dy2 = -2;
		/**
		 * 縦横比の設定方法(cell:セルに合わせる。image:元画像に合わせる)
		 */
		private String aspect = "cell";

		/**
		 * 行数を取得します。
		 * @return 行数。
		 */
		public int getRows() {
			return rows;
		}

		/**
		 * 行数を設定します。
		 * @param rows 行数。
		 */
		public void setRows(final int rows) {
			this.rows = rows;
		}


		/**
		 * カラム数を取得します。
		 * @return カラム数。
		 */
		public int getColumns() {
			return columns;
		}


		/**
		 * カラム数を設定します。
		 * @param columns カラム数。
		 */
		public void setColumns(final int columns) {
			this.columns = columns;
		}

		/**
		 * 左オフセットを取得します。
		 * @return 左オフセット。
		 */
		public int getDx1() {
			return dx1;
		}

		/**
		 * 左オフセットを設定します。
		 * @param dx1 左オフセット。
		 */
		public void setDx1(final int dx1) {
			this.dx1 = dx1;
		}

		/**
		 * 上オフセットを取得します。
		 * @return 上オフセット。
		 */
		public int getDy1() {
			return dy1;
		}

		/**
		 * 上オセットを設定します。
		 * @param dy1 上オセット。
		 */
		public void setDy1(final int dy1) {
			this.dy1 = dy1;
		}

		/**
		 * 右オフセットを取得します。
		 * @return 右オフセット。
		 */
		public int getDx2() {
			return dx2;
		}


		/**
		 * 右オフセットを設定します。
		 * @param dx2 右オフセット。
		 */
		public void setDx2(final int dx2) {
			this.dx2 = dx2;
		}

		/**
		 * 下オフセットを取得します。
		 * @return 下オフセット。
		 */
		public int getDy2() {
			return dy2;
		}

		/**
		 * 下オフセットを取得します。
		 * @param dy2 下オフセット。
		 */
		public void setDy2(final int dy2) {
			this.dy2 = dy2;
		}

		/**
		 * 縦横比の設定方法を取得します。
		 * @return 縦横比の設定方法。
		 */
		public String getAspect() {
			return aspect;
		}


		/**
		 * 縦横比の設定方法を設定します。
		 * @param aspect 縦横比の設定方法("cell":セルに合わせる、 "image":画像に合わせる。)
		 */
		public void setAspect(final String aspect) {
			this.aspect = aspect;
		}



	}

	/**
	 * 各フィールドの位置情報マップ。
	 */
	private Map<String, CellPosition> cellPositionMap = null;

	
	/**
	 * セルの値からフィールドIDを取得します。
	 * @param value セルの値。
	 * @return フィールドID。
	 */
	private String getFieldId(final String value) {
		String ret = null;
		Pattern p = Pattern.compile("\\$\\{(.+?)\\}");
		Matcher m = p.matcher(value);
		if (m.find()) {
			String g = m.group();
			ret = g.replaceAll("[\\$\\{\\}]", "");
		} else {
			String[] sp = value.substring(1).split("[\\{\\}]");
			ret = sp[0].trim();
		}
		log.debug("fieldId=" + ret);
		return ret;
	}
	

	/**
	 * フィールドIDとセルのマップを取得する。
	 * @param wb ワークブック。
	 * @return マップ。
	 * @throws Exception 例外。
	 */
	private Map<String, CellPosition> getCellPositionMap(final Workbook wb) throws Exception {
		Map<String, CellPosition> ret = new HashMap<String, CellPosition>();
		Sheet sheet = wb.getSheetAt(this.sheetIndex);
		for (Row row: sheet) {
			for (Cell cell: row) {
				if (cell.getCellTypeEnum() == CellType.STRING) {
					int ridx = cell.getRowIndex();
					int cidx = cell.getColumnIndex();
					String value = cell.getStringCellValue();
					if (value != null) {
						if (value.length() > 0) {
							if (value.charAt(0) == '$') {
								CellPosition p = new CellPosition(ridx, cidx);
								String str = value.substring(1);
								String[]sp = str.split("[\\{\\}]");
								ret.put(this.getFieldId(value), p);
								if (Pattern.matches("^\\$(.+?)\\{.+\\}$", value) 
									|| Pattern.matches("^\\$\\{(.+?)\\}\\{.+\\}$", value)) {
									for (String s: sp) {
										log.debug("s=" + s);
									}
									@SuppressWarnings("unchecked")
									Map<String, Object> opt = (Map<String, Object>) JSON.decode(sp[sp.length - 1], Map.class);
									log.debug("opt=" + opt.toString());
									if (opt.containsKey("aspect")) {
										p.setAspect((String) opt.get("aspect"));
									}
									if (opt.containsKey("rows")) {
										p.setRows(((BigDecimal) opt.get("rows")).intValue());
									}
									if (opt.containsKey("columns")) {
										p.setColumns(((BigDecimal) opt.get("columns")).intValue());
									}
									if (opt.containsKey("dx1")) {
										p.setDx1(((BigDecimal) opt.get("dx1")).intValue());
									}
									if (opt.containsKey("dy1")) {
										p.setDy1(((BigDecimal) opt.get("dy1")).intValue());
									}
									if (opt.containsKey("dx2")) {
										p.setDx2(((BigDecimal) opt.get("dx2")).intValue());
									}
									if (opt.containsKey("dy2")) {
										p.setDy2(((BigDecimal) opt.get("dy2")).intValue());
									}
								}
							}
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * ページの最終行を取得します。
	 * @param map フィールドIDとセルのマップ。
	 * @return ページの最終行。
	 * @throws Exception 例外。
	 */
	private int getPageLastRow(final Map<String, CellPosition> map) {
		int row = 0;
		for (CellPosition c: map.values()) {
			if (row < c.getRow()) {
				row = c.getRow();
			}
		}
		return row;
	}

	/**
	 * 指定数分のページを展開します。
	 * @param sheet シート。
	 * @param pages ページ。
	 * @return 1ページの行数。
	 */
	private int addPage(final Sheet sheet, final int pages) {
		int lastrow = this.getPageLastRow(this.cellPositionMap);
		int lines = lastrow + 1;
		//log.debug("last row=" + lastrow);
		for (int i = 1; i < pages; i++) {
			this.copyRows(sheet, 0, lines, lines * i);
			sheet.setRowBreak((lines * i) - 1);
		}
		for (int i = 0; i < pages; i++) {
			this.clearPage(sheet, i, lines);
		}
		return lines;
	}

	/**
	 * ページをクリアします。
	 * @param sheet シート。
	 * @param page ページインデックス。
	 * @param lines 1ページの行数。
	 */
	private void clearPage(final Sheet sheet, final int page, final int lines) {
		for (String key: this.cellPositionMap.keySet()) {
			CellPosition p = this.cellPositionMap.get(key);
			Row r = sheet.getRow(lines * page + p.getRow());
			Cell c = r.getCell(p.getCol());
			c.setCellValue("");
		}
	}

	/**
	 * セルの値を設定します。
	 * @param c セル。
	 * @param value 値。
	 * @param p セル位置情報。
	 */
	protected void setCellValue(final Cell c, final Object value, final CellPosition p) {
		if (value != null) {
			if (value instanceof Number) {
				Number n = (Number) value;
				c.setCellValue(n.doubleValue());
			} else if (value instanceof java.sql.Date) {
				java.sql.Date sqldate = (java.sql.Date) value;
				java.util.Date date = new java.util.Date();
				date.setTime(sqldate.getTime());
				c.setCellValue(date);
			} else if (value instanceof java.sql.Time) {
				java.sql.Time sqltime = (java.sql.Time) value;
				java.util.Date date = new java.util.Date();
				date.setTime(sqltime.getTime());
				c.setCellValue(date);
			} else if (value instanceof java.sql.Timestamp) {
				java.sql.Timestamp sqltimestamp = (java.sql.Timestamp) value;
				java.util.Date date = new java.util.Date();
				date.setTime(sqltimestamp.getTime());
				c.setCellValue(date);
			} else if (value instanceof ImageData) {
				this.setImage(c, value, p);
			} else {
				c.setCellValue(value.toString());
			}
		} else {
			c.setCellValue("");
		}
	}

	/**
	 * セルに対し画像を設定します。
	 * @param c セル。
	 * @param value 値。
	 * @param p セル位置情報。
	 */
	private void setImage(final Cell c, final Object value, final CellPosition p) {
		ImageData img = (ImageData) value;
		int cidx = c.getColumnIndex();
		int ridx = c.getRowIndex();
		ClientAnchor anchor = new XSSFClientAnchor();
		anchor.setCol1(cidx);
		anchor.setCol2(cidx + p.getColumns());
		anchor.setRow1(ridx);
		anchor.setRow2(ridx + p.getRows());
		anchor.setDx1(Units.EMU_PER_PIXEL * p.getDx1());
		anchor.setDy1(Units.EMU_PER_PIXEL * p.getDy1());
		anchor.setDx2(Units.EMU_PER_PIXEL * p.getDx2());
		anchor.setDy2(Units.EMU_PER_PIXEL * p.getDy2());
		anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
		int imgtype = XSSFWorkbook.PICTURE_TYPE_PNG;
		if (ImageData.CONTENT_TYPE_JPEG.equals(img.getContentType())) {
			imgtype = XSSFWorkbook.PICTURE_TYPE_JPEG;
		} else if (ImageData.CONTENT_TYPE_GIF.equals(img.getContentType())) {
			imgtype = XSSFWorkbook.PICTURE_TYPE_GIF;
		}
		int pidx = this.workbook.addPicture(img.getContents(), imgtype);
		Picture pic = this.drawing.createPicture(anchor, pidx);
		this.resizeImage(c, pic, p);
	}

	/**
	 * 画像貼り付けアンカーの幅を取得します。
	 * @param p セルの位置情報。
	 * @return 画像貼り付けアンカーの幅(pixcel)。
	 */
	private double getAnchorWidth(final CellPosition p) {
		double ret = 0.0;
		for (int i = 0; i < p.getColumns(); i++) {
			ret += this.sheet.getColumnWidthInPixels(p.getCol() + i);
		}
		return ret;
	}


	/**
	 * 画像貼り付けアンカーの高さを取得します。
	 * @param p セルの位置情報。
	 * @return 画像貼り付けアンカーの高さ(pixcel)。
	 */
	private double getAnchorHeight(final CellPosition p) {
		double ret = 0.0;
		for (int i = 0; i < p.getRows(); i++) {
			ret += this.sheet.getRow(p.getRow() + i).getHeightInPoints();
		}
		return (ret / 0.75);
	}


	/**
	 * 画像サイズの調整。
	 * @param c セル。
	 * @param pic 画像。
	 * @param p セル位置情報。
	 */
	private void resizeImage(final Cell c, final Picture pic, final CellPosition p) {
		if ("image".equals(p.getAspect())) {
			double w = this.getAnchorWidth(p) - (p.getDx1() - p.getDx2());
			double h = this.getAnchorHeight(p) - (p.getDy1() - p.getDy2());
			Dimension d = pic.getImageDimension();
			log.debug("w,h=" + w + "," + h);
			log.debug("iw,ih=" + d.getWidth() + "," + d.getHeight());
			if (w > h) {
				double cw = w / h;
				double iw = d.getWidth() / d.getHeight();
				pic.resize((iw / cw) * 1.0, 1.0);
			} else {
				double ch = h / w;
				double ih = d.getHeight() / d.getWidth();
				pic.resize(1.0, (ih / ch) * 1.0);
			}
		}
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 指定されたフィールドを対応するセルに出力します。
	 * </pre>
	 */
	@Override
	protected void printField(final int page, final Field<?> field, final Map<String, Object> data) throws Exception {
		//log.debug("printField:" + field.getId());
		CellPosition p = this.cellPositionMap.get(field.getId());
		if (p != null) {
			//log.debug(field.getId() + "=" + p.getRow() + "," + p.getCol());
			final Sheet sheet = this.sheet;
			int baseRow = this.rowsPerExcelPage * page;
			Row r = sheet.getRow(baseRow + p.getRow());
			Cell c = r.getCell(p.getCol());
			Object obj = MapUtil.getValue(field.getId(), data);
			this.setCellValue(c, obj, p);
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * POIを使いExcelテンプレートを読み込み、必要なページを構築します。
	 * </pre>
	 */
	@Override
	protected void initPage(final int pages) throws Exception {
		this.workbook = this.getTamplate();
		this.sheet = this.workbook.getSheetAt(this.sheetIndex);
		this.cellPositionMap = this.getCellPositionMap(this.workbook);
		this.addPage(sheet, pages);
		this.rowsPerExcelPage = this.getPageLastRow(this.cellPositionMap) + 1;
		this.drawing = this.sheet.createDrawingPatriarch();
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * Excelワークブックの内容を取得します。
	 * </pre>
	 */
	@Override
	public byte[] getReport() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			this.workbook.write(os);
		} finally {
			os.close();
		}
		return os.toByteArray();
	}
	
	/**
	 * シートのコピーを作成します。
	 * @param idx コピー元シートのインデックス。
	 * @throws Exception 例外。
	 */
	protected void copySheet(final int idx) throws Exception {
		Workbook wb = this.getTamplate();
		wb.cloneSheet(idx);
		PrintSetup printSetting = wb.getSheetAt(1).getPrintSetup();
		int lidx = wb.getNumberOfSheets() - 1;
		wb.getSheetAt(lidx).getPrintSetup().setLandscape(printSetting.getLandscape());
		wb.getSheetAt(lidx).getPrintSetup().setPaperSize(printSetting.getPaperSize());
		// シートの印刷範囲をコピー
		String pa = wb.getPrintArea(idx);
		if (!StringUtil.isBlank(pa)) {
			String[] sp = pa.split("!");
			if (sp.length >= 2) {
				wb.setPrintArea(lidx, sp[1]);
			}
		}
	}

	
	/**
	 * シートのコピーを作成します。
	 * @param sheets コピーを作成するシートの数。
	 * @throws Exception 例外。
	 */
	protected void addSheets(final int sheets) throws Exception {
		this.workbook = this.getTamplate();
		for (int i = 0; i < sheets; i++) {
			log.debug("wb = " + i);
			this.workbook.cloneSheet(1);
		}
		PrintSetup printSetting = this.workbook.getSheetAt(1).getPrintSetup();
		for (int i = 2; i < this.workbook.getNumberOfSheets(); i++) {
			this.workbook.getSheetAt(i).getPrintSetup().setLandscape(printSetting.getLandscape());
			this.workbook.getSheetAt(i).getPrintSetup().setPaperSize(printSetting.getPaperSize());
		}
		for (int i = 0; i < this.workbook.getNumberOfSheets(); i++) {
			this.workbook.getSheetAt(i).getFooter().setRight("dataforms.jar " + Page.getDataformsVersion());
		}
	}
	
	/**
	 * シート名を設定します。
	 * @param sheetIndex シートインデックス。
	 * @param name シート名。
	 */
	public void setSheetName(final int sheetIndex, final String name) {
		this.workbook.setSheetName(sheetIndex, name);
	}
	
	/**
	 * 指定されたシートを指定されたパスワードでロックします。
	 * @param sh シート。
	 * @param password パスワード。
	 */
	public void lockSheet(final XSSFSheet sh, final String password) {
		sh.lockDeleteColumns(true);
		sh.lockDeleteRows(true);
		sh.lockFormatCells(true);
		sh.lockFormatRows(true);
		sh.lockInsertColumns(true);
		sh.lockInsertRows(true);
		sh.protectSheet(password);
		sh.enableLocking();
	}

}
