'use strict';

var minibeanPromo = angular.module('minibeanPromo');

//
// Controllers
//

minibeanPromo.controller('PromoController', function($scope, $routeParams, $location) {
    log("PromoController starts");

    log("PromoController completed");
});

minibeanPromo.controller('PromoPNController',function($scope, $routeParams, $http, promoPNService) {
    log("PromoPNController starts");

    $scope.pnHome = false;
    if ($routeParams.id == undefined) {
        $scope.pnHome = true;
    }
    
    $scope.pnCommunities = promoPNService.PNCommunities.get();

    log("PromoPNController completed");
});

minibeanPromo.controller('ViewCommunityPNController',function($scope, $routeParams, $http, promoPNService) {
    log("ViewCommunityPNController starts");

    $scope.tagDistricts = function(data) {
        var curDistrict = '';
        var tagColorIndex = -1;
        angular.forEach(data, function(request, key){
            if (curDistrict == '' || curDistrict != request.dis) {
                curDistrict = request.dis;
                tagColorIndex++;
                //log(curDistrict + ":" + DefaultValues.tagColors[tagColorIndex]);
            }
            request.tagc = DefaultValues.tagColors[tagColorIndex];
        });
    }
    
    $scope.pns = promoPNService.PNs.get({id:$routeParams.id}, $scope.tagDistricts);

    log("ViewCommunityPNController completed");
});

minibeanPromo.controller('ViewCommunityPageController', function($scope, $routeParams, $http, viewCommunityPageService, usSpinnerService){
    
    log("ViewCommunityPageController starts");

    $scope.isPNCommunity = true;
    $scope.show = false;
    
    $scope.selectedTab = 1;
    $scope.selectedSubTab = 1;
    
    $scope.$on('$viewContentLoaded', function() {
        usSpinnerService.spin('loading...');
    });
    
    $scope.community = viewCommunityPageService.Community.get({id:$routeParams.id}, function(data){
        usSpinnerService.stop('loading...');
        
        // make sure it's PN community
        if (data.ttyp != 'PRE_NURSERY') {
            $scope.community = [];
            $scope.isPNCommunity = false;
        }
    });
    
    $scope.showImage = function(imageId) {
        $scope.img_id = imageId;
    }
    
    $scope.coverImage = "/image/get-cover-community-image-by-id/" + $routeParams.id;
    
    log("ViewCommunityPageController completed");
});

minibeanPromo.controller('ViewCommunityQnAController', function($scope, $routeParams, $http, viewCommunityPageService, allAnswersService, usSpinnerService){
    log("ViewCommunityQnAController starts");
    
    var firstBatchLoaded = false;
    var offsetq = 0;
    var time = 0;
    var noMore = false;
    
    $scope.QnAs = viewCommunityPageService.QnAs.get({id:$routeParams.id}, function(){
        firstBatchLoaded = true;
        usSpinnerService.stop('loading...');
    });
    
    $scope.nextPosts = function() {
        if ($scope.isBusy) return;
        if (!firstBatchLoaded) return;
        if (noMore) return;
        $scope.isBusy = true;
        if(angular.isObject($scope.QnAs.posts) && $scope.QnAs.posts.length > 0) {
            time = $scope.QnAs.posts[$scope.QnAs.posts.length - 1].t;
        }
        viewCommunityPageService.GetQnAs.get({id:$routeParams.id,offset:offsetq,time:time}, function(data){
            var posts = data;
            if(data.length == 0) {
                noMore = true;
            }
            
            for (var i = 0; i < posts.length; i++) {
                $scope.QnAs.posts.push(posts[i]);
            }
            $scope.isBusy = false;
            offsetq++;
        });
    }
    
    $scope.get_all_answers = function(id) {
        angular.forEach($scope.QnAs.posts, function(post, key){
            if(post.id == id) {
                post.cs = allAnswersService.answers.get({id:id});
                post.ep = true;
            }
        });
    }
    $scope.get_all_comments = $scope.get_all_answers;
    
    log("ViewCommunityQnAController completed");
});

minibeanPromo.controller('ViewCommunityPostController', function($scope, $routeParams, $http, viewCommunityPageService, allCommentsService, usSpinnerService){
    log("ViewCommunityPostController starts");

    var firstBatchLoaded = false;
    var offset = 0;
    var time = 0;
    var noMore = false;
    
    $scope.posts = viewCommunityPageService.Posts.get({id:$routeParams.id}, function(){
        firstBatchLoaded = true;
        usSpinnerService.stop('loading...');
    });
    
    $scope.nextPosts = function() {
        if ($scope.isBusy) return;
        if (!firstBatchLoaded) return;
        if (noMore) return;
        $scope.isBusy = true;
        if(angular.isObject($scope.posts.posts) && $scope.posts.posts.length > 0) {
            time = $scope.posts.posts[$scope.posts.posts.length - 1].t;
        }
        viewCommunityPageService.GetPosts.get({id:$routeParams.id,offset:offset,time:time}, function(data){
            var posts = data;
            if(data.length == 0) {
                noMore = true;
            }
            
            for (var i = 0; i < posts.length; i++) {
                $scope.posts.posts.push(posts[i]);
            }
            $scope.isBusy = false;
            offset++;
        });
    }
    
    $scope.get_all_comments = function(id) {
        angular.forEach($scope.posts.posts, function(post, key){
            if(post.id == id) {
                post.cs = allCommentsService.comments.get({id:id});
                post.ep = true;
            }
        });
    }

    log("ViewCommunityPostController completed");
});

//
// Services
//

minibeanPromo.service('promoPNService',function($resource){
    this.PNCommunities = $resource(
            '/get-promo-pn-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', isArray:true}
            }
    );
    this.PNs = $resource(
            '/get-promo-pns/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'},isArray:true}
            }
    );
});

minibeanPromo.service('viewCommunityPageService',function($resource){
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

minibeanPromo.service('allAnswersService',function($resource){
    this.answers = $resource(
            '/answers/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'},isArray:true}
            }
    );
});

minibeanPromo.service('allCommentsService',function($resource){
    this.comments = $resource(
            '/comments/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'},isArray:true}
            }
    );
});

