package net.simpleframework.module.dict.web.component.dictSelect;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.DictItem;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.tree.TreeBean;
import net.simpleframework.mvc.component.ui.tree.TreeNode;
import net.simpleframework.mvc.component.ui.tree.TreeNodes;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictTreeSelectHandler extends AbstractDictSelectHandler implements
		IDictTreeSelectHandler {

	protected TreeNode createItem(final TreeBean treeBean, final TreeNode parent,
			final DictItem dictItem) {
		final TreeNode node = new TreeNode(treeBean, parent, dictItem);
		node.setId(dictItem.getCodeNo());
		return node;
	}

	protected ID getOrgId(final PageParameter pp) {
		return pp.getLdept().getDomainId();
	}

	@Override
	public TreeNodes getDictItems(final ComponentParameter cp, final TreeBean treeBean,
			final TreeNode parent) {
		final Dict dict = getDict(cp);
		if (dict == null) {
			return null;
		}

		final TreeNodes nodes = TreeNodes.of();
		final IDataQuery<DictItem> dq;
		if (parent == null) {
			dq = _dictItemService.queryRoot(dict, getOrgId(cp));
		} else {
			dq = _dictItemService.queryChildren((DictItem) parent.getDataObject());
		}
		DictItem dictItem;
		while ((dictItem = dq.next()) != null) {
			final TreeNode node = createItem(treeBean, parent, dictItem);
			if (node != null) {
				nodes.add(node);
			}
		}
		return nodes;
	}

	@Override
	public Map<String, Object> getTreenodeAttributes(final ComponentParameter cp,
			final TreeNode treeNode) {
		return new KVMap();
	}
}
