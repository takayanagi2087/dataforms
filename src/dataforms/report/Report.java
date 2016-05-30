package dataforms.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.dao.Table;
import dataforms.dao.file.WebResource;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import net.arnx.jsonic.JSON;

/**
 * レポートの基本クラス。
 *
 */
public abstract class Report {

	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(Report.class);


	/**
	 * フィールドリスト。
	 *
	 */
	private FieldList fieldList = new FieldList();

	/**
	 * HTMLテーブルのリスト。
	 */
	private List<ReportTable> tableList = new ArrayList<ReportTable>();


	/**
	 * 主テーブルのID。
	 */
	private String mainTableId = null;

	/**
	 * 主テーブルの指定フィールドが変化した場合改ページするフィールドのリスト。
	 */
	private FieldList breakFieldList = new FieldList();

	/**
	 * ページ開始位置リスト。
	 */
	private List<Integer> pageStartPositionList = new ArrayList<Integer>();


	/**
	 * 1ページの行数。
	 */
	private int rowsPerPage = 20;

	/**
	 * コンストラクタ。
	 */
	public Report() {

	}

	/**
	 * フィールドリストを取得します。
	 * @return フィールドリスト。
	 */
	public FieldList getFieldList() {
		return fieldList;
	}

	/**
	 * テーブルリストを取得します。
	 * @return テーブルリスト。
	 */
	public List<ReportTable> getTableList() {
		return tableList;
	}

	/**
	 * DBテーブル中のフィールド情報を追加します。
	 * @param table テーブル。
	 */
	protected void addTableFields(final Table table) {
		for (Field<?> f : table.getFieldList()) {
			this.addField(f);
		}
	}



	/**
	 * フィールドを追加します。
	 * @param field フィールド。
	 * @return 追加したフィールド。
	 */
	public Field<?> addField(final Field<?> field) {
		this.fieldList.add(field);
		return field;
	}

	/**
	 * HTMLテーブルを追加します。
	 * @param tbl テーブル。
	 * @return 追加したHTMLテーブル。
	 */
	public ReportTable addReportTable(final ReportTable tbl) {
		this.tableList.add(tbl);
		return tbl;
	}

	/**
	 * 主テーブルのIDを取得します。
	 * @return 主テーブルのID。
	 */
	public String getMainTableId() {
		return mainTableId;
	}

	/**
	 * 主テーブルのIDを設定します。
	 * <pre>
	 * 指定されたIDのテーブルが1ページ行数を超えた場合、複数ページに分けて印刷します。
	 * </pre>
	 * @param mainTableId 主テーブルのID。
	 */
	public void setMainTableId(final String mainTableId) {
		this.mainTableId = mainTableId;
	}

	/**
	 * 1ページの行数を取得します。
	 * @return 1ページの行数。
	 */
	public int getRowsParPage() {
		return rowsPerPage;
	}

	/**
	 * 1ページの行数を設定します。
	 * @param rowsPerPage 1ページの行数。
	 */
	public void setRowsParPage(final int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	/**
	 * ブレークフィールドを指定します。
	 * <pre>
	 * テーブル中の指定フィールドが変化した場合、改ページします。
	 * </pre>
	 *
	 * @param field 追加するフィールド。
	 */
	public void addBreakField(final Field<?> field) {
		this.breakFieldList.add(field);
	}

	/**
	 * フィールド単位の出力を行います。
	 * @param page ページ。
	 * @param field フィールド情報。
	 * @param data 印刷データ。
	 * @throws Exception 例外。
	 */
	protected abstract void printField(final int page, final Field<?> field, final Map<String, Object> data) throws Exception;


	/**
	 * レポートの指定ページを出力します。
	 * @param page ページNo。
	 * @param data データ。
	 * @throws Exception 例外。
	 */
	protected void printPage(final int page, final Map<String, Object> data) throws Exception {
		for (Field<?> field: this.getFieldList()) {
			this.printField(page, field, data);
		}
		for (ReportTable tbl: this.getTableList()) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(tbl.getId());
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> m = list.get(i);
					for (Field<?> f: tbl.getFieldList()) {
						String id = f.getId();
						if (m.get(id) != null) {
							String fid = tbl.getId() + "[" + i + "]." + id;
							Field<?> field = f.clone();
							field.setId(fid);
							this.printField(page, field, data);
						}
					}
				}
			}
		}
	}

	/**
	 * 改ページキーの値リストを取得します。
	 * @param m マップ。
	 * @return 改ページキーの値リスト。
	 */
	protected List<Object> getBreakValue(final Map<String, Object> m) {
		List<Object> ret = new ArrayList<Object>();
		for (Field<?> f: this.breakFieldList) {
			ret.add(m.get(f.getId()));
		}
		return ret;
	}

	/**
	 * 改ページの判定を行います。
	 * @param l0 改ページ判定情報0。
	 * @param l1 改ページ判定情報1。
	 * @return 改ページの場合true。
	 */
	protected boolean isBreak(final List<Object> l0, final List<Object> l1) {
		boolean ret = false;
		if (l0.size() != l1.size()) {
			ret = true;
		} else {
			for (int i = 0; i < l0.size(); i++) {
				if (!l0.get(i).equals(l1.get(i))) {
					ret = true;
				}
			}
		}
		return ret;
	}

	/**
	 * ページ数を計算する。
	 * <pre>
	 * 主テーブルの件数からページ数を計算します。
	 * キーブレークによって改ページするような場合、このメソッドをオーバーライドしてください。
	 * </pre>
	 * @param data 出力データ。
	 * @return ページ数。
	 * @throws Exception 例外。
	 */
	protected int countPage(final Map<String, Object> data) throws Exception {
		int ret = 0;
		String id = this.getMainTableId();
		if (id == null) {
			// テーブルの指定がない場合1ページ。
			ret = 1;
		} else {
			// breakキーが無い場合。
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(id);
			if (list != null) {
				int page = 1;
				if (list.size() > 0) {
					List<Object> breakValues0 = this.getBreakValue(list.get(0));
					int rows = 0;
					this.pageStartPositionList.add(Integer.valueOf(0));
					for (int i = 0; i < list.size(); i++) {
						List<Object> breakValues = this.getBreakValue(list.get(i));
//						log.debug("i=" + i);
						if (rows < this.getRowsParPage() && (!this.isBreak(breakValues0, breakValues))) {
							rows++;
						} else {
							breakValues0 = breakValues;
							page++;
							rows = 1;
							this.pageStartPositionList.add(Integer.valueOf(i));
						}
					}
				}
				ret = page;
			} else {
				ret = 1;
			}
		}
		return ret;
	}

	/**
	 * 指定されたページ用の印刷データを取得します。
	 * @param pageIndex ページインデックス。
	 * @param data 印刷データ。
	 * @return 指定されたページ用の印刷データ。
	 * @throws Exception 例外。
	 */
	protected Map<String, Object> getPageData(final int pageIndex, final Map<String, Object> data) throws Exception {
//		log.debug("pageStartPositionList=" + this.pageStartPositionList);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.putAll(data);
		if (this.getMainTableId() != null) {
//			log.debug("mainTableId=" + this.getMainTableId());
			ret.remove(this.getMainTableId());
			List<Map<String, Object>> plist = new ArrayList<Map<String, Object>>();
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(this.getMainTableId());
			if (list.size() > 0) {
				int st = this.pageStartPositionList.get(pageIndex);
				int ed = Integer.MAX_VALUE;
				if ((pageIndex + 1) < this.pageStartPositionList.size()) {
					ed = this.pageStartPositionList.get(pageIndex + 1);
				}
				for (int i = 0; st + i < ed; i++) {
					if (st + i < list.size()) {
						plist.add(list.get(st + i));
					} else {
						break;
					}
				}
			}
			ret.put(this.getMainTableId(), plist);
		}
		return ret;
	}

	/**
	 * ページの初期化処理を行います。
	 * @param pages ページ数。
	 * @throws Exception 例外。
	 */
	protected abstract void initPage(final int pages) throws Exception;


	/**
	 * レポートファイルのデータイメージを取得します。
	 * @return レポートファイルのデータイメージ。
	 * @throws Exception 例外。
	 */
	protected abstract byte[] getReport() throws Exception;


	/**
	 * WebResourceフィールドのデータを取得し、dataに設定します。
	 * @param data データマップ。
	 * @throws Exception 例外。
	 */
	protected void getWebResource(final Map<String, Object> data) throws Exception {
		for (Field<?> field: this.getFieldList()) {
			if (field instanceof WebResource) {
				if (!data.containsKey(field.getId())) {
					data.put(field.getId(), field.getValue());
				}
			}
		}
		for (ReportTable tbl: this.getTableList()) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(tbl.getId());
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> m = list.get(i);
					for (Field<?> f: tbl.getFieldList()) {
						if (f instanceof WebResource) {
							String id = f.getId();
							if (m.get(id) == null) {
								m.put(id, f.getValue());
							}
						}
					}
				}
			}
		}
	}

	/**
	 * レポートを出力します。
	 * @param data 出力データ。
	 * @return 出力データ。
	 * @throws Exception 例外。
	 */
	public byte[] print(final Map<String, Object> data) throws Exception {
		this.getWebResource(data);
		log.debug("data=" + JSON.encode(data, true));
		int pages = this.countPage(data);
		this.initPage(pages);
		for (int i = 0; i < pages; i++) {
			Map<String, Object> pmap = this.getPageData(i, data);
			this.printPage(i, pmap);
		}
		return this.getReport();
	}
}
