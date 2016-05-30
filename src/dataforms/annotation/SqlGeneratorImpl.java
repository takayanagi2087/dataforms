// CHECKSTYLE_OFF

package dataforms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SqlGenerator実装クラスアノテーション。
 * <pre>
 * SqlGeneratorの実装クラスであることを示すアノテーションです。
 * 対応するデータベースサーバの製品名を指定します。
 * 製品名はDatabaseMetaData#getDatabaseProductName()で取得する値と一致させる必要があります。
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlGeneratorImpl {
	/**
	 * データベースサーバの製品名。
	 * <pre>
     * connection.getMetaData().getDatabaseProductName() で取得できる名前と一致させて下さい。
     * </pre>
     * @return データベースサーバの製品名。
	 */
	String databaseProductName();
}
// CHECKSTYLE_ON
