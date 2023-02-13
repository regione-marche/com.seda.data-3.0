/**
 * 
 */
package com.seda.data.procedure;

import java.sql.Connection;
import java.util.List;

import com.seda.data.procedure.result.ResultBounds;

/**
 * @author f.ricci
 *
 */
public interface Session {

	  Object selectOne(String procedure);

	  Object selectOne(String procedure, Object parameter);

	  List selectList(String procedure);

	  List selectList(String procedure, Object parameter);

	  List selectList(String procedure, Object parameter, ResultBounds resultBounds);

	  int execute(String statement);

	  int execute(String statement, Object parameter);

	  void commit();

	  void commit(boolean force);

	  void rollback();

	  void rollback(boolean force);

	  void close();

	  SubSystem getSubSystem();

	  Connection getConnection();
	}
