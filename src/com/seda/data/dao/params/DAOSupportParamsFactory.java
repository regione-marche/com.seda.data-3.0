/**
 * 
 */
package com.seda.data.dao.params;

import com.seda.commons.params.DefaultParametersFactory;
import com.seda.commons.params.Parameters;

/**
 * @author f.ricci
 *
 */
public class DAOSupportParamsFactory extends DefaultParametersFactory {
	
	public synchronized Parameters getDefaultParams() {
		if (parameters == null) {
			parameters = createParams();
		}

		return parameters;
	}

	protected Parameters createParams() {
		DAOSupportParams  params = new DAOSupportParams(null);

		params.setConnectionAutocommit(true);
		params.setConnectionRetry(3);
		params.setConnectionRetryWaitTime(20000);

		params.setExecuteRetry(5);
		params.setExecuteRetryWaitTime(5000);

		return params;
	} 
	
}
