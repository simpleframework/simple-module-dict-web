package net.simpleframework.module.dict.web.component.dictSelect;

import java.util.Map;

import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.dictionary.IDictionaryHandle;
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
public interface IDictTreeSelectHandler extends IDictionaryHandle {

	/**
	 * 获取字典的条目列表
	 * 
	 * @param cParameter
	 * @param treeBean
	 * @param parent
	 * @return
	 */
	TreeNodes getDictItems(ComponentParameter cp, TreeBean treeBean, TreeNode parent);

	/**
	 * 获取树节点的附加属性
	 * 
	 * @param cp
	 * @param treeNode
	 * @return
	 */
	Map<String, Object> getTreenodeAttributes(ComponentParameter cp, TreeNode treeNode);
}
