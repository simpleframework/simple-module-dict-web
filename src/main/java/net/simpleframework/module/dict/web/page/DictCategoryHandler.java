package net.simpleframework.module.dict.web.page;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.Dict.EDictMark;
import net.simpleframework.module.dict.DictItemStat;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.IDictService;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.EElementEvent;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ext.category.ctx.CategoryBeanAwareHandler;
import net.simpleframework.mvc.component.ext.deptselect.DeptSelectBean;
import net.simpleframework.mvc.component.ui.propeditor.InputComp;
import net.simpleframework.mvc.component.ui.propeditor.PropEditorBean;
import net.simpleframework.mvc.component.ui.propeditor.PropField;
import net.simpleframework.mvc.component.ui.propeditor.PropFields;
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
		final ID domainId = DictUtils.getDomainId(pp);
		if (domainId != null) {
			DictItemStat stat = _dictItemStatService.getDictItemStat(dict.getId(), null);
			int count = stat.getNums();
			stat = _dictItemStatService.getDictItemStat(dict.getId(), domainId);
			count += stat.getNums();
			return count;
		} else {
			return _dictItemStatService.getAllNums(dict.getId());
		}
	}

	@Override
	public TreeNodes getCategoryDictTreenodes(final ComponentParameter cp, final TreeBean treeBean,
			final TreeNode parent) {
		final Object o;
		if (parent != null && (o = parent.getDataObject()) instanceof Dict) {
			parent.setImage(DictUtils.getIconPath(cp, (Dict) o));
		}
		cp.setRequestAttr("_dict", Boolean.TRUE);
		return super.getCategoryTreenodes(cp, treeBean, parent);
	}

	@Override
	protected TreeNode createTreeNode(final ComponentParameter cp, final TreeBean treeBean,
			final TreeNode parent, final Object bean) {
		final Boolean b = (Boolean) cp.getRequestAttr("_dict");
		if (b != null && b.booleanValue()) {
			if (((Dict) bean).getDictMark() != EDictMark.category) {
				return null;
			}
		}
		return super.createTreeNode(cp, treeBean, parent, bean);
	}

	@Override
	protected Dict getParent_onLoaded(final ComponentParameter cp) {
		final IDictService mgr = getBeanService();
		Dict parent = mgr.getBean(cp.getParameter(PARAM_CATEGORY_PARENTID));
		while (parent != null) {
			if (parent.getDictMark() == EDictMark.category) {
				return parent;
			} else {
				parent = mgr.getBean(parent.getParentId());
			}
		}
		return parent;
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
		if (cp.isLmanager()) {
			final String domain_id = cp.getParameter("domain_id");
			dict.setDomainId(StringUtils.hasText(domain_id) ? ID.of(domain_id) : null);
		}
	}

	@Override
	protected String[] getContextMenuKeys() {
		return new String[] { "Add", "Edit", "Delete", "-", "Refresh", "-", "Move" };
	}

	@Override
	public KVMap categoryEdit_attri(final ComponentParameter cp) {
		return ((KVMap) super.categoryEdit_attri(cp)).add(window_title, $m("DictCategoryHandler.3"))
				.add(window_height, 360);
	}

	@Override
	protected AbstractComponentBean categoryEdit_createPropEditor(final ComponentParameter cp) {
		final Dict dict = _dictService.getBean(cp.getParameter(PARAM_CATEGORY_ID));
		final PropEditorBean editor = (PropEditorBean) super.categoryEdit_createPropEditor(cp);
		final PropFields fields = editor.getFormFields();
		if (cp.isLmanager()) {
			cp.addComponentBean("DictCategoryHandler_deptSelect", DeptSelectBean.class).setOrg(true)
					.setBindingId("domain_id").setBindingText("domain_text");

			final InputComp domain_id = InputComp.hidden("domain_id");
			final InputComp domain_text = InputComp.textButton("domain_text")
					.setAttributes("readonly")
					.addEvent(EElementEvent.click, "$Actions['DictCategoryHandler_deptSelect']();");
			PermissionDept org = null;
			if (dict != null) {
				org = cp.getPermission().getDept(dict.getDomainId());
			} else {
				org = AbstractMVCPage.getPermissionOrg(cp);
			}
			if (org != null) {
				domain_id.setDefaultValue(org.getId());
				domain_text.setDefaultValue(org.getText());
			}
			fields.add(2,
					new PropField($m("DictCategoryHandler.4")).addComponents(domain_id, domain_text));
		}

		if (dict == null) {
			fields.add(
					2,
					new PropField($m("DictCategoryHandler.2")).addComponents(InputComp.select(
							"dict_mark").setDefaultEnumValue(EDictMark.normal, EDictMark.category)));
		}
		return editor;
	}
}
