package viewmodel;

import java.util.Date;

import models.GameAccountTransaction;

import org.codehaus.jackson.annotate.JsonProperty;

public class GameTransactionVM {
	@JsonProperty("uid")  public Long userId;
    @JsonProperty("tt")  public Date transactedTime;
    @JsonProperty("ty")  public String transactionType;
    @JsonProperty("tp")  public Long transactedPoints;
    @JsonProperty("td")  public String transactionDescription;
    @JsonProperty("ntp")  public Long newTotalPoints;

    public GameTransactionVM(GameAccountTransaction transaction) {
    	this.userId = transaction.userId;
        this.transactedTime = transaction.transactedTime;
        this.transactionType = transaction.transactionType.name();
        this.transactedPoints = transaction.transactedPoints;
        this.transactionDescription = transaction.transactionDescription;
        this.newTotalPoints = transaction.newTotalPoints;
    }
}
