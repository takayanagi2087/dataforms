package dataforms.dao;

import java.sql.Connection;

/**
 * JDBCに接続可能なオブジェクトインターフェース。
 *
 */
public interface JDBCConnectableObject {
	/**
	 * JDBC接続を取得します。
	 * @return JDBC接続。
	 */
	Connection getConnection();
}
