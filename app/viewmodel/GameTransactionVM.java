package viewmodel;

import java.util.Date;

import models.GameAccountTransaction;

import org.codehaus.jackson.annotate.JsonProperty;

public class GameTransactionVM {
    @JsonProperty("tt")  public Date transactedTime;
    @JsonProperty("tp")  public Long transactedPoints;
    @JsonProperty("td")  public String transactionDescription;

    public GameTransactionVM(GameAccountTransaction transaction) {
        this.transactedTime = transaction.transactedTime;
        this.transactedPoints = transaction.transactedPoints;
        this.transactionDescription = transaction.transactionDescription;
    }
}
