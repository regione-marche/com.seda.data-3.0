/**
 * 
 */
package com.seda.data.procedure.executor;

import java.sql.SQLException;
import java.util.List;

import com.seda.commons.cache.CacheKey;
import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.result.ResultBounds;
import com.seda.data.procedure.result.ResultHandler;
import com.seda.data.procedure.transaction.Transaction;

/**
 * @author f.ricci
 *
 */
public interface Executor {
	
	  ResultHandler NO_RESULT_HANDLER = null;

	  int execute(Procedure mc, Object parameter) throws SQLException;

	  List query(Procedure mc, Object parameter, ResultBounds rowBounds, ResultHandler resultHandler) throws SQLException;

	  void commit(boolean required) throws SQLException;

	  void rollback(boolean required) throws SQLException;

	  Transaction getTransaction();

	  void close(boolean forceRollback);
	  
	  CacheKey createResultKey(Procedure procedure, Object parameterObject, ResultBounds resultBounds);

	  boolean isCached(CacheKey key);

	  void clearLocalCache();
	  
	  boolean isClosed();
}
