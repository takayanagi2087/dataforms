/**
 * @fileOverview {@link SideMenu}クラスを記述したファイルです。
 */

/**
 * @class SideMenu
 *
 * サイドメニュークラス。
 * <pre>
 * </pre>
 * @extends Menu
 */
SideMenu = createSubclass("SideMenu", {}, "Menu");

/**
 * HTMLエレメントとの対応付けを行います。
 */
SideMenu.prototype.attach = function() {
	Menu.prototype.attach.call(this);
	var thisMenu = this;
	var menu = this.get();
	menu.find(".sideMenuGroup").click(function() {
		var menuGroupId = $(this).attr("data-menu-group-id");
		$(this).next().slideToggle("fast", function() {
			thisMenu.setCookie("menuGroup_" + menuGroupId, $(this).is(":visible"));
		});
	});
	menu.find(".sideMenuGroup").each(function() {
		var menuGroupId = $(this).attr("data-menu-group-id");
		var status = thisMenu.getCookie("menuGroup_" + menuGroupId);
		if (status == "true") {
			$(this).next().show();
		} else {
			$(this).next().hide();
		}
	});
};

