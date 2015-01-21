package com.risevision.monitoring.api.accessor;

import com.risevision.monitoring.api.response.APIResponse;
import com.risevision.monitoring.api.wrappers.AbstractWrapper;

import java.util.logging.Logger;

public abstract class AbstractEntityAccessor<ItemWrapper extends AbstractWrapper, ListWrapper extends AbstractWrapper>{

	public APIResponse<ItemWrapper> get(String id) throws Exception {
		
		return APIResponse.<ItemWrapper> builder().build(); 
	}
	
	// Utilities
	
	protected Logger getLogger() {
		
		return Logger.getLogger(this.getClass().getName());
	}

}