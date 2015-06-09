package viewmodel;

import java.util.Date;

import models.GameAccountTransaction;

import org.codehaus.jackson.annotate.JsonProperty;

public class GameTransactionVM {
    @JsonProperty("tt")  public Date transactedTime;
    @JsonProperty("ty")  public String transactionType;
    @JsonProperty("tp")  public Long transactedPoints;
    @JsonProperty("td")  public String transactionDescription;

    public GameTransactionVM(GameAccountTransaction transaction) {
        this.transactedTime = transaction.transactedTime;
        this.transactionType = transaction.transactionType.name();
        this.transactedPoints = transaction.transactedPoints;
        this.transactionDescription = transaction.transactionDescription;
    }
}
