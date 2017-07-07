package dataforms.app.form;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.app.dao.menu.MenuDao;
import dataforms.controller.Form;
import dataforms.controller.JsonResponse;
import dataforms.controller.Page;
import dataforms.menu.Menu;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.ClassFinder;
import dataforms.util.MessagesUtil;
import dataforms.util.SequentialProperties;

/**
 * メニューフォームクラス。
 */
public class MenuForm extends Form {
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(MenuForm.class.getName());

    /**
     * メニュー。
     */
    private Menu menu = null;

	/**
	 * コンストラクタ。
	 */
	public MenuForm() {
		super(null);
		this.menu = this.newMenuComponent();
		this.addComponent(this.menu);
	}


	/**
	 * コンストラクタ。
	 * @param id ID。
	 */
	public MenuForm(final String id) {
		super(id);
		this.menu = this.newMenuComponent();
		this.addComponent(this.menu);
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.menu.setPageList(this.getMenuList());
	}

	/**
	 *　メニューを取得します。
	 * @return メニュー。
	 */
	public Menu getMenu() {
		return menu;
	}

	/**
	 * メニューを設定します。
	 * @param menu メニュー。
	 */
	public void setMenu(final Menu menu) {
		this.menu = menu;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * ニューフォームの場合アプリケーションによってデザインを変更できるように
	 * フレームフォルダを参照するように変更します。
	 * </pre>
	 * @param cls パスを取得するクラス。
	 * @return デフォルトのhtml,jsパス。
	 */
	@Override
	public String getWebResourcePath(final Class<?> cls) {
		if (cls.getName().equals(this.getClass().getName())) {
			return this.getPage().getPageFramePath().substring(1) + "/" + cls.getSimpleName();
		} else {
			return super.getWebResourcePath(cls);
		}
	}


	/**
	 * メニューのインスタンスを作成します。
	 * @return メニュー。
	 */
	protected  Menu newMenuComponent() {
		return new Menu();
	}

	/**
	 * メニューリストを取得します。
	 * <pre>
	 * 機能のリストを取得し、各機能に対応したパッケージ内のPageクラスの一覧を作成し、
	 * ログインユーザが使用可能なページをリストアップします。
	 * メニューに表示する名称は各機能のパスのFunction.propertiesから取得します。
	 * </pre>
	 * @return メニュリスト。
	 * @throws Exception 例外。
	 */
	protected List<Map<String, Object>> getMenuList() throws Exception {
		MenuDao dao = new MenuDao(this);
		List<Map<String, Object>> list = this.funcListToMenuList(dao.getFuncList());

		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: list) {
			String classname = DataFormsServlet.convertPageClassName((String) m.get("pageClass"));
			@SuppressWarnings("unchecked")
			Class<? extends Page> clazz = (Class<? extends Page>) Class.forName(classname);
			try {
		    	Page page = clazz.newInstance();
		    	page.setRequest(this.getPage().getRequest());
		    	if (page.isMenuItem()) {
			    	if (page.isAuthenticated(new HashMap<String, Object>())) {
			    		String menuName = (String) m.get("menuName");
			    		log.debug("menuName=" + menuName);
			    		m.put("menuName", page.decorateMenuName(menuName));
			    		mlist.add(m);
			    	}
		    	}
			} catch (Error e) {
				log.error(e.getMessage(), e);
			}
		}
		return mlist;
	}

	/**
	 * 機能リストからメニューリストを作成します。
	 * <pre>
	 * 機能リストに対応したFunction.propertiesを読み込み、メニューリストを作成します。
	 * </pre>
	 * @param funcList 機能リスト。
	 * @return メニューリスト。
	 * @throws Exception 例外。
	 */
	protected List<Map<String, Object>> funcListToMenuList(final List<Map<String, Object>> funcList) throws Exception {
		List<Map<String, Object>> menuList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: funcList) {
			String funcPath = (String) m.get("funcPath");
			String propFile = funcPath + "/Function";
			SequentialProperties prop = MessagesUtil.getProperties(this.getPage(), propFile);
			if (prop.getKeyList() != null) {
				for (int i = 1; i < prop.getKeyList().size(); i++) {
					String key = prop.getKeyList().get(i);
					Map<String, Object> menu = new HashMap<String, Object>();
					menu.put("pageClass", key);
					String text = (String) prop.get(key);
					String[] menuinfo = text.split("\\|");
					menu.put("menuName", menuinfo[0]);
					if (menuinfo.length >= 2) {
						menu.put("description", menuinfo[1]);
					} else {
						menu.put("description", "");
					}
					menu.put("menuGroup", "menuGroup" + m.get("funcId"));
					menu.put("menuGroupName", prop.get(prop.getKeyList().get(0)));
					menuList.add(menu);
				}
			}
			// プロパティに存在しないページクラスを追加する.
	    	String packageName = funcPath.substring(1).replaceAll("/", ".");
	    	if (packageName != null) {
	    		if (prop.getKeyList() != null) {
		    		if (prop.getKeyList().size() > 0) {
				    	ClassFinder finder = new ClassFinder();
				    	List<Class<?>> pageList = finder.findClasses(packageName, Page.class);
				    	for (Class<?> cls: pageList) {
				    		if ((cls.getModifiers() & Modifier.ABSTRACT) != 0) {
				    			continue;
				    		}
				    		if (prop.get(cls.getName()) == null) {
								Map<String, Object> menu = new HashMap<String, Object>();
								menu.put("pageClass", cls.getName());
								menu.put("menuName", cls.getSimpleName());
								menu.put("menuGroup", "menuGroup" + m.get("funcId"));
								menu.put("menuGroupName", prop.get(prop.getKeyList().get(0)));
								menuList.add(menu);
				    		}
				    	}
		    		}
	    		}
	    	}
		}
		return menuList;
	}


	/**
	 * メニューを取得します。
	 * @param param パラメータ。
	 * @return メニュー情報。
	 * @throws Exception 例外。
	 */
	@WebMethod(everyone = true)
	public JsonResponse getMenu(final Map<String, Object> param) throws Exception {
		//List<Map<String, Object>> pageList = this.getMenuList();
		this.menu.setPageList(this.getMenuList());
    	JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, this.menu.getProperties());
    	return ret;
    }


	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret =  super.getProperties();
		Menu menu = this.getMenu();
		ret.put(menu.getId(), menu.getProperties());
		return ret;
	}

}
