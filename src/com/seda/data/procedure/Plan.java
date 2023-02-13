/**
 * 
 */
package com.seda.data.procedure;

import javax.sql.DataSource;

import com.seda.data.procedure.transaction.TransactionFactory;
/**
 * @author f.ricci
 *
 */
public class Plan {
	
	  private String id;
	  private TransactionFactory transactionFactory;	  
	  private DataSource dataSource;

	  public String getId() {
	    return id;
	  }

	  public TransactionFactory getTransactionFactory() {
		  return transactionFactory;
	  }	  
	  
	  public DataSource getDataSource() {
	    return dataSource;
	  }	  
	  
	  public Plan(String id, TransactionFactory transactionFactory, DataSource dataSource) {
	    this.id = id;
	    this.transactionFactory = transactionFactory;	    
	    this.dataSource = dataSource;
	  }

	  private Plan() {  }
	  
	  public static class Factory {
		  
		  private Plan plan = new Plan();

		  public Factory(String id, TransactionFactory transactionManager, DataSource dataSource) {
			  plan.id = id;
			  plan.transactionFactory = transactionManager;
			  plan.dataSource = dataSource;
		  }

		  public Factory transactionFactory(TransactionFactory transactionFactory) {
			  plan.transactionFactory = transactionFactory;
			  return this;
		  }

		  public Factory dataSource(DataSource dataSource) {
			  plan.dataSource = dataSource;
			  return this;
		  }

		  public String id() {
			  return plan.id;
		  }

		  public Plan getPlan() {
			  return plan;
		  }

	  }	  

}
