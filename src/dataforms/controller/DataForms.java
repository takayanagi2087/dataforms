package dataforms.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.util.FileUtil;
import dataforms.util.StringUtil;

/**
 * DataFormsクラス。
 * <pre>
 * 複数のformクラスまとめるためのクラスです。
 * DataFormsのサブクラスはPageとDialogです。
 * Pageは複数のFormと複数のDialogを持つことができます。
 * Dialogはjquery-uiのdialogで実装し、複数のFormオブジェクトを持つことができます。
 * QueryForm,QueryResultForm,EditFormを部品として持つ場合、
 * それぞれのFormを適切に制御します。
 * </pre>
 *
 */
public class DataForms extends WebComponent {

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(DataForms.class.getName());



	/**
	 * 認証済みかどうかを返します。
	 * @param params POSTされたパラメータ。
	 * @return 認証済みの場合true。
	 * @throws Exception 例外。
	 */
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		return false;
	}

    /**
     * HTMLテキスト中のtitleの内容を取得します。
     * <pre>
     * title TAGが無い場合nullを返します。
     * </pre>
     * @param htmltext HTMLのテキスト。
     * @return titleの中のテキスト。
     */
    protected String getHtmlTitle(final String htmltext) {
    	Pattern p = Pattern.compile("<title[\\s\\S]*>[\\s\\S]*</title>",
    			Pattern.MULTILINE
    	);
    	Matcher m = p.matcher(htmltext);
    	if (m.find()) {
    		return m.group().replaceAll("(<[Tt][Ii][Tt][Ll][Ee][\\s\\S]*?>)|(</[Tt][Ii][Tt][Ll][Ee]>)", "");
    	} else {
    		return null;
    	}
    }


    /**
     * HTML部品のHEADを取得します。
     * <pre>
     * partsパラメータに指定されたパスのHTMLのHEADの内容を返します。
     * </pre>
     * @param params パラメータ。
     * @return 部品のHTML文字列。
     * @throws Exception 例外。
     */
    @WebMethod(useDB = false)
	public JsonResponse getHead(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log, params);
		String parts = (String) params.get("parts");
		String blockParts = parts;
    	String htmlpath = this.getAppropriatePath(blockParts, this.getPage().getRequest());
    	String htmltext = this.getWebResource(htmlpath); //FileUtil.readTextFile(htmlpath, DataFormsServlet.getEncoding());
    	if (htmltext != null) {
        	htmltext = this.getHtmlHead(htmltext);
    	}
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, htmltext);
		this.methodFinishLog(log, ret);
		return ret;
	}

    /**
     * HTML部品を取得します。
     * <pre>
     * partsパラメータに指定されたパスのHTMLのBODYの内容を返します。
     * </pre>
     * @param params パラメータ。
     * @return 部品のHTML文字列。
     * @throws Exception 例外。
     */
    @WebMethod(useDB = false)
	public JsonResponse getParts(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log, params);
		String parts = (String) params.get("parts");
		String blockParts = parts;
    	String htmlpath = this.getAppropriatePath(blockParts, this.getPage().getRequest());
    	String htmltext = this.getWebResource(htmlpath); //FileUtil.readTextFile(htmlpath, DataFormsServlet.getEncoding());
    	if (htmltext != null) {
        	htmltext = this.getHtmlBody(htmltext);
    	}
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, htmltext);
		this.methodFinishLog(log, ret);
		return ret;
	}

    /**
     * リクエストに応じた適切なファイルpathを取得します。
     * <pre>
     * ブラウザの言語、ユーザエージェントに応じた適切なファイルのPathを取得します。
     * pathに/home/hoge.htmlが指定された場合、以下のような優先順位でパスを選択します。
     * UserAgentが携帯電話で言語がjaの場合以下の順序でファイルの存在を確認し、最初に見つけたファイルのパスを返します。
     * 1. /home/hoge_phone_ja.html
     * 2. /home/hoge_phone.html
     * 3. /home/hoge_ja.html
     * 4. /home/hoge.html
     * </pre>
     * @param path パス。
     * @param req HTTP要求情報。
     * @return html等のパス。
     * @throws Exception 例外。
     */
	public String getAppropriatePath(final String path, final HttpServletRequest req) throws Exception {
		String ua = req.getHeader("user-agent");
//		log.debug("user-agent:" + ua);
		String ret = null;
	    if (ua.indexOf("iPhone") > 0 || ua.indexOf("iPod") > 0 || ua.indexOf("Android") > 0 && ua.indexOf("Mobile") > 0) {
			String ext = FileUtil.getExtention(path);
	    	String htmlpath = path.replaceAll("\\." + ext + "$", "_phone." + ext);
			ret = getAppropriateLangPath(htmlpath, req);
	    }
	    if (ret == null) {
			ret = getAppropriateLangPath(path, req);
	    }
	    return ret;
	}


	/**
	 * 言語に応じた適切なファイルパスを検索検索します。
	 * <pre>
     * ブラウザの言語に応じた適切なファイルのPathを取得します。
     * pathに/home/hoge.htmlが指定された場合、以下のような優先順位でパスを選択します。
     * 言語がjaの場合以下の順序でファイルの存在を確認し、最初に見つけたファイルのパスを返します。
     * 1. /home/hoge_ja.html
     * 2. /home/hoge.html
     * </pre>
     * @param path パス。
     * @param req HTTP要求情報。
     * @return html等のパス。
     * @throws Exception 例外。
	 */
	protected String getAppropriateLangPath(final String path, final HttpServletRequest req) throws Exception {
 		String spath = path;
 		if (spath.charAt(0) != '/') {
 			spath = "/" + spath;
 		}
		String lang = req.getLocale().getLanguage();
		String ext = FileUtil.getExtention(spath);
    	String p = spath.replaceAll("\\." + ext + "$", "_" + lang + "." + ext);
    	if (StringUtil.isBlank(this.getWebResource(p))) {
        	p = spath.replaceAll("\\." + ext + "$", "." + ext);
           	if (StringUtil.isBlank(this.getWebResource(p))) {
            	spath = null;
        	} else {
        		spath = p;
        	}
    	} else {
    		spath = p;
    	}
   		return spath;
	}


    /**
     * javascriptファイルを取得します。
     * <pre>
     * partsパラメータに対応したjavascriptファイルの内容を取得します。
     * </pre>
     *
     * @param params パラメータ。
     * @return javascriptファイルの内容。
     * @throws Exception 例外。
     */
    @WebMethod(useDB = false)
	public JsonResponse getJs(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log, params);
		String jspath = (String) params.get("parts");
		String jstext = this.getWebResource("/" + jspath);
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, jstext);
		this.methodFinishLog(log, ret);
		return ret;
	}



	/**
	 * フォームマップを取得します。
	 * @return フォームマップ。
	 */
	public Map<String, WebComponent> getFormMap() {
		return this.getComponentMap();
	}

	/**
	 * フォームを追加します。
	 * @param form フォーム。
	 * @return 追加したフォーム。
	 */
	public Form addForm(final Form form) {
		this.addComponent(form);
		return form;
	}

	/**
	 * このページまたはダイアログ中のフォームのマップを取得します。
	 * @return 保持するフォームのマップ。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getFormInfo() throws Exception {
		log.info("fmap=" + this.getFormMap());
		Map<String, Object> map = new HashMap<String, Object>();
		Set<String> keyset = this.getFormMap().keySet();
		for (String key : keyset) {
			WebComponent f = this.getFormMap().get(key);
			if (f instanceof Form) {
				map.put(key, f.getClassInfo());
			}
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 * クラス情報にフォームのマップを追加します。
	 *
	 */
	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> map = super.getClassInfo();
		map.put("formMap", this.getFormInfo());
		return map;
	}
}
