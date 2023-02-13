/**
 * 
 */
package com.seda.data.dao.params;

import com.seda.commons.logging.Log;
import com.seda.commons.logging.LogFactory;
import com.seda.commons.params.DefaultParameters;
import com.seda.commons.params.ParametersFactory;

/**
 * @author f.ricci
 *
 */
public class DAOSupportParams extends DefaultParameters {

	/** Log object for this class. */
    private static final Log LOG = LogFactory.getLog(DAOSupportParams.class);

    /** Parameters class factory. */
    protected static ParametersFactory paramsFactory = new DAOSupportParamsFactory();    
    
    /**
     * Sets the autocommit behavior. 
     * <p>
     * This parameter expects a value of type {@link Boolean}.
     * </p>
     */
    public static final String CONNECTION_AUTOCOMMIT = "dao.connection.autommit";    
    
    /**
     * Sets the number of time that DAOSupportExtended retry getConnection. 
     * A value of zero is interpreted as no retry.
     * <p>
     * This parameter expects a value of type {@link Integer}.
     * </p>
     */
    public static final String CONNECTION_RETRY = "dao.connection.retry";
    
    /**
     * Sets the time in milliseconds to be wait when retrying getConnection. 
     * A value of zero is interpreted as no wait time.
     * <p>
     * This parameter expects a value of type {@link Long}.
     * </p>
     */
    public static final String CONNECTION_WAIT_BETWEEN_RETRY = "dao.connection.retry.wait";

    /**
     * Sets number of time that DAOSupportExtended retry execution of a method. 
     * A value of zero is interpreted as no retry.
     * <p>
     * This parameter expects a value of type {@link Integer}.
     * </p>
     */
    public static final String EXECUTE_RETRY = "dao.execute.retry";    
    
    /**
     * Sets the time in milliseconds to be wait when retrying execution of a method. 
     * A value of zero is interpreted as no wait time.
     * <p>
     * This parameter expects a value of type {@link Long}.
     * </p>
     */
    public static final String EXECUTE_WAIT_BETWEEN_RETRY = "dao.execute.retry.wait";
    
    /**
     * Creates a new collection of parameters with the collection returned
     * by {@link #getDefaultParams()} as a parent. The collection will defer
     * to its parent for a default value if a particular parameter is not 
     * explicitly set in the collection itself.
     * 
     * @see #getDefaultParams()
     */
    public DAOSupportParams() {
        super(paramsFactory.getDefaultParameters());
    }

    /**
     * Creates a new collection of parameters with the given parent. 
     * The collection will defer to its parent for a default value 
     * if a particular parameter is not explicitly set in the collection
     * itself.
     * 
     * @param defaults the parent collection to defer to, if a parameter
     * is not explictly set in the collection itself.
     *
     * @see #getDefaultParams()
     */
    public DAOSupportParams(DAOSupportParams defaults) {
        super(defaults);
    }
    /**
     * Returns the autocommit behavior for the connection.
     */
    public boolean getConnectionAutocommit() {
        return getBooleanParameter(CONNECTION_AUTOCOMMIT, true);
    }
    /**
     * Sets the autocommit behavior for the connection. 
     * <p>
     * This parameter expects a value of type {@link Boolean}.
     * </p>
     */
    public void setConnectionAutocommit(boolean autoCommit) {
        setBooleanParameter(CONNECTION_AUTOCOMMIT, autoCommit);
    }
    /**
     * Returns the number of time that DAOSupportExtended retry getConnection. A value of zero is interpreted as no 
     * retry.  
     *
     * @return number of retries
     */
    public int getConnectionRetry() {
        return getIntParameter(CONNECTION_RETRY, 3);
    }
    /**
     * Sets the number of time that DAOSupportExtended retry getConnection. 
     * A value of zero is interpreted as no retry.
     * <p>
     * This parameter expects a value of type {@link Integer}.
     * </p>
     */
    public void setConnectionRetry(int retry) {
        setIntParameter(CONNECTION_WAIT_BETWEEN_RETRY, retry);
    }
    
    /**
     * Returns the default wait time in millis which is used between retry getConnection. A value of zero is interpreted as no 
     * wait time.  
     *
     * @return wait time in milliseconds
     */
    public long getConnectionRetryWaitTime() {
        return getLongParameter(CONNECTION_WAIT_BETWEEN_RETRY, 20000);
    }

    /**
     * Sets the time in milliseconds to be wait when retrying getConnection. 
     * A value of zero is interpreted as no wait time.
     * <p>
     * This parameter expects a value of type {@link Long}.
     * </p>
     */
    public void setConnectionRetryWaitTime(long wait) {
        setLongParameter(CONNECTION_WAIT_BETWEEN_RETRY, wait);
    }
    
    /**
     * Returns the default number of time that DAOSupportExtende retry execute a method. A value of zero is interpreted as no 
     * retry.  
     *
     * @return number of retry
     */
    public int getExecuteRetry() {
        return getIntParameter(EXECUTE_RETRY, 5);
    }

    /**
     * Sets number of time that DAOSupportExtended retry execute a method. 
     * A value of zero is interpreted as no retry.
     * <p>
     * This parameter expects a value of type {@link Integer}.
     * </p>
     */
    public void setExecuteRetry(int retry) {
        setIntParameter(EXECUTE_RETRY, retry);
    }    
    
    /**
     * Returns the default wait time in millis used between retry execution of a method. A value of zero is interpreted as no 
     * wait time.  
     *
     * @return timeout in milliseconds
     */
    public long getExecuteRetryWaitTime() {
        return getLongParameter(EXECUTE_WAIT_BETWEEN_RETRY, 10000);
    }

    /**
     * Sets the time in milliseconds to be wait when retrying execute method. 
     * A value of zero is interpreted as no wait time.
     * <p>
     * This parameter expects a value of type {@link Long}.
     * </p>
     */
    public void setExecuteRetryWaitTime(long wait) {
        setLongParameter(EXECUTE_WAIT_BETWEEN_RETRY, wait);
    }
}
