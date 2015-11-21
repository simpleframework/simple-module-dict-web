package net.simpleframework.module.dict.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.dict.Dict;
import net.simpleframework.module.dict.Dict.EDictMark;
import net.simpleframework.module.dict.DictException;
import net.simpleframework.module.dict.DictItem;
import net.simpleframework.module.dict.IDictContext;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.web.page.mgr2.DictMgrTPage;
import net.simpleframework.module.dict.web.page.t1.DictMgrPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.common.element.TextButton;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.component.ext.category.ICategoryHandler;
import net.simpleframework.mvc.component.ext.deptselect.DeptSelectBean;
import net.simpleframework.mvc.component.ui.dictionary.DictionaryBean;
import net.simpleframework.mvc.component.ui.dictionary.DictionaryTreeHandler;
import net.simpleframework.mvc.component.ui.tree.TreeBean;
import net.simpleframework.mvc.component.ui.tree.TreeNode;
import net.simpleframework.mvc.component.ui.tree.TreeNodes;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;
import net.simpleframework.mvc.template.t1.ext.CategoryTableLCTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DictItemEditPage extends FormTableRowTemplatePage implements IDictContextAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		// 验证
		addFormValidationBean(pp).addValidators(
				new Validator(EValidatorMethod.required, "#di_text, #di_dictText, #di_codeNo"));

		// 字典选取
		addComponentBean(pp, "DictItemEditPage_dictSelectTree", TreeBean.class).setHandlerClass(
				DictSelectedTree.class);
		addComponentBean(pp, "DictItemEditPage_dictSelect", DictionaryBean.class)
				.setBindingId("di_dictId").setBindingText("di_dictText")
				.addTreeRef(pp, "DictItemEditPage_dictSelectTree").setTitle($m("DictItemPage.0"))
				.setHeight(300);

		// 上级条目选取
		addComponentBean(pp, "DictItemEditPage_itemParentTree", TreeBean.class).setHandlerClass(
				ItemParentSelectedTree.class);
		addComponentBean(pp, "DictItemEditPage_itemParentSelect", DictionaryBean.class)
				.addTreeRef(pp, "DictItemEditPage_itemParentTree").setBindingId("di_parentId")
				.setBindingText("di_parentText").setTitle($m("DictItemPage.1")).setHeight(300)
				.setDestroyOnClose(true);

		// 机构选取
		addComponentBean(pp, "DictItemEditPage_deptSelect", DeptSelectBean.class).setOrg(true)
				.setBindingId("di_domainId").setBindingText("di_domainText");
	}

	private DictItem getDictItem(final PageParameter pp) {
		return _dictItemService.getBean(pp.getParameter("itemId"));
	}

	@Override
	protected boolean isShowOptNext(final PageParameter pp) {
		return getDictItem(pp) == null;
	}

	protected DictItem createDictItem(final PageParameter pp, final Dict dict) {
		final DictItem item = _dictItemService.createBean();
		item.setDictId(dict.getId());
		item.setDomainId(DictUtils.getDomainId(pp));
		item.setUserId(pp.getLoginId());
		return item;
	}

	@Override
	@Transaction(context = IDictContext.class)
	public JavascriptForward onSave(final ComponentParameter cp) {
		final Dict dict = _dictService.getBean(cp.getParameter("di_dictId"));
		if (dict == null) {
			throw DictException.of($m("DictItemPage.2"));
		}

		DictItem item = getDictItem(cp);
		final boolean insert = item == null;
		if (insert) {
			item = createDictItem(cp, dict);
		}
		item.setText(cp.getParameter("di_text"));
		item.setCodeNo(cp.getParameter("di_codeNo"));

		item.setExt1(cp.getParameter("di_ext1"));
		item.setExt2(cp.getIntParameter("di_ext2"));
		item.setExt3(cp.getParameter("di_ext3"));
		item.setExt4(cp.getLongParameter("di_ext4"));

		DictItem parent = null;
		final String[] arr = StringUtils.split(cp.getParameter("di_parentText"), ";");
		if (arr.length == 1) {
			parent = _dictItemService.getItemByCode(dict, arr[0]);
		} else if (arr.length > 1) {
		}
		if (parent == null) {
			parent = _dictItemService.getBean(cp.getParameter("di_parentId"));
		}
		if (parent != null) {
			item.setParentId(parent.getId());
		}

		item.setDescription(cp.getParameter("di_description"));
		if (insert) {
			_dictItemService.insert(item);
		} else {
			_dictItemService.update(item);
		}

		final JavascriptForward js = jsTableRefresh(dict);
		if (Convert.toBool(cp.getParameter(OPT_NEXT))) {
			js.append("$w('di_text di_codeNo di_description').each(function(e) { $(e).clear(); }); $('di_text').focus();");
		} else {
			js.append("$Actions['DictMgrPage_itemWin'].close();");
		}
		return js;
	}

	protected JavascriptForward jsTableRefresh(final Dict dict) {
		return CategoryTableLCTemplatePage.createTableRefresh("dictId=" + dict.getId());
	}

	public static class DictSelectedTree extends DictionaryTreeHandler {
		private AbstractComponentBean categoryBean;

		@Override
		public TreeNodes getTreenodes(final ComponentParameter cp, final TreeNode parent) {
			if (categoryBean == null) {
				categoryBean = get(DictMgrPage.class).getCategoryBean();
				if (categoryBean == null) {
					categoryBean = get(DictMgrTPage.class).getComponentBeanByName(cp,
							"DictMgrTPage_tree");
				}
			}

			final ComponentParameter nCP = ComponentParameter.get(cp, categoryBean);
			final ICategoryHandler cHandle = (ICategoryHandler) nCP.getComponentHandler();
			return cHandle.getCategoryDictTreenodes(nCP, (TreeBean) cp.componentBean, parent);
		}

		@Override
		public Map<String, Object> getTreenodeAttributes(final ComponentParameter cp,
				final TreeNode treeNode, final TreeNodes children) {
			final KVMap kv = (KVMap) super.getTreenodeAttributes(cp, treeNode, children);
			Object o;
			if (treeNode != null && (o = treeNode.getDataObject()) instanceof Dict
					&& ((Dict) o).getDictMark() == EDictMark.category) {
				kv.put(TN_ATTRI_SELECT_DISABLE, Boolean.TRUE);
			}
			return kv;
		}
	}

	public static class ItemParentSelectedTree extends DictionaryTreeHandler {
		@Override
		public Map<String, Object> getFormParameters(final ComponentParameter cp) {
			final Map<String, Object> parameters = super.getFormParameters(cp);
			final Dict dict = DictUtils.getDict(cp);
			if (dict != null) {
				parameters.put("dictId", dict.getId());
			}
			return parameters;
		}

		@Override
		public TreeNodes getTreenodes(final ComponentParameter cp, final TreeNode parent) {
			IDataQuery<DictItem> dq;
			final Dict dict = DictUtils.getDict(cp);
			if (dict == null) {
				dq = DataQueryUtils.nullQuery();
			} else {
				DictItem pItem = null;
				if (parent != null) {
					pItem = (DictItem) parent.getDataObject();
				}
				final ID domainId = DictUtils.getDomainId(cp);
				dq = (pItem == null ? _dictItemService.queryRoot(dict, domainId) : _dictItemService
						.queryChildren(pItem, domainId));
			}
			final TreeNodes nodes = TreeNodes.of();
			DictItem item;
			while ((item = dq.next()) != null) {
				final TreeNode node = new TreeNode((TreeBean) cp.componentBean, parent, item);
				nodes.add(node);
			}
			return nodes;
		}
	}

	@Override
	public int getLabelWidth(final PageParameter pp) {
		return 80;
	}

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final InputElement itemId = InputElement.hidden("itemId");
		final InputElement di_text = new InputElement("di_text");
		final InputElement di_codeNo = new InputElement("di_codeNo");

		final TextButton di_parentText = new TextButton("di_parentText")
				.setHiddenField("di_parentId").setOnclick(
						"$Actions['DictItemEditPage_itemParentSelect']('dictId=' + $F('di_dictId'))");

		final InputElement di_ext1 = new InputElement("di_ext1");
		final InputElement di_ext2 = new InputElement("di_ext2");
		final InputElement di_ext3 = new InputElement("di_ext3");
		final InputElement di_ext4 = new InputElement("di_ext4");

		final InputElement di_description = InputElement.textarea("di_description").setRows(4);

		final DictItem item = getDictItem(pp);
		if (item != null) {
			itemId.setVal(item.getId());
			di_text.setVal(item.getText());
			di_codeNo.setVal(item.getCodeNo());

			final DictItem parent = _dictItemService.getBean(item.getParentId());
			if (parent != null) {
				di_parentText.getHiddenField().setVal(parent.getId());
				di_parentText.setVal(parent.getText());
			}

			di_ext1.setVal(item.getExt1());
			di_ext2.setVal(item.getExt2());
			di_ext3.setVal(item.getExt3());
			di_ext4.setVal(item.getExt4());
			di_description.setVal(item.getDescription());
		}

		final TextButton di_dictText = new TextButton("di_dictText").setHiddenField("di_dictId")
				.setOnclick("$Actions['DictItemEditPage_dictSelect']();");
		final Dict dict = _dictService.getBean(item != null ? item.getDictId() : pp
				.getParameter("dictId"));
		if (dict != null) {
			di_dictText.getHiddenField().setVal(dict.getId());
			di_dictText.setVal(dict.getText());
		}

		final TableRow r1 = new TableRow(
				new RowField($m("DictMgrPage.1"), itemId, di_text).setStarMark(true), new RowField(
						$m("DictItemPage.0"), di_dictText).setStarMark(true));
		final TableRow r2 = new TableRow(
				new RowField($m("DictMgrPage.2"), di_codeNo).setStarMark(true), new RowField(
						$m("DictItemPage.1"), di_parentText));
		final TableRow r3 = new TableRow(new RowField($m("DictItemEditPage.0"), di_ext1),
				new RowField($m("DictItemEditPage.1"), di_ext2));
		final TableRow r4 = new TableRow(new RowField($m("DictItemEditPage.2"), di_ext3),
				new RowField($m("DictItemEditPage.3"), di_ext4));
		final TableRows rows = TableRows.of(r1, r2, r3, r4);
		if (pp.isLmanager()) {
			final TextButton di_domainText = new TextButton("di_domainText").setHiddenField(
					"di_domainId").setOnclick("$Actions['DictItemEditPage_deptSelect']();");
			PermissionDept org = null;
			if (item != null) {
				org = pp.getPermission().getDept(item.getDomainId());
			} else {
				org = AbstractMVCPage.getPermissionOrg(pp);
			}
			if (org != null) {
				di_domainText.getHiddenField().setVal(org.getId());
				di_domainText.setVal(org.getText());
			}
			rows.append(new TableRow(new RowField($m("DictCategoryHandler.4"), di_domainText)));
		}
		final TableRow r5 = new TableRow(new RowField($m("Description"), di_description));
		return rows.append(r5);
	}
}
