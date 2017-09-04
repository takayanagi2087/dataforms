package dataforms.dao.sqldatatype;

/**
 * numeric型インターフェース。
 * <pre>
 * このインターフェースを実装したフィールドは基本的にnumeric型となります。
 * </pre>
 */
public interface SqlNumeric {
	/**
	 * 有効桁数を取得します。
	 * @return 有効桁数。
	 */
	public int getPrecision();

	/**
	 * 小数点以下桁数を取得します。
	 * @return 小数点以下桁数。
	 */
	public int getScale();


}
