package dataforms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 各種オブジェクトのシリアル化ユーティリティクラス。
 *
 */
public final class ObjectUtil {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(ObjectUtil.class.getName());

    /**
     * コンストラクタ。
     */
	private ObjectUtil() {

	}

	/**
	 * オブジェクトをシリアル化し、バイト配列として取得します。
	 * @param obj オブジェクト。
	 * @return バイト配列。
	 * @throws Exception 例外。
	 */
	public static byte[] getBytes(final Serializable obj) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			try {
				oos.writeObject(obj);
			} finally {
				oos.close();
			}
		} finally {
			bos.close();
		}
		return bos.toByteArray();
	}

	/**
	 * シリアル化したオブジェクトの入力ストリームを取得します。
	 * @param obj オブジェクト。
	 * @return 入力ストリーム。
	 * @throws Exception 例外。
	 */
	public static ByteArrayInputStream getInputStream(final Serializable obj) throws Exception {
		byte[] bin = getBytes(obj);
		return new ByteArrayInputStream(bin);
	}

	/**
	 * 入力ストリームから入力したオブジェクトを取得します。
	 * @param is 入力ストリーム。
	 * @return 入力したオブジェクト。
	 * @throws Exception 例外。
	 */
	public static Object getObject(final InputStream is) throws Exception {
		Object obj = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			try {
				obj = ois.readObject();
			} finally {
				ois.close();
			}

		} finally {
			is.close();
		}
		return obj;
	}

	/**
	 * バイト列をObjectに変換します。
	 * @param bin バイト列。
	 * @return オブジェクト。
	 * @throws Exception 例外。
	 */
	public static Object getObject(final byte[] bin) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(bin);
		Object obj = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			try {
				obj = ois.readObject();
			} finally {
				ois.close();
			}
		} finally {
			is.close();
		}
		return obj;
	}

}
