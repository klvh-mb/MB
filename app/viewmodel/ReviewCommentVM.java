package viewmodel;

import models.ReviewComment;
import models.User;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/2/15
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReviewCommentVM {
    @JsonProperty("id") public Long id;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("cd") public Long postedOn;
	@JsonProperty("d") public String commentText;
	@JsonProperty("nol") public int noOfLikes;
	@JsonProperty("n") public int number;

	@JsonProperty("isO") public boolean isOwner = false;
	@JsonProperty("isLike") public boolean isLike = false;     // filled outside

    public static ReviewCommentVM toVM(ReviewComment reviewComment, User user, int number) {
        ReviewCommentVM vm = new ReviewCommentVM();
        vm.id = reviewComment.getId();
        vm.ownerId = reviewComment.owner.getId();
        vm.postedOn = reviewComment.getCreatedDate().getTime();
        vm.commentText = reviewComment.body;
        vm.noOfLikes = reviewComment.noOfLikes;
        vm.number = number;

        vm.isOwner = reviewComment.owner.id == user.id;
        return vm;
    }
}
