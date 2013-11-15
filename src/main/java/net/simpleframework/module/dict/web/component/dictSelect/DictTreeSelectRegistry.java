package net.simpleframework.module.dict.web.component.dictSelect;

import java.util.Map;

import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRender;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.ui.dictionary.DictionaryBean;
import net.simpleframework.mvc.component.ui.dictionary.DictionaryRegistry;
import net.simpleframework.mvc.component.ui.dictionary.DictionaryTreeHandler;
import net.simpleframework.mvc.component.ui.tree.TreeBean;
import net.simpleframework.mvc.component.ui.tree.TreeNode;
import net.simpleframework.mvc.component.ui.tree.TreeNodes;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@ComponentBean(DictTreeSelectBean.class)
@ComponentName(DictTreeSelectRegistry.DICTTREESELECT)
@ComponentRender(DictSelectRender.class)
public class DictTreeSelectRegistry extends DictionaryRegistry {
	public static final String DICTTREESELECT = "dictTreeSelect";

	@Override
	public DictionaryBean createComponentBean(final PageParameter pp, final Object attriData) {
		final DictTreeSelectBean dictSelect = (DictTreeSelectBean) super.createComponentBean(pp,
				attriData);

		final ComponentParameter nCP = ComponentParameter.get(pp, dictSelect);

		final String dictSelectName = nCP.getComponentName();
		final TreeBean treeBean = (TreeBean) pp.addComponentBean(dictSelectName + "_tree",
				TreeBean.class).setHandleClass(DictTree.class);
		dictSelect.addTreeRef(pp, treeBean.getName());
		treeBean.setAttr("$dictSelect", dictSelect);

		return dictSelect;
	}

	public static class DictTree extends DictionaryTreeHandler {

		@Override
		public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
			if ("dynamicLoading".equals(beanProperty)) {
				final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$dictSelect");
				return nCP.getBeanProperty("dynamicTree");
			} else if ("cookies".equals(beanProperty)) {
				final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$dictSelect");
				return nCP.getBeanProperty("cookies");
			}
			return super.getBeanProperty(cp, beanProperty);
		}

		@Override
		public Map<String, Object> getFormParameters(final ComponentParameter cp) {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$dictSelect");
			return ComponentUtils.toFormParameters(nCP);
		}

		@Override
		public TreeNodes getTreenodes(final ComponentParameter cp, final TreeNode parent) {
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$dictSelect");
			return ((IDictTreeSelectHandler) nCP.getComponentHandler()).getDictItems(nCP,
					(TreeBean) cp.componentBean, parent);
		}

		@Override
		public Map<String, Object> getTreenodeAttributes(final ComponentParameter cp,
				final TreeNode treeNode, final TreeNodes children) {
			final KVMap kv = (KVMap) super.getTreenodeAttributes(cp, treeNode, children);
			final ComponentParameter nCP = ComponentParameter.getByAttri(cp, "$dictSelect");
			kv.putAll(((IDictTreeSelectHandler) nCP.getComponentHandler()).getTreenodeAttributes(nCP,
					treeNode));
			return kv;
		}
	}
}
