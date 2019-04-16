/**
 * @fileOverview {@link Menu}クラスを記述したファイルです。
 */

/**
 * @class Menu
 * メニュークラス。
 * <pre>
 * このクラスに対応するHTMLの記述は以下のようなものになります。
 * 以下の例はid="menu"の場合です。
 * <code>
 * 	&lt;div id=&quot;menu&quot; style=&quot;width:100%;&quot;&gt;
 *		&lt;table id=&quot;menu[0]&quot; style=&quot;width:100%;&quot;&gt;
 *			&lt;thead&gt;
 *				&lt;tr&gt;
 *					&lt;th id=&quot;menu[0].name&quot; colspan=&quot;2&quot;&gt;&lt;/th&gt;
 *				&lt;/tr&gt;
 *			&lt;/thead&gt;
 *			&lt;tbody id=&quot;menu[0].pageList&quot;&gt;
 *				&lt;tr&gt;
 *					&lt;td style=&quot;width:150px;&quot;&gt;
 *						&lt;a id=&quot;menu[0].pageList[0].url&quot;&gt;
 *							&lt;span id=&quot;menu[0].pageList[0].name&quot;&gt;Menu item&lt;/span&gt;
 *						&lt;/a&gt;
 *					&lt;/td&gt;
 *					&lt;td&gt;
 *						&lt;span id=&quot;menu[0].pageList[0].description&quot;&gt;Description&lt;/span&gt;
 *					&lt;/td&gt;
 *				&lt;/tr&gt;
 *			&lt;/tbody&gt;
 *		&lt;/table&gt;
 *	&lt;/div&gt;
 * </code>
 *
 * </pre>
 * @extends WebComponent
 */
Menu = createSubclass("Menu", {}, "WebComponent");


/**
 * メニューのレイアウト情報を取得します。
 */
Menu.prototype.getMenuLayout = function() {
	this.menuLayout = this.get().html();
}

/**
 * HTMLエレメントとの対応付けを行います。
 */
Menu.prototype.attach = function() {
	WebComponent.prototype.attach.call(this);
	this.getMenuLayout();
	this.update();
}

/**
 * メニューの表示内容を更新します。
 */
Menu.prototype.update = function() {
	var mglist = this.menuGroupList;
	var menuHtml = this.getMenuHtml(mglist);
	var menu = this.get();
	menu.html(menuHtml);
}

/**
 * メニューの内容を展開したHTML作成します。
 * @param {Array} mglist メニューグループリスト.
 * @returns {String} HTML.
 */
Menu.prototype.getMenuHtml = function(mglist) {
    logger.dir(mglist);
    var ret = "";
	var pat = new RegExp(this.id + "\\[0\\]", "g");
	var patl = new RegExp("pageList\\[0\\]", "g");
	for (var i = 0; i < mglist.length; i++) {
		var mg = this.menuLayout.replace(pat, this.id + "[" + i + "]")
		var q = $("<div>" + mg + "</div>");
		var menu = $(q.find("[id$='\\.pageList']").get()[0]).html();
		var plist = q.find("[id$='\\.pageList']");
		plist.empty();
		for (var j = 0; j < mglist[i].pageList.length; j++) {
			plist.append(menu.replace(patl, "pageList[" + j +"]"));
		}
		q.find("#" + this.id + "\\[" + i + "\\]\\.name").html(mglist[i].name);
		q.find("#" + this.id + "\\[" + i + "\\]\\.name").attr("data-menu-group-id", mglist[i].id);
		for (var j = 0; j < mglist[i].pageList.length; j++) {
            plist.find("[id$='\\.pageList\\[" + j + "\\]\\.url']").attr("href", mglist[i].pageList[j].url);
            if (mglist[i].pageList[j].menuTarget != null) {
                plist.find("[id$='\\.pageList\\[" + j + "\\]\\.url']").attr("target", mglist[i].pageList[j].menuTarget);
            }
			plist.find("[id$='\\.pageList\\[" + j + "\\]\\.name']").html(mglist[i].pageList[j].menuName);
			plist.find("[id$='\\.pageList\\[" + j + "\\]\\.description']").html(mglist[i].pageList[j].description);
		}
		ret += q.html();
	}
	return ret;
}
