package viewmodel;

import java.util.ArrayList;
import java.util.List;

import common.collection.Pair;
import models.Comment;
import models.Post;
import models.PKViewMeta;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * VM Class for PKView
 */
public class PKViewVM extends CommunityPostVM {
	
    @JsonProperty("id") public Long id;
    @JsonProperty("pid") public Long postId;
    
	@JsonProperty("red_ds") public String redDescription;
    @JsonProperty("red_im") public String redImage;
	@JsonProperty("n_rv") public long noOfRedVotes;
	@JsonProperty("n_rc") public long noOfRedComments;
	@JsonProperty("red_cs") public List<CommunityPostCommentVM> redComments;
	@JsonProperty("isRed") public boolean isRed = false;
	@JsonProperty("red_ep") public boolean redExpanded = true;     // always expanded
	
	@JsonProperty("blue_ds") public String blueDescription;
    @JsonProperty("blue_im") public String blueImage;
	@JsonProperty("n_bv") public long noOfBlueVotes;
	@JsonProperty("n_bc") public long noOfBlueComments;
	@JsonProperty("blue_cs") public List<CommunityPostCommentVM> blueComments;
	@JsonProperty("isBlue") public boolean isBlue = false;
	@JsonProperty("blue_ep") public boolean blueExpanded = true;       // always expanded
	
    public PKViewVM(PKViewMeta pkViewMeta, Post post, User user) {
        super(post, user);
        
        // fix id
        this.id = pkViewMeta.id;
        this.postId = post.id;
        
		this.redImage = pkViewMeta.getYesImage();
        this.blueImage = pkViewMeta.getNoImage();

        // fetch comments
        Pair<List<CommunityPostCommentVM>,List<CommunityPostCommentVM>>
                yesNoComments = extractYesNoCommentVMs(post, user);

		this.redDescription = pkViewMeta.getYesText();
		this.noOfRedVotes = pkViewMeta.getYesVoteCount();
		this.noOfRedComments = yesNoComments.first.size();
		this.redComments = yesNoComments.first;
		
		this.blueDescription = pkViewMeta.getNoText();
		this.noOfBlueVotes = pkViewMeta.getNoVoteCount();
		this.noOfBlueComments = yesNoComments.second.size();
		this.blueComments = yesNoComments.second;

		// TODO
        this.isRed = true;
        this.isBlue = false;
	}

    private static Pair<List<CommunityPostCommentVM>,List<CommunityPostCommentVM>>
            extractYesNoCommentVMs(Post p, User user) {
        List<Comment> yesComments = p.getCommentsOfPostByAttribute(PKViewMeta.COMMENT_ATTR_YES);
        List<Comment> noComments = p.getCommentsOfPostByAttribute(PKViewMeta.COMMENT_ATTR_NO);

        int yesCommentCount = yesComments.size();
        int noCommentCount = noComments.size();
        List<CommunityPostCommentVM> yesCommentVMs = new ArrayList<>(yesCommentCount);
        List<CommunityPostCommentVM> noCommentVMs = new ArrayList<>(noCommentCount);

        for(int i = yesCommentCount- 1; i >= 0 ; i--) {
            Comment yesComment = yesComments.get(i);
            CommunityPostCommentVM vm = CommunityPostCommentVM.communityPostCommentVM(yesComment, user, yesCommentCount-i);
            yesCommentVMs.add(vm);
        }
        for(int i = noCommentCount- 1; i >= 0 ; i--) {
            Comment noComment = noComments.get(i);
            CommunityPostCommentVM vm = CommunityPostCommentVM.communityPostCommentVM(noComment, user, noCommentCount-i);
            noCommentVMs.add(vm);
        }
        return new Pair<>(yesCommentVMs, noCommentVMs);
    }
}
