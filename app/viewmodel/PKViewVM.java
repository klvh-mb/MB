package viewmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import common.collection.Pair;
import models.PrimarySocialRelation;

import models.Comment;
import models.PKViewMeta;
import models.Post;
import models.User;
import org.codehaus.jackson.annotate.JsonProperty;

import controllers.Application;
import processor.PrimarySocialRelationManager;
import static processor.PrimarySocialRelationManager.PrimarySocialResult;

/**
 * VM Class for PKView
 */
public class PKViewVM extends CommunityPostVM {
	
    @JsonProperty("id") public Long id;
    @JsonProperty("pid") public Long postId;
    @JsonProperty("im") public String postImage;

    @JsonProperty("red_vp") public Long redVotePercent;
    @JsonProperty("blue_vp") public Long blueVotePercent;
    @JsonProperty("red_w") public Long redBarWidth;
    @JsonProperty("blue_w") public Long blueBarWidth;

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
	
	public static final long MIN_BAR_WIDTH = 12;
	
	public PKViewVM(PKViewMeta pkViewMeta, Post post, User user) {
	    this(pkViewMeta, post, user, false);
	}
	
    public PKViewVM(PKViewMeta pkViewMeta, Post post, User user, boolean skipComments) {
        super(post, user);
        
        // fix id
        this.id = pkViewMeta.id;
        this.postId = post.id;
        this.postImage = pkViewMeta.getPostImage();
        
		this.redImage = pkViewMeta.getYesImage();
        this.blueImage = pkViewMeta.getNoImage();
        this.redDescription = pkViewMeta.getYesText();
        this.noOfRedVotes = pkViewMeta.getYesVoteCount();
        this.blueDescription = pkViewMeta.getNoText();
        this.noOfBlueVotes = pkViewMeta.getNoVoteCount();
        
        if (!skipComments) {
            // fetch comments
            Pair<List<CommunityPostCommentVM>,List<CommunityPostCommentVM>> 
                yesNoComments = extractYesNoCommentVMs(post, user);
        
            this.noOfRedComments = yesNoComments.first.size();
            this.redComments = yesNoComments.first;
            
            this.noOfBlueComments = yesNoComments.second.size();
            this.blueComments = yesNoComments.second;
        }

        // check if user has voted
        Boolean vote = post.getYesNoVote(user);
        if (vote != null) {
            if (vote) {
                this.isRed = true;
            } else {
                this.isBlue = true;
            }
        }

        // UI
        long minBarWidth = Application.isMobileUser()? MIN_BAR_WIDTH * 2 : MIN_BAR_WIDTH;
        long totalVotes = noOfRedVotes + noOfBlueVotes;
        if (totalVotes == 0) {
            this.redVotePercent = 0L;
            this.redBarWidth = 50L;
            this.blueVotePercent = 0L;
            this.blueBarWidth = 50L;
        } else {
            this.redVotePercent = noOfRedVotes * 100 / totalVotes;
            this.blueVotePercent = 100 - redVotePercent;
            if (redVotePercent > blueVotePercent) {
                blueBarWidth = (blueVotePercent < minBarWidth)? minBarWidth : blueVotePercent;
                redBarWidth = 100 - blueBarWidth;
            } else {
                redBarWidth = (redVotePercent < minBarWidth)? minBarWidth : redVotePercent;
                blueBarWidth = 100 - redBarWidth;
            }
        }
	}

    // Extract Yes/No Comments
    private static Pair<List<CommunityPostCommentVM>,List<CommunityPostCommentVM>>
            extractYesNoCommentVMs(Post p, User user) {
        List<Comment> yesComments = p.getCommentsOfPostByAttribute(PKViewMeta.COMMENT_ATTR_YES);
        List<Comment> noComments = p.getCommentsOfPostByAttribute(PKViewMeta.COMMENT_ATTR_NO);

        int yesCommentCount = yesComments.size();
        int noCommentCount = noComments.size();
        List<CommunityPostCommentVM> yesCommentVMs = new ArrayList<>(yesCommentCount);
        List<CommunityPostCommentVM> noCommentVMs = new ArrayList<>(noCommentCount);

        Set<PrimarySocialResult> srByUser = null;
        if (User.isLoggedIn(user)){
            // optimization for like checking
            List<Long> likeCheckIds = new ArrayList<>(yesCommentCount+noCommentCount);
            for (Comment yesComment : yesComments) {
                likeCheckIds.add(yesComment.getId());
            }
            for (Comment noComment : noComments) {
                likeCheckIds.add(noComment.getId());
            }
            srByUser = PrimarySocialRelationManager.getSocialRelationBy(user, likeCheckIds);
        }

        for(int i = yesCommentCount- 1; i >= 0 ; i--) {
            Comment yesComment = yesComments.get(i);
            CommunityPostCommentVM vm = CommunityPostCommentVM.communityPostCommentVM(yesComment, user, yesCommentCount-i);
            vm.isLike = srByUser != null &&
                    srByUser.contains(new PrimarySocialResult(yesComment.id, yesComment.objectType, PrimarySocialRelation.Action.LIKED));
            yesCommentVMs.add(vm);
        }
        for(int i = noCommentCount- 1; i >= 0 ; i--) {
            Comment noComment = noComments.get(i);
            CommunityPostCommentVM vm = CommunityPostCommentVM.communityPostCommentVM(noComment, user, noCommentCount-i);
            vm.isLike = srByUser != null &&
                    srByUser.contains(new PrimarySocialResult(noComment.id, noComment.objectType, PrimarySocialRelation.Action.LIKED));
            noCommentVMs.add(vm);
        }
        return new Pair<>(yesCommentVMs, noCommentVMs);
    }
}
