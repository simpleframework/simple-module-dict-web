package net.simpleframework.module.dict.web.page.mgr2;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.script.MVEL2Template;
import net.simpleframework.module.common.web.page.AbstractMgrTPage;
import net.simpleframework.module.dict.EDictItemMark;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.dict.web.page.DictCategoryHandler;
import net.simpleframework.module.dict.web.page.DictItemList;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.component.ext.category.CategoryBean;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;

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

		addComponentBean(pp, "DictMgrTPage_category", CategoryBean.class).setContainerId(
				"idDictMgrTPage_category").setHandlerClass(_DictCategoryHandler.class);

		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp, "DictMgrTPage_tbl",
				TablePagerBean.class).setShowLineNo(true).setPagerBarLayout(EPagerBarLayout.top)
				.setContainerId("idDictMgrTPage_tbl").setHandlerClass(_DictItemList.class);
		tablePager
				.addColumn(new TablePagerColumn("text", $m("DictMgrPage.1")))
				.addColumn(new TablePagerColumn("codeNo", $m("DictMgrPage.2")))
				.addColumn(new TablePagerColumn("parentId", $m("DictMgrPage.8"), 150).setFilter(false))
				.addColumn(
						new TablePagerColumn("itemMark", $m("DictMgrPage.3"), 100)
								.setPropertyClass(EDictItemMark.class)).addColumn(TablePagerColumn.OPE(70));
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		return null;
		// return ElementList.of(new SpanElement("Test"));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='DictMgrTPage'>");
		sb.append("	<table width='100%'><tr>");
		sb.append("  <td valign='top' class='ltree'><div id='idDictMgrTPage_category'></div></td>");
		sb.append("  <td class='rtbl'><div id='idDictMgrTPage_tbl'></div></td>");
		sb.append(" </tr></table>");
		sb.append("</div>");
		sb.append(MVEL2Template.replace(new KVMap(), DictMgrTPage.class, "Test.html"));
		return sb.toString();
	}

	public static class _DictCategoryHandler extends DictCategoryHandler {
	}

	public static class _DictItemList extends DictItemList {
	}
}
