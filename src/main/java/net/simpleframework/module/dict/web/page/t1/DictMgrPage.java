package net.simpleframework.module.dict.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.Dict.EDictMark;
import net.simpleframework.module.dict.IDictContext;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.web.DictLogRef;
import net.simpleframework.module.dict.web.IDictWebContext;
import net.simpleframework.module.dict.web.page.DictCategoryHandler;
import net.simpleframework.module.dict.web.page.DictItemCategoryPage;
import net.simpleframework.module.dict.web.page.DictItemEditPage;
import net.simpleframework.module.dict.web.page.DictItemTbl;
import net.simpleframework.module.dict.web.page.DictUtils;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.EElementEvent;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Icon;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ext.deptselect.DeptSelectBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.TablePagerUtils;
import net.simpleframework.mvc.component.ui.pager.db.NavigationTitle;
import net.simpleframework.mvc.component.ui.pager.db.NavigationTitle.NavigationTitleCallback;
import net.simpleframework.mvc.component.ui.tooltip.ETipElement;
import net.simpleframework.mvc.component.ui.tooltip.ETipPosition;
import net.simpleframework.mvc.component.ui.tooltip.ETipStyle;
import net.simpleframework.mvc.component.ui.tooltip.TipBean;
import net.simpleframework.mvc.component.ui.tooltip.TipBean.HideOn;
import net.simpleframework.mvc.component.ui.tooltip.TipBean.Hook;
import net.simpleframework.mvc.component.ui.tooltip.TooltipBean;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.mvc.template.t1.ext.CategoryTableLCTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/dict/mgr")
public class DictMgrPage extends CategoryTableLCTemplatePage implements IDictContextAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(DictCategoryHandler.class, "/dict_mgr.css");

		addCategoryBean(pp, DictCategoryHandler.class);

		// 部门选取
		addComponentBean(pp, "DictMgrPage_deptSelect", DeptSelectBean.class).setOrg(true)
				.setMultiple(false).setClearAction("false")
				.setJsSelectCallback("$Actions['" + COMPONENT_TABLE
						+ "']('filter_cur_col=domainId&filter=%3D;' + selects[0].id);return true;");

		// 字典条目
		final TablePagerBean tablePager = addTablePagerBean(pp, DictItemTbl.class);
		tablePager.addColumn(TablePagerColumn.ICON())
				.addColumn(new TablePagerColumn("text", $m("DictMgrPage.1")))
				.addColumn(new TablePagerColumn("codeNo", $m("DictMgrPage.2")))
				.addColumn(new TablePagerColumn("domainId", $m("DictMgrPage.9"), 200) {
					private static final long serialVersionUID = -5540421628661585177L;

					@Override
					public String getFilterVal(final String val) {
						if (val == null) {
							return null;
						}
						final PermissionDept dept = pp.getPermission().getDept(ID.of(val));
						return dept.exists() ? dept.getText() : val;
					}
				}.setFilterAdvClick("$Actions['DictMgrPage_deptSelect']();"))
				.addColumn(
						new TablePagerColumn("parentId", $m("DictMgrPage.8"), 100).setFilterSort(false))
				.addColumn(TablePagerColumn.OPE(70))
				.setJsLoadedCallback("$Actions['DictMgrPage_Tip']();");

		// 字典条目
		AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "DictMgrPage_itemPage",
				DictItemEditPage.class);
		addWindowBean(pp, "DictMgrPage_itemWin", ajaxRequest).setTitle($m("DictMgrPage.4"))
				.setHeight(360).setWidth(540);

		// 树视图
		ajaxRequest = addAjaxRequest(pp, "DictMgrPage_categoryPage", DictItemCategoryPage.class);
		addWindowBean(pp, "DictMgrPage_categoryWin", ajaxRequest).setTitle($m("DictMgrPage.7"))
				.setHeight(450).setWidth(380);

		// 移动
		addAjaxRequest(pp, "DictMgrPage_move").setHandlerMethod("doMove");

		// 删除
		addDeleteAjaxRequest(pp, "DictMgrPage_delete");

		// tooltip
		final TooltipBean tooltip = (TooltipBean) addComponentBean(pp, "DictMgrPage_Tip",
				TooltipBean.class).setRunImmediately(false);
		tooltip.addTip(new TipBean(tooltip).setSelector("#" + tablePager.getContainerId() + " a")
				.setDelay(0.5).setTipStyle(ETipStyle.tipDarkgrey).setStem(ETipPosition.rightTop)
				.setHook(new Hook(ETipPosition.leftTop, ETipPosition.rightTop))
				.setHideOn(new HideOn(ETipElement.tip, EElementEvent.mouseleave)).setWidth(320));

		// 修改日志
		final IModuleRef ref = ((IDictWebContext) dictContext).getLogRef();
		if (ref != null) {
			((DictLogRef) ref).addLogComponent(pp);
		}
	}

	@Override
	public String getPageRole(final PageParameter pp) {
		return getPageManagerRole(pp);
	}

	@Transaction(context = IDictContext.class)
	public IForward doMove(final ComponentParameter cp) {
		_dictItemService.exchange(TablePagerUtils.getExchangeBeans(cp, _dictItemService));
		return createTableRefresh();
	}

	@Transaction(context = IDictContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("id"));
		_dictItemService.delete(ids);
		return createTableRefresh();
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(new SpanElement($m("DictMgrPage.0")));
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return TabButtons.of(new TabButton($m("DictMgrPage.0"), url(DictMgrPage.class)));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList
				.of(new LinkButton($m("DictMgrPage.5"))
						.setOnclick("$Actions['DictMgrPage_itemWin']('dictId=' + $F('dictId'));"))
				.append(SpanElement.SPACE)
				.append(delete_btn("DictMgrPage_delete").setText($m("DictMgrPage.6")))
				.append(SpanElement.SPACE)
				.append(new LinkButton($m("DictMgrPage.7")).setIconClass(Icon.folder_open)
						.setOnclick("$Actions['DictMgrPage_categoryWin']('dictId=' + $F('dictId'));"));
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList.of(
				NavigationTitle.toElement(pp, DictUtils.getDict(pp), new _NavigationTitleCallback()));
	}

	public static class _NavigationTitleCallback extends NavigationTitleCallback<Dict> {
		public _NavigationTitleCallback() {
			super($m("DictItemTbl.0"), COMPONENT_TABLE);
		}

		@Override
		protected Dict get(final Object id) {
			return _dictService.getBean(id);
		}

		@Override
		protected String getCategoryIdKey() {
			return "dictId";
		}

		@Override
		protected boolean isLink(final Dict t) {
			return t.getDictMark() != EDictMark.category;
		}

		@Override
		protected String getText(final Dict t) {
			return !isLink(t) ? super.getText(t)
					: t.getText() + SpanElement.shortText("(" + t.getName() + ")");
		}
	}
}
