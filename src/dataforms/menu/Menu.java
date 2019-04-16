package dataforms.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.controller.WebComponent;

// TODO:メニューのリストをセッションにキャッシュするようにした方が良いかも。

/**
 * メニュークラス。
 * <pre>
 * サイトマップに表示するような機能一覧用のコンポーネントです。
 * <a href="../../../jsdoc/Menu.html" target="_blank">jsdocを参照</a>
 * </pre>
 */
public class Menu extends WebComponent {
	/**
	 * ページリスト。
	 */
	private List<Map<String, Object>> pageList = null;

	/**
	 * コンストラクタ。
	 */
	public Menu() {
		this.setId(this.getDefaultId());
	}

	/**
	 * コンストラクタ。
	 * @param id メニューID。
	 */
	public Menu(final String id) {
		if (id == null) {
			this.setId(this.getDefaultId());
		} else {
			this.setId(id);
		}
	}


	/**
	 * コンストラクタ。
	 * @param id メニューID。
	 * @param list ページリスト。
	 */
	public Menu(final String id, final List<Map<String, Object>> list) {
		if (id == null) {
			this.setId(this.getDefaultId());
		} else {
			this.setId(id);
		}
		this.setPageList(list);
	}

	/**
	 * ページリストを取得します。
	 * @return ページリスト。
	 */
	public List<Map<String, Object>> getPageList() {
		return pageList;
	}

	/**
	 * ページリストを設定します。
	 * @param pageList ページリスト。
	 */
	public void setPageList(final List<Map<String, Object>> pageList) {
		this.pageList = pageList;
		String cpath = Menu.getServlet().getServletContext().getContextPath();
		for (Map<String, Object> m : pageList) {
			String menuUrl = (String) m.get("menuUrl");
			if (menuUrl == null) {
				String clazz = (String) m.get("pageClass");
				m.put("url", cpath + "/" + clazz.replaceAll("\\.", "/") + "." + this.getPage().getPageExt());
			} else {
				m.put("url", menuUrl);
			}
		}
	}

	/**
	 * ページリストをメニューグループリストに変換します。
	 * @param pageList ページリスト。
	 * @return メニューグループリスト。
	 */
	private List<Map<String, Object>> convertToMenuGroupList(final List<Map<String, Object>> pageList) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		Map<String, Object> mg = new HashMap<String, Object>();
		mg.put("name", "");
		ArrayList<Map<String, Object>> linklist = new ArrayList<Map<String, Object>>();
		mg.put("pageList", linklist);
		for (Map<String, Object> p: pageList) {
			if (!mg.get("name").equals(p.get("menuGroupName"))) {
				if (!"".equals(mg.get("name"))) {
					ret.add(mg);
				}
				mg = new HashMap<String, Object>();
				mg.put("id", p.get("menuGroup"));
				mg.put("name", p.get("menuGroupName"));
				linklist = new ArrayList<Map<String, Object>>();
				mg.put("pageList", linklist);
			}
			Map<String, Object> link = new HashMap<String, Object>();
			link.put("url", p.get("url"));
			link.put("menuTarget", p.get("menuTarget"));
			link.put("menuName", p.get("menuName"));
			link.put("description", p.get("description"));
			linklist.add(link);
		}
		ret.add(mg);
		return ret;
	}


	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("menuGroupList", this.convertToMenuGroupList(pageList));
		return ret;
	}
}
