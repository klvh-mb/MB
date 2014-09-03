'use strict';

var minibean = angular.module('minibean');

minibean.service('magazineNewsFeedService',function($resource){
    this.NewsFeeds = $resource(
            '/get-businessfeeds/:offset/:cat',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset',cat:'@cat'}}
            }
    );
});
