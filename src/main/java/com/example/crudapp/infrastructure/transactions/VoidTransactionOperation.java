package com.example.crudapp.infrastructure.transactions;

import java.sql.SQLException;
import java.sql.Connection;

@FunctionalInterface
public interface VoidTransactionOperation {
    void apply(Connection connection) throws SQLException;
}
