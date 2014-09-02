'use strict';

var minibean = angular.module('minibean');

minibean.controller('MagazineNewsFeedController', function($scope, $timeout, $upload, $http, 
    bookmarkPostService, likeFrameworkService, allCommentsService, usSpinnerService, magazineNewsFeedService, iconsService) {
    
    log("MagazineNewsFeedController starts");
    
    $scope.newsFeeds = { posts: [] };

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
