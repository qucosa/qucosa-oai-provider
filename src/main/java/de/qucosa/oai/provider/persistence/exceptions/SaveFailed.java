package de.qucosa.oai.provider.persistence.exceptions;

import java.sql.SQLException;

public class SaveFailed extends Exception {

    public SaveFailed(String s) {
        super(s);
    }

    public SaveFailed(String s, SQLException e) {
        super(s);
    }
}
