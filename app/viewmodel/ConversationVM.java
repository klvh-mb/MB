package viewmodel;

import java.util.Date;

import models.Conversation;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class ConversationVM {
	@JsonProperty("nm") public String name;
	@JsonProperty("uid") public Long userID;
	@JsonProperty("id") public Long id;
	@JsonProperty("cd") public Date creationDate;
	@JsonProperty("lm") public String lastMsg;
	@JsonProperty("isReaded") public Boolean isReaded = false;
	
	public ConversationVM(Conversation conversation, User user) {
		this.name = user.name;
		this.userID = user.id;
		this.id = conversation.id;
		this.creationDate = conversation.getUpdatedDate();
		try{
			this.lastMsg = conversation.getLastMessage();
			this.isReaded = conversation.isReadedBy(user);
		} catch(NullPointerException e){
			
		}
	}
}
