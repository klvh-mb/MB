package viewmodel;

import java.util.Date;

import models.Conversation;
import models.User;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConversationVM {
	@JsonProperty("nm") public String name;
	@JsonProperty("uid") public Long userID;
	@JsonProperty("id") public Long id;
	@JsonProperty("lmd") public Date lastMessageDate;
	@JsonProperty("isToday") public Boolean isToday;
	@JsonProperty("lm") public String lastMsg;
	@JsonProperty("isReaded") public Boolean isReaded = false;
	@JsonProperty("mc") public Long msgCount;
	
	public ConversationVM(Conversation conversation, User user) {
		this.name = user.name;
		this.userID = user.id;
		this.id = conversation.id;
		this.lastMessageDate = conversation.getUpdatedDate();
		this.msgCount = conversation.getMessageCount(user);
		try{
			this.lastMsg = conversation.getLastMessage();
			this.isToday = DateUtils.isSameDay(this.lastMessageDate, new Date());
			this.isReaded = conversation.isReadedBy(user);
		} catch(NullPointerException e){
			
		}
	}
}
