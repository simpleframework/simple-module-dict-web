package net.simpleframework.module.dict.web.page.t1;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.dict.DictItem;
import net.simpleframework.module.dict.EDictItemMark;
import net.simpleframework.module.dict.IDictContext;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.IDictItemService;
import net.simpleframework.module.dict.web.DictLogRef;
import net.simpleframework.module.dict.web.IDictWebContext;
import net.simpleframework.module.dict.web.page.DictCategoryHandler;
import net.simpleframework.module.dict.web.page.DictItemCategoryPage;
import net.simpleframework.module.dict.web.page.DictItemEditPage;
import net.simpleframework.module.dict.web.page.DictItemList;
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
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.TablePagerUtils;
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
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(DictMgrPage.class, "/dict_mgr.css");

		addCategoryBean(pp, DictCategoryHandler.class);

		// 字典条目
		final TablePagerBean tablePager = addTablePagerBean(pp, DictItemList.class);
		tablePager
				.addColumn(new TablePagerColumn("text", $m("DictMgrPage.1")))
				.addColumn(new TablePagerColumn("codeNo", $m("DictMgrPage.2")))
				.addColumn(new TablePagerColumn("parentId", $m("DictMgrPage.8"), 150).setFilter(false))
				.addColumn(
						new TablePagerColumn("itemMark", $m("DictMgrPage.3"), 100)
								.setPropertyClass(EDictItemMark.class))
				.addColumn(TablePagerColumn.OPE().setWidth(130))
				.setJsLoadedCallback("$Actions['DictMgrPage_Tip']();");

		// 字典条目
		AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "DictMgrPage_itemPage",
				DictItemEditPage.class);
		addWindowBean(pp, "DictMgrPage_itemWin", ajaxRequest).setTitle($m("DictMgrPage.4"))
				.setHeight(300).setWidth(500);

		// 树视图
		ajaxRequest = addAjaxRequest(pp, "DictMgrPage_categoryPage", DictItemCategoryPage.class);
		addWindowBean(pp, "DictMgrPage_categoryWin", ajaxRequest).setTitle($m("DictMgrPage.7"))
				.setHeight(450).setWidth(380);

		// 移动
		addAjaxRequest(pp, "DictMgrPage_Move").setHandlerMethod("doMove");

		// 删除
		addDeleteAjaxRequest(pp, "DictMgrPage_Delete");

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
	public String getRole(final PageParameter pp) {
		return dictContext.getModule().getManagerRole();
	}

	@Transaction(context = IDictContext.class)
	public IForward doMove(final ComponentParameter cp) {
		final IDictItemService service = dictContext.getDictItemService();
		final DictItem item = service.getBean(cp.getParameter(TablePagerUtils.PARAM_MOVE_ROWID));
		final DictItem item2 = service.getBean(cp.getParameter(TablePagerUtils.PARAM_MOVE_ROWID2));
		if (item != null && item2 != null) {
			service.exchange(item, item2,
					Convert.toBool(cp.getParameter(TablePagerUtils.PARAM_MOVE_UP)));
		}
		return createTableRefresh();
	}

	@Transaction(context = IDictContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("id"));
		dictContext.getDictItemService().delete(ids);
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
	public ElementList getLeftElements(final PageParameter pp) {
		return ElementList
				.of(new LinkButton($m("DictMgrPage.5"))
						.setOnclick("$Actions['DictMgrPage_itemWin']('dictId=' + $F('dictId'));"))
				.append(delete_btn("DictMgrPage_Delete").setText($m("DictMgrPage.6")))
				.append(SpanElement.SPACE)
				.append(
						new LinkButton($m("DictMgrPage.7")).setIconClass(Icon.folder_open).setOnclick(
								"$Actions['DictMgrPage_categoryWin']('dictId=' + $F('dictId'));"));
	}
}
