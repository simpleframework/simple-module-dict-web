package net.simpleframework.module.dict.web;

import net.simpleframework.module.dict.DictItem;
import net.simpleframework.module.dict.IDictContextAware;
import net.simpleframework.module.log.LogRef;
import net.simpleframework.module.log.web.page.EntityUpdateLogPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DictLogRef extends LogRef implements IDictContextAware {

	public void addLogComponent(final PageParameter pp) {
		pp.addComponentBean("DictMgrPage_logPage", AjaxRequestBean.class).setUrlForward(
				AbstractMVCPage.url(DictItemLogPage.class));
		pp.addComponentBean("DictMgrPage_logWin", WindowBean.class)
				.setContentRef("DictMgrPage_logPage").setHeight(600).setWidth(960);
	}

	public static class DictItemLogPage extends EntityUpdateLogPage {

		@Override
		protected DictItem getBean(final PageParameter pp) {
			return getCacheBean(pp, context.getDictItemService(), getBeanIdParameter());
		}
	}
}
