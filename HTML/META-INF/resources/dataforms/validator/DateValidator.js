/**
 * @fileOverview {@link DateValidator}クラスを記述したファイルです。
 */

/**
 * @class DateValidator
 * 日付バリデータクラス。
 * <pre>
 * </pre>
 * @extends DateTimeValidator
 */
DateValidator = createSubclass("DateValidator", {dateFormatKey: "format.datefield"}, "DateTimeValidator");
