package dataforms.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 暗号化ユーティリティクラス。
 *
 * <pre>
 * web.xmlのコンテキストパラメータcrypt-passwordで暗号化のパスワードを設定することができます。
 * </pre>
 *
 */
public final class CryptUtil {

	/**
	 * コンストラクタ。
	 */
	private CryptUtil() {

	}

	/**
	 * 暗号化のためのパスワード。
	 */
	private static String cryptPassword = "P@ssw0rd";


	/**
	 * 暗号用パスワードを設定します。
	 * @param cryptPassword 暗号用パスワード。
	 */
	public static void setCryptPassword(final String cryptPassword) {
		CryptUtil.cryptPassword = cryptPassword;
	}

	/**
	 * キーを取得します。
	 * @return キー。
	 * @throws Exception 例外。
	 */
	private static byte[] getKey() throws Exception {
        byte[] password = cryptPassword.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password);
        Arrays.fill(password, (byte) 0x00);
        byte[] pssKey = md.digest();
        return pssKey;
	}

	/**
	 * キーを取得します。
	 * @param pass パスワード。
	 * @return キー。
	 * @throws Exception 例外。
	 */
	private static byte[] getKey(final String pass) throws Exception {
        byte[] password = pass.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password);
        Arrays.fill(password, (byte) 0x00);
        byte[] pssKey = md.digest();
        return pssKey;
	}


	/**
	 * 秘密キーを取得します。
	 * @return 秘密キー。
	 * @throws Exception 例外。
	 */
	private static SecretKey getSecretKey() throws Exception {
		byte[] pssKey = getKey();
		DESKeySpec desKeySpec = new DESKeySpec(pssKey);
		Arrays.fill(pssKey, (byte) 0x00);
		SecretKeyFactory desKeyFac = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = desKeyFac.generateSecret(desKeySpec);
		return desKey;
	}

	/**
	 * 秘密キーを取得します。
	 * @param pssKey パスワード。
	 * @return 秘密キー。
	 * @throws Exception 例外。
	 */
	private static SecretKey getSecretKey(final byte[] pssKey) throws Exception {
		DESKeySpec desKeySpec = new DESKeySpec(pssKey);
		Arrays.fill(pssKey, (byte) 0x00);
		SecretKeyFactory desKeyFac = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = desKeyFac.generateSecret(desKeySpec);
		return desKey;
	}


	/**
	 * 暗号化を行ないます。
	 * @param text 暗号化するテキスト。
	 * @return 暗号化されたテキスト。
	 * @throws Exception 例外。
	 */
	public static String encrypt(final String text) throws Exception {
		if (!StringUtil.isBlank(text)) {
			SecretKey desKey = getSecretKey();
			Cipher c = Cipher.getInstance("DES");
			c.init(Cipher.ENCRYPT_MODE, desKey);
		    byte[] input = text.getBytes("utf-8");
		    byte[] encrypted = c.doFinal(input);
		    return Base64.getEncoder().encodeToString(encrypted);
		} else {
			return null;
		}
	}

	/**
	 * 暗号化を行ないます。
	 * @param text 暗号化するテキスト。
	 * @param password パスワード。
	 * @return 暗号化されたテキスト。
	 * @throws Exception 例外。
	 */
	public static String encrypt(final String text, final String password) throws Exception {
		if (!StringUtil.isBlank(text)) {
			SecretKey desKey = getSecretKey(getKey(password));
			Cipher c = Cipher.getInstance("DES");
			c.init(Cipher.ENCRYPT_MODE, desKey);
		    byte[] input = text.getBytes("utf-8");
		    byte[] encrypted = c.doFinal(input);
		    return Base64.getEncoder().encodeToString(encrypted);
		} else {
			return null;
		}
	}


	/**
	 * 複合を行ないます。
	 * @param text 暗号化されたテキスト。
	 * @return 複合されたテキスト。
	 * @throws Exception 例外。
	 */
	public static String decrypt(final String text) throws Exception {
		if (!StringUtil.isBlank(text)) {
			SecretKey desKey = getSecretKey();
			Cipher c = Cipher.getInstance("DES");
			c.init(Cipher.DECRYPT_MODE, desKey);
		    byte[] input =  Base64.getDecoder().decode(text);
		    byte[] decrypted = c.doFinal(input);
		    return new String(decrypted, "utf-8");
		} else {
			return null;
		}
	}


	/**
	 * 複合を行ないます。
	 * @param text 暗号化されたテキスト。
	 * @param password パスワード。
	 * @return 複合されたテキスト。
	 * @throws Exception 例外。
	 */
	public static String decrypt(final String text, final String password) throws Exception {
		if (!StringUtil.isBlank(text)) {
			SecretKey desKey = getSecretKey(getKey(password));
			Cipher c = Cipher.getInstance("DES");
			c.init(Cipher.DECRYPT_MODE, desKey);
		    byte[] input =  Base64.getDecoder().decode(text);
		    byte[] decrypted = c.doFinal(input);
		    return new String(decrypted, "utf-8");
		} else {
			return null;
		}
	}
}
