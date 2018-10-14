package dataforms.menu;

import java.util.Map;

/**
 * サイドメニュークラス。
 * <pre>
 * 全てのページからアクセスできるように画面の端に常に固定したり、ボタンによって表示することができるメニューです。
 * <a href="../../../jsdoc/SideMenu.html" target="_blank">jsdocを参照</a>
 * </pre>
 */
public class SideMenu extends Menu {
	
	/**
	 * メニュー表示設定。
	 */
	private static Boolean multiOpenMenu = true;
	
	/**
	 * コンストラクタ。
	 */
	public SideMenu() {
		super(null);
	}

	/**
	 * メニューの表示設定を取得します。
	 * @return メニューの表示設定。
	 */
	public static Boolean getMultiOpenMenu() {
		return SideMenu.multiOpenMenu;
	}

	/**
	 * メニューの表示設定を設定します。
	 * @param multiOpenMenu メニューの表示設定。
	 */
	public static void setMultiOpenMenu(final Boolean multiOpenMenu) {
		SideMenu.multiOpenMenu = multiOpenMenu;
	}
	
	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("multiOpenMenu", SideMenu.multiOpenMenu);
		return ret;
	}
}
