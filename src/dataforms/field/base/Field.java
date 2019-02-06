package dataforms.field.base;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.ApplicationError;
import dataforms.controller.JsonResponse;
import dataforms.controller.WebComponent;
import dataforms.dao.Dao;
import dataforms.dao.Query;
import dataforms.dao.Table;
import dataforms.util.StringUtil;
import dataforms.validator.FieldValidator;
import dataforms.validator.RequiredValidator;
import dataforms.validator.ValidationError;

/**
 * フィールドの基本クラス。
 *
 *
 * @param <TYPE> サーバで処理するJavaのデータ型。
 */
public abstract class Field<TYPE> extends WebComponent implements Cloneable {
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(Field.class.getName());

	/**
	 * autocompleteや関連データ取得時に対象のフィールドIDがこのパラメータで渡されます。
	 */
	public static final String ID_CURRENT_FIELD_ID = "currentFieldId";



	/**
	 * 条件マッチタイプ。
	 */
	public enum MatchType  {
		/**
		 * 検索条件フィールドとして使用しない。
		 *
		 */
		NONE,
		/**
		 * 完全一致。
		 * <pre>
		 * 以下の条件式を生成します。
		 * field_id = :field_id
		 * </pre>
		 */
		FULL,
		/**
		 * 部分一致。
		 * <pre>
		 * 以下の条件式を生成します。
		 * field_id like :field_id
		 * パラメータとして渡す値の前後に"%"を付加します。
		 * </pre>
		 */
		PART,
		/**
		 * 先頭一致。
		 * <pre>
		 * 以下の条件式を生成します。
		 * field_id like :field_id
		 * パラメータとして渡す値の末尾に"%"を付加します。
		 * </pre>
		 */
		BEGIN,
		/**
		 * 末尾一致。
		 * <pre>
		 * 以下の条件式を生成します。
		 * field_id like :field_id
		 * パラメータとして渡す値の先頭に"%"を付加します。
		 * </pre>
		 */
		END,
		/**
		 * 範囲開始。
		 * <pre>
		 * 以下の条件式を生成します。
		 * hoge &gt;= :hoge_from
		 *
		 * フィールドIDは"hogeFrom"のように必ず"From"で終わるようにする必要があります。
		 * </pre>
		 */
		RANGE_FROM,
		/**
		 * 範囲終了。
		 *
		 * <pre>
		 * 以下の条件式を生成します。
		 * hoge &lt;= :hoge_to
		 * フィールドIDは"hogeTo"のように必ず"To"で終わるようにする必要があります。
		 * </pre>
		 */
		RANGE_TO,
		/**
		 * 配列の一部一致。
		 * <pre>
		 * 以下の様な条件式を生成します。
		 * hoge IN ('0','1',...'n');
		 * </pre>
		 */
		IN
	}


	/**
	 * ソート順タイプ。
	 */
	public enum SortOrder  {
		/**
		 * 昇順。
		 */
		ASC,
		/**
		 * 降順。
		 */
		DESC,
		/**
		 * ソート無。
		 */
		NONE
	}

	/**
	 * 条件タイプ。
	 */
	private MatchType matchType = Field.MatchType.FULL;

	/**
	 * 条件マッチの際大文字、小文字を区別しない。
	 */
	private boolean caseInsensitive = false;

	/**
	 * ソート順。
	 *
	 */
	private SortOrder sortOrder = Field.SortOrder.ASC;



	/**
	 * テーブル内のフィールドの場合、テーブルへの参照を保持します。
	 */
	private Table table = null;


	/**
	 * デフォルト値。
	 */
	private Object defaultValue = null;


	/**
	 * 値。
	 */
	private Object value = null;



	/**
	 * 項目長。
	 */
	private int length = -1;

	/**
	 * not null 属性。
	 */
	private boolean notNull = false;

	/**
	 * フィールドコメント。
	 */
	private String comment = null;

	/**
	 * フィールドバリデータリスト。
	 *
	 */
	private List<FieldValidator> validatorList = new ArrayList<FieldValidator>();


	/**
	 * 読み取り専用フィールド。
	 */
	private boolean readonly = false;


	/**
	 * 隠しフィールドフラグ。
	 */
	private boolean hidden = false;


	/**
	 * SPANフィールドフラグ。
	 */
	private boolean spanField = false;


	/**
	 * ソート可能フラグ。
	 */
	private boolean sortable = false;



	/**
	 * 読み取り専用フラグを取得します。
	 * @return 読み取り専用の場合true。
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * フィールドを読み取り専用に設定します。
	 *
	 * @param readonly 読み取り専用の場合true。
	 * @return 設定したフィールド。
	 */
	public Field<?> setReadonly(final boolean readonly) {
		this.readonly = readonly;
		return this;
	}

	/**
	 * 隠しフィールドフラグを取得します。
	 * @return 隠しフィールドフラグ。
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * 隠しフィールドフラグを設定します。
	 * <pre>
	 * trueの場合&lt;input type=&quot;hidden&quot; ...&gt;に対応するフィールドであることを示します。
	 * このプロパティは開発ツールでHTMLを作成する際参照されるだけで、実行時には参照されません。
	 * </pre>
	 * @param hidden 隠しフィールドフラグ。
	 * @return 設定したフィールド。
	 */
	public Field<?> setHidden(final boolean hidden) {
		this.hidden = hidden;
		return this;
	}

	/**
	 * spanフィールドフラグを取得します。
	 * @return spanフィールドフラグ。
	 */
	public boolean isSpanField() {
		return spanField;
	}

	/**
	 * spanフィールドフラグを設定します。
	 * trueの場合&lt;span id=&quot;fieldId&quot; &gt;&lt;/span&gt;に対応するフィールドであることを示します。
	 * このプロパティは開発ツールでHTMLを作成する際参照されるだけで、実行時には参照されません。
	 * @param spanField spanフィールドフラグ。
	 * @return 設定したフィールド。
	 */
	public Field<?> setSpanField(final boolean spanField) {
		this.spanField = spanField;
		return this;
	}


	/**
	 * ajax呼び出しの際のパラメータモード。
	 * <pre>
	 * フィールドに定義されたサーバメソッドを呼び出す際に、フィールドが保持する値のみを送信する場合
	 * FIELD_ONLYを設定します。
	 * FORM全体の値を送信する場合FORMを指定します。
	 * </pre>
	 */
	public enum AjaxParameter {
		/** サーバに対し該当フィールドのみ送信します。*/
		FIELD_ONLY,
		/** サーバに対しフォームの全データを送信します。*/
		FORM
	}

	/**
	 * ajax呼び出しの際のパラメータモード。
	 */
	private AjaxParameter ajaxParameter = AjaxParameter.FIELD_ONLY;


	/**
	 * 計算イベント発生フィールドフラグ。
	 */
	private boolean calcEventField = false;


	/**
	 * 計算イベントフィールドフラグを取得する.
	 * @return 計算イベントフィールドフラグ。
	 */
	public boolean isCalcEventField() {
		return calcEventField;
	}

	/**
	 * 計算イベントフィールドフラグを設定しまする
	 * <pre>
	 * trueを設定した場合、対応フィールドのonchageイベントで、配置されたフォームのonCalcメソッドを呼び出します。
	 * </pre>
	 * @param calcEventField 計算イベントフィールド。
	 * @return 設定したフィールド。
	 */
	public Field<?> setCalcEventField(final boolean calcEventField) {
		this.calcEventField = calcEventField;
		return this;
	}


	/**
	 * ajaxパラメータモードを取得します。
	 * @return ajaxパラメータモード。
	 */
	public AjaxParameter getAjaxParameter() {
		return ajaxParameter;
	}

	/**
	 * ajaxパラメータモードを設定します。
	 * @param ajaxParameter ajaxパラメータモード。
	 * @return 設定したフィールド。
	 */
	public Field<?> setAjaxParameter(final AjaxParameter ajaxParameter) {
		this.ajaxParameter = ajaxParameter;
		return this;
	}


	/**
	 * コンストラクタ。
	 * <pre>
	 * idにnullを指定すると、クラス名の先頭を小文字にし末尾の"Field"を取り除いた文字列をIDとします。
	 * </pre>
	 * @param id フィールドID.
	 */
	public Field(final String id) {
		if (id == null) {
			this.setId(this.getDefaultId());
		} else {
			this.setId(id);
		}
	}

	/**
	 * テーブルを取得します。
	 * <pre>
	 * フィールドをテーブルに配置した場合、そのテーブルを返します。
	 * </pre>
	 * @return テーブル。
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * テーブルを設定する。
	 * <pre>
	 * テーブルに配置されると呼び出され、テーブルを設定します。
	 * </pre>
	 * @param table テーブル。
	 */
	public void setTable(final Table table) {
		this.table = table;
	}


	/**
	 * フィールド長を取得します。
	 * @return フィールド長。
	 */
	public int getLength() {
		return length;
	}



	/**
	 * 項目長を設定します。
	 * @param length 項目長。
	 * @return 変更されたフィールド。
	 */
	public Field<?> setLength(final int length) {
		this.length = length;
		return this;
	}



	/**
	 * コメントを取得します。
	 * @return コメント。
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * コメントを設定します。
	 * @param comment コメント。
	 * @return 変更されたフィールド。
	 */
	public Field<?> setComment(final String comment) {
		this.comment = comment;
		return this;
	}


	/**
	 * not null属性を取得します。
	 * @return not null属性。
	 */
	public boolean isNotNull() {
		return notNull;
	}

	/**
	 * not null属性を設定する.
	 * @param notNull not null属性.
	 * @return 変更されたフィールド.
	 */
	public Field<?> setNotNull(final boolean notNull) {
		this.notNull = notNull;
		return this;
	}



	/**
	 * フィールドバリデータを追加します。
	 * @param validators 追加するバリデータ。
	 * @return 設定されたフィールド。
	 */
	public final Field<?> addValidator(final FieldValidator ... validators) {
		int idx = this.validatorList.size();
		for (FieldValidator v : validators) {
			v.setId("v" + (idx++));
			v.setFieldId(this.getId());
			this.validatorList.add(v);
			this.addComponent(v);
		}
		return this;
	}

	/**
	 * フィールドバリデータをクリアします。
	 * @return パリデータがクリアされたフィールド。
	 */
	public final Field<?> clearValidator() {
		this.validatorList.clear();
		return this;
	}

	/**
	 * 必須バリデータを削除します。
	 * @return 必須パリデータが削除されたフィールド。
	 */
	public final Field<?> removeRequiredValidator() {
		ArrayList<FieldValidator> newlist = new ArrayList<FieldValidator>();
		for (FieldValidator v: this.validatorList) {
			if (!(v instanceof RequiredValidator)) {
				newlist.add(v);
			}
		}
		this.validatorList = newlist;
		return this;
	}

	/**
	 * 指定クラスのバリデータを削除します。
	 * @param validatorClass バリデータクラス。
	 * @return バリデータが削除されたフィールド。
	 */
	public final Field<?> removeValidator(final Class<? extends FieldValidator> validatorClass) {
		ArrayList<FieldValidator> newlist = new ArrayList<FieldValidator>();
		for (FieldValidator v: this.validatorList) {
			if (!(validatorClass.isAssignableFrom(v.getClass()))) {
				newlist.add(v);
			}
		}
		this.validatorList = newlist;
		return this;
	}


	/**
	 * ポストされた情報からフィールドに対応した値を取得します。
	 * @param param ポストされた情報。
	 * @return フィールドに対応した値。
	 */
	public Object getValue(final Map<String, Object> param) {
		return param.get(this.getId());
	}

	/**
	 * フィールド単位のバリデーションを行います。
	 * @param value 確認する値。
	 * @return 確認結果。
	 * @throws Exception 例外。
	 */
	public ValidationError validate(final Object value) throws Exception {
		ValidationError ret = null;
		for (FieldValidator v : this.validatorList) {
			if (!v.validate(value)) {
				ret = new ValidationError(this.getId(), v.getMessage());
				break;
			}
		}
		return ret;
	}

	/**
	 * like 文用のエスケープを行ないます。
	 * @param str 文字列。
	 * @return エスケープした文字列。
	 */
	private String likeEscape(final String str) {
		String ret = str;
		ret = ret.replaceAll("\\\\", "\\\\\\\\");
		ret = ret.replaceAll("%", "\\\\%");
		ret = ret.replaceAll("_", "\\\\_");
		return ret;
	}

	/**
	 * 値を取得します。
	 * @return 値。
	 */
	@SuppressWarnings("unchecked")
	public TYPE getValue() {
		Object ret = value;
		return (TYPE) ret;
	}

	/**
	 * 値を設定します。
	 * <pre>
	 * 型がチェックされます。
	 * </pre>
	 * @param value 値。
	 */
	public void setValue(final TYPE value) {
		this.value = value;
	}


	/**
	 * 値を設定します。
	 * <pre>
	 * 型がチェックされません。
	 * </pre>
	 * @param value 値.
	 */
	public void setValueObject(final Object value) {
		this.value = value;
	}

	/**
	 * 値を取得します。
	 * @return 値。
	 */
	public Object getValueObject() {
		return this.value;
	}

	/**
	 * フォームから送信された値を設定します。
	 * <pre>
	 * フォームから呼び出された時に送信された値をフィールドに設定する処理を実装します。
	 * 通常送信される型は文字列で、それを数値や日時などのJavaで処理しやすい型に変換して
	 * 設定する処理を実装します。
	 * </pre>
	 * @param v フォームから送信された値。
	 */
	public abstract void setClientValue(final Object v);


	/**
	 * クライアントに送信する値を取得します。
	 * <pre>
	 * valueプロパティに保持した値をクライアントに送信するための文字列に変換します。
	 * 通常はvalueそのものを返すようになっていますが、数値や日時などのフィールドは
	 * クライアントに表示する形式の文字列に変換します。
	 * </pre>
	 * @return クライアントに送信する値。
	 */
	public Object getClientValue() {
		if (this.value instanceof String) {
			return StringEscapeUtils.escapeHtml4((String) value);
		} else {
			return this.value;
		}

	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> map = super.getProperties();
		if (this.validatorList != null) {
			List<Map<String, Object>> vlist = new ArrayList<Map<String, Object>>();
			for (FieldValidator f : this.validatorList) {
				vlist.add(f.getProperties());
			}
			map.put("validatorList", vlist);
		}
		if (this.length > 0) {
			map.put("length" , this.length);
		}
		if (this.isRelationDataAcquisition()) {
			map.put("relationDataAcquisition", true);
		} else {
			map.put("relationDataAcquisition", false);
		}
		if (this.getRelationDataEvent() == RelationDataEvent.BLUR) {
			map.put("relationDataEvent", "BLUR");
		} else {
			map.put("relationDataEvent", "CHANGE");
		}
		if (this.isAutocomplete()) {
			map.put("autocomplete", true);
		} else {
			map.put("autocomplete", false);
		}
		if (this.isCalcEventField()) {
			map.put("calcEventField", true);
		} else {
			map.put("calcEventField", false);
		}
		map.put("ajaxParameter", this.getAjaxParameter().name());
		map.put("readonly", this.isReadonly());
		map.put("sortable", this.isSortable());
		map.put("sortOrder", this.getSortOrder().name());
		return map;
	}


	/**
	 * DBのカラム名を取得します。
	 * <pre>
	 * Camel記法で指定されたIDをSnake記法に変換した文字列を返します。
	 * </pre>
	 * @return DBのカラム名。
	 */
	public String getDbColumnName() {
		return StringUtil.camelToSnake(this.getId());
	}


	/**
	 * 問い合わせ用のカラム名(alias.columnname形式)を取得します。
	 * @return DBのカラム名。
	 */
	public String getQueryColumnName() {
		return this.getTable().getAlias() + "." + StringUtil.camelToSnake(this.getId());
	}


	/**
	 * マッチタイプを取得します。
	 * @return マッチタイプ。
	 */
	public MatchType getMatchType() {
		return matchType;
	}


	/**
	 * マッチタイプを設定するします。
	 * <pre>
	 * 問い合わせフォームにフィールドを配置する場合、マッチタイプを指定すると
	 * 問い合わせの際に作成する条件式を制御することができます。
	 * </pre>
	 *
	 * @param matchType マッチタイプ。
	 * @return 変更後のフィールド。
	 */
	public Field<?> setMatchType(final MatchType matchType) {
		this.matchType = matchType;
		return this;
	}

	/**
	 * マッチ対象のフィールドIDを取得します。
	 * <pre>
	 * MatchType.RANGE_FROMの時は末尾のFromを削除します。
	 * MatchType.RANGE_TOの時は末尾のToを削除します。
	 * 上記以外はidをそのまま返すします。
	 * </pre>
	 * @return マッチ対象のフィールドID。
	 */
	public String getMatchFieldId() {
		String id = this.getId();
		if (this.getMatchType() == MatchType.RANGE_FROM) {
			id = id.replaceAll("From$", "");
		} else if (this.getMatchType() == MatchType.RANGE_TO) {
			id = id.replaceAll("To$", "");
		}
		return id;
	}

	/**
	 * バリデータリストを取得します。
	 * @return バリテータリスト。
	 */
	public List<FieldValidator> getValidatorList() {
		return validatorList;
	}

	@Override
	public Field<?> clone() {
		Field<?> ret = null;
		try {
			ret = (Field<?>) super.clone();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
		return ret;
	}

	/**
	 * 関連データ取得フラグ。
	 */
	private boolean relationDataAcquisition = false;

	/**
	 * 自動入力補完フラグ。
	 */
	private boolean autocomplete = false;

	/**
	 * 関連データ取得フラグを取得します。
	 *
	 * @return 関連データ取得フラグフラグ。
	 */
	public boolean isRelationDataAcquisition() {
		return relationDataAcquisition;
	}

	/**
	 * 関連データ取得フラグを設定します。
	 * <pre>
	 * trueの場合、onblurイベントでフィールド自身の
	 * queryRelationDataメソッドを呼び出す用に設定されます。
	 * queryRelationDataメソッドには、入力データに対応した関連情報を取得する処理を実装します。
	 * </pre>
	 * @param relationDataAcquisition 関連データ取得フラグ。
	 * @return 変更後のフィールド。
	 */
	public Field<?> setRelationDataAcquisition(final boolean relationDataAcquisition) {
		this.relationDataAcquisition = relationDataAcquisition;
		return this;
	}

	/**
	 * 関連データの取得イベントの種類。
	 */
	public enum RelationDataEvent  {
		/**
		 * フォーカスを失ったとき。
		 */
		BLUR,
		/**
		 * 変更されたとき。
		 */
		CHANGE,
	}

	/**
	 * 関連データ取得イベント。
	 */
	private RelationDataEvent relationDataEvent = RelationDataEvent.BLUR;


	/**
	 * 関連データの取得イベントの種類を取得します。
	 * @return 関連データの取得イベントの種類。
	 */
	public RelationDataEvent getRelationDataEvent() {
		return relationDataEvent;
	}

	/**
	 * 関連データの取得イベントの種類を設定します。
	 * @param relationDataEvent 関連データの取得イベントの種類。
	 */
	public void setRelationDataEvent(final RelationDataEvent relationDataEvent) {
		this.relationDataEvent = relationDataEvent;
	}

	/**
	 * 自動入力補完フラグを取得します。
	 * @return 自動入力補完フラグ。
	 */
	public boolean isAutocomplete() {
		return autocomplete;
	}

	/**
	 * 自動入力補完フラグを設定します。
	 * <pre>
	 * trueの場合、フィールドに対してautocompleteが設定されます。
	 * この場合querySourceListメソッドを実装し、入力候補リストを返す必要があります。
	 * </pre>
	 * @param autocomplete 自動入力補完フラグ。
	 * @return 設定されたフィールド。
	 */
	public Field<?> setAutocomplete(final boolean autocomplete) {
		this.autocomplete = autocomplete;
		return this;
	}

	/**
	 * 関連データマップを取得します。
	 * <pre>
	 * relationDataAcquisition=trueの場合、フィールドの値が変更された時に
	 * 呼び出されます。入力されたデータに対応した関連情報を返します。
	 * </pre>
	 * @param data パラメータ。
	 * @return 関連データマップ。
	 * @throws Exception 例外。
	 */
	protected Map<String, Object> queryRelationData(final Map<String, Object> data) throws Exception {
		return null;
	}

	/**
	 * データのキーに行IDを追加します。
	 * <pre>
	 * データベースから取得したデータを、テーブルの指定行に設定するために
	 * 各項目のキーに行IDを追加します。
	 * </pre>
	 * @param rowid 行ID。
	 * @param map データ。
	 * @return 行IDを追加したデータ。
	 */
	protected Map<String, Object> addRowInfo(final String rowid, final Map<String, Object> map) {
		if (StringUtil.isBlank(rowid)) {
			return map;
		} else {
			if (map != null) {
				Map<String, Object> ret = new HashMap<String, Object>();
				for (String key: map.keySet()) {
					if (key.indexOf("].") > 0) {
						ret.put(key, map.get(key));
					} else {
						Object value = map.get(key);
						ret.put(rowid + "." + key, value);
					}
				}
				return ret;
			} else {
				return map;
			}
		}
	}


	/**
	 * 関連データを取得します。
	 * @param param パラメータ。
	 * @return JsonResponse。
	 * @throws Exception 例外。
	 */
    @WebMethod
	public JsonResponse getRelationData(final Map<String, Object> param) throws Exception {
    	this.methodStartLog(log, param);
		String id = (String) param.get(ID_CURRENT_FIELD_ID);
		String rowid = this.getHtmlTableRowId(id);
    	JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, this.addRowInfo(rowid, this.queryRelationData(param)));
    	this.methodFinishLog(log, param);
    	return ret;
    }


	/**
	 * 選択肢ソースのリストを取得する処理.
	 * @param data パラメータ.
	 * @return 入力候補のリスト.
	 * @throws Exception 例外.
	 */
	protected List<Map<String, Object>> queryAutocompleteSourceList(final Map<String, Object> data) throws Exception {
		return new ArrayList<Map<String, Object>>();
	}

	/**
	 * 選択肢ソースのリストを取得する処理.
	 * @param param パラメータ.
	 * @return 選択肢リスト.
	 * @throws Exception 例外.
	 */
    @WebMethod
	public JsonResponse getAutocompleteSource(final Map<String, Object> param) throws Exception {
    	this.methodStartLog(log, param);
    	List<Map<String, Object>> list = this.queryAutocompleteSourceList(param);
    	JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, list);
    	this.methodFinishLog(log, result);
    	return result;
    }


	/**
	 * キーから"xxx[x]."の部分を取り除いたマップを取得します。
	 * @param param パラメータ。
	 * @return 変換したマップ。
	 *
	 * @deprecated getRowMapを利用してください。
	 */
	public Map<String, Object> getLineMap(final Map<String, Object> param) {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (String k : param.keySet()) {
			Object v = param.get(k);
			ret.put(k.replaceAll("^.+\\[[0-9]+\\]\\.", ""), v);
		}
		return ret;
	}


	/**
	 * キーから"xxx[x]."の部分を取り除いたマップを取得します。
	 * @param param パラメータ。
	 * @return 変換したマップ。
	 *
	 */
	public Map<String, Object> getRowMap(final Map<String, Object> param) {
		Map<String, Object> ret = new HashMap<String, Object>();
		for (String k : param.keySet()) {
			Object v = param.get(k);
			ret.put(k.replaceAll("^.+\\[[0-9]+\\]\\.", ""), v);
		}
		return ret;
	}

	/**
	 * 自動入力補完用リストを作成します。
	 * @param rowid テーブル行ID(tablename[idx]形式の文字列)。
	 * <pre>
	 * 基本的にcurrentFieldIdで指定されたフィールドIDをgetHtmlTableRowIdメソッドに渡した結果を指定します。
	 * この項目を指定することにより、HtmlTable中のフィールドに対応することができるようになります。
	 * HtmlTable中で使用しなければ、nullを指定します。
	 * </pre>
	 * @param result クエリ結果。
	 * @param valueField 値となるフィールド。
	 * @param labelField ラベルとなるフィールド。
	 * @param options オプションフィール。
	 * @return 結果リスト。
	 */
	protected List<Map<String, Object>> convertToAutocompleteList(final String rowid, final List<Map<String, Object>> result, final String valueField, final String labelField, final String ... options) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: result) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (String f: options) {
				map.put(f, m.get(f));
			}
			Map<String, Object> r = this.addRowInfo(rowid, map);
			r.put("value", m.get(valueField));
			r.put("label", m.get(labelField));
			list.add(r);
		}
		return list;
	}

	/**
	 * デフォルト値を取得します。
	 * <pre>
	 * テーブルに登録する際のデフォルト値を取得します。
	 * </pre>
	 * @return デフォルト値。
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * デフォルト値を設定します。
	 * <pre>
	 * テーブルに登録する際のデフォルト値を設定します。
	 * </pre>
	 * @param defaultValue デフォルト値。
	 * @return 設定されたフィールド。
	 */
	public Field<?> setDefaultValue(final Object defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}


	/**
	 * DBに記録する値を取得します。
	 * <pre>
	 * 通常のフィールドではvalueをそのままDBに記録します。
	 * DB保存の際に何らかの変換が必要な場合オーバーライドしたメソッドで変換します。
	 * MatchType.BEGIN, MatchType.PART, MatchType.ENDが指定されている場合、
	 * like文のパラメータで使用できるように"%"を前後に付加します。
	 * </pre>
	 * @return DBに記録する値.
	 */
	public Object getDBValue() {
		Object ret = value;
		Object cv = value;
		if (cv instanceof String) {
			String str = (String) cv;
			if (!StringUtil.isBlank(str)) {
				if (this.getMatchType() == MatchType.BEGIN) {
					str = this.likeEscape(str) + "%";
				} else if (this.getMatchType() == MatchType.PART) {
					str = "%" + this.likeEscape(str) + "%";
				} else if (this.getMatchType() == MatchType.END) {
					str =  "%" + this.likeEscape(str);
				}
			}
			ret = str;
		}
		return ret;
	}

	/**
	 * DBから読み込んだ値を設定します。
	 * <pre>
	 * 通常のフィールドではDBから読み込んだ値をそのまま設定します。
	 * DB保存の際に何らかの変換が必要な場合オーバーライドしたメソッドで変換します。
	 * </pre>
	 * @param value DBから読み込んだ値。
	 */
	public void setDBValue(final Object value) {
		this.value = value;
	}


/*	protected boolean superclassHasLengthOption() {
		boolean ret = false;
		Class<?> cls = this.getClass().getSuperclass();
		Constructor<?>[] cns = cls.getConstructors();
		for (Constructor<?> c: cns) {
			Class<?>[] ptlist = c.getParameterTypes();
			for (Class<?> pt: ptlist) {
				if (pt.isPrimitive()) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
*/
	/**
	 * フィールドのオプションを取得します。
	 * <pre>
	 * 主に項目長等のオプション文字列を取得します。
	 * テーブルJavaクラス作成のフィールドオプション欄で編集する文字列になり、
	 * フィールドクラスのコンストラクタの引数に指定します。
	 * </pre>
	 * @return フィールドのオプション。
	 */
	public String getFieldOption() {
		return null;
	}


	/**
	 * dataforms.jarが提供するフィールドかどうかを判定します。
	 * @param classname フィールドの完全クラス名。
	 * @return dataforms.jarが提供するフィールドクラスの場合true.
	 */
	public static boolean isDataformsField(final String classname) {
		boolean ret = false;
		if (classname.indexOf("dataforms.field") >= 0 || classname.indexOf("dataforms.app.field") >= 0) {
			try {
				Class<?> cls = Class.forName(classname);
				if (cls != null) {
					if (Field.class.isAssignableFrom(cls)) {
						ret = true;
					}
				}
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
		}
		return ret;
	}


	/**
	 * コンストラクタの引数を解析し、項目長のパラメータがあるかどうかを確認します。
	 * @return 項目長があるフィールドの場合true。
	 */
	public boolean hasLengthParameter() {
		return hasLengthParameter(this.getClass());
	}

	/**
	 * 指定されたクラスのコンストラクタの引数を解析し、項目長のパラメータがあるかどうかを確認します。
	 * @param cls 指定されたクラス。
	 * @return 項目長があるフィールドの場合true。
	 */
	public static boolean hasLengthParameter(final Class<?> cls) {
		boolean ret = false;
		Constructor<?>[] clist = cls.getConstructors();
		Constructor<?> cns = null;
		for (Constructor<?> c: clist) {
			if (c.getParameterCount() > 1) {
				cns = c;
			}
		}
		if (cns != null) {
			// 引数が2件以上のコンストラクタがある
			Class<?> [] plist = cns.getParameterTypes();
			if ("int".equals(plist[1].getName())) {
				ret = true;
			}
		}
		return ret;
	}


	/**
	 * フィールド長パラメータを作成します。
	 * @return フィールド長パラメータ。
	 * @throws Exception 例外。
	 */
	public String getLengthParameter() throws Exception {
		if (Field.isDataformsField(this.getClass().getName())) {
			if (this.hasLengthParameter()) {
				return this.getLengthParameterSample();
			} else {
				return null;
			}
		} else {
			return this.getFieldOption();
		}
	}

	/**
	 * フィールド長パラメータサンプルを作成します。
	 * @return フィールド長パラメータサンプル。
	 * @throws Exception 例外。
	 */
	protected String getLengthParameterSample() throws Exception {
		return null;
	}


	/**
	 * フィールド長パラメータチェックパターンを返します。
	 * @return パラメータチェックパターン(正規表現)。
	 * @throws Exception 例外。
	 */
	public String getLengthParameterPattern() throws Exception {
		return null;
	}

	/**
	 * 設定されているIDがデフォルトIDかどうかを確認します。
	 * @return デフォルトIDの場合true。
	 */
	public boolean idIsDefault() {
		return this.getId().equals(this.getDefaultId());
	}

	/**
	 * SubQuery用フィールドを作成します。
	 * <pre>
	 * SubQuery用のフィールドリストに設定するためのインスタンスコピーを作成します。
	 * 通常のフィールドは自分自身のコピーを返しますが、
	 * SumField等は集計対象のフィールドのコピーを返します。
	 * </pre>
	 * @return SubQuery用フィールド。
	 */
	public Field<?> cloneForSubQuery() {
		return this.clone();
	}

	/**
	 * 問い合わせ実行時のソート順を取得します。
	 * @return ソート順。
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}

	/**
	 * 問い合わせ実行時のソート順を設定します。
	 * <pre>
	 * この値は問い合わせ用SQL作成時に参照されます。
	 * </pre>
	 * @param sortOrder ソート順。
	 * @return 設定したフィールド。
	 */
	public Field<?> setSortOrder(final SortOrder sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}

	/**
	 * 大文字/小文字無視フラグを取得します。
	 * @return 大文字/小文字無視フラグ。
	 */
	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}

	/**
	 * 大文字/小文字無視フラグを設定します。
	 * <pre>
	 * trueを設定すると、'=','like'の条件マッチの際、大文字小文字を無視します。
	 * </pre>
	 * @param caseInsensitive 大文字/小文字無視フラグ。
	 * @return 変更後のフィールド。
	 */
	public Field<?> setCaseInsensitive(final boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
		return this;
	}

	/**
	 * ソート可能フラグを取得します。
	 * <pre>
	 * HtmlTableに配置した場合カラムソートを行います。
	 * </pre>
	 * @return ソート可能フラグ。
	 *
	 */
	public boolean isSortable() {
		return sortable;
	}

	/**
	 * ソート可能フラグを設定します。
	 * <pre>
	 * trueを指定した場合、HtmlTableに配置した場合カラムソートを行います。
	 * ソート順はデフォルトでソート無の状態となります。
	 * </pre>
	 * @param sortable ソート可能フラグ。
	 * @return 設定したフィールド。
	 */
	public Field<?> setSortable(final boolean sortable) {
		this.sortable = sortable;
		this.sortOrder = SortOrder.NONE;
		return this;
	}

	/**
	 * ソート可能フラグを設定します。
	 * @param sortable ソート可能フラグ。
	 * @param order デフォルトソート順。
	 * @return 設定したフィールド。
	 */
	public Field<?>  setSortable(final boolean sortable, final SortOrder order) {
		this.sortable = sortable;
		this.sortOrder = order;
		return this;
	}

	/**
	 * デフォルト条件マッチタイプを取得します。
	 * <pre>
	 * フィールドクラス単位に設定された、デフォルト条件マッチタイプを返します。
	 * このメソットは開発ツールでQueryFormのソースコード生成時に呼び出され、
	 * この値に応じたフィールドが生成されます。と
	 * </pre>
	 * @return デフォルト条件マッチタイプ。
	 */
	public MatchType getDefaultMatchType() {
		return MatchType.FULL;
	}


	/**
	 * データベースごとのタイプマップ。
	 */
	private Map<String, String> dbDependentTypeMap = null;

	/**
	 * 標準以外のデータ型を使用する場合に指定します。
	 * <pre>
	 * SqlBlobから派生したフィールドは、通常はblob型のカラムが作成されます。
	 * ところがRDBMSによってはblobに対応するデータ型の名称が異なる場合があります。
	 * たとえばMySQLではlongblobで作成されるようになっています。
	 * さらにMySQLではblobに対応するデータ型は最大サイズによってtinyblob、blob、mediumblob、longblobの4種類
	 * があります。
	 * あえてこれらのデータ型を使用した場合、このメソッドで、RDBMS毎に使用するデータ型を指定することができます。
	 * MySQLではtinyblobで運用したいフィールドの場合、コンストラクタで以下のように設定します。
	 *
	 * 	setDatabaseType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "tinyblob");
	 *
	 * </pre>
	 * @param databaseProductName データベース製品名。
	 * @param typeName データタイプ名。
	 */
	public void setDbDependentType(final String databaseProductName, final String typeName) {
		if (this.dbDependentTypeMap == null) {
			this.dbDependentTypeMap = new HashMap<String, String>();
		}
		this.dbDependentTypeMap.put(databaseProductName, typeName);
	}

	/**
	 * データベース製品名毎の特殊データ型を取得します。
	 *
	 * @param databaseProductName データベース製品名。
	 * @return RDBMS毎の特殊データ型。
	 */
	public String getDbDependentType(final String databaseProductName) {
		if (this.dbDependentTypeMap != null) {
			return this.dbDependentTypeMap.get(databaseProductName);
		} else {
			return null;
		}
	}

	/**
	 * フィールドのデフォルトカラム幅を計算します。
	 * <pre>
	 * setFixedColumnsに0以上が設定されたHtmlTableにフィールドが配置された場合、
	 * そのテーブルの&lt;tbody&gt;&lt;/tbody&gt;内の各&lt;td&gt;&lt;/td&gt;は、
	 * その幅を指定する必要があります。
	 * 解発ツールのWebリソース作成でHTMLを自動生成する場合、このメソットを
	 * 利用しててテーブルのカラム幅を決定します。
	 * </pre>
	 * @return フィールドのデフォルトカラム幅。
	 */
	public int calcDefaultColumnWidth() {
		String label = this.getComment();
		if (label == null) {
			label = this.getId();
		}
		return (label.length() + 2) * 14;
	}

	/**
	 * HTML生成フラグ。
	 * <pre>
	 * 開発ツールでHTMLに出力する場合trueを設定します。
	 * </pre>
	 */
	private boolean htmlGeneration = true;

	/**
	 * 開発ツールでHTMLを生成するフィールドの場合trueを返します。
	 * @return HTML生成フラグ。
	 */
	public boolean isHtmlGeneration() {
		return htmlGeneration;
	}

	/**
	 * 開発ツールでHTMLを生成するフィールドの場合trueを設定します。
	 * @param htmlGeneration HTML生成フラグ。
	 */
	public void setHtmlGeneration(final boolean htmlGeneration) {
		this.htmlGeneration = htmlGeneration;
	}


	/**
	 * ラベル構築用インターフェース。
	 *
	 */
	@FunctionalInterface
	public interface AutocompleteLabelBuilder {
		/**
		 * Autocompleteに表示するラベルを構築します。
		 * @param data データ。
		 * @param ids マップリスト。
		 * @return ラベル。
		 */
		public String getLabel(final Map<String, Object> data, final String... ids);
	}

	/**
	 * 問い合わせ条件の構築用関数インターフェース。
	 *
	 */
	@FunctionalInterface
	public interface QueryConditionBuilder {
		/**
		 * Autocompleteに表示するラベルを構築します。
		 * @param query 問合せ。
		 * @param cfid 編集されたフィールドID。
		 * @param data データ。
		 * @param ids マップリスト。
		 */
		public void setCondition(final Query query, final String cfid, final Map<String, Object> data, final String... ids);
	}



	/**
	 * オートコンプリート用のリストを取得します。
	 * <pre>
	 * 	List&lt;Map&lt;String, Object&gt;&gt; queryAutocompleteSourceList(final Map&lt;String, Object&gt; data)メソッドの
	 * 実装に便利なメソッドです。
	 * </pre>
	 * @param data 問合せ時にPOSTされたデータ。
	 * @param query 問合せクラス。
	 * @param lb ラベル構築関数インターフェース。
	 * @param qcb 問合せ条件構築関数インターフェース。
	 * @param ids フィールドIDリスト
	 * @return オートコンプリートのリスト。
	 * @throws Exception 例外。
	 */
	protected List<Map<String, Object>> queryAutocompleteSourceList(final Map<String, Object> data, final Query query, final AutocompleteLabelBuilder lb, final QueryConditionBuilder qcb, final String... ids) throws Exception {
		String id = (String) data.get(ID_CURRENT_FIELD_ID); // 対象のフィールドIDを取得する。

		qcb.setCondition(query, id, data, ids);
		Dao dao = new Dao(this);
		List<Map<String, Object>> list = dao.executeQuery(query);
		for (Map<String, Object> m: list) {
			m.put("label", lb.getLabel(m, ids));
		}
		String[] ridlist = new String[ids.length - 1];
		for (int i = 1; i < ids.length; i++) {
			ridlist[i - 1] = ids[i];
		}
		// 入力候補リストを返す。
		return this.convertToAutocompleteList(
				this.getHtmlTableRowId(id), // EditableHtmlTable中のフィールドに対応するため、HtmlTableの行のIDを取得する。
				list,	// 検索結果リスト。
				ids[0],	// このフィールドに設定する値
				"label",	// 選択肢のリストに表示する値
				ridlist
		);
	}

	/**
	 * オートコンプリート用のリストを取得します。
	 * <pre>
	 * 	List&lt;Map&lt;String, Object&gt;&gt; queryAutocompleteSourceList(final Map&lt;String, Object&gt; data)メソッドの実装に便利なメソッドです。
	 * </pre>
	 * @param data 問合せ時にPOSTされたデータ。
	 * @param query 問合せクラス。
	 * @param lb ラベル構築関数インターフェース。
	 * @param ids フィールドIDリスト
	 * @return オートコンプリートのリスト。
	 * @throws Exception 例外。
	 */
	protected List<Map<String, Object>> queryAutocompleteSourceList(final Map<String, Object> data, final Query query, final AutocompleteLabelBuilder lb,  final String... ids) throws Exception {
		return this.queryAutocompleteSourceList(
			data
			, query
			, lb
			, (final Query q, final String cfid, final Map<String, Object> d, final String... idlist)->{
				FieldList flist = new FieldList();
				flist.addField(query.getFieldList().get(idlist[0])).setMatchType(MatchType.PART);
				Map<String, Object> p = new HashMap<String, Object>();
				p.put(idlist[0], d.get(cfid));
				query.setQueryFormFieldList(flist);
				query.setQueryFormData(p);
			}
			, ids
		);
	}

	/**
	 * オートコンプリート用のリストを取得します。
	 * <pre>
	 * 	List&lt;Map&lt;String, Object&gt;&gt; queryAutocompleteSourceList(final Map&lt;String, Object&gt; data)メソッドの実装に便利なメソッドです。
	 * </pre>
	 * @param data 問合せ時にPOSTされたデータ。
	 * @param query 問合せクラス。
	 * @param ids フィールドIDリスト
	 * @return オートコンプリートのリスト。
	 * @throws Exception 例外。
	 */
	protected List<Map<String, Object>> queryAutocompleteSourceList(final Map<String, Object> data, final Query query, final String... ids) throws Exception {
		return this.queryAutocompleteSourceList(
			data
			, query
			, (Map<String, Object> map, String ... idlist) -> {
				return (String) map.get(idlist[0]);
			}
			, ids
		);
	}
/**
	 * 関連データを取得します。
	 * <pre>
	 * Map&lt;String, Object&gt; queryRelationData(final Map&lt;String, Object&gt; data)メソッドの実装に便利なメソッドです。
	 * </pre>
	 * @param data 問合せ時にPOSTされたデータ。
	 * @param query 問合せクラス。
	 * @param qcb 問合せ条件構築関数インターフェース。
	 * @param ids IDリスト。
	 * @return 関連データマップ。
	 * @throws Exception 例外。
	 */
	protected Map<String, Object> queryRelationData(final Map<String, Object> data, final Query query, final QueryConditionBuilder qcb, final String... ids) throws Exception {
		Map<String, Object> blankMap = new HashMap<String, Object>();
		for (int i = 1; i < ids.length; i++) {
			blankMap.put(ids[i], null);
		}
		String id = (String) data.get(ID_CURRENT_FIELD_ID);
//		String fid = this.getHtmlTableColumnId(id);
		String text = (String) data.get(id);
		if (StringUtil.isBlank(text)) {
			return blankMap;
		}
		qcb.setCondition(query, id, data, ids);
		Dao dao = new Dao(this); // Dao使用し、完全一致で検索する。
		List<Map<String, Object>> list = dao.executeQuery(query);
		if (list.size() >= 1) {
			Map<String, Object> ret = new HashMap<String, Object>();
			for (int i = 1; i < ids.length; i++) {
				ret.put(ids[i], list.get(0).get(ids[i]));
			}
			return ret;
		} else {
			return blankMap;
		}
	}

	/**
	 * 関連データを取得します。
	 * <pre>
	 * Map&lt;String, Object&gt; queryRelationData(final Map&lt;String, Object&gt; data)メソッドの実装に便利なメソッドです。
	 * </pre>
	 * @param data 問合せ時にPOSTされたデータ。
	 * @param query 問合せクラス。
	 * @param ids IDリスト。
	 * @return 関連データマップ。
	 * @throws Exception 例外。
	 */
	protected Map<String, Object> queryRelationData(final Map<String, Object> data, final Query query, final String... ids) throws Exception {
		return this.queryRelationData(
			data
			, query
			, (final Query q, final String cfid, final Map<String, Object> d, final String... idlist)->{
				FieldList flist = new FieldList();
				flist.addField(query.getFieldList().get(idlist[0])).setMatchType(MatchType.FULL);
				Map<String, Object> p = new HashMap<String, Object>();
				p.put(idlist[0], d.get(cfid));
				query.setQueryFormFieldList(flist);
				query.setQueryFormData(p);
			}
			, ids
		);
	}
}
