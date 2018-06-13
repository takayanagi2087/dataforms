/**
 * @fileOverview {@link DocFramePage}クラスを記述したファイルです。
 */

/**
 * @class DocFramePage
 *
 * @extends BasePage
 */
DocFramePage = createSubclass("DocFramePage", {}, "BasePage");

/**
 * HTMLエレメントとの対応付けを行います。
 */
DocFramePage.prototype.attach = function() {
	var thisPage = this;
	BasePage.prototype.attach.call(this);
	var qs = this.getQueryString();
	logger.log("doc=" + qs["doc"])
	if (qs["doc"] != null) {
		var src = "../../../../doc/" + qs["doc"];
		this.find("#docFrame").attr("src", src);
	} else {
		var src = "../../../../doc/introduction";
		this.find("#docFrame").attr("src", src);
	}
	this.find("a").click(function() {
		var id = $(this).attr("id");
		var src = "../../../../doc/" + id;
		thisPage.find("#docFrame").attr("src", src);

	});
	thisPage.find("#docFrame").on('load', function() {
		thisPage.onLoadDocFrame($(this));
	});

	$(window).resize(function() {
		thisPage.onResize();
	});
	thisPage.onResize();
}

/**
 * Windowサイズ変更の際のiframeのサイズを調整します。
 */
DocFramePage.prototype.onResize = function() {
	var h = $(window).height() - $("#headerDiv").height() - $("#footerDiv").height();
	logger.log("h=" + h + "," + $(window).height() + "," + $("#headerDiv").height() + "," + $("#footerDiv").height());
	$("#mainDiv").css("height", (h - 32) + "px");
	$("#docFrame").css("height", (h - 64) + "px");
};

/**
 * docFrame内に各機ドキュメントが読み込まれた際の処理を行います。
 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
 */
DocFramePage.prototype.onLoadDocFrame = function(docFrame) {
	var src = docFrame.attr("src");
	if (src.indexOf("javadoc") < 0 && src.indexOf("jsdoc") < 0) {
		this.setH2No(docFrame);
		this.setH3No(docFrame);
		this.setFigNo(docFrame);
		this.setFigEvent(docFrame);
		this.setTableNo(docFrame);
		this.setFileNo(docFrame);
	}
};

/**
 * 各H2タグに番号を設定します。
 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
 */
DocFramePage.prototype.setH2No = function(docFrame) {
	var n = docFrame.contents().find("h1 > span").text();
	var no = 1;
	docFrame.contents().find("h2").each(function() {
		var text = $(this).html();
		var caption = "<span>" + n + (no++) + ".</span>" + text;
		$(this).html(caption);
	});
};

/**
 * 各H3タグに番号を設定します。
 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
 */
DocFramePage.prototype.setH3No = function(docFrame) {
	var no = 1;
	var baseno = "";
	docFrame.contents().find("h3").each(function() {
		var n = $(this).prevAll("h2:first").find("span").text();
		if (baseno != n) {
			no = 1;
			baseno = n;
		}
		var text = $(this).html();
		var caption = "<span>" + n + (no++) + ".</span>" + text;
		$(this).html(caption);
	});
};


/**
 * 図番を設定します。
 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
 */
DocFramePage.prototype.setFigNo = function(docFrame) {
	var no = 1;
	docFrame.contents().find("figure > figcaption").each(function() {
		var text = $(this).html();
		var caption = "図 " + (no++) + "." + text;
		$(this).html(caption);
	});
};


/**
 * 表番を設定します。
 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
 */
DocFramePage.prototype.setTableNo = function(docFrame) {
	var no = 1;
	docFrame.contents().find("table caption").each(function() {
		var text = $(this).html();
		var caption = "表 " + (no++) + "." + text;
		$(this).html(caption);
	});
};


/**
 * ファイル番号を設定します。
 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
 */
DocFramePage.prototype.setFileNo = function(docFrame) {
	var no = 1;
	docFrame.contents().find("div.filecaption").each(function() {
		var text = $(this).html();
		var caption = "ファイル " + (no++) + "." + text;
		$(this).html(caption);
	});
};


/**
 * 図に対するイベント処理を設定します。
 * @param {jQuery} docFrame ドキュメントが読み込まれたiframe。
 */
DocFramePage.prototype.setFigEvent = function(docFrame) {
	docFrame.contents().find("img").click(function() {
		var url = docFrame.attr("src");
		var img = $(this).attr("src");
		var title = $(this).next("figcaption").text();
		$("#imageViewer").attr("src", url + "/" + img);
		var image = new Image();
		image.src = url + "/" + img;
		$("#imageDialog").dialog({
			modal: true
			,width: image.width + 34
			,height: image.height + 64
			,title: title
			,resizable: true
		});
	});
};
