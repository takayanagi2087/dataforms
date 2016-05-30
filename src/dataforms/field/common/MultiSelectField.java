package dataforms.field.common;

import java.util.ArrayList;
import java.util.List;

import dataforms.dao.sqldatatype.SqlClob;
import dataforms.field.base.Field;
import dataforms.util.StringUtil;
import net.arnx.jsonic.JSON;

/**
 * 複数選択可能な選択肢フィールドクラス。
 * <pre>
 * {@code
 * 対応するHTMLのタグは複数選択の&lt;select&gt;と&lt;input type=&quot;checkbox&quot;&gt;になります。
 * Listはjson形式に変換し、CLOBに記録します。
 * }
 * </pre>
 * @param <TYPE> データ型。
 */
public class MultiSelectField<TYPE> extends SelectField<List<TYPE>> implements SqlClob {
	/**
	 * HTMLフィールドタイプ。
	 */
	public enum HtmlFieldType {
		/**
		 * チェックボックス。
		 */
		CHECKBOX,
		/**
		 * マルチ選択リスト。
		 */
		SELECT
	};

	/**
	 * Htmlフィールドタイプ。
	 */
	private HtmlFieldType htmlFieldType = HtmlFieldType.CHECKBOX;

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
	 * @param len 長さ。
	 */
	public MultiSelectField(final String id, final int len) {
		super(id, len);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void setClientValue(final Object v) {
		if (v instanceof String) {
			// 選択件数が1件の場合、文字列で渡ったてくるので、Listを作成します。
			List<TYPE> list = new ArrayList<TYPE>();
			list.add((TYPE) v);
			this.setValue(list);
		} else {
			this.setValue((List<TYPE>) v);
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * DBに保存する場合json形式に変換します。
	 * </pre>
	 */
	@Override
	public Object getDBValue() {
		String ret = null;
		if (!StringUtil.isBlank(this.getValue())) {
			ret = JSON.encode(this.getValue(), true);
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * DBにから取得したjson形式の文字列を、リスト形式に変換します。
	 * </pre>
	 */
	@Override
	public void setDBValue(final Object value) {
		List<TYPE> list = new ArrayList<TYPE>();
		if (!StringUtil.isBlank(value)) {
			list = JSON.decode(value.toString());
		}
		super.setValue(list);
	}
}

