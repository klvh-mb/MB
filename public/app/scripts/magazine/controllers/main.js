'use strict';

var minibean = angular.module('minibean');

minibean.controller('MagazineNewsFeedController', function($scope, $timeout, $upload, $http, $routeParams,  
    bookmarkPostService, likeFrameworkService, postManagementService, allCommentsService, magazineNewsFeedService, iconsService, usSpinnerService) {
    
    log("MagazineNewsFeedController starts");
    
    var cat = $routeParams.cat;
    if (cat == undefined) {
        cat = 0;
    }
    $scope.selectNavBar('MAGAZINE');
    $scope.selectNavSubBar(cat);
    
    $scope.newsFeeds = { posts: [] };

    var noMore = false;
    var offset = 0;
    $scope.nextNewsFeeds = function() {
        if ($scope.isBusy) return;
        if (noMore) return;
        $scope.isBusy = true;
        magazineNewsFeedService.NewsFeeds.get({offset:offset,cat:cat},
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

    //
    // Post management
    //
    
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
                    post.nol--;
                    post.isLike=false;
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
    
    $scope.comment_on_post = function(id, commentText) {
        // first convert to links
        commentText = convertText(commentText);

        var data = {
            "post_id" : id,
            "commentText" : commentText,
            "withPhotos" : $scope.commentSelectedFiles.length != 0
        };
        var post_data = data;
        usSpinnerService.spin('loading...');
        $http.post('/community/post/comment', data) 
            .success(function(comment_id) {
                $('.commentBox').val('');
                
                $scope.commentText = "";
                angular.forEach($scope.newsFeeds.posts, function(post, key){
                        if(post.id == data.post_id) {
                            post.n_c++;
                            post.ut = new Date();
                            var comment = {"oid" : $scope.newsFeeds.lu, "d" : commentText, "on" : $scope.newsFeeds.lun,
                                    "isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
                            post.cs.push(comment);
                            
                            if($scope.commentSelectedFiles.length == 0) {
                                return;
                            }
                            
                            $scope.commentSelectedFiles = [];
                            $scope.commentDataUrls = [];
                            
                            // when post is done in BE then do photo upload
                            //log($scope.commentTempSelectedFiles.length);
                            for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
                                usSpinnerService.spin('loading...');
                                $upload.upload({
                                    url : '/image/uploadCommentPhoto',
                                    method: $scope.httpMethod,
                                    data : {
                                        commentId : comment_id
                                    },
                                    file: $scope.commentTempSelectedFiles[i],
                                    fileFormDataName: 'comment-photo'
                                }).success(function(data, status, headers, config) {
                                    $scope.commentTempSelectedFiles.length = 0;
                                    if(post.id == post_data.post_id) {
                                        angular.forEach(post.cs, function(cmt, key){
                                            if(cmt.id == comment_id) {
                                                cmt.hasImage = true;
                                                if(cmt.imgs) {
                                                    
                                                } else {
                                                    cmt.imgs = [];
                                                }
                                                cmt.imgs.push(data);
                                            }
                                        });
                                    }
                                });
                        }
                    }
            });
            usSpinnerService.stop('loading...');    
        });
    };
    
    log("MagazineNewsFeedController completed");
});
