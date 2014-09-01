'use strict';

var minibeanMag = angular.module('minibeanMag');

minibeanMag.controller('UIController', function($scope, $location, $anchorScroll, $window) {
    $scope.gotoTop = function() {
        // set the location.hash to the id of
        // the element you wish to scroll to
        $window.scrollTo($window.pageXOffset, 0);
    };
});

minibeanMag.controller('AnnouncementsWidgetController',function($scope, $http, announcementsService) {
    log("AnnouncementsWidgetController starts");
    
    $scope.announcements = announcementsService.getGeneralAnnouncements.get();
    
    log("AnnouncementsWidgetController completed");
});

minibeanMag.controller('TodayWeatherInfoController',function($scope, $http, todayWeatherInfoService) {
    log("TodayWeatherInfoController starts");
    
    $scope.todayWeatherInfo = todayWeatherInfoService.getTodayWeatherInfo.get();
    
    log("TodayWeatherInfoController completed");
});

minibeanMag.controller('MagazineController', function($scope, $routeParams, $location) {
    log("MagazineController starts");

    log("MagazineController completed");
});

minibeanMag.controller('MagazineNewsFeedController', function($scope, postManagementService, bookmarkPostService, likeFrameworkService, $timeout, $upload, $http, allCommentsService, usSpinnerService, magazineNewsFeedService, iconsService) {
    log("MagazineNewsFeedController starts");
    
    $scope.newsFeeds = { posts: [] };
    
    $scope.displayLink = function(link) {
        var link = $scope.applicationInfo.baseUrl + link;
        
        bootbox.dialog({
            message: 
                "<input type='text' name='post-link' id='post-link' value="+link+"></input>" + 
                "<a style='margin-left:5px;padding:2px 7px;font-size:14px;' class='toolsbox toolsbox-single' onclick='highlightLink(\"post-link\")'><i class='glyphicon glyphicon-link'></i></a>",
            title: "",
            className: "post-bootbox-modal post-copy-link-modal",
        });
    }
    
    $scope.deletePost = function(postId) {
        postManagementService.deletePost.get({"postId":postId}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == postId) {
                    $scope.newsFeeds.posts.splice($scope.newsFeeds.posts.indexOf(post),1);
                }
            })
        });
    }
    
    $scope.bookmarkPost = function(post_id) {
        bookmarkPostService.bookmarkPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == post_id) {
                    post.isBookmarked = true;
                }
            })
        });
    }
    
    $scope.unBookmarkPost = function(post_id) {
        bookmarkPostService.unbookmarkPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == post_id) {
                    post.isBookmarked = false;
                }
            })
        });
    }
    
    $scope.want_answer = function(post_id) {
        likeFrameworkService.hitWantAnswerOnQnA.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == post_id) {
                    post.isWtAns=true;
                    post.nowa++;
                }
            })
        });
    }
    
    $scope.unwant_answer = function(post_id) {
        likeFrameworkService.hitUnwantAnswerOnQnA.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == post_id) {
                    post.isWtAns=false;
                    post.nowa--;
                }
            })
        });
    }
    
    $scope.like_post = function(post_id) {
        likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == post_id) {
                    post.isLike=true;
                    post.nol++;
                }
            })
        });
    }
    
    $scope.unlike_post = function(post_id) {
        likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == post_id) {
                    post.isLike=false;
                    post.nol--;
                }
            })
        });
    }

    $scope.like_comment = function(post_id,comment_id) {
        likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == post_id) {
                    angular.forEach(post.cs, function(comment, key){
                        if(comment.id == comment_id) {
                            comment.nol++;
                            comment.isLike=true;
                        }
                    })
                }
            })
        });
    }
    
    $scope.unlike_comment = function(post_id,comment_id) {
        likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == post_id) {
                    angular.forEach(post.cs, function(comment, key){
                        if(comment.id == comment_id) {
                            comment.nol--;
                            comment.isLike=false;
                        }
                    })
                }
            })
        });
    }
    
    $scope.get_all_comments = function(id) {
        angular.forEach($scope.newsFeeds.posts, function(post, key){
            if(post.id == id) {
                post.cs = allCommentsService.comments.get({id:id});
                post.ep = true;
            }
        });
    }

    var noMore = false;
    var offset = 0;
    $scope.nextNewsFeeds = function() {
        if ($scope.isBusy) return;
        if (noMore) return;
        $scope.isBusy = true;
        magazineNewsFeedService.NewsFeeds.get({offset:offset},
            function(data){
                var posts = data.posts;
                if(posts.length == 0) {
                    noMore = true;
                }
                
                for (var i = 0; i < posts.length; i++) {
                    $scope.newsFeeds.posts.push(posts[i]);
                }
                $scope.isBusy = false;
                offset=offset + 1;
            }
        );
    }

    log("MagazineNewsFeedController completed");
});
