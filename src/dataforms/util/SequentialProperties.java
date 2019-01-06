package dataforms.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dataforms.servlet.DataFormsServlet;


/**
 * 順序を保持したプロパティクラス。
 *
 * <pre>
 * 各機能フォルダ中のFunction.propertiesのアクセスするためのクラスです。
 * Function.propertiesには機能名と各ページの名称リストが記録されいます。
 * メニューはFunction.propertiesの登録順で表示されます。
 * </pre>
 */
public class SequentialProperties extends Properties {
	/**
	 * UID。
	 */
	private static final long serialVersionUID = 4764311915468513238L;


    /**
     * Log.
     */
//	private static Logger log = Logger.getLogger(SequentialProperties.class.getName());

	/**
	 * 順序を保持するリスト。
	 */
	private List<String> keyList = null;

	/**
	 * キーの順序リストを取得します。
	 * @param path パス。
	 * @throws Exception 例外。
	 */
	private void readKeyList(final String path) throws Exception {
		String text = FileUtil.readTextFile(path, "utf-8");
		parseKeyList(text);
	}

	/**
	 * キーリストを取得します。
	 * @param text プロパティファイルを解析し、キーのリストを取得します。
	 */
	private void parseKeyList(final String text) {
		keyList = new ArrayList<String>();
		Pattern p = Pattern.compile("^.+=", Pattern.MULTILINE);
		Matcher m = p.matcher(text);
		while (m.find()) {
			String key = m.group().replaceAll("=", "").trim();
			if (key.charAt(0) == '#') {
				continue;
			}
//			log.debug("key = " + key);
			keyList.add(key);
		}
	}

	/**
	 * プロパティファイルを読み込みます。
	 * @param path パス。
	 * @throws Exception 例外。
	 */
	public void loadFile(final String path) throws Exception {
		this.readKeyList(path);
		FileInputStream is = new FileInputStream(path);
		try {
			this.load(is);
		} finally {
			is.close();
		}
	}

	/**
	 * *.properties形式の文字列を読み込みます。
	 * @param text *.properties形式の文字列。
	 * @throws Exception 例外。
	 */
	public void loadText(final String text) throws Exception {
		this.parseKeyList(text);
		InputStream bais = new ByteArrayInputStream(text.getBytes(DataFormsServlet.getEncoding()));
		try {
			this.load(bais);
		} finally {
			bais.close();
		}
	}

	/**
	 * プロパティの登録順を保持したリストを取得します。
	 * @return プロパティの登録順を保持したリスト。
	 */
	public List<String> getKeyList() {
		return keyList;
	}

	@Override
	public synchronized Object put(final Object key, final Object value) {
		if (!this.keyList.contains(key)) {
			this.keyList.add((String) key);
		}
		return super.put(key, value);
	}

	/**
	 * 指定された文字列をunicode文字列に変換する。
	 * @param original 変換元。
	 * @return 変換結果。
	 */
	private String toUnicode(final String original) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < original.length(); i++) {
			sb.append(String.format("\\u%04X", Character.codePointAt(original, i)));
		}
		return sb.toString();
	}

	/**
	 * 保存文字列を取得します。
	 * @return 保存文字列。
	 * @throws Exception 例外。
	 */
	public String getSaveText() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String key: this.keyList) {
			sb.append(key + "=" + this.toUnicode(this.getProperty(key)) + "\n");
		}
		return sb.toString();
	}


}

