package chatsystem.log;

/* Error that is thrown when a table containing the same recipient (from) that already exists in another table in the database. */
@SuppressWarnings("serial")
public class TableAlreadyExists extends Exception {

    private final String table;

    public TableAlreadyExists(String table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "TableAlreadyExists{" + table + "}";
    }
}
