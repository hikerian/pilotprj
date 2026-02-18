package org.hddbscan.repository;

import java.io.IOException;

import org.springframework.core.io.support.ResourcePropertySource;


public class SQLMapper {
	private final ResourcePropertySource batchSql;
	
	
	public SQLMapper() throws IOException {
		this.batchSql = new ResourcePropertySource("classpath:hd-dbscan-sql.xml");
	}
	
	public String getSQL(String name) {
		Object sql = this.batchSql.getProperty(name);
		return (String)sql;
	}

}
