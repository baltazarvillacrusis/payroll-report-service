package com.svi.payroll.reports.util;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.mapping.MappingManager;
import com.svi.payroll.reports.enums.ReportEnum;
import com.svi.payroll.reports.object.cassandraDAO.PayrollComputeAccessor;


public class CassandraConnectionUtility {	

	private static SocketOptions socketOptions = new SocketOptions().setReadTimeoutMillis(30000);
	private static Cluster cluster = Cluster.builder().addContactPoint(ReportEnum.CASSANDRA_IP_ADD.value())
			.withSocketOptions(socketOptions)
			.build();

	private static Session session = cluster.connect(ReportEnum.CASSANDRA_KEYSPACE.value());
	
	private static MappingManager manager = new MappingManager(session);

	private static PayrollComputeAccessor userAccessor = manager.createAccessor(PayrollComputeAccessor.class);

	public static void close() {
		session.close();
		cluster.close();
	}

	
	
	public static ResultSet getTaxComputationDetail(String query){
		return session.execute(query);	 
	}

	public static MappingManager getManager() {
		return manager;
	}

	public static PayrollComputeAccessor getUserAccessor() {
		return userAccessor;
	}

	
	
}
