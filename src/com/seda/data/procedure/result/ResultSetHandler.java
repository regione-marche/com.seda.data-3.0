package com.seda.data.procedure.result;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface ResultSetHandler {

  List processResultSets(Statement stmt) throws SQLException;

  void processOutputParameters(CallableStatement cs) throws SQLException;

}
