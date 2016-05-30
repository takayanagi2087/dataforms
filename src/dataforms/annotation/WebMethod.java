package dataforms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ウエブメソッドアノテーション。
 * <pre>
 *  ウエブブラウザから呼び出すことができるメソッドを示すアノテーションです。
 *  ウエブクライアントから呼び出されるサーバメソッドは以下の形式のメソッドになります。
 *  {@code @WebMethod}
 *	{@code public Response getUserInfo(final Map<String, Object> param) throws Exception}
 * </pre>
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebMethod {
	// CHECKSTYLE_OFF

	/**
	 * DBを使用するかどうかを指定します。
	 * <pre>
	 * このメソッドを呼び出す前にDBへの接続を確保するかどうかを指定します。
	 * デフォルト値true.
	 * </pre>
	 * @return DBを使用する場合true。
	 */
	boolean useDB() default true;

	/**
	 * 全てのユーザで実行できるメソッドであることを示すフラグです。
	 * <pre>
	 * trueが指定された場合、認証チェックを行わず呼び出します。
	 * デフォルト値false.
	 * </pre>
	 * @return 認証チェックを行わない場合true。
	 */
	boolean everyone() default false;
	// CHECKSTYLE_ON
}

//CSON:javadoc.unusedTagGeneral
