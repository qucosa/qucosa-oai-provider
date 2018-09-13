package de.qucosa.oai.provider.persitence.exceptions;

import java.sql.SQLException;

public class DeleteFailed extends Exception {
    public DeleteFailed(String s) {
    }

    public DeleteFailed(String s, SQLException e) {
    }
}
