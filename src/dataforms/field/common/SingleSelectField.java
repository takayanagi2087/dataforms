

package dataforms.field.common;

import dataforms.field.base.Field;

/**
 * 単一の選択フィールドクラス。
 * <pre>
 * 対応するHTMLのタグは単一選択の&lt;select&gt;や&lt;input type=&quot;radio&quot; ...&gt;になります。
 * </pre>
 * @param <TYPE> データ型。
 */
public class SingleSelectField<TYPE> extends SelectField<TYPE> {
	/**
	 * HTMLフィールドタイプ。
	 */
	public enum HtmlFieldType {
		/**
		 * チェックボックス。
		 */
		RADIO,
		/**
		 * マルチ選択リスト。
		 */
		SELECT
	};

	/**
	 * Htmlフィールドタイプ。
	 */
	private HtmlFieldType htmlFieldType = HtmlFieldType.SELECT;

	/**
	 * HTMLフィールドタイプを取得します。
	 * @return HTMLフィールドタイプ。
	 */
	public HtmlFieldType getHtmlFieldType() {
		return htmlFieldType;
	}

	/**
	 * HTMLフィールドタイプを設定します。
	 * <pre>
	 * このプロパティは開発ツールでHTMLを作成する際参照されるだけで、実行時には参照されません。
	 * </pre>
	 * @param htmlFieldType HTMLフィールドタイプ。
	 * @return 設定されたフィールド。
	 */
	public Field<?> setHtmlFieldType(final HtmlFieldType htmlFieldType) {
		this.htmlFieldType = htmlFieldType;
		return this;
	}



	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SingleSelectField(final String id) {
		super(id);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param len 長さ。
	 */
	public SingleSelectField(final String id, final int len) {
		super(id, len);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setClientValue(final Object v) {
		this.setValue((TYPE) v);
	}

}
