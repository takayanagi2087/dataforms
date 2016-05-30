/**
 * メニューフォーム.
 */



/**
 * メニューフォーム.
 */

createSubclass("SiteMapForm", {}, "MenuForm");
/**
 * ページの各エレメントとの対応付け.
 */
SiteMapForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	this.menu = this.newInstance(this.menu);
	this.menu.init();
	this.menu.attach();
};
