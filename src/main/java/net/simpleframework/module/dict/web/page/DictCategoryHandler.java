package net.simpleframework.module.dict.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.ModuleContextException;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.EDictMark;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.IDictService;
import net.simpleframework.module.dict.web.page.t1.DictMgrPage;
import net.simpleframework.mvc.IPageHandler.PageSelector;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ext.category.ctx.CategoryBeanAwareHandler;
import net.simpleframework.mvc.component.ui.propeditor.InputComp;
import net.simpleframework.mvc.component.ui.propeditor.PropEditorBean;
import net.simpleframework.mvc.component.ui.propeditor.PropField;
import net.simpleframework.mvc.component.ui.tree.TreeBean;
import net.simpleframework.mvc.component.ui.tree.TreeNode;
import net.simpleframework.mvc.component.ui.tree.TreeNodes;
import net.simpleframework.mvc.template.t1.ext.CategoryTableLCTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictCategoryHandler extends CategoryBeanAwareHandler<Dict> implements
		IDictContextAware {

	@Override
	protected IDictService getBeanService() {
		return dictContext.getDictService();
	}

	@Override
	public TreeNodes getCategoryTreenodes(final ComponentParameter cp, final TreeBean treeBean,
			final TreeNode parent) {
		final String imgBase = getImgBase(cp, DictMgrPage.class);
		if (parent == null) {
			final TreeNode tn = createRoot(treeBean, $m("DictCategoryHandler.0"),
					$m("DictCategoryHandler.1"));
			tn.setImage(imgBase + "dict_root.png");
			tn.setContextMenu("none");
			tn.setAcceptdrop(true);
			tn.setJsClickCallback(CategoryTableLCTemplatePage.createTableRefresh("dictId=").toString());
			return TreeNodes.of(tn);
		} else {
			final Object obj = parent.getDataObject();
			if (obj instanceof Dict) {
				final Dict dict = (Dict) obj;
				final EDictMark dictMark = dict.getDictMark();
				parent.setImage(dictIcon(cp, dict));
				if (dictMark != EDictMark.category) {
					final int count = dictContext.getDictItemService().queryCount(dict);
					if (count > 0) {
						parent.setPostfixText("(" + count + ")");
					}
					parent.setJsClickCallback(CategoryTableLCTemplatePage.createTableRefresh(
							"dictId=" + dict.getId()).toString());
				}
			}
		}
		return super.getCategoryTreenodes(cp, treeBean, parent);
	}

	@Override
	public TreeNodes getCategoryDictTreenodes(final ComponentParameter cp, final TreeBean treeBean,
			final TreeNode parent) {
		final Object dict;
		if (parent != null && (dict = parent.getDataObject()) instanceof Dict) {
			parent.setImage(dictIcon(cp, (Dict) dict));
		}
		return super.getCategoryTreenodes(cp, treeBean, parent);
	}

	private String dictIcon(final ComponentParameter cp, final Dict dict) {
		final String imgBase = getImgBase(cp, DictMgrPage.class);
		final EDictMark dictMark = dict.getDictMark();
		if (dictMark == EDictMark.category) {
			return imgBase + "dict_c.png";
		} else if (dictMark == EDictMark.normal) {
			return imgBase + "dict.png";
		} else if (dictMark == EDictMark.builtIn) {
			return imgBase + "dict_lock.png";
		}
		return null;
	}

	@Override
	protected void onLoaded_dataBinding(final ComponentParameter cp,
			final Map<String, Object> dataBinding, final PageSelector selector, final Dict dict) {
		if (dict != null) {
			dataBinding.put("dict_mark", dict.getDictMark());
			// 该字段不能编辑
			selector.disabledSelector = "#dict_mark";
		}
	}

	@Override
	protected void onSave_setProperties(final ComponentParameter cp, final Dict dict,
			final boolean insert) {
		final String dictMark = cp.getParameter("dict_mark");
		if (StringUtils.hasText(dictMark)) {
			dict.setDictMark(Convert.toEnum(EDictMark.class, dictMark));
		}
	}

	@Override
	protected void onDelete_assert(final ComponentParameter cp, final Dict dict) {
		super.onDelete_assert(cp, dict);
		if (dict.getDictMark() == EDictMark.builtIn) {
			throw ModuleContextException.of($m("DictCategoryHandler.4"));
		}
	}

	@Override
	protected String[] getContextMenuKeys() {
		return new String[] { "Add", "Edit", "Delete", "-", "Refresh", "-", "Move" };
	}

	@Override
	public KVMap categoryEdit_attri(final ComponentParameter cp) {
		return ((KVMap) super.categoryEdit_attri(cp)).add(window_title, $m("DictCategoryHandler.3"))
				.add(window_height, 320);
	}

	@Override
	protected AbstractComponentBean categoryEdit_createPropEditor(final ComponentParameter cp) {
		final PropEditorBean editor = (PropEditorBean) super.categoryEdit_createPropEditor(cp);
		editor.getFormFields().add(
				2,
				new PropField($m("DictCategoryHandler.2")).addComponents(InputComp.select("dict_mark")
						.setDefaultValue(EDictMark.normal, EDictMark.category)));
		return editor;
	}
}
