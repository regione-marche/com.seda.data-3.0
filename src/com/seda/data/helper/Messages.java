/**
 * 
 */
package com.seda.data.helper;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * @author dbadm
 *
 */
public enum Messages {
	METADATA_UNSUPPORTED, ARGUMENT_NULL, PARAMETER_COUNT_ERROR,
	SQLE_MESSAGE, SQLE_SQLSTATE, SQLE_ERROR_CODE, SQLE_STACK,
	SQLW_MESSAGE, SQLW_SQLSTATE, SQLW_ERROR_CODE	
    ;

    private static ResourceBundle rb;

    public String format( Object... args ) {
        synchronized(Messages.class) {
            if(rb==null)
                rb = ResourceBundle.getBundle(Messages.class.getName());
            return MessageFormat.format(rb.getString(name()),args);
        }
    }
}
