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
			this.isReaded = conversation.isReadedBy(user);
			String s = conversation.getLastMessage();
		this.lastMsg = s.substring(0, Math.min(s.length(), 10));
		} catch(NullPointerException e){
			
		}
	}
}
