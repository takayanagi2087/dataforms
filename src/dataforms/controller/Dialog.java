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

	@Override
	public void init() throws Exception {
		super.init();
		String path = this.getViewPath();
		this.setAdditionalHtml(path + ".html");
	}
	
	/**
	 * タイトルの取得。
	 * @return タイトル文字列。
	 */
	protected String getTitle() {
		try {
			String path = this.getViewPath();
			String htmlpath = this.getAppropriatePath(path + ".html", this.getPage().getRequest());
			String htmltext = this.getWebResource(htmlpath);
			return this.getHtmlTitle(htmltext);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> map =  super.getProperties();
		map.put("title", this.getTitle());
		return map;
	}
}
