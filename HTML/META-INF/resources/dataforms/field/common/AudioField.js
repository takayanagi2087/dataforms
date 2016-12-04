/**
 * @fileOverview {@link AudioField}クラスを記述したファイルです。
 */

/**
 * @class AudioField
 * 音声ファイルアップロードフィールドクラス。
 * @extends FileField
 */
AudioField = createSubclass("AudioField", {}, "FileField");


/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 削除チェックボックス、ダウンロードリンクなどの設定を行います。
 * </pre>
 */
AudioField.prototype.attach = function() {
	FileField.prototype.attach.call(this);
	var thisField = this;
	var linkid = this.id + "_link";
	var link = this.parent.find("#" + this.selectorEscape(linkid));
	var playerid = this.id + "_player"; // サムネイルID.
	var player = this.parent.find("#" + this.selectorEscape(playerid));
	this.get().change(function(){
		for(var j=0; j<this.files.length; j++){
			var url = URL.createObjectURL(this.files[j])
			logger.log("url=" + url);
			player.attr("src", url);
		}
	});
};


/**
 * ファイルフィールドに付随する各種コンポーネントを配置します。
 * @param comp ファイルフィールド。
 */
AudioField.prototype.addElements = function(comp) {
	var getParts = new SyncServerMethod("getParts");
	var htmlstr = getParts.execute("parts=" + escape(currentPage.framePath + "/AudioField.html"));
	if (htmlstr.status == ServerMethod.SUCCESS) {
		var html = htmlstr.result.replace(/\$\{fieldId\}/g, this.id);
		logger.log("htmlstr=" + html);
		var tag = comp.prop("tagName");
		var type = comp.prop("type");
		if ("INPUT" == tag && type == "file") {
			comp.after(html);
		} else if (tag == "DIV") {
			comp.html(html);
			this.parent.find("#" + this.selectorEscape(this.id + "_ck")).hide();
			this.parent.find("label[for='" + this.selectorEscape(this.id + "_ck") + "']").hide();
		}
	}
};

/**
 * 値を設定します。
 *
 * @param {String} value 値。
 */
AudioField.prototype.setValue = function(value) {
	FileField.prototype.setValue.call(this, value);
	var videoid = this.id + "_player"; // プレーヤーID.
	var video = this.parent.find("#" + this.selectorEscape(videoid));
	if (value != null) {
		var url = location.pathname + "?dfMethod=" + this.getUniqId() + ".download"  + "&" + value.downloadParameter;
		video.attr("src", url);
	} else {
		video.attr("src", null);
	}

};