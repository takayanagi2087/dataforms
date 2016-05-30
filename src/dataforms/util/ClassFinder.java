package dataforms.util;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 指定パッケージ中のクラスのリストを取得します。
 *
 */
public class ClassFinder {
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(ClassFinder.class.getName());


    /**
     * クラスローダー。
     */
    private ClassLoader classLoader = null;

    /**
     * コンストラクタ。
     */
	public ClassFinder() {
    	//
		this.classLoader = Thread.currentThread().getContextClassLoader();
	}

	/**
	 * クラスファイルかどうかを判定します。
	 * @param path パス。
	 * @return クラスファイルの場合true。
	 */
	private boolean isClassFile(final String path) {
		return Pattern.matches(".*\\.class$", path);
	}

	/**
	 * ファイル名をクラス名に変換します。
	 * @param classname クラスファイル名。
	 * @return クラス名。
	 */
	private String fileNameToClassName(final String classname) {
		return classname.replaceAll("\\.class$", "");
	}

	/**
	 * フォルダの場合のクラス一覧を取得します。
	 * @param packageName パッケージ名。
	 * @param dir ディレクトリ。
	 * @param baseclass ベースクラス。
	 * @return クラスの一覧。
	 * @throws Exception 例外。
	 */
	private List<Class<?>> findClassesWithFile(final String packageName, final File dir, final Class<?> baseclass) throws Exception {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String path : dir.list()) {
			File entry = new File(dir, path);
			if (entry.isFile() && isClassFile(entry.getName())) {
				Class<?> clazz = classLoader.loadClass(packageName + "." + fileNameToClassName(entry.getName()));
				if (baseclass.isAssignableFrom(clazz)) {
					classes.add(clazz);
				}
			} else if (entry.isDirectory()) {
				classes.addAll(findClassesWithFile(
						packageName + "." + entry.getName(), entry, baseclass));
			}
		}
		return classes;
	}


	/**
	 * jarファイル中のクラス一覧を取得します。
	 * @param rootPackageName ルートパッケージ名。
	 * @param jarFileUrl jarファイルのURL。
	 * @param baseclass ベースクラス。
	 * @return クラスリスト。
	 * @throws Exception 例外。
	 */
	private List<Class<?>> findClassesWithJarFile(final String rootPackageName, final URL jarFileUrl, final Class<?> baseclass) throws Exception {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		JarURLConnection jarUrlConnection = (JarURLConnection) jarFileUrl.openConnection();
		JarFile jarFile = null;
		try {
			jarFile = jarUrlConnection.getJarFile();
			Enumeration<JarEntry> jarEnum = jarFile.entries();
			String packageNameAsResourceName = rootPackageName.replaceAll("\\.", "/");
			while (jarEnum.hasMoreElements()) {
				JarEntry jarEntry = jarEnum.nextElement();
				if (jarEntry.getName().startsWith(packageNameAsResourceName) && isClassFile(jarEntry.getName())) {
					Class<?> clazz = classLoader
							.loadClass(fileNameToClassName(jarEntry.getName().replaceAll("/", ".")));
					if (baseclass.isAssignableFrom(clazz)) {
						classes.add(clazz);
					}
				}
			}
		} finally {
			if (jarFile != null) {
				jarFile.close();
			}
		}

		return classes;
	}

	/**
	 * URLをfile:またはjar:に変換します。
	 * <pre>
	 * アプリケーションサーバによってzip;だったり、vfs:のURLを返してくるので、jar:file:のURLに変換します。
	 * </pre>
	 * @param url リソースのURL.
	 * @return 変換後のURL.
	 * @throws Exception 例外。
	 */
	public URL convertUrl(final URL url) throws Exception {
		if (url != null) {
			if ("zip".equals(url.getProtocol())) {
				// WebLogicは何故かzip:から始まるurlで返してきて、weblogic独自のクラスで展開しようとする。
				// それをjar:形式のURLに変換し、java標準クラスで処理するようにする。
				String u = url.toString().replaceAll("^zip\\:", "jar:file:/");
				URL jarurl = new URL(u);
				return jarurl;
			} else if ("vfs".equals(url.getProtocol())) {
				// jboss(wildfly)対応
				String u = url.toString().replaceAll("^vfs\\:", "jar:file:").replaceAll("\\.jar\\/", "\\.jar\\!/");
//				log.info("JARURL = " + u);
				URL jarurl = new URL(u);
				return jarurl;
			} else {
				return url;
			}
		} else {
			return null;
		}
	}

	/**
	 * クラスを検索します。
	 * @param rootpackage ルートパッケージ。
	 * @param c ベースクラス。
	 * @return クラスのリスト。
	 * @throws Exception 例外。
	 */
	public List<Class<?>> findClasses(final String rootpackage, final Class<?> c) throws Exception {
		List<Class<?>> ret = new ArrayList<Class<?>>();
		if (rootpackage == null) {
			return ret;
		}
		String resourceName = rootpackage.replace('.', '/');
		URL url = this.convertUrl(classLoader.getResource(resourceName));
		if (url != null) {
			log.info("findClasses:URL = " + url);
			log.info("findClasses:Protocol = " + url.getProtocol());
			if ("file".equals(url.getProtocol())) {
				String fname = url.getFile().replaceAll("%20", " "); // パスのスペース対応。
				ret = this.findClassesWithFile(rootpackage, new File(fname), c);
			} else {
				ret = this.findClassesWithJarFile(rootpackage, url, c);
			}
		}
		return ret;
	}


	/**
	 * フォルダの場合のクラス一覧を取得します。
	 * @param dir ディレクトリ。
	 * @param rpath リソースパス。
	 * @return リソースの一覧。
	 * @throws Exception 例外。
	 */
	private List<String> findFile(final File dir, final String rpath) throws Exception {
		List<String> classes = new ArrayList<String>();
		for (String path : dir.list()) {
			File entry = new File(dir, path);
			if (entry.isFile()) {
				String text = entry.getAbsolutePath().replaceAll("\\\\", "/");
				int idx = text.indexOf(rpath);
				if (idx >= 0) {
					classes.add(text.substring(idx));
				}
			} else if (entry.isDirectory()) {
				classes.addAll(findFile(entry, rpath));
			}
		}
		return classes;
	}


	/**
	 * jarファイル中のクラス一覧を取得します。
	 *
	 * @param jarFileUrl
	 *            jarファイルのURL。
	 * @param path
	 *            jar中のpath。
	 * @return ファイルリスト。
	 * @throws Exception
	 *             例外。
	 */
	private List<String> findJarFile(final URL jarFileUrl, final String path) throws Exception {
		List<String> classes = new ArrayList<String>();
		JarURLConnection jarUrlConnection = (JarURLConnection) jarFileUrl.openConnection();
		JarFile jarFile = null;
		try {
			jarFile = jarUrlConnection.getJarFile();
			Enumeration<JarEntry> jarEnum = jarFile.entries();
			while (jarEnum.hasMoreElements()) {
				JarEntry jarEntry = jarEnum.nextElement();
				String name = "/" + jarEntry.getName();
				if (name.startsWith(path)) {
					if (!jarEntry.isDirectory()) {
						classes.add(name);
					}
				}
			}
		} finally {
			if (jarFile != null) {
				jarFile.close();
			}
		}

		return classes;
	}

	/**
	 * 指定されたパス内のリソースリストを取得します。
	 * @param path パス。
	 * @return リソースリスト。
	 * @throws Exception 例外。
	 */
	public List<String> findResource(final String path) throws Exception {
		URL url = this.convertUrl(this.classLoader.getResource(path));
		log.debug("url=" + url.toString());
		List<String> ret = null;
		if ("file".equals(url.getProtocol())) {
			String fname = url.getFile().replaceAll("%20", " "); // パスのスペース対応。
			log.debug("fname=" + fname);
			ret = this.findFile(new File(fname), path);
		} else {
			ret = this.findJarFile(url, path);
		}
		return ret;
	}
}
