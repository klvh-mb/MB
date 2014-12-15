'use strict';

var minibeanPromo = angular.module('minibeanPromo');

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

