package net.simpleframework.module.dict.web.page;

import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.DictItem;
import net.simpleframework.module.dict.EDictMark;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.IDictItemService;
import net.simpleframework.module.dict.web.page.t1.DictMgrPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ext.category.CategoryBean;
import net.simpleframework.mvc.component.ext.category.ctx.CategoryBeanAwareHandler;
import net.simpleframework.mvc.component.ui.tree.TreeBean;
import net.simpleframework.mvc.component.ui.tree.TreeNode;
import net.simpleframework.mvc.component.ui.tree.TreeNodes;
import net.simpleframework.mvc.template.t1.ext.OneCategoryTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictItemCategoryPage extends OneCategoryTemplatePage implements IDictContextAware {

	private static Dict getDict(final PageRequestResponse rRequest) {
		Dict dict = (Dict) rRequest.getRequestAttr("dictId");
		if (dict == null) {
			dict = context.getDictService().getBean(rRequest.getParameter("dictId"));
		}
		return dict;
	}

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		final CategoryBean category = addCategoryBean(pp, "DictItemCategoryPage_items",
				DictItemCategory.class).setCookies(false);
		if (context.getDictItemService().queryItems(getDict(pp)).getCount() > 100) {
			category.setDynamicTree(true);
		}
	}

	public static class DictItemCategory extends CategoryBeanAwareHandler<DictItem> {

		@Override
		protected IDictItemService getBeanService() {
			return context.getDictItemService();
		}

		@Override
		public Map<String, Object> getFormParameters(final ComponentParameter cp) {
			final KVMap parameters = (KVMap) super.getFormParameters(cp);
			final Dict dict = context.getDictService().getBean(cp.getParameter("dictId"));
			if (dict != null) {
				parameters.add("dictId", dict.getId());
				cp.setRequestAttr("dictId", dict);
			}
			return parameters;
		}

		@Override
		protected IDataQuery<?> categoryBeans(final ComponentParameter cp, final Object categoryId) {
			final Dict dict = getDict(cp);
			if (dict == null) {
				return DataQueryUtils.nullQuery();
			}
			final IDictItemService service = getBeanService();
			final DictItem parent = service.getBean(categoryId);
			return parent == null ? service.queryRoot(dict) : service.queryChildren(parent);
		}

		@Override
		protected String[] getContextMenuKeys() {
			return new String[] { "Refresh", "-", "Move" };
		}

		@Override
		public TreeNodes getCategoryTreenodes(final ComponentParameter cp, final TreeBean treeBean,
				final TreeNode parent) {
			if (parent == null) {
				final Dict dict = getDict(cp);
				if (dict != null) {
					final TreeNodes nodes = TreeNodes.of();
					final TreeNode node = new TreeNode(treeBean, parent, null);
					node.setText(dict.getText());
					node.setAcceptdrop(true);
					node.setOpened(true);
					final String imgBase = getImgBase(cp, DictMgrPage.class);
					final EDictMark dictMark = dict.getDictMark();
					node.setImage(imgBase
							+ (dictMark == EDictMark.builtIn ? "dict_lock.png" : "dict.png"));
					nodes.add(node);
					return nodes;
				}
			} else {
				if (parent.getDataObject() instanceof DictItem) {
					final String imgBase = getImgBase(cp, DictMgrPage.class);
					parent.setImage(imgBase + "dict-item.png");
				}
			}
			return super.getCategoryTreenodes(cp, treeBean, parent);
		}
	}
}
