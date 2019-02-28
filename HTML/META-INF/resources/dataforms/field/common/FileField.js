/**
 * @fileOverview {@link FileField}クラスを記述したファイルです。
 */

/**
 * @class FileField
 * ファイルフィールドクラス。
 * <pre>
 * 各種ファイルフィールドの基底クラスです。
 * </pre>
 * @extends Field
 */
FileField = createSubclass("FileField", {}, "Field");

/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
 * </pre>
 */
FileField.prototype.attach = function() {
	var comp = this.get();
	this.addSpan(comp);
	this.addElements(comp);
	Field.prototype.attach.call(this);
	var thisComp = this;
	var linkid = this.id + "_link"; // ダウンロードリンク.
	var fnid = this.id + "_fn"; // ファイル名のリンク.
	var ckid = this.id + "_ck"; // ファイル削除のチェックボックス.
	var fnlink = this.parent.find("#" + this.selectorEscape(linkid))
	var fnhidden = this.parent.find("[name='" + this.selectorEscape(fnid) + "']");
	this.parent.find("#" + this.selectorEscape(ckid)).click(function() {
		thisComp.delFile($(this));
	});
	comp.change(function() {
		var ck = thisComp.parent.find("#" + thisComp.selectorEscape(ckid));
		ck.attr("checked", false);
		fnlink.html(fnlink.attr("data-value"));
		fnhidden.val(fnlink.attr("data-value"));
		thisComp.id = $(this).attr("id");
		thisComp.showDelCheckbox();
	});
	if (this.readonly) {
		this.lock(true);
	} else {
		this.lock(false);
	}

};

/**
 * 削除チェックボックスの処理を行います。
 */
FileField.prototype.delFile = function(ck) {
	var comp = this.get();
	var linkid = this.id + "_link"; // ダウンロードリンク.
	var fnid = this.id + "_fn"; // ファイル名のリンク.
	var fnlink = this.parent.find("#" + this.selectorEscape(linkid))
	var fnhidden = this.parent.find("[name='" + this.selectorEscape(fnid) + "']");
	logger.log("checked=" + ck.prop("checked"));
	if (ck.prop("checked")) {
		fnhidden.val("");
		fnlink.html("");
		comp.val("");
	} else {
		fnlink.html(fnlink.attr("data-value"));
		fnhidden.val(fnlink.attr("data-value"));
	}
};

/**
 * 削除チェックボックスを表示します。
 */
FileField.prototype.showDelCheckbox = function() {
	var ckid = this.id + "_ck";
	logger.log("ckid=" + ckid);
	var delcheck = this.parent.find("#" + this.selectorEscape(ckid));
	delcheck.show();
	delcheck.next("label:first").show();
};


/**
 * 削除チェックボックスを隠します。
 */
FileField.prototype.hideDelCheckbox = function() {
	var ckid = this.id + "_ck";
	var delcheck = this.parent.find("#" + this.selectorEscape(ckid));
	delcheck.hide();
	delcheck.next("label:first").hide();
};

/**
 * ファイルフィールドに付随する各種コンポーネントを配置します。
 * @param comp ファイルフィールド。
 */
FileField.prototype.addElements = function(comp) {
	var htmlstr = this.additionalHtmlText;
	var html = htmlstr.replace(/\$\{fieldId\}/g, this.id);
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	if ("INPUT" == tag && type == "file") {
		comp.after(html);
	} else if (tag == "DIV") {
		comp.html(html);
	}
};

/**
 * 値を設定します。
 *
 * @param {String} value 値。
 */
FileField.prototype.setValue = function(value) {
	var comp = this.get();
	var tag = comp.prop("tagName");
	var type = comp.prop("type");
	var linkid = this.id + "_link";
	var fnid = this.id + "_fn";
	var ckid = this.id + "_ck";
	var delcheck = this.parent.find("#" + this.selectorEscape(ckid));
	if (value != null) {
		var form = this.getParentForm();
		var url = location.pathname + "?dfMethod=" + encodeURIComponent(this.getUniqId()) + ".download"  + "&" + value.downloadParameter;
		if (currentPage.csrfToken != null) {
			url += "&csrfToken=" + currentPage.csrfToken;
		}
		var fnlink = this.parent.find("#" + this.selectorEscape(linkid));
		fnlink.attr("href", url);
		var fnhidden = this.parent.find("[name='" + this.selectorEscape(fnid) + "']");
		fnlink.html(value.fileName);
		fnlink.attr("data-value", value.fileName);
		fnlink.attr("data-size", value.size);
		fnlink.attr("data-dlparam", value.downloadParameter);
		fnhidden.val(value.fileName);
		if (this.readonly) {
			this.hideDelCheckbox();
		} else {
			var tag = comp.prop("tagName");
			var type = comp.prop("type");
			if ("INPUT" == tag && type == "file") {
				this.showDelCheckbox();
			}
		}
		delcheck.attr("checked", false);
	} else {
		var fnlink = this.parent.find("#" + this.selectorEscape(linkid));
		fnlink.attr("href", "");
		var fnhidden = this.parent.find("[name='" + this.selectorEscape(fnid) + "']");
		fnlink.html("");
		fnlink.attr("data-value", "");
		fnlink.attr("data-size", "");
		fnlink.attr("data-dlparam", "");
		fnhidden.val("");
		this.hideDelCheckbox();
		delcheck.attr("checked", false);
	}
	if ("INPUT" == tag) {
		comp.val("");
	}
};

/**
 * 値を取得します。
 * @return {String} 値。
 */
FileField.prototype.getValue = function() {
	var ret = this.get().val();
	if (ret.length == 0) {
		var fnid = this.id + "_link";
		ret = this.parent.find("#" + this.selectorEscape(fnid)).text();
	}
	return ret;
};

/**
 * フィールドの検証を行ないます。
 * <pre>
 * 各フィールドのバリデータを呼び出します。
 * 追加のチェックが必要な場合、このメソッドをオーバーライドします。
 * </pre>
 * @returns {ValidationError} 検証結果。問題が発生しなければnullを返します。
 */
FileField.prototype.validate = function() {
	var val = this.getValue();
	this.value = val;
	if (this.validators != null) {
		for (var i = 0; i < this.validators.length; i++) {
			var v = this.validators[i];
			if (v.validate(val) == false) {
				var msg = v.getMessage(this.label);
				return new ValidationError(this.id, msg);
			}
		}
	}
	return null;
};

