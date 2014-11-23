'use strict';

var minibean = angular.module('minibean');

minibean.service('headerBarMetadataService',function($resource){
	this.headerBardata = $resource(
			'/get-headerBar-data',
	        {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
 	);
});

minibean.service('userSettingsService',function($resource){
    this.privacySettings = $resource(
            '/get-privacy-settings',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.edmSettings = $resource(
            '/get-edm-settings',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );    
});

minibean.service('subscriptionService',function($resource){
    this.allUnsubscriptions = $resource(
            '/get-all-unsubscriptions',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.unsubscribe = $resource(
            '/unsubscribe/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('announcementsService',function($resource) {
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

minibean.service('todayWeatherInfoService',function($resource) {
    this.getTodayWeatherInfo = $resource(
            '/get-today-weather-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('gameService',function($resource) {
    this.signInForToday = $resource(
            '/sign-in-for-today',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('locationService',function($resource){
    this.getAllDistricts = $resource(
            '/get-all-districts',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

minibean.service('postLandingService',function($resource){
    this.postLanding = $resource(
            '/post-landing/:id/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{id:'@id',communityId:'@communityId'}}
            }
    );
});

minibean.service('qnaLandingService',function($resource){
    this.qnaLanding = $resource(
            '/qna-landing/:id/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{id:'@id',communityId:'@communityId'}}
            }
    );
});

///////////////////////// Search Service Start //////////////////////////////////
minibean.service('searchService',function($resource){
    this.userSearch = $resource(
            '/user-search?query=:q',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{q:'@q'}, isArray:true}
            }
    );
});

minibean.service('sendInvitation',function($resource){
    this.inviteFriend = $resource(
            '/send-invite?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

minibean.service('unFriendService',function($resource){
    this.doUnfriend = $resource(
            '/un-friend?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

minibean.service('applicationInfoService',function($resource){
    this.ApplicationInfo = $resource(
            '/get-application-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

minibean.service('userInfoService',function($resource){
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
    this.CompleteHomeTour = $resource(
            '/complete-home-tour',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

minibean.service('acceptFriendRequestService',function($resource){
    this.acceptFriendRequest = $resource(
            '/accept-friend-request?friend_id=:id&notify_id=:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id',notify_id:'@notify_id'}, isArray:true}
            }
    );
});

minibean.service('acceptJoinRequestService',function($resource){
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

minibean.service('notificationMarkReadService',function($resource){
    this.markAsRead = $resource(
            '/mark-as-read/:notify_ids',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{notify_ids:'@notify_ids'}, isArray:true}
            }
    );

    this.ignoreIt = $resource(
            '/ignore-it/:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
            }
    );
});

minibean.service('userAboutService',function($resource){
    this.UserAbout = $resource(
            '/about-user',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('profilePhotoModal',function( $modal){
    
    this.OpenModal = function(arg, successCallback) {
        this.instance = $modal.open(arg);
        this.onSuccess = successCallback;
    }
    
    this.CloseModal = function() {
        this.instance.dismiss('close');
        this.onSuccess();
    }
});

minibean.service('editCommunityPageService',function($resource){
    this.EditCommunityPage = $resource(
            '/editCommunity/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

minibean.service('membersWidgetService',function($resource){
    this.CommunityMembers = $resource(
            '/get-community-members/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('pnService',function($resource){
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
    this.RequestUpdate = $resource(
            '/pn-request-update/:name/:districtId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{name:'@name',districtId:'@districtId'}}
            }
    );
});

minibean.service('unJoinedCommunityWidgetService',function($resource){
    this.UnJoinedCommunities = $resource(
            '/get-unjoined-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('sendJoinRequest',function($resource){
    this.sendRequest = $resource(
            '/send-request?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

minibean.service('friendsService',function($resource){
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

minibean.service('sendJoinRequest',function($resource){
    this.sendRequest = $resource(
            '/send-request?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

minibean.service('communitiesDiscoverService',function($resource){
    this.ZodiacYearCommunities = $resource(
            '/get-zodiac-year-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.ZodiacYearMonthCommunities = $resource(
            '/get-zodiac-year-month-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.DistrictCommunities = $resource(
            '/get-district-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.TopicCommunities = $resource(
            '/get-topic-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.OtherCommunities = $resource(
            '/get-other-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('communityWidgetService',function($resource){
    this.MyCommunities = $resource(
            '/get-my-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('communityWidgetByUserService',function($resource){
    this.UserCommunities = $resource(
            '/get-user-communities/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('profileService',function($resource){
    this.Profile = $resource(
            '/profile/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

minibean.service('communitySearchPageService',function($resource){
    this.GetPostsFromIndex = $resource(
            '/searchForPosts/index/:query/:community_id/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{community_id:'@community_id',query:'@query',offset:'@offset'},isArray:true}
            }
    );
});

minibean.service('communityPageService',function($resource){
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

minibean.service('postManagementService',function($resource){
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

minibean.service('allCommentsService',function($resource){
    this.comments = $resource(
            '/comments/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'},isArray:true}
            }
    );
});

minibean.service('communityJoinService',function($resource){
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

minibean.service('iconsService',function($resource){
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

minibean.service('searchMembersService',function($resource){
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

minibean.service('bookmarkPostService', function($resource) {
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
    
minibean.service('likeFrameworkService', function($resource) {

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

minibean.service('communityQnAPageService',function($resource){
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

minibean.service('allAnswersService',function($resource){
    this.answers = $resource(
            '/answers/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'},isArray:true}
            }
    );
});

minibean.service('communityCategoryService',function($resource){
    this.getAllBusinessCommunityCategories = $resource(
            '/get-all-business-community-categories',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.getAllSocialCommunityCategories = $resource(
            '/get-all-social-community-categories',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.getSocialCommunityCategoriesMap = $resource(
            '/get-social-community-categories-map?indexOnly=:indexOnly',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{indexOnly:'@indexOnly'},isArray:true}
            }
    );
});

minibean.service('tagwordService',function($resource){
    this.HotArticlesTagwords = $resource(
            '/get-hot-articles-tagwords',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.SoonMomsTagwords = $resource(
            '/get-soon-moms-tagwords',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.ClickTagword = $resource(
            '/click-tagword/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});
    
minibean.service('articleService',function($resource){
    this.AllArticleCategories = $resource(
            '/get-all-article-categories',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.AllArticles = $resource(
            '/get-all-Articles',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.NewArticles = $resource(
            '/get-new-Articles/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.HotArticles = $resource(
            '/get-hot-Articles/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.RecommendedArticles = $resource(
            '/get-recommended-Articles/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );  
    this.ArticleCategorywise = $resource(
            '/get-Articles-Categorywise/:category_id/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.ArticlesByTagword = $resource(
            '/get-Articles-TagWise/:tagword_id/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.SixArticles = $resource(
            '/get-Six-Articles/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.getRelatedArticles = $resource(
            '/get-Related-Articles/:id/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.ArticleInfo = $resource(
            '/get-article-info/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('showImageService',function($resource){
    this.getImage = $resource(
            '/get-image-url/:id',
            {alt:'json',callback:'JSON_CALLBACK', },
            {
                get: {method:'get'}
            }
    );
});

minibean.service('articleCommentsService',function($resource){
    this.comments = $resource(
            '/ArticleComments/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

minibean.service('myMagazineNewsFeedService',function($resource){
    this.NewsFeeds = $resource(
            '/get-my-businessfeeds/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}}
            }
    );
});

minibean.service('newsFeedService',function($resource){
    this.NewsFeeds = $resource(
            '/get-newsfeeds/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}}
            }
    );
});
      
minibean.service('userNewsFeedService',function($resource){

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

minibean.service('bookmarkService',function($resource){
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

minibean.service('allConversationService',function($resource){
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

minibean.service('getMessageService',function($resource){
    this.getMessages = $resource(
        '/get-messages/:id/:offset',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get'}
        }
    );
});

minibean.service('searchFriendService',function($resource){
    this.userSearch = $resource(
        '/user-friend-search?query=:q',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'GET', params:{q:'@q'}, isArray:true}
        }
    );
});