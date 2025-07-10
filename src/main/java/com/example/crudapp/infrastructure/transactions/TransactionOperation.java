package com.example.crudapp.infrastructure.transactions;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransactionOperation<T> {
    T apply(Connection connection) throws SQLException;
}
