package database;

import classes.PreparedStatementWrapper;

public class Transaction {
    PreparedStatementWrapper pstmtw;
    long timestamp;

    public Transaction(PreparedStatementWrapper pstmtw) {
        this.pstmtw = pstmtw;
        this.timestamp = System.currentTimeMillis();
    }
}
