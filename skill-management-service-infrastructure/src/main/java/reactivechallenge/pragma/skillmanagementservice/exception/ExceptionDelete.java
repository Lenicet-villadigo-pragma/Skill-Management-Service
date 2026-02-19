package reactivechallenge.pragma.skillmanagementservice.exception;

import org.springframework.transaction.TransactionException;

public class ExceptionDelete extends TransactionException {
    public ExceptionDelete(String msg, Throwable cause) {
        super(msg, cause);
    }
}
