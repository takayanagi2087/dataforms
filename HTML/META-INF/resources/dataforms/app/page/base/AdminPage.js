/**
 * @fileOverview {@link AdminPage}クラスを記述したファイルです。
 */

/**
 * @class AdminPage
 * 監理者向けページクラス。
 * <pre>
 * 管理者権限を持っていないと表示できません。
 * </pre>
 * @extends BasePage
 */
AdminPage = createSubclass("AdminPage", {}, "BasePage");
