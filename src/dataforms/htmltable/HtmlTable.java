package dataforms.htmltable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataforms.controller.WebComponent;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.validator.ValidationError;

/**
 * HTMLテーブルクラス。
 * <pre>
 * 対応するHTMLのタグは&lt;table id=&quot;tableId&quot;&gt;...&lt;/table&gt;になります。
 * &lt;table&gt;中に表示するリストのフィールドを管理します。
 * <a href="../../../jsdoc/HtmlTable.html" target="_blank">jsdocを参照</a>
 * </pre>
 */
public class HtmlTable extends WebComponent {

	/**
	 * Logger.
	 */
//	private static Logger log = Logger.getLogger(HtmlTable.class.getName());

	/**
	 * テーブルのキャプション。
	 */
	private String caption = null;
	
	/**
	 * フィールドリスト.
	 */
	private FieldList fieldList = new FieldList();

	/**
	 * 固定カラム数。
	 */
	private int fixedColumns = -1;
	
	/**
	 * 固定カラム幅(テーブル幅に対する%)。
	 */
	private Double fixedWidth = null;
	
	/**
	 * コンストラクタ。
	 * @param id テーブルID。
	 * @param flist フィールドリスト。
	 */
	public HtmlTable(final String id, final FieldList flist) {
		this.setId(id);
		this.fieldList.addAll(flist);
	}


	/**
	 * コンストラクタ。
	 * @param id ID。
	 * @param flist フィールドリスト。
	 */
	public HtmlTable(final String id, final Field<?>... flist) {
		this.setId(id);
		for (Field<?> f : flist) {
			this.fieldList.add(f);
		}
	}

	/**
	 * 固定カラム数を取得します。
	 * @return 固定カラム数。
	 */
	public int getFixedColumns() {
		return fixedColumns;
	}

	/**
	 * スクロール時に固定するカラム数を設定します。
	 * <pre>
	 * CSSでposition:stickyをサポートしたブラウザで動作します。
	 * Chrome/Firefox/Edge/Safariで動作します。IEでは動作しません。
	 * またEdgeでは、横スクロール時にヘッダの固定部分がちらつくという問題が残っています。
	 * </pre>
	 * @param fixedColumns 固定カラム数。
	 * <pre>
	 * &lt;td&gt;&lt;/td&gt;の数で固定するカラム数を指定します。
	 * デフォルト値は-1で、通常のスクロールしないテーブルです。
	 * 0を指定するとヘッダのみ固定し、tbody以下がスクロールします。
	 * 1以上を指定すると横スクロール時に左から指定された数分のカラムが固定されます。
	 * 0以上を指定した場合、対応する&lt;table&gt;&lt;/table&gt;の幅と高さ、
	 * &lt;tbody&gt;&lt;tbody&gt;中の各&lt;td&gt;&lt;/td&gt;の幅を設定する必要があります。
	 * </pre>
	 */
	public void setFixedColumns(final int fixedColumns) {
		this.fixedColumns = fixedColumns;
	}
	
	
	/**
	 * 固定カラム幅(%)を取得します。
	 * @return 固定カラム幅(%)。
	 */
	public Double getFixedWidth() {
		return fixedWidth;
	}


	/**
	 * 固定カラム幅(%)を設定します。
	 * 
	 * <pre>
	 * このプロパティは固定するカラムをテーブル幅の%で指定します。
	 * テーブルの幅に応じて、固定するカラムの幅が変化します。
	 * </pre>
	 * 
	 * @param percent 固定カラム幅(%)。
	 */
	public void setFixedWidth(final Double percent) {
		this.fixedWidth = percent;
	}


	/**
	 * テーブルのキャプションを取得します。
	 * @return テーブルのキャプション。
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * テーブルのキャプションを設定します。
	 * @param caption テーブルのキャプション。
	 * @return 設定を行ったテーブル。
	 */
	public HtmlTable setCaption(final String caption) {
		this.caption = caption;
		return this;
	}


	/**
	 * フィールドリストをコンポーネントマップに登録します。
	 */
	private void setComponentMap() {
		for (Field<?> f : this.fieldList) {
			this.addComponent(f);
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.setComponentMap();
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * テーブル中のフィールドの初期化を行います。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
		for (Field<?> f : this.fieldList) {
//			log.info("field id ="  + f.getId());
			f.init();
		}
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("fieldList", this.fieldList.getFieldListClassInfo());
		ret.put("fixedColumns", this.getFixedColumns());
		ret.put("fixedWidth", this.getFixedWidth());
		return ret;
	}

	/**
	 * フィールドリストを取得します。
	 * @return  フィールドリスト。
	 */
	public FieldList getFieldList() {
		return fieldList;
	}

	/**
	 * フィールドリストを設定します。
	 * @param fieldList フィールドリスト。
	 */
	protected void setFieldList(final FieldList fieldList) {
		this.fieldList = fieldList;
	}

	/**
	 * テーブルのバリデーションを行います。
	 * <pre>
	 * バリデーション対象のデータは変換前のデータであるため、テーブル中の値はtableId[idx].fieldId形式のキーで登録されています。
	 * param中から送信されたデータを取得し、対応するFieldクラスでチェックします。
	 * </pre>
	 * @param param パラメータマップ。
	 * @return 	チェック結果。
	 * @throws Exception 例外。
	 */
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		for (int i = 0;; i++) {
			boolean flg = false;
			for (Field<?> f : this.fieldList) {
				String key = this.getId() + "[" + i + "]." + f.getId();
				if (param.containsKey(key)) {
					flg = true;
					ValidationError err = f.validate(param.get(key));
					if (err != null) {
						err.setFieldId(key);
						ret.add(err);
					}
				}
			}
			if (!flg) {
				break;
			}
		}
		return ret;
	}
}
