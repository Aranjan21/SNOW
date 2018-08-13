package com.lunera.db.config;

import java.sql.Connection;


import lombok.Data;

@Data
public class RdsConnection {
	private Connection connection;
}
