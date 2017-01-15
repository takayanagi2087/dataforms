package dataforms.controller;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.field.base.Field;
import dataforms.htmltable.HtmlTable;
import dataforms.menu.Menu;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.ClassFinder;
import dataforms.util.MessagesUtil;
import dataforms.util.SequentialProperties;
import dataforms.util.StringUtil;
import dataforms.validator.FieldValidator;


/**
 * ページ制御クラス。
 */
public class Page extends DataForms {

	/**
     * Logger.
     */
    private static Logger log = Logger.getLogger(Page.class.getName());

    /**
     * 問い合わせフォームのID。
     */
    public static final String ID_QUERY_FORM = "queryForm";

    /**
     * 問い合わせ結果フォームのID。
     */
    public static final String ID_QUERY_RESULT_FORM = "queryResultForm";
    /**
     * 問い合わせ結果ID。
     */
    public static final String ID_QUERY_RESULT = "queryResult";
    /**
     * 編集フォームのID。
     */
    public static final String ID_EDIT_FORM = "editForm";

	/**
	 * dataforms.jarのバージョン。
	 */
	private static String dataformsVersion = null;

	/**
	 * dataforms.jarの提供者。
	 */
	private static String dataformsVendor = null;

	/**
	 * dataforms.jarの作成日時。
	 */
	private static String dataformsCreateDate = null;

	/**
	 * フレーム情報パス。
	 */
	private static String framePath = null;



	/**
	 * ページの拡張子。
	 */
	private String pageExt = null;

	/**
	 * ページの拡張子を取得します。
	 * @return ページの拡張子、
	 */
    public String getPageExt() {
		return pageExt;
	}

    /**
     * ページの拡張子を設定します。
     * <pre>
     * DataFormsServletがServletアノテーションのUrlPatternからURLの拡張子を取得し設定します。
     * </pre>
     * @param pageExt ページの拡張子。
     */
    public void setPageExt(final String pageExt) {
		this.pageExt = pageExt;
	}


	/**
     * 事前に読み込むcssのリスト。
     */
    private List<String> preloadCssList = new ArrayList<String>();

	/**
     * 事前に読み込むcssのリスト(Media対応)。
     */
    private static List<String[]> preloadMediaCssList = new ArrayList<String[]>();

    /**
     * ブラウザの戻るボタンの設定。
     */
    private static String browserBackButton = "enabled";

    /**
     * トップページ。
     */
    private static String topPage = "/dataforms/app/page/top/TopPage.df";


	/**
	 * JDBC接続。
	 */
	private WeakReference<Connection> connection = null;

	/**
	 * メニュー表示フラグ。
	 */
	private boolean menuItem = true;

	/**
	 * フレーム無フラグ.
	 */
	private boolean noFrame = false;


	/**
	 * コンストラクタ。
	 */
	public Page() {
		this.addDialog(new AlertDialog());
		this.addDialog(new ConfirmDialog());
	}


	/**
	 * クラスを継承関係を元にソートします。
	 * @param list クラスリスト。
	 * @param baseclass 基本クラス。
	 * @return ソート結果リスト。
	 */
	protected  List<Class<?>> sortClass(final List<Class<?>> list, final Class<?> baseclass) {
		List<Class<?>> ret = new ArrayList<Class<?>>();
		ret.add(baseclass);
		for (int i = 0; i < ret.size(); i++) {
			Class<?> sc = ret.get(i);
			for (Class<?> c: list) {
				if (c.getSuperclass().equals(sc)) {
					ret.add(c);
				}
			}
		}
		return ret;
	}

	/**
	 * 指定パッケージの指定クラスから派生したクラスのリストを継承順に取得します。
	 * @param pkg パッケージ。
	 * @param baseclass 基本クラス。
	 * @return クラスリスト。
	 */
	protected List<Class<?>> findClassTree(final String pkg, final Class<?> baseclass) {
		List<Class<?>> list = null;
		ClassFinder finder = new ClassFinder();
		try {
			 list = this.sortClass(finder.findClasses(pkg, baseclass), baseclass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 指定パッケージの指定クラスから派生したクラスのjavascriptファイルを継承順に取得します。
	 * @param pkg パッケージ。
	 * @param baseclass 基本クラス。
	 * @return javascriptリスト。
	 * @throws Exception 例外。
	 */
	protected List<String> findJsClassTree(final String pkg, final Class<?> baseclass) throws Exception {
		List<Class<?>> list = this.findClassTree(pkg, baseclass);
		List<String> ret = new ArrayList<String>();
		for (Class<?> cls: list) {
			String jspath = this.getClassScriptPath(cls);
			String script = this.getWebResource(jspath);
			if (!StringUtil.isBlank(script)) {
				ret.add(jspath);
			}
		}
		return ret;
	}



	/**
	 * 基本javascriptリスト。
	 */
	private static List<String> basicJsCache = null;

	/**
	 * 基本的jvascriptのリストを取得します。
	 * @return 基本的jvascriptのリスト。
	 * @throws Exception 例外。
	 */
	private synchronized List<String> getBaseicJsCache() throws Exception {
		if (basicJsCache == null) {
			basicJsCache = Collections.synchronizedList(new ArrayList<String>());
			basicJsCache.add("/dataforms/util/createSubclass.js");
			basicJsCache.add("/dataforms/util/MessagesUtil.js");
			basicJsCache.add("/dataforms/util/StringUtil.js");
			basicJsCache.add("/dataforms/util/NumberUtil.js");
			basicJsCache.add("/dataforms/util/ServerMethod.js");
			basicJsCache.add("/dataforms/util/SimpleDateFormat.js");

			basicJsCache.addAll(this.findJsClassTree("dataforms.controller", WebComponent.class));
			basicJsCache.addAll(this.findJsClassTree("dataforms.htmltable", HtmlTable.class));
			basicJsCache.addAll(this.findJsClassTree("dataforms.menu", Menu.class));
			basicJsCache.addAll(this.findJsClassTree("dataforms.field", Field.class));

			basicJsCache.add("/dataforms/validator/ValidationError.js");
			basicJsCache.addAll(this.findJsClassTree("dataforms.validator", FieldValidator.class));
		}
		return basicJsCache;
	}

	/**
	 * 指定されたクラスを使用するのに必要なスクリプトリストを取得します。
	 * @param list スクリプトリスト。
	 * @param cls クラス。
	 * @return 指定されたクラスを使用するのに必要なスクリプトリスト。
	 * @throws Exception 例外。
	 */
	protected List<String> getScriptTree(final List<String> list, final Class<?> cls) throws Exception {
		Class<?> c = cls;
		List<String> l = new ArrayList<String>();
		while (true) {
			String js = this.getClassScriptPath(c);
			if (basicJsCache.contains(js)) {
				break;
			}
			String script = this.getWebResource(js);
			if (!StringUtil.isBlank(script)) {
				l.add(0, js);
			}
			c = c.getSuperclass();
		}
		for (String js: l) {
			if (!list.contains(js)) {
				list.add(js);
			}
		}
		return list;
	}


	/**
	 * フィールド関連スクリプトリストを取得する。
	 * @param list スクリプトリスト。
	 * @param field フィール。
	 * @throws Exception 例外。
	 */
	protected void getFieldAppScripts(final List<String> list, final Field<?> field) throws Exception {
		this.getScriptTree(list, field.getClass());
		for (FieldValidator v: field.getValidatorList()) {
			this.getScriptTree(list, v.getClass());
		}
	}


	/**
	 * テーブル関連スクリプトリストを取得する。
	 * @param list スクリプトリスト。
	 * @param table テーブル。
	 * @throws Exception 例外。
	 */
	protected void getTableAppScripts(final List<String> list, final HtmlTable table) throws Exception {
		this.getScriptTree(list, table.getClass());
		for (Field<?> f: table.getFieldList()) {
			this.getFieldAppScripts(list, f);
		}
	}

	/**
	 * フォーム関連スクリプトリストを取得する。
	 * @param list スクリプトリスト。
	 * @param form フォーム。
	 * @throws Exception 例外。
	 */
	protected void getFormAppScripts(final List<String> list, final Form form) throws Exception {
		this.getScriptTree(list, form.getClass());
		for (Field<?> f: form.getFieldList()) {
			this.getFieldAppScripts(list, f);
		}
		List<HtmlTable> tlist = form.getHtmlTableList();
		for (HtmlTable table: tlist) {
			this.getTableAppScripts(list, table);
		}
	}

	/**
	 * Dataforms関連スクリプトリストを取得する。
	 * @param list スクリプトリスト。
	 * @param df DataForms。
	 * @throws Exception 例外。
	 */
	protected void getDataformsAppScripts(final List<String> list, final DataForms df) throws Exception {
		this.getScriptTree(list, this.getClass());
		Map<String, WebComponent> map = this.getFormMap();
		for (String key: this.getFormMap().keySet()) {
			WebComponent f = (WebComponent) map.get(key);
			if (f instanceof Form) {
				this.getFormAppScripts(list, (Form) f);
			}
		}
	}

	/**
	 * アプリケーションスクリプトリストを取得します。
	 * @return アプリケーションスクリプトリスト。
	 * @throws Exception 例外。
	 */
	protected List<String> getAppScript() throws Exception {
		List<String> list = new ArrayList<String>();
		this.getDataformsAppScripts(list, this);
		for (String key: this.getDialogMap().keySet()) {
			Dialog dlg = this.getDialogMap().get(key);
			this.getDataformsAppScripts(list, dlg);
		}
		return list;
	}

	/**
	 * cssとjavascriptのロードタグを取得します。
	 * @return cssとjavascriptのロードタグ。
	 * @throws Exception 例外.
	 */
	public String getPreloadTags() throws Exception {

		this.addPreloadCss(this.getPageFramePath() + "/Frame.css");
		this.addPreloadCss(this.getPageFramePath() + "/FrameCommon.css"); // 廃止予定
		String context = this.getRequest().getContextPath();
		StringBuilder sb = new StringBuilder();
		for (String css : this.preloadCssList) {
			String csspath = this.getAppropriatePath(css, this.getRequest());
			if (csspath != null) {
				String t = this.getLastUpdate(csspath);
				sb.append("\t\t<link type=\"text/css\" href=\"" + context + csspath + "?t=" + t + "\" rel=\"stylesheet\" />\n");
			}
		}
		for (String[] css : Page.preloadMediaCssList) {
			String csspath = this.getAppropriatePath(this.getPageFramePath() + "/" + css[0], this.getRequest());
			if (csspath != null) {
				String t = this.getLastUpdate(csspath);
				sb.append("\t\t<link type=\"text/css\" href=\"" + context + csspath + "?t=" + t + "\" rel=\"stylesheet\" media=\"" + css[1] + "\"/>\n");
			}
		}
		List<String> basicScripts = this.getBaseicJsCache();
		for (String js : basicScripts) {
			String jspath = this.getAppropriatePath(js, this.getRequest());
			if (jspath != null) {
				String t = this.getLastUpdate(jspath);
				sb.append("\t\t<script type=\"text/javascript\" src=\"" + context + jspath + "?t=" + t + "\"></script>\n");
			}
		}
		List<String> appScripts = this.getAppScript();
		appScripts.add(this.getPageFramePath() + "/Frame.js");
		for (String js: appScripts) {
			String jspath = this.getAppropriatePath(js, this.getRequest());
			if (jspath != null) {
				String t = this.getLastUpdate(jspath);
				sb.append("\t\t<script type=\"text/javascript\" src=\"" + context + jspath + "?t=" + t + "\"></script>\n");
			}
		}
		return sb.toString();
	}


	/**
	 * {@inheritDoc}
	 * DB接続を取得します。
	 */
	@Override
	public Connection getConnection() {
		if (this.connection != null) {
			return this.connection.get();
		} else {
			return null;
		}
	}

	/**
	 * JDBC接続を設定します。
	 * @param connection JDBC接続。
	 */
	public void setConnection(final Connection connection) {
		this.connection = new WeakReference<Connection>(connection);
	}

	/**
	 * 事前にロードするcssを追加します。
	 * @param css CSSファイルのパス。
	 */
    protected void addPreloadCss(final String css) {
    	this.preloadCssList.add(css);
    }


	/**
	 * 事前にロードするcssを追加します。
	 * @param css CSSファイルのパス。
	 * @param media media指定。
	 */
    public static void addPreloadCss(final String css, final String media) {
    	String []ent = new String[2];
    	ent[0] = css;
    	ent[1] = media;
    	Page.preloadMediaCssList.add(ent);
    }

    /**
     * フォーム初期化メソッド0。
     */
    private static final  String INIT_SCRIPT0 =
    	"\t\t<script type=\"text/javascript\">\n" +
		"\t\t<!--\n" +
		"\t\t$(function() {\n";

    /**
     * フォーム初期化メソッド1。
     */
    private static final  String INIT_SCRIPT1 =
//		"\t\t\tdf.clientValidation = false;\n" +
		"\t\t\twindow.currentPage = page;\n" +
		"\t\t\tpage.init();\n" +
		"\t\t\tpage.attach();\n" +
		"\t\t});\n" +
		"\t\t-->\n" +
		"\t\t</script>\n";

	/**
	 * HTMLにcssとscriptを追加します。
	 * @param html HTML。
	 * @param scripts 追加するscript。
	 * @param context コンテキスト。
	 * @return scriptを追加したhtml。
	 * @throws Exception 例外。
	 */
	protected String editHtml(final String html, final String scripts, final String context) throws Exception {
		//String scriptPath = this.getScriptPath();
		StringBuilder sb = new StringBuilder(scripts);
		String framepath = this.getAppropriatePath(this.getPageFramePath() + "/Frame.css", this.getRequest());
		if (framepath.indexOf("_phone.") > 0) {
			// スマートフォンの場合の設定.
			sb.append("<meta name=\"viewport\" content=\"width=device-width\">\n");
		}
		sb.append(this.getPreloadTags());
		String pageclass = this.getJsClass();
		/*HttpServletRequest req = this.getRequest();
		if (req.getRequestURI().indexOf(pageclass + "." + this.getPageExt()) >= 0) {
			sb.append("\t\t<script type=\"text/javascript\" src=\"" + context + "/" + scriptPath + "\"></script>\n");
		}*/
		sb.append(INIT_SCRIPT0);
		sb.append("\t\t\tvar page = new " + pageclass + "();\n");
		sb.append(INIT_SCRIPT1);

		String s = sb.toString();
//		log.info("scriptPath=" + scriptPath);
		Pattern pat = Pattern.compile("<head.*>");
		Matcher m = pat.matcher(html);
		StringBuilder htmlbuffer = new StringBuilder();
		if (m.find()) {
			int start = m.start();
			int end = m.end();
			htmlbuffer.append(html.substring(0, start));
			htmlbuffer.append(m.group());
			htmlbuffer.append(s);
			htmlbuffer.append(html.substring(end));
		}
		return htmlbuffer.toString();
	}


	/**
	 * QueryStringを処理メソッド。
	 * <pre>
	 * 各コンポーネントのinit()メソッド等は初期Html取得時のリクエストとは
	 * 別のリクエストで呼び出されるため、初期Html取得時のQueryStringを参照できません。
	 * これらのinit()メソッド等でQueryStringを参照したい場合、このメソッドを実装し、
	 * QueryStringの情報をセッションに記録してください。
	 * </pre>
	 * @param p QueryStringを展開したMap。
	 * @throws Exception 例外。
	 */
	protected void processQueryString(final Map<String, Object> p) throws Exception {
		
	}
	

    /**
     * ページのHTMLを取得します。
     * @param params パラメータ。
     * @return HTMLページ応答。
     * @throws Exception 例外。
     */
    @WebMethod(useDB = true)
	public Response getHtml(final Map<String, Object> params) throws Exception {
    	this.processQueryString(params);
		HttpServletRequest req = this.getRequest();
		String uri = req.getRequestURI();
		String context = req.getContextPath();
		log.info("context=" + context + ", uri=" + uri);
		log.info("path=" + req.getServletPath());
		String htmlpath = this.getWebResourcePath(this.getClass()) + ".html";
		htmlpath = this.getAppropriatePath(htmlpath, req);
		log.info("sendHtml=" + htmlpath);
		String htmltext = this.getWebResource(htmlpath); //FileUtil.readTextFile(htmlpath, DataFormsServlet.getEncoding());
		String scripts = this.getWebResource(DataFormsServlet.getCssAndScript());
		scripts = scripts.replaceAll("\\$\\{context\\}", req.getContextPath());

		htmltext = htmltext.replaceAll("\\</[Bb][Oo][Dd][Yy]\\>", "\t<noscript><br/><div class='noscriptDiv'><b>" + MessagesUtil.getMessage(this.getPage(), "message.noscript") + "</b></div></noscript>\n\t</body>");
    	HtmlResponse resp = new HtmlResponse(this.editHtml(htmltext, scripts, context));
    	return resp;
    }

    /**
     * ログインダイアログのID。
     */
    public static final String ID_LOGIN_DIALOG = "loginDialog";

    /**
     * クライアント用メッセージを取得します。
	 * @param params パラメータ。
	 * @return 応答。
	 * @throws Exception 例外。
	 */
    @WebMethod(useDB = false)
	public JsonResponse getClientMessages(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log, params);
		Map<String, String> map = getMessageMap();
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, map);
		this.methodFinishLog(log, ret);
		return ret;
	}

    /**
     * ページに対応したメッセージマップを取得します。
     * @return ページに対応したメッセージマップ。
     */
	protected Map<String, String> getMessageMap() {
		Map<String, String> map = MessagesUtil.getClientMessageMap(this);
		String clsname = this.getClass().getName();
		String pageprop = "/" + clsname.replaceAll("\\.", "/");
		Map<String, String> pageMap = MessagesUtil.getMessageMap(this.getPage(), pageprop);
		map.putAll(pageMap);
		return map;
	}

	/**
	 * メッセージを取得します。
	 * @param key メッセージキー。
	 * @return メッセージ。
	 * @throws Exception 例外。
	 */
    public String getMessage(final String key) throws Exception {
    	return MessagesUtil.getMessage(this, key);
    }


    /**
	 * メッセージを取得する.
	 * @param key メッセージキー.
     * @param args メッセージ引数.
     * @return メッセージ.
     * @throws Exception 例外.
     */
    public String getMessage(final String key, final String... args) throws Exception {
    	return MessagesUtil.getMessage(this, key, args);
    }

    /**
     * メッセージの取得.
	 * @param params パラメータ.
	 * @return 応答.
	 * @throws Exception 例外.
	 */
    @WebMethod(useDB = false)
	public JsonResponse getServerMessage(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log, params);
		String key = (String) params.get("key");
		String msg = MessagesUtil.getMessage(this, key);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, msg);
		this.methodFinishLog(log, ret);
		return ret;
	}

    /**
     * ダイアログの一覧.
     */
    private Map<String, Dialog> dialogMap = new HashMap<String, Dialog>();

    /**
     * ダイアログ一覧を取得する.
     * @return ダイアログ一覧.
     */
	public Map<String, Dialog> getDialogMap() {
		return dialogMap;
	}

	/**
	 * ダイアログを追加する.
	 * @param dlg ダイアログ.
	 */
	protected void addDialog(final Dialog dlg) {
		this.dialogMap.put(dlg.getId(), dlg);
		this.addComponent(dlg);
	}

	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> map = super.getClassInfo();
		map.put("clientLogLevel", DataFormsServlet.getClientLogLevel());
		Map<String, Object> dlgmap = new HashMap<String, Object>();
		for (String key : this.dialogMap.keySet()) {
			dlgmap.put(key, this.dialogMap.get(key).getClassInfo());
		}
		map.put("dialogMap", dlgmap);
		map.put("contextPath", this.getRequest().getContextPath());
		map.put("framePath", this.getPageFramePath());
		map.put("noFrame", this.isNoFrame());
		map.put("pageExt", this.getPageExt());
		map.put("errorPage", DataFormsServlet.getErrorPage());
		map.put("pageTitle", this.getPageTitle());
		map.put("topPage", Page.getTopPage());
		map.put("browserBackButton", Page.getBrowserBackButton());
		map.put("dataformsVersion", Page.dataformsVersion);
		map.put("dataformsVendor", Page.dataformsVendor);
		return map;
	}

    /**
     * 要求情報への弱参照.
     */
    private WeakReference<HttpServletRequest> request = null;

    /**
     * 応答情報への弱参照.
     */
    private WeakReference<HttpServletResponse> response = null;



	/**
	 * 要求情報を取得する.
	 * @return 要求情報.
	 */
	public final HttpServletRequest getRequest() {
		return request.get();
	}

	/**
	 * 要求情報を設定する.
	 * @param request 要求情報.
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = new WeakReference<HttpServletRequest>(request);
	}

	/**
	 * 応答情報を取得する.
	 * @return 応答情報.
	 */
	public final HttpServletResponse getResponse() {
		return response.get();
	}

	/**
	 * 応答情報を設定する.
	 * @param response 応答情報.
	 */
	public void setResponse(final HttpServletResponse response) {
		this.response = new WeakReference<HttpServletResponse>(response);
	}


	/**
	 * dataforms.jarのバージョンを取得します。
	 * @throws Exception 例外.
	 *
	 */
	public void initDataformsVersion() throws Exception {
		if (Page.dataformsVersion == null) {
			byte[] manifest = this.getBinaryWebResource("/dataforms.mf");
			if (manifest != null) {
				ByteArrayInputStream is = new ByteArrayInputStream(manifest);
				try {
					Manifest m = new Manifest(is);
					Attributes a = m.getMainAttributes();
					Page.dataformsVersion = a.getValue("Implementation-Version");
					Page.dataformsVendor = a.getValue("Implementation-Vendor");
					Page.dataformsCreateDate = a.getValue("CreatedTime");
				} finally {
					is.close();
				}
			}
		}
	}

	@Override
	public void init() throws Exception {
		this.initDataformsVersion();
		super.init();
	}


    /**
	 * ページ情報を取得します。
	 * @param params パラメータ.
	 * @return 応答.
	 * @throws Exception 例外.
	 */
    @WebMethod(useDB = true)
	public JsonResponse getPageInfo(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log, params);
		this.init();
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, this.getClassInfo());
		this.methodFinishLog(log, ret);
		return ret;
	}

    /**
     * ログイン中のユーザIDを取得する.
     * @return ログイン中のID.
     */
    public long getUserId() {
    	long userid = -1L;
    	@SuppressWarnings("unchecked")
		Map<String, Object> userInfo = (Map<String, Object>) this.getRequest().getSession().getAttribute("userInfo");
    	if (userInfo != null) {
        	userid = (Long) userInfo.get("userId");
    	}
    	return userid;
    }


	/**
	 * 現在の言語コードを取得する.
	 * <pre>
	 * アクセスしたブラウザの言語コードがサポートされていない場合、
	 * "default"を設定する.
	 * </pre>
	 * @return 現在の言語コード.
	 */
	public String getCurrentLanguage() {
		String lang = this.getRequest().getLocale().getLanguage();
		if (DataFormsServlet.getSupportLanguage().indexOf(lang) < 0) {
			lang = "default";
		}
		return lang;
	}

	/**
	 * セッションからユーザ情報を取得する.
	 * @return ユーザ情報.
	 */
	public Map<String, Object> getUserInfo() {
		@SuppressWarnings("unchecked")
		Map<String, Object> userInfo = (Map<String, Object>) this.getRequest().getSession().getAttribute("userInfo");
		return userInfo;
	}

	/**
	 * 該当するユーザ属性を持つかをチェックする.
	 * @param t ユーザ属性.
	 * @param v ユーザ属性値.
	 * @return 指定されたユーザ属性を持つ場合true.
	 */
	@SuppressWarnings("unchecked")
	protected boolean checkUserAttribute(final String t, final String v) {
		Map<String, Object> userInfo = (Map<String, Object>) this.getRequest().getSession().getAttribute("userInfo");
		if (userInfo != null) {
			List<Map<String, Object>> attlist = (List<Map<String, Object>>) userInfo.get("attTable");
			for (Map<String, Object> m: attlist) {
				String type = (String) m.get("userAttributeType");
				String value = (String) m.get("userAttributeValue");
				if (t.equals(type) && v.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * フレームのパスを取得する.
	 * @return レイアウトのパス.
	 */
    public static String getFramePath() {
		return framePath;
	}

    /**
     * フレームのパスを設定する.
     * @param framePath レイアウトのパス.
     */
	public static void setFramePath(final String framePath) {
		Page.framePath = framePath;
	}

	/**
	 * ページのフレームパスを取得します。
	 * <pre>
	 * 	基本的にPage.getFramePathの値を返します。
	 *  各ページごとに異なるフレームを使いたい場合、このメソッドをオーバーライドします。
	 * </pre>
	 * @return ページのフレームパス。
	 */
	public String getPageFramePath() {
		return Page.getFramePath();
	}

	/**
	 * メニュー項目フラグを取得する.
	 * @return メニュー項目フラグ.
	 */
	public boolean isMenuItem() {
		return menuItem;
	}

	/**
	 * メニュー項目フラグを設定する.
	 * @param menuItem メニュー項目フラグ.
	 */
	public void setMenuItem(final boolean menuItem) {
		this.menuItem = menuItem;
	}



	/**
	 * フレーム無フラグを取得します。
	 * @return フレーム無フラグ。
	 */
	public boolean isNoFrame() {
		return noFrame;
	}

	/**
	 * フレーム無フラグを設定します。
	 * @param noFrame フレーム無フラグ.
	 */
	public void setNoFrame(final boolean noFrame) {
		this.noFrame = noFrame;
	}

	/**
	 * ページ名称をFunction.propertiesから取得します。
	 * @return ページ名称。
	 * @throws Exception 例外。
	 */
	protected String getPageTitle() throws Exception {
		String ret = null;
		String clsname = this.getClass().getName();
		String[] dirs = clsname.split("\\.");
		String path = "";
		for (int i = 0; i < dirs.length - 1; i++) {
			path += ("/" + dirs[i]);
			String funcprop = this.getAppropriateLangPath(path + "/Function.properties", this.getRequest());
			log.debug("funcprop=" + funcprop);
			if (funcprop != null) {
				String str = this.getWebResource(funcprop);
				SequentialProperties p = new SequentialProperties();
				p.loadText(str);
				String v = p.getProperty(clsname);
				log.debug("v=" + v);
				if (v != null) {
					String [] t = v.split("\\|");
					ret = t[0];
				}
				break;
			}
		}
		return ret;
	}

	/**
	 * メニューの表示名を飾る場合オーバーライドします。
	 * <pre>
	 * メニュー名称にアイコン等を追加する場合にオーバーライドします。
	 * </pre>
	 * @param menuName メニュー名称。
	 * @return 変換後のメニュー名称。
	 */
	public String decorateMenuName(final String menuName) {
		return menuName;
	}
	
	/**
	 * ブラウザの戻るボタンの許可状態を取得します。
	 * @return ブラウザの戻るボタンの許可状態。
	 */
	public static String getBrowserBackButton() {
		return browserBackButton;
	}

	/**
	 * ブラウザの戻るボタン許可状態を設定します。
	 * @param browserBackButton ブラウザの戻るボタンの許可状態。
	 */
	public static void setBrowserBackButton(final String browserBackButton) {
		Page.browserBackButton = browserBackButton;
	}

	/**
	 * トップページのURLを取得します。
	 * @return トップページのURL。
	 */
	public static String getTopPage() {
		return topPage;
	}

	/**
	 * トップページのURLを設定します。
	 * @param topPage トップページのURL。
	 */
	public static void setTopPage(final String topPage) {
		Page.topPage = topPage;
	}

	/**
	 * dataformsのバージョンを取得します。
	 * @return dataformsのバージョン。
	 */
	public static String getDataformsVersion() {
		return dataformsVersion;
	}

	/**
	 * dataformsのベンダーを取得します。
	 * @return dataformsのベンダー。
	 *
	 */
	public static String getDataformsVendor() {
		return dataformsVendor;
	}

	/**
	 * dataformsの作成日時を取得します。
	 * @return dataformsの作成日時。
	 *
	 */
	public static String getDataformsCreateDate() {
		return dataformsCreateDate;
	}


}
