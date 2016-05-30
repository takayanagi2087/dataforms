package dataforms.controller;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * ダイアログクラス。
 * <pre>
 * DataFormsのサブクラスのDialogクラスです。
 * ダイアログはjquery-uiのdialogで実装しています。
 * </pre>
 *
 */
public abstract class Dialog extends DataForms {

    /**
     * Logger。
     */
    private static Logger log = Logger.getLogger(Dialog.class.getName());


	/**
	 * コンストラクタ。
	 * @param id DialogのID。
	 */
	public Dialog(final String id) {
		if (id == null) {
			this.setId(this.getDefaultId());
		} else {
			this.setId(id);
		}
	}


	@Override
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		return true;
	}

	/**
	 * タイトルの取得。
	 * @return タイトル文字列。
	 */
	protected String getTitle() {
		try {
			String path = this.getViewPath();
			String htmlpath = this.getAppropriatePath(path + ".html", this.getPage().getRequest());
			String htmltext = this.getWebResource(htmlpath); //FileUtil.readTextFile(htmlpath, DataFormsServlet.getEncoding());
			return this.getHtmlTitle(htmltext);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
//		return null;
	}

	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> map =  super.getClassInfo();
		map.put("title", this.getTitle());
		return map;
	}
}
