/**
 * @fileOverview  {@link createSubclass}メソッドを記述したファイルです。
 */


/**
 *
 * サブクラス作成メソッド。
 * <pre>
 * <code>
 * SubClass = createSubclass("SubClass", {"id":"subClass", "p1":"v1","p2":2}, "SuperClass");
 * </code>
 * "SubClass = "の記述は無くてもSubClassというクラスは作成されますが、jsDocに対して
 * クラスを認識させるため、上記のように記述しています。
 * 上記呼び出しで、以下のコードを生成して実行します。
 * <code>
 * SubClass = function () {
 * 	SuperClass.call(this);
 * 	this.id = "subClass";
 * 	this.p1 = "v1";
 * 	this.p2 = 2;
 * };
 * SubClass.prototype = Object.create(SuperClass.prototype);
 * SubClass.prototype.constructor = SubClass;
 * </code>
 * </pre>
 *
 * @param {String} subClassName サブクラスの名前を文字列で指定します。
 * @param {Object} properties コンストラクタの中で初期化するプロパティを指定します。
 * @param {String} superClass スーパークラスの名前を文字列で指定します。
 * @returns {Function} 作成したサブクラスを返します。
 */
function createSubclass(subClassName, properties, superClass) {
	var script = subClassName + " = function() {\n";
	script += "\t" + superClass + ".call(this);\n";
	for (var key in properties) {
		script += "\t" + "this." + key + " = " + JSON.stringify(properties[key]) + ";\n";
	}
	script += "};\n";

	script += subClassName + ".prototype = Object.create(" + superClass + ".prototype);\n";
	script += subClassName + ".prototype.constructor = " + subClassName + ";\n";
	return eval(script);
};

// IE8に対応するためObject.createを実装する。
if (!Object.create) {
  Object.create = function (o) {
    if (arguments.length > 1) {
      throw new Error('Object.create implementation only accepts the first parameter.');
    }
    function F() {}
    F.prototype = o;
    return new F();
  };
}

