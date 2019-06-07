package dataforms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.dao.Table;
import dataforms.dao.file.FileObject;
import dataforms.dao.sqldatatype.SqlBlob;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.common.FileField;
import dataforms.htmltable.HtmlTable;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.StringUtil;
import dataforms.validator.ValidationError;


/**
 * フォームの基本クラス。
 *
 */
public  class Form extends WebComponent {
	/**
	 * Log.
	 */
	private static Logger log = Logger.getLogger(Form.class.getName());

	/**
	 * フィールドリスト。
	 * <pre>
	 * フィールドのバリデーション順を保持するため、リストも用意しておく。
	 * </pre>
	 * TODO:WebComponentのcomponentListに統合することを検討。
	 */
	private FieldList fieldList = new FieldList();

	/**
	 * HTMLテーブルのリスト。
	 */
	private List<HtmlTable> htmlTableList = new ArrayList<HtmlTable>();

	/**
	 * 初期化データ。
	 */
	private Map<String, Object> formDataMap = new HashMap<String, Object>();

	/**
	 * 変換後のサーバデータ。
	 */
	private Map<String, Object> convertedServerData = null;

	/**
	 * コンストラクタ。
	 * @param id フォームID.
	 */
	public Form(final String id) {
		if (id == null) {
			this.setId(this.getDefaultId());
		} else {
			this.setId(id);
		}
		this.convertedServerData = null;
	}


	/**
	 * フィールドリストを取得します。
	 * @return フィールドリスト。
	 */
	public final FieldList getFieldList() {
		return this.fieldList;
	}

	/**
	 * フィールドを追加します。
	 * @param field 追加するフィールドのインスタンス。
	 * @return 追加したフィールド。
	 */
	protected final Field<?> addField(final Field<?> field) {
		this.fieldList.add(field);
		this.addComponent(field);
		return field;
	}

	/**
	 * 指定されたIDの後にフィールドを挿入します。
	 * @param field フィールド。
	 * @param id 指定ID。
	 */
	protected final void insertFieldAfter(final Field<?> field, final String id) {
		this.fieldList.insertAfter(field, id);
		this.addComponent(field);
	}

	/**
	 * 指定されたIDの前にフィールドを挿入します。
	 * @param field フィールド。
	 * @param id 指定ID。
	 */
	protected final void insertFieldBefore(final Field<?> field, final String id) {
		this.fieldList.insertBefore(field, id);
		this.addComponent(field);
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
	 * フィールドリスト中のフィールドをフォームに追加ます。
	 * @param flist フィールドリスト。
	 */
	protected void addFieldList(final FieldList flist) {
		for (Field<?> f : flist) {
			this.addField(f);
		}
	}

	/**
	 * HTMLテーブルを追加します。
	 * @param table HTMLテーブル構造リスト。
	 */
	protected void addHtmlTable(final HtmlTable table) {
		this.htmlTableList.add(table);
		this.addComponent(table);
	}

	/**
	 * HTMLテーブルのリストを取得します。
	 * @return HTMLテーブルのリスト。
	 */
	public List<HtmlTable> getHtmlTableList() {
		return htmlTableList;
	}

	/**
	 * フォームのデータマップを取得します。
	 * @return フォームのデータマップ。
	 */
	public Map<String, Object> getFormDataMap() {
		return this.formDataMap;
	}

	/**
	 * フォームのデータマップを設定します。
	 * @param formDataMap フォームのデータマップ。
	 */
	public void setFormDataMap(final Map<String, Object> formDataMap) {
		this.formDataMap = formDataMap;
	}


	/**
	 * フォームのデータを設定します。
	 * @param id フィールドID。
	 * @param value 値。
	 */
	public void setFormData(final String id, final Object value) {
		this.formDataMap.put(id, value);
	}

	/**
	 * フォームデータを取得します。
	 * @param id フィールドID。
	 * @return データ。
	 */
	public Object getFormData(final String id) {
		return this.formDataMap.get(id);
	}

	@Override
	public void init() throws Exception {
		super.init();
		String htmlPath = this.getHtmlPath();
		if (htmlPath != null) {
			this.setAdditionalHtml(htmlPath);
		}
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> map = super.getProperties();
		map.put("clientValidation", DataFormsServlet.isClientValidation());
		map.put("fieldList", this.fieldList.getFieldListClassInfo());
		List<Map<String, Object>> tlist = new ArrayList<Map<String, Object>>();
		for (HtmlTable ht : this.htmlTableList) {
			tlist.add(ht.getProperties());
		}
		map.put("htmlTableList", tlist);
		map.put("formData", this.convertToClientData(this.formDataMap));
//		map.put("formData", this.formDataMap);
		String htmlPath = this.getHtmlPath();
		if (htmlPath != null) {
			map.put("htmlPath", htmlPath);
		}
		return map;
	}

	/**
	 * htmlに対応するFormクラスを返します。
	 * <pre>
	 * 基本的に自分自身のクラス(this.getClass()の値)を返します。
	 * このメソットが返すクラスに対応した*.htmlファイルをフォームのHTMLとします。
	 * ページクラスと異なるクラスのHTMLを使用したい場合、このメソッド
	 * をオーバーライドします。
	 * </pre>
	 * @return Pageクラス。
	 */
	protected Class<?> getHtmlFormClass() {
		return this.getClass();
	}


	/**
	 * クラスに対応するHTMLが存在する場合、そのPathを返します。
	 * @return HTMLのパス。
	 * @throws Exception 例外。
	 */
	public String getHtmlPath() throws Exception {
		String htmlpath = this.getWebResourcePath(this.getHtmlFormClass()) + ".html";
		String html = this.getWebResource("/" + htmlpath);
		if (StringUtil.isBlank(html)) {
			htmlpath = null;
		}
		return htmlpath;
	}

	/**
	 * フィールド単位のバリデーションを行います。
	 * @param param パラメータ。
	 * @return バリデーションの結果。
	 * @throws Exception 例外。
	 */
	protected List<ValidationError> validateFields(final Map<String, Object> param) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		for (Field<?> field : this.fieldList) {
			Object value = field.getValue(param);
			ValidationError err = field.validate(value);
			if (err != null) {
				ret.add(err);
			}
		}
		for (HtmlTable tbl : this.htmlTableList) {
			ret.addAll(tbl.validate(param));
		}
		return ret;
	}

	/**
	 * フォーム単位のバリデーションを行います。
	 * <pre>
	 * フォーム単位のバリデーションが必要な場合、実装してください。
	 * </pre>
	 * @param data ポストされたデータ。
	 * @return バリデーション結果。
	 * @throws Exception 例外。
	 */
	protected List<ValidationError> validateForm(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		return ret;
	}

	/**
	 * パラメータの内容をチェックします。
	 * @param param パラメータ。
	 * @return チェック結果。
	 * @throws Exception 例外。
	 */
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> ret = validateFields(param);
		if (ret.size() == 0) {
			ret = this.validateForm(this.convertToServerData(param));
		}
		return ret;
	}

	/**
	 * POSTされたパラメータ中の配列のサイズを取得します。
	 * @param id 配列のID。
	 * @param param パラメータ。
	 * @return 配列のサイズ。
	 */
	private int getArraySize(final String id, final Map<String, Object> param) {
		int ret = 0;
		for (String key: param.keySet()) {
			if (key.indexOf(id + "[") == 0) {
				String[] sp = key.split("[\\[\\]]");
				int cnt = Integer.parseInt(sp[1]) + 1;
				if (ret < cnt) {
					ret = cnt;
				}
			}
		}
		return ret;
	}

	/**
	 * Postされたパラメータをサーバで処理する型に変換します。
	 * <pre>
	 * {@code
	 * 各メソッドに渡されたパラメータは基本的にPOSTされた情報のままなので、
	 * 数値型フィールドや日付フィールドも文字列のままPOSTされてきます。
	 * そのデータを数値や日付型に変換したMapを作成します。
	 * またHtmlTable中のデータはtable[i].fieldの形式で送信されてきたものを
	 * List<Map<String, Object>> tableの形式に変換します。
	 * }
	 * </pre>
	 * @param param POSTされたパラメータ。
	 * @return 変換したマップ。
	 */
	public Map<String, Object> convertToServerData(final Map<String, Object> param) {
		if (this.convertedServerData != null) {
			return this.convertedServerData;
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Field<?> f : this.fieldList) {
			String id = f.getId();
			Object v = param.get(f.getId());
			setFieldValue(f, id, v, ret, param);
		}
		if (this.htmlTableList.size() > 0) {
			// HtmlTableの対応.
			for (HtmlTable tbl : this.htmlTableList) {
				String key = tbl.getId();
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				int count = this.getArraySize(key, param);
				log.debug(key + ".size()=" + count);
				for (int i = 0; i < count; i++) {
					Map<String, Object> m = new HashMap<String, Object>();
					for (Field<?> f : tbl.getFieldList()) {
						String id = key + "[" + i + "]." + f.getId();
						Object v = param.get(id);
						this.setFieldValue(f, id, v, m, param);
					}
					list.add(m);
				}
				ret.put(key, list);
			}
		}
		this.convertedServerData = ret;
		return ret;
	}

	/**
	 * フィールドの値を設定します。
	 * @param f フィールド。
	 * @param id paramのID。
	 * @param v 設定する値。
	 * @param m 設定するマップ。
	 * @param param POSTされたパラメータ。
	 */
	private void setFieldValue(final Field<?> f, final String id, final Object v, final Map<String, Object> m, final Map<String, Object> param) {
		if (v != null) {
			// ファイルが指定された場合
			f.setClientValue(v);
			Object cv = f.getValue();
			m.put(f.getId(), cv);
			if (f instanceof FileField) {
				m.put(f.getId() + "Kf", "0"); // 更新を実行する.
			}
		} else {
			// ファイルが指定されていない場合
			Object cv = v;
			if (f instanceof FileField) {
				cv = param.get(id + "_fn");
				if (!StringUtil.isBlank(cv)) {
					// ファイルが指定されておらず、削除も設定されていない場合、データをキープする.
					m.put(f.getId() + "Kf", "1"); // 更新は行わない.
				} else {
					m.put(f.getId() + "Kf", "0"); // 更新する.
				}
			} else {
				f.setClientValue(v);
				cv = f.getValue();
				m.put(f.getId(), cv);
			}
		}
	}

	/**
	 * BlobStoreFileField用のダウンロードパラメータを設定します。
	 * @param field 設定するフィールド。
	 * @param m データマップ。
	 * @throws Exception 例外。
	 */
	protected void setDownloadParameter(final SqlBlob field, final Map<String, Object> m) throws Exception {
		FileObject v = (FileObject) m.get(field.getId());
		v.setDownloadParameter(field.getBlobDownloadParameter(m));
	}


	/**
	 * サーバーで処理したデータをクライアントに表示するための形式に変換します。
	 * @param serverData サーバで処理したデータ。
	 * @return 変換したマップ。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> convertToClientData(final Map<String, Object> serverData) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Field<?> f : this.fieldList) {
			Object v = serverData.get(f.getId());
			if (v != null) {
				// BLOBデータの内容は送信しない.
				if (f instanceof SqlBlob) {
					this.setDownloadParameter((SqlBlob) f, serverData);
				}
				f.setValueObject(v);
				ret.put(f.getId(), f.getClientValue());
			} else {
				f.setValueObject(v);
				ret.put(f.getId(), f.getClientValue());
			}
		}
		if (this.htmlTableList.size() > 0) {
			// HtmlTableの対応.
			for (HtmlTable tbl : this.htmlTableList) {
				String key = tbl.getId();
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> slist = (List<Map<String, Object>>) serverData.get(key);
				if (slist != null) {
					for (int i = 0; i < slist.size(); i++) {
						Map<String, Object> m = new HashMap<String, Object>();
						Map<String, Object> sm = (Map<String, Object>) slist.get(i);
						for (Field<?> f : tbl.getFieldList()) {
							if (sm.containsKey(f.getId())) {
								Object v = sm.get(f.getId());
								if (v != null) {
									if (f instanceof SqlBlob) {
										this.setDownloadParameter((SqlBlob) f, slist.get(i));
									}
								}
								f.setValueObject(v);
								m.put(f.getId(), f.getClientValue());
							}
						}
						if (m.size() == 0) {
							break;
						} else {
							list.add(m);
						}
					}
					ret.put(key, list);
				}
			}

		}
		return ret;
	}

	/**
	 * データ中にユーザ情報設定します。
	 * <pre>
	 * セッション中のログイン情報を取得し、以下キーに設定します。
	 * </pre>
	 * 設定内容
	 *
	 * <table>
	 * 	<caption>設定内容</caption>
	 * 	<thead>
	 * 		<tr>
	 * 			<th>キー</th><th>内容</th><th>備考</th>
	 * 		</tr>
	 *	</thead>
	 *	<tbody>
	 * 		<tr>
	 * 			<td>currentUserId</td><td>ユーザID</td><td>ユーザを表す数値</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>currentLangCode</td><td>現在のリクエストの言語コード</td><td></td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>createUserId</td><td>作成ユーザID</td><td></td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>updateUserId</td><td>更新ユーザID</td><td></td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>deleteFlag</td><td>削除フラグ</td><td>指定されていなかった場合"0"を設定します。</td>
	 * 		</tr>
	 * 	 </tbody>
	 * </table>
	 * @param data 設定するパラメータ。
	 */
	@SuppressWarnings("unchecked")
	public void setUserInfo(final Map<String, Object> data) {
		long userid = this.getPage().getUserId();
		data.put("currentUserId", userid);
		String lang = this.getPage().getCurrentLanguage();
		data.put("currentLangCode", lang);
		data.put("createUserId", userid);
		data.put("updateUserId", userid);
		if (StringUtil.isBlank(data.get("deleteFlag"))) {
			data.put("deleteFlag", "0");
		}
		for (String key : this.getComponentMap().keySet()) {
			Object comp = this.getComponentMap().get(key);
			if (comp instanceof HtmlTable) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(((HtmlTable) comp).getId());
				if (list != null) {
					for (Map<String, Object> m : list) {
						m.put("createUserId", userid);
						m.put("updateUserId", userid);
						if (StringUtil.isBlank(m.get("deleteFlag"))) {
							m.put("deleteFlag", "0");
						}
					}
				}
			}
		}
	}

	/**
	 * リスト中の全マップにユーザ情報を設定します。
	 * @param list リスト。
	 */
	public void setUserInfo(final List<Map<String, Object>> list) {
		for (Map<String, Object> m : list) {
			this.setUserInfo(m);
		}
	}

}
