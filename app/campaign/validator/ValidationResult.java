package campaign.validator;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/12/14
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationResult {
    public static ValidationResult VALIDATOR_NOT_FOUND = new ValidationResult(false, "Validator class not found");


    public boolean success;
    public String message;

    public ValidationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
