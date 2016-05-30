/**
 * メニューフォーム.
 */

createSubclass("SideMenuForm", {}, "MenuForm");
/**
 * ページの各エレメントとの対応付け.
 */
SideMenuForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	this.menu = this.newInstance(this.sideMenu);
	this.menu.init();
	this.menu.attach();
};
