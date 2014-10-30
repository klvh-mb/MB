package viewmodel;

import models.Subscription;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class SubscriptionVM {
    @JsonProperty("id") public long id;
    @JsonProperty("nm") public String name;
    @JsonProperty("isSub") public boolean isSubscribed = false;
    
    public SubscriptionVM() {
    }   
    
    public SubscriptionVM(Subscription subscription,User user) {
        this.id = subscription.id;
        this.name = subscription.name;
        this.isSubscribed = user.isSubscribedBy(subscription);
    }   
}