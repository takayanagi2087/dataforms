package dataforms.dao;

import java.util.ArrayList;

/**
 * テーブルリストクラス。
 *
 */
public class TableList extends ArrayList<Table> {
	/**
	 *
	 */
	private static final long serialVersionUID = -4469643124552536741L;

	/**
	 * コンストラクタ。
	 * @param tables テーブルの配列。
	 */
	public TableList(final Table... tables) {
		for (Table t : tables) {
			this.add(t);
		}
	}
}
