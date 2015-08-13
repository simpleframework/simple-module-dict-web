package net.simpleframework.module.dict.web.page.mgr2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.common.web.page.AbstractMgrTPage;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.DictItem;
import net.simpleframework.module.dict.EDictMark;
import net.simpleframework.module.dict.IDictContext;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.web.DictLogRef;
import net.simpleframework.module.dict.web.IDictWebContext;
import net.simpleframework.module.dict.web.page.DictCategoryHandler;
import net.simpleframework.module.dict.web.page.DictItemCategoryPage;
import net.simpleframework.module.dict.web.page.DictItemEditPage;
import net.simpleframework.module.dict.web.page.DictItemList;
import net.simpleframework.module.dict.web.page.DictUtils;
import net.simpleframework.module.dict.web.page.t1.DictMgrPage._NavigationTitleCallback;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.Icon;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.TablePagerUtils;
import net.simpleframework.mvc.component.ui.pager.db.NavigationTitle;
import net.simpleframework.mvc.component.ui.tree.AbstractTreeHandler;
import net.simpleframework.mvc.component.ui.tree.TreeBean;
import net.simpleframework.mvc.component.ui.tree.TreeNode;
import net.simpleframework.mvc.component.ui.tree.TreeNodes;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictMgrTPage extends AbstractMgrTPage implements IDictContextAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(DictCategoryHandler.class, "/dict_mgr.css");

		// 导航树
		addComponentBean(pp, "DictMgrTPage_tree", TreeBean.class).setContainerId(
				"idDictMgrTPage_category").setHandlerClass(_DictCategoryHandler.class);
		// 表格
		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp, "DictMgrTPage_tbl",
				TablePagerBean.class).setShowLineNo(true).setPageItems(30)
				.setPagerBarLayout(EPagerBarLayout.top).setContainerId("idDictMgrTPage_tbl")
				.setHandlerClass(_DictItemList.class);
		tablePager
				.addColumn(new TablePagerColumn("text", $m("DictMgrPage.1")))
				.addColumn(new TablePagerColumn("codeNo", $m("DictMgrPage.2")))
				.addColumn(
						new TablePagerColumn("parentId", $m("DictMgrPage.8"), 100).setFilterSort(false))
				.addColumn(
						new TablePagerColumn("itemMark", $m("DictMgrPage.3"), 70).setFilterSort(false))
				.addColumn(TablePagerColumn.OPE(70));

		// 字典条目
		AjaxRequestBean ajaxRequest = addAjaxRequest(pp, "DictMgrPage_itemPage",
				_DictItemEditPage.class);
		addWindowBean(pp, "DictMgrPage_itemWin", ajaxRequest).setTitle($m("DictMgrPage.4"))
				.setHeight(340).setWidth(520);

		// 删除
		addDeleteAjaxRequest(pp, "DictMgrPage_delete");
		// 移动
		addAjaxRequest(pp, "DictMgrPage_move").setHandlerMethod("doMove");

		// 树视图
		ajaxRequest = addAjaxRequest(pp, "DictMgrTPage_categoryPage", _DictItemCategoryPage.class);
		addWindowBean(pp, "DictMgrTPage_categoryWin", ajaxRequest).setTitle($m("DictMgrPage.7"))
				.setHeight(450).setWidth(380);

		// 修改日志
		final IModuleRef ref = ((IDictWebContext) dictContext).getLogRef();
		if (ref != null) {
			((DictLogRef) ref).addLogComponent(pp);
		}
	}

	@Transaction(context = IDictContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("id"));
		_dictItemService.delete(ids);
		return _jsTableRefresh(null);
	}

	@Transaction(context = IDictContext.class)
	public IForward doMove(final ComponentParameter cp) {
		final DictItem item = _dictItemService.getBean(cp
				.getParameter(TablePagerUtils.PARAM_MOVE_ROWID));
		final DictItem item2 = _dictItemService.getBean(cp
				.getParameter(TablePagerUtils.PARAM_MOVE_ROWID2));
		if (item != null && item2 != null) {
			_dictItemService.exchange(item, item2,
					Convert.toBool(cp.getParameter(TablePagerUtils.PARAM_MOVE_UP)));
		}
		return _jsTableRefresh(null);
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='DictMgrTPage'>");
		sb.append("	<table width='100%'><tr>");
		sb.append("  <td valign='top' class='ltree'><div id='idDictMgrTPage_category'></div></td>");
		sb.append("  <td valign='top' class='rtbl'><div id='idDictMgrTPage_tbl'></div></td>");
		sb.append(" </tr></table>");
		sb.append("</div>");
		return sb.toString();
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		return ElementList
				.of(LinkButton.of($m("DictMgrPage.5")).setOnclick(
						"$Actions['DictMgrPage_itemWin']('dictId=' + $F('dictId'));"))
				.append(SpanElement.SPACE)
				.append(
						LinkButton.deleteBtn().setOnclick(
								"$Actions['DictMgrTPage_tbl'].doAct('DictMgrPage_delete');"))
				.append(SpanElement.SPACE)
				.append(
						new LinkButton($m("DictMgrPage.7")).setIconClass(Icon.folder_open).setOnclick(
								"$Actions['DictMgrTPage_categoryWin']('dictId=' + $F('dictId'));"));
	}

	public static class _DictCategoryHandler extends AbstractTreeHandler {
		@Override
		public TreeNodes getTreenodes(final ComponentParameter cp, final TreeNode parent) {
			final TreeNodes nodes = TreeNodes.of();
			final IDataQuery<Dict> dq = _dictService.queryChildren(parent == null ? null
					: (Dict) parent.getDataObject());
			final TreeBean treeBean = (TreeBean) cp.componentBean;
			Dict dict;
			while ((dict = dq.next()) != null) {
				final TreeNode treeNode = new TreeNode(treeBean, parent, dict);
				treeNode.setImage(DictUtils.getIconPath(cp, dict));
				if (dict.getDictMark() != EDictMark.category) {
					treeNode.setJsClickCallback("$Actions['DictMgrTPage_tbl']('dictId=" + dict.getId()
							+ "')");
				}
				nodes.add(treeNode);
			}
			return nodes;
		}
	}

	public static class _DictItemList extends DictItemList {

		@Override
		public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
			if ("title".equals(beanProperty)) {
				return NavigationTitle.toElement(cp, DictUtils.getDict(cp),
						new _NavigationTitleCallback() {
							@Override
							protected String getComponentTable() {
								return "DictMgrTPage_tbl";
							}
						}).toString();
			}
			return super.getBeanProperty(cp, beanProperty);
		}

		@Override
		protected ID getOrgId(final PageParameter pp) {
			return getPermissionOrg(pp).getId();
		}
	}

	public static class _DictItemEditPage extends DictItemEditPage {

		@Override
		protected ID getOrgId(final PageParameter pp) {
			return getPermissionOrg(pp).getId();
		}

		@Override
		protected JavascriptForward jsTableRefresh(final Dict dict) {
			return _jsTableRefresh(dict);
		}
	}

	public static class _DictItemCategoryPage extends DictItemCategoryPage {

		@Override
		protected ID getOrgId(final PageParameter pp) {
			return getPermissionOrg(pp).getId();
		}
	}

	static JavascriptForward _jsTableRefresh(final Dict dict) {
		final JavascriptForward js = new JavascriptForward("$Actions['DictMgrTPage_tbl'](");
		if (dict != null) {
			js.append("'dictId=").append(dict.getId()).append("'");
		}
		js.append(");");
		return js;
	}
}
