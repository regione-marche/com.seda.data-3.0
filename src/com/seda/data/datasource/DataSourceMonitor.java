/**
 * 
 */
package com.seda.data.datasource;

import java.util.ArrayList;
import java.util.List;

import com.seda.commons.management.annotations.ManagedAttribute;
/**
 * @author f.ricci
 *
 */
public class DataSourceMonitor {

	protected PooledDataSource dataSource;

	protected List idleConnections = new ArrayList();
	protected List activeConnections = new ArrayList();
	protected long requestCount = 0;
	protected long accumulatedRequestTime = 0;
	protected long accumulatedCheckoutTime = 0;
	protected long claimedOverdueConnectionCount = 0;
	protected long accumulatedCheckoutTimeOfOverdueConnections = 0;
	protected long accumulatedWaitTime = 0;
	protected long hadToWaitCount = 0;
	protected long badConnectionCount = 0;

	public DataSourceMonitor(PooledDataSource dataSource) {
		this.dataSource = dataSource;
	}
	@ManagedAttribute
	public synchronized long getRequestCount() {
		return requestCount;
	}
	@ManagedAttribute
	public synchronized long getAverageRequestTime() {
		return requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
	}
	@ManagedAttribute
	public synchronized long getAverageWaitTime() {
		return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;

	}
	@ManagedAttribute
	public synchronized long getHadToWaitCount() {
		return hadToWaitCount;
	}
	@ManagedAttribute
	public synchronized long getBadConnectionCount() {
		return badConnectionCount;
	}
	@ManagedAttribute
	public synchronized long getClaimedOverdueConnectionCount() {
		return claimedOverdueConnectionCount;
	}
	@ManagedAttribute
	public synchronized long getAverageOverdueCheckoutTime() {
		return claimedOverdueConnectionCount == 0 ? 0 : accumulatedCheckoutTimeOfOverdueConnections / claimedOverdueConnectionCount;
	}
	@ManagedAttribute
	public synchronized long getAverageCheckoutTime() {
		return requestCount == 0 ? 0 : accumulatedCheckoutTime / requestCount;
	}

	@ManagedAttribute
	public synchronized int getIdleConnectionCount() {
		return idleConnections.size();
	}
	@ManagedAttribute
	public synchronized int getActiveConnectionCount() {
		return activeConnections.size();
	}

	public synchronized String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n===CONFINGURATION==============================================");
		buffer.append("\n jdbcDriver                     ").append(dataSource.getDriver());
		buffer.append("\n jdbcUrl                        ").append(dataSource.getUrl());
		buffer.append("\n jdbcUsername                   ").append(dataSource.getUsername());
		buffer.append("\n jdbcPassword                   ").append((dataSource.getPassword() == null ? "NULL" : "************"));
		buffer.append("\n poolMaxActiveConnections       ").append(dataSource.maximumActiveConnections);
		buffer.append("\n poolMaxIdleConnections         ").append(dataSource.maximumIdleConnections);
		buffer.append("\n poolMaxCheckoutTime            ").append(dataSource.maximumCheckoutTime);
		buffer.append("\n poolTimeToWait                 ").append(dataSource.timeToWait);
		buffer.append("\n poolPingEnabled                ").append(dataSource.pingEnabled);
		buffer.append("\n poolPingQuery                  ").append(dataSource.pingQuery);
		buffer.append("\n poolPingConnectionsNotUsedFor  ").append(dataSource.pingConnectionTimeout);
		buffer.append("\n ---STATUS-----------------------------------------------------");
		buffer.append("\n activeConnections              ").append(getActiveConnectionCount());
		buffer.append("\n idleConnections                ").append(getIdleConnectionCount());
		buffer.append("\n requestCount                   ").append(getRequestCount());
		buffer.append("\n averageRequestTime             ").append(getAverageRequestTime());
		buffer.append("\n averageCheckoutTime            ").append(getAverageCheckoutTime());
		buffer.append("\n claimedOverdue                 ").append(getClaimedOverdueConnectionCount());
		buffer.append("\n averageOverdueCheckoutTime     ").append(getAverageOverdueCheckoutTime());
		buffer.append("\n hadToWait                      ").append(getHadToWaitCount());
		buffer.append("\n averageWaitTime                ").append(getAverageWaitTime());
		buffer.append("\n badConnectionCount             ").append(getBadConnectionCount());
		buffer.append("\n===============================================================");
		return buffer.toString();
	}	

}
