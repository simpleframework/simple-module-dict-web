package net.simpleframework.module.dict.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.Module;
import net.simpleframework.module.dict.impl.DictContext;
import net.simpleframework.module.dict.web.page.t1.DictMgrPage;
import net.simpleframework.mvc.ctx.WebModuleFunction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DictWebContext extends DictContext implements IDictWebContext {

	@Override
	public IModuleRef getLogRef() {
		return getRef("net.simpleframework.module.dict.web.DictLogRef");
	}

	@Override
	protected Module createModule() {
		return super.createModule().setDefaultFunction(
				new WebModuleFunction(DictMgrPage.class).setName(MODULE_NAME + "-DictMgrPage").setText(
						$m("DictContext.0")));
	}
}
