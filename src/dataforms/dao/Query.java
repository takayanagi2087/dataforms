package dataforms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.sqlfunc.GroupSummaryField;
import dataforms.util.StringUtil;


/**
 * 問い合わせクラス。
 * <pre>
 * SQLのSELECT文に対応するクラスです。
 * DAOに対して、このクラスのインスタンスを渡すと、DBMSに対応したSqlGeneratorが
 * 適切なSQLを作成し、問い合わせを行います。
 * </pre>
 */
public class Query {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(Query.class.getName());

	/**
	 * フィールドリスト。
	 * <pre>
	 * 結果リストに格納するフィールドリスト.
	 * </pre>
	 */
	private FieldList fieldList = new FieldList();

	/**
	 * distinctフラグ。
	 * <pre>
	 * trueの場合生成するSQLは"select distinct ..."となります。
	 * </pre>
	 */
	private boolean distinct = false;

	/**
	 * 結合元のテーブル。
	 */
	private Table mainTable = null;
	/**
	 * 内部結合テーブルリスト。
	 */
	private TableList joinTableList = null;
	/**
	 * 左外部結合テーブルリスト。
	 */
	private TableList leftJoinTableList = null;
	/**
	 * 右外部結合テーブルリスト。
	 */
	private TableList rightJoinTableList = null;

	/**
	 * 問い合わせフォームフィールドリスト。
	 *
	 */
	private FieldList queryFormFieldList = null;
	/**
	 * 問い合わせフォームの入力データ。
	 */
	private Map<String, Object> queryFormData = null;

	/**
	 * ソートフィールドリスト。
	 */
	private FieldList orderByFieldList = null;

	/**
	 * 内部結合するテーブルの別名リスト。
	 */
	private List<String> joinAliasList = null;
	/**
	 * 左外部結合するテーブルの別名リスト。
	 */
	private List<String> leftJoinAliasList = null;
	/**
	 * 右外部結合するテーブルの別名リスト。
	 */
	private List<String> rightJoinAliasList = null;

	/**
	 * フィールドとテーブル別名とのマップ。
	 */
	private Map<String, String> fieldTableAliasMap = null;

	/**
	 * 削除フラグの有効性。
	 */
	private boolean effectivenessOfDeleteFlag = true;

	/**
	 * 固定検索条件。
	 */
	private String condition = null;

	/**
	 * コンストラクタ。
	 */
	public Query() {

	}

	/**
	 * フィールドリストを取得します。
	 * @return フィールドリスト。
	 */
	public FieldList getFieldList() {
		return fieldList;
	}


	/**
	 * フィールドリストを設定します。
	 * @param fieldList フィールドリスト。
	 */
	public void setFieldList(final FieldList fieldList) {
		this.fieldList = fieldList;
	}


	/**
	 * 結合元のテーブルを取得します。
	 * @return 結合元のテーブル。
	 */
	public Table getMainTable() {
		return mainTable;
	}

	/**
	 * 結合元のテーブルを設定します。
	 * @param mainTable 結合元のテーブル。
	 */
	public void setMainTable(final Table mainTable) {
		this.mainTable = mainTable;
	}

	/**
	 * 内部結合するテーブルリストを取得します。
	 * @return 内部結合するテーブルリスト。
	 */
	public TableList getJoinTableList() {
		return joinTableList;
	}

	/**
	 * 内部結合するテーブルリストを設定します。
	 * @param joinTableList 内部結合するテーブリスト。
	 */
	public void setJoinTableList(final TableList joinTableList) {
		this.joinTableList = joinTableList;
	}

	/**
	 * 左外部結合するテーブルリストを取得します。
	 * @return 左外部結合するテーブルリスト。
	 */
	public TableList getLeftJoinTableList() {
		return leftJoinTableList;
	}

	/**
	 * 左外部結合するテーブルリストを設定します。
	 * @param leftJoinTableList 左外部結合するテーブルリスト。
	 */
	public void setLeftJoinTableList(final TableList leftJoinTableList) {
		this.leftJoinTableList = leftJoinTableList;
	}

	/**
	 * 右外部結合するテーブルリストを取得します。
	 * @return 右外部結合するテーブルリスト。
	 */
	public TableList getRightJoinTableList() {
		return rightJoinTableList;
	}

	/**
	 * 右外部結合するテーブルリストを設定します。
	 * @param rightJoinTableList 右外部結合するテーブルリスト。
	 */
	public void setRightJoinTableList(final TableList rightJoinTableList) {
		this.rightJoinTableList = rightJoinTableList;
	}

	/**
	 * 問い合わせフォームのフィールドリストを取得します。
	 * @return 条件フィールドリスト。
	 */
	public FieldList getQueryFormFieldList() {
		return queryFormFieldList;
	}


	/**
	 * 問い合わせフォームのフィールドリストを設定します。
	 * <pre>
	 * 検索条件に使用するフィールドリスト指定します。
	 * このフィールドリストに存在しかつ問い合わせフォームの入力データが存在した場合、
	 * 検索の条件式を生成します。
	 * このリスト中のフィールドにはMatchTypeを指定し、検索条件式の生成を制御できます。
	 * </pre>
	 *　
	 *　<table>
	 *  	<caption>MatchType一覧</caption>
	 *		<thead>
	 *			<tr>
	 *				<th>条件タイプ</th><th>意味</th><th>生成条件式</th><th>データ編集</th>
	 *			</tr>
	 *		</thead>
	 *		<tbody>
	 *			<tr>
	 *				<td>FULL</td><td>完全一致</td><td>field_id = :field_id</td><td></td>
	 *				<td>PART</td><td>部分一致</td><td>field_id like :field_id</td><td>'%入力値%'</td>
	 *				<td>BEGIN</td><td>先頭一致</td><td>field_id like :field_id</td><td>'入力値%'</td>
	 *				<td>END</td><td>末尾一致</td><td>field_id like :field_id</td><td>'%入力値'</td>
	 *				<td>RANGE_FROM</td><td>範囲開始</td><td>field_id &gt;= :field_id</td><td></td>
	 *				<td>RANGE_TO</td><td>範囲終了</td><td>field_id &lt;= :field_id</td><td></td>
	 *			</tr>
	 *		</tbody>
	 *  </table>
	 *
	 * @param queryFormFieldList 条件フィールドリスト。
	 */
	public void setQueryFormFieldList(final FieldList queryFormFieldList) {
		this.queryFormFieldList = queryFormFieldList;
		// 無条件に条件式を生成するように、仮パラメータを設定.
		Map<String, Object> cond = new HashMap<String, Object>();
		for (Field<?> f : queryFormFieldList) {
			cond.put(f.getId(), new Object());
		}
		this.setQueryFormData(cond);
	}


	/**
	 * 問い合わせフォームの入力データを取得します。
	 * @return 問い合わせフォームの入力データ。
	 */
	public Map<String, Object> getQueryFormData() {
		return queryFormData;
	}

	/**
	 * 問い合わせフォームの入力データを設定します。
	 * @param queryFormData 問い合わせフォームの入力データ。
	 */
	public void setQueryFormData(final Map<String, Object> queryFormData) {
		this.queryFormData = queryFormData;
	}


	/**
	 * 固定検索条件を取得します。
	 * @return 固定検索条件。
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * 固定検索条件を取得します。
	 * @param condition 固定検索条件。
	 */
	public void setCondition(final String condition) {
		this.condition = condition;
	}


	/**
	 * 内部結合するテーブルの別名リストを取得します。
	 * @return 内部結合するテーブルの別名リスト。
	 */
	public List<String> getJoinAliasList() {
		return joinAliasList;
	}

	/**
	 * 内部結合するテーブルの別名リストを設定します。
	 * @param joinAliasList 内部結合するテーブルの別名リスト。
	 */
	public void setJoinAliasList(final List<String> joinAliasList) {
		this.joinAliasList = joinAliasList;
	}

	/**
	 * 左外部結合するテーブルの別名リストを取得します。
	 * @return 左外部結合するテーブルの別名リスト。
	 */
	public List<String> getLeftJoinAliasList() {
		return leftJoinAliasList;
	}


	/**
	 * 左外部結合するテーブルの別名リストを設定します。
	 * @param leftJoinAliasList 左外部結合するテーブルの別名リスト。
	 */
	public void setLeftJoinAliasList(final List<String> leftJoinAliasList) {
		this.leftJoinAliasList = leftJoinAliasList;
	}

	/**
	 * 右外部結合するテーブルの別名リストを取得します。
	 * @return 右外部結合するテーブルの別名リスト。
	 */
	public List<String> getRightJoinAliasList() {
		return rightJoinAliasList;
	}


	/**
	 * 右外部結合するテーブルの別名リストを設定します。
	 * @param rightJoinAliasList 右外部結合するテーブルの別名リスト。
	 */
	public void setRightJoinAliasList(final List<String> rightJoinAliasList) {
		this.rightJoinAliasList = rightJoinAliasList;
	}

	/**
	 * フィールドとテーブル別名とのマップを設定します。
	 * @param fieldTableAliasMap フィールドとテーブル別名とのマップ。
	 */
	public void setFieldTableAliasMap(final Map<String, String> fieldTableAliasMap) {
		this.fieldTableAliasMap = fieldTableAliasMap;
	}

	/**
	 * マッチ対象のフィールドの構文を取得します。
	 * @param field フィールド。
	 * @return フィールド式。
	 */
	public String getMatchFieldSql(final Field<?> field) {
		String fid = field.getMatchFieldId();
		String t = null;
		if (field.getTable() != null) {
			t = field.getTable().getAlias();
		}
		if (t == null) {
			if (this.fieldTableAliasMap != null) {
				t = this.fieldTableAliasMap.get(fid);
			}
		}
		if (t != null) {
			String ret = t + "." + StringUtil.camelToSnake(fid);
			return ret;
		}
		return null;
	}

	/**
	 * distinctフラグを取得します。
	 * @return distinctフラグ。
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * distinctフラグを設定します。
	 * @param distinct distinctフラグ。
	 */
	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * Group by対象のフィールドリストを取得します。
	 * @return Group by対象のフィールドリスト。
	 */
	public FieldList getGroupByFieldList() {
		FieldList ret = new FieldList();
		boolean flg = false;
		for (Field<?> f : this.fieldList) {
			if (f instanceof GroupSummaryField) {
				flg = true;
				break;
			}
		}
		if (flg) {
			for (Field<?> f : this.fieldList) {
				if (!(f instanceof GroupSummaryField)) {
					ret.add(f);
				}
			}
		}
		return ret;
	}

	/**
	 * ソートするフィールドリストを取得します。
	 * @return ソートするフィールドリスト。
	 */
	public FieldList getOrderByFieldList() {
		return orderByFieldList;
	}

	/**
	 * ソートするフィールドリストを設定します。
	 * @param orderByFieldList ソートするフィールドリスト。
	 */
	public void setOrderByFieldList(final FieldList orderByFieldList) {
		this.orderByFieldList = orderByFieldList;
	}

	/**
	 * 削除フラグの有効性を取得します。
	 * <pre>
	 * trueの場合delete_flag='0'の条件を自動生成します。
	 * </pre>
	 * @return 削除フラグの有効性.
	 */
	public boolean isEffectivenessOfDeleteFlag() {
		return effectivenessOfDeleteFlag;
	}

	/**
	 * 削除フラグの有効性を設定します。
	 * @param effectivenessOfDeleteFlag 有効の場合true。
	 */
	public void setEffectivenessOfDeleteFlag(final boolean effectivenessOfDeleteFlag) {
		this.effectivenessOfDeleteFlag = effectivenessOfDeleteFlag;
	}
}
