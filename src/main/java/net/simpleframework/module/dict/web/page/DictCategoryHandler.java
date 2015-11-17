package net.simpleframework.module.dict.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.Dict.EDictMark;
import net.simpleframework.module.dict.DictException;
import net.simpleframework.module.dict.DictItemStat;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.IDictService;
import net.simpleframework.mvc.IPageHandler.PageSelector;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.SpanElement;
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
		return _dictService;
	}

	@Override
	protected IDataQuery<?> categoryBeans(final ComponentParameter cp, final Object categoryId) {
		return _dictService
				.queryChildren(_dictService.getBean(categoryId), DictUtils.getDomainId(cp));
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cp) {
		return DictUtils.setDomainId(cp, super.getFormParameters(cp));
	}

	@Override
	public TreeNodes getCategoryTreenodes(final ComponentParameter cp, final TreeBean treeBean,
			final TreeNode parent) {
		final String imgBase = getImgBase(cp, DictCategoryHandler.class);
		if (parent == null) {
			final TreeNode tn = createRoot(treeBean, $m("DictCategoryHandler.0"),
					$m("DictCategoryHandler.1"));
			tn.setImage(imgBase + "dict_root.png");
			tn.setContextMenu("none");
			tn.setAcceptdrop(true);
			setJsClickCallback(tn, null);
			return TreeNodes.of(tn);
		} else {
			final Object obj = parent.getDataObject();
			if (obj instanceof Dict) {
				final Dict dict = (Dict) obj;
				final EDictMark dictMark = dict.getDictMark();

				final ID domainId = DictUtils.getDomainId(cp);
				if (domainId == null) {
					final PermissionDept dept = cp.getPermission().getDept(dict.getDomainId());
					if (dept.getId() != null) {
						parent.setText("(" + SpanElement.color999(dept) + ") " + parent.getText());
					}
				}

				parent.setImage(DictUtils.getIconPath(cp, dict));
				if (dictMark != EDictMark.category) {
					final int count = getNums(cp, dict);
					if (count > 0) {
						parent.setPostfixText("(" + count + ")");
					}
					setJsClickCallback(parent, dict);
				}
			}
		}
		return super.getCategoryTreenodes(cp, treeBean, parent);
	}

	protected void setJsClickCallback(final TreeNode parent, final Dict dict) {
		String params = "dictId=";
		if (dict != null) {
			params += dict.getId();
		}
		parent.setJsClickCallback(CategoryTableLCTemplatePage.createTableRefresh(params).toString());
	}

	protected int getNums(final PageParameter pp, final Dict dict) {
		DictItemStat stat = _dictItemStatService.getDictItemStat(dict.getId(), null);
		int count = stat.getNums();
		final ID domainId = DictUtils.getDomainId(pp);
		if (domainId != null) {
			stat = _dictItemStatService.getDictItemStat(dict.getId(), domainId);
			count += stat.getNums();
		}
		return count;
	}

	@Override
	public TreeNodes getCategoryDictTreenodes(final ComponentParameter cp, final TreeBean treeBean,
			final TreeNode parent) {
		final Object dict;
		if (parent != null && (dict = parent.getDataObject()) instanceof Dict) {
			parent.setImage(DictUtils.getIconPath(cp, (Dict) dict));
		}
		return super.getCategoryTreenodes(cp, treeBean, parent);
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
		if (insert) {
			dict.setDomainId(DictUtils.getDomainId(cp));
		}
		final String dictMark = cp.getParameter("dict_mark");
		if (StringUtils.hasText(dictMark)) {
			dict.setDictMark(Convert.toEnum(EDictMark.class, dictMark));
		}
	}

	@Override
	protected void onDelete_assert(final ComponentParameter cp, final Dict dict) {
		super.onDelete_assert(cp, dict);
		if (dict.getDictMark() == EDictMark.builtIn) {
			throw DictException.of($m("DictCategoryHandler.4"));
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
						.setDefaultEnumValue(EDictMark.normal, EDictMark.category)));
		return editor;
	}
}
