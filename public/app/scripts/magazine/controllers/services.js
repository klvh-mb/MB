'use strict';

var minibeanMag = angular.module('minibeanMag');

minibeanMag.service('announcementsService',function($resource) {
    this.getGeneralAnnouncements = $resource(
            '/get-general-announcements',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    
    this.getTopAnnouncements = $resource(
            '/get-top-announcements',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

minibeanMag.service('todayWeatherInfoService',function($resource) {
    this.getTodayWeatherInfo = $resource(
            '/get-today-weather-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('locationService',function($resource){
    this.getAllDistricts = $resource(
            '/get-all-districts',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

minibeanMag.service('postLandingService',function($resource){
    this.postLanding = $resource(
            '/post-landing/:id/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{id:'@id',communityId:'@communityId'}}
            }
    );
});

minibeanMag.service('qnaLandingService',function($resource){
    this.qnaLanding = $resource(
            '/qna-landing/:id/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{id:'@id',communityId:'@communityId'}}
            }
    );
});

///////////////////////// Search Service Start //////////////////////////////////
minibeanMag.service('searchService',function($resource){
    this.userSearch = $resource(
            '/user-search?query=:q',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{q:'@q'}, isArray:true}
            }
    );
});

minibeanMag.service('sendInvitation',function($resource){
    this.inviteFriend = $resource(
            '/send-invite?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

minibeanMag.service('unFriendService',function($resource){
    this.doUnfriend = $resource(
            '/un-friend?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

minibeanMag.service('applicationInfoService',function($resource){
    this.ApplicationInfo = $resource(
            '/get-application-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

minibeanMag.service('userInfoService',function($resource){
    this.UserInfo = $resource(
            '/get-user-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
    
    this.UserTargetProfile = $resource(
            '/get-user-target-profile',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

minibeanMag.service('userNotification',function($resource){
    this.getAllFriendRequests = $resource(
            '/get-friend-requests',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', isArray:true}
            }
    );
});

minibeanMag.service('acceptFriendRequestService',function($resource){
    this.acceptFriendRequest = $resource(
            '/accept-friend-request?friend_id=:id&notify_id=:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id',notify_id:'@notify_id'}, isArray:true}
            }
    );
});

minibeanMag.service('userSimpleNotifications',function($resource){
    this.getAllJoinRequests = $resource(
            '/get-join-requests',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', isArray:true}
            }
    );
});

minibeanMag.service('userMessageNotifications',function($resource){
    this.getUnreadMsgCount = $resource(
            '/get-unread-msg-count',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});


minibeanMag.service('acceptJoinRequestService',function($resource){
    this.acceptJoinRequest = $resource(
            '/accept-join-request/:member_id/:group_id/:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
            }
    );
    
    this.acceptInviteRequest = $resource(
            '/accept-invite-request/:member_id/:group_id/:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
            }
    );
});

minibeanMag.service('notificationMarkReadService',function($resource){
    this.markAsRead = $resource(
            '/mark-as-read/:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
            }
    );
});

minibeanMag.service('userAboutService',function($resource){
    this.UserAbout = $resource(
            '/about-user',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('profilePhotoModal',function( $modal){
    
    this.OpenModal = function(arg, successCallback) {
        this.instance = $modal.open(arg);
        this.onSuccess = successCallback;
    }
    
    this.CloseModal = function() {
        this.instance.dismiss('close');
        this.onSuccess();
    }
});

minibeanMag.service('editCommunityPageService',function($resource){
    this.EditCommunityPage = $resource(
            '/editCommunity/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

minibeanMag.service('membersWidgetService',function($resource){
    this.CommunityMembers = $resource(
            '/get-community-members/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('pnService',function($resource){
    this.PNCommunities = $resource(
            '/get-pn-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', isArray:true}
            }
    );
    this.PNs = $resource(
            '/getPNs/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'},isArray:true}
            }
    );
});

minibeanMag.service('unJoinedCommunityWidgetService',function($resource){
    this.UserCommunitiesNot = $resource(
            '/get-not-join-community',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('sendJoinRequest',function($resource){
    this.sendRequest = $resource(
            '/send-request?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

minibeanMag.service('friendsService',function($resource){
    this.MyFriendsForUtility = $resource(
            '/get-my-friends-for-utility',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.UserFriendsForUtility = $resource(
            '/get-user-friends-for-utility/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
    
    this.MyFriends = $resource(
            '/get-all-my-friends',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.UserFriends = $resource(
            '/get-all-user-friends/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
    
    this.SuggestedFriends = $resource(
            '/get-suggested-friends',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('sendJoinRequest',function($resource){
    this.sendRequest = $resource(
            '/send-request?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

minibeanMag.service('communityWidgetService',function($resource){
    this.UserCommunities = $resource(
            '/get-my-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('communityWidgetByUserService',function($resource){
    this.UserCommunities = $resource(
            '/get-user-communities/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('allCommunityWidgetByUserService',function($resource){
    this.UserAllCommunities = $resource(
            '/get-user-all-communities/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('profileService',function($resource){
    this.Profile = $resource(
            '/profile/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

minibeanMag.service('communitySearchPageService',function($resource){
    this.GetPostsFromIndex = $resource(
            '/searchForPosts/index/:query/:community_id/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{community_id:'@community_id',query:'@query',offset:'@offset'},isArray:true}
            }
    );
});

minibeanMag.service('communityPageService',function($resource){
    this.Community = $resource(
            '/community/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
    this.Posts = $resource(
            '/community/posts/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
    this.GetPosts = $resource(
            '/posts?id=:id&offset=:offset&time=:time',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id',offset:'@offset',time:'@time'},isArray:true}
            }
    );
    
    this.isNewsfeedEnabled = $resource(
            '/is-newsfeed-enabled-for-community/:community_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{community_id:'@community_id'}}
            }
    );
    
    this.toggleNewsfeedEnabled = $resource(
            '/toggle-newsfeed-enabled-for-community/:community_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{community_id:'@community_id'}}
            }
    );
});

minibeanMag.service('postManagementService',function($resource){
    this.deletePost = $resource(
            '/delete-post/:postId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{postId:'@postId'}}
            }
    );
    
    this.deleteComment = $resource(
            '/delete-comment/:commentId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{commentId:'@commentId'}}
            }
    );
});

minibeanMag.service('allCommentsService',function($resource){
    this.comments = $resource(
            '/comments/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'},isArray:true}
            }
    );
});

minibeanMag.service('communityJoinService',function($resource){
    this.sendJoinRequest = $resource(
            '/community/join/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
    this.leaveCommunity = $resource(
            '/community/leave/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

minibeanMag.service('iconsService',function($resource){
    this.getCommunityIcons = $resource(
            '/image/getCommunityIcons',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
    
    this.getEmoticons = $resource(
            '/image/getEmoticons',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

minibeanMag.service('searchMembersService',function($resource){
    this.getUnjoinedUsers = $resource(
            '/getAllUnjoinedMembers/:id/:query',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id',query : '@query'}, isArray:true}
            }
    );
    
    this.sendInvitationToNonMember = $resource(
            '/inviteToCommunity/:group_id/:user_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{group_id:'@group_id',user_id : '@user_id'}, isArray:true}
            }
    );
});

minibeanMag.service('bookmarkPostService', function($resource) {
    this.bookmarkPost = $resource(
            '/bookmark-post/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.unbookmarkPost = $resource(
            '/unbookmark-post/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.bookmarkArticle = $resource(
            '/bookmark-article/:article_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{article_id:'@article_id'}}
            }
    );
    
    this.unbookmarkArticle = $resource(
            '/unbookmark-article/:article_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{article_id:'@article_id'}}
            }
    );
});
    
minibeanMag.service('likeFrameworkService', function($resource) {

    this.hitWantAnswerOnQnA = $resource(
            '/want-ans/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitUnwantAnswerOnQnA = $resource(
            '/unwant-ans/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitLikeOnPost = $resource(
            '/like-post/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitUnlikeOnPost = $resource(
            '/unlike-post/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitLikeOnComment = $resource(
            '/like-comment/:comment_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitUnlikeOnComment = $resource(
            '/unlike-comment/:comment_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitLikeOnArticle = $resource(
            '/like-article/:article_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{article_id:'@article_id'}}
            }
    );
    
    this.hitUnlikeOnArticle = $resource(
            '/unlike-article/:article_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{article_id:'@article_id'}}
            }
    );
});

minibeanMag.service('communityQnAPageService',function($resource){
    this.QnAs = $resource(
            '/communityQnA/questions/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    this.GetQnAs = $resource(
            '/questions?id=:id&offset=:offset&time=:time',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id',offset:'@offset',time:'@time'},isArray:true}
            }
    );
});

minibeanMag.service('allAnswersService',function($resource){
    this.answers = $resource(
            '/answers/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'},isArray:true}
            }
    );
});

minibeanMag.service('articleCategoryService',function($resource){
    this.getAllArticleCategory = $resource(
            '/getAllArticleCategory',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

minibeanMag.service('allArticlesService',function($resource){
    this.AllArticles = $resource(
            '/get-all-Articles',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
    this.NewArticles = $resource(
            '/get-new-Articles',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
    this.HotArticles = $resource(
            '/get-hot-Articles',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
    this.RecommendedArticles = $resource(
            '/get-recommended-Articles',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );  
    this.ArticleCategorywise = $resource(
            '/get-Articles-Categorywise/:id/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
    this.SixArticles = $resource(
            '/get-Six-Articles',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('getDescriptionService',function($resource){
    this.GetDescription = $resource(
            '/getDescriptionOfArticle/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('allRelatedArticlesService',function($resource){
    this.getRelatedArticles = $resource(
            '/get-Related-Articles/:id/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

minibeanMag.service('deleteArticleService',function($resource){
    this.DeleteArticle = $resource(
            '/deleteArticle/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('showImageService',function($resource){
    this.getImage = $resource(
            '/get-image-url/:id',
            {alt:'json',callback:'JSON_CALLBACK', },
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('articleService',function($resource){
    this.ArticleInfo = $resource(
            '/article/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibeanMag.service('articleCommentsService',function($resource){
    this.comments = $resource(
            '/ArticleComments/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

minibeanMag.service('magazineNewsFeedService',function($resource){
    this.NewsFeeds = $resource(
            '/get-businessfeeds/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}}
            }
    );
});

minibeanMag.service('newsFeedService',function($resource){
    this.NewsFeeds = $resource(
            '/get-newsfeeds/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}}
            }
    );
});
      
minibeanMag.service('userNewsFeedService',function($resource){

    this.NewsFeedsPosts = $resource(
            '/get-user-newsfeeds-posts/:offset/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset',id:'@id'}}
            }
    );
    
    this.NewsFeedsComments = $resource(
            '/get-user-newsfeeds-comment/:offset/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset',id:'@id'}}
            }
    );
});

minibeanMag.service('bookmarkService',function($resource){
    this.bookmarkPost = $resource(
            '/get-bookmark-post/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}, isArray:true}
            }
    );
    
    this.bookmarkArticle = $resource(
            '/get-bookmark-article/:offsetA',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offsetA:'@offsetA'}, isArray:true}
            }
    );
    
    this.bookmarkSummary = $resource(
            '/get-bookmark-summary',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

minibeanMag.service('allConversationService',function($resource){
    this.UserAllConversation = $resource(
        '/get-all-Conversation',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get',isArray:true}
        }
    );
    
    this.startConversation = $resource(
        '/start-Conversation/:id',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get', isArray:true}
        }
    );
    
    this.deleteConversation = $resource(
        '/delete-Conversation/:id',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get', isArray:true}
        }
    );
});

minibeanMag.service('getMessageService',function($resource){
    this.getMessages = $resource(
        '/get-messages/:id/:offset',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get'}
        }
    );
});

minibeanMag.service('searchFriendService',function($resource){
    this.userSearch = $resource(
        '/user-friend-search?query=:q',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'GET', params:{q:'@q'}, isArray:true}
        }
    );
});