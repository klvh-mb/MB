'use strict';

var minibean = angular.module('minibean');


minibean.service('searchService',function($resource){
	this.Search = $resource(
			'/search/:q',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'JSON'}
			}
	);
});

minibean.controller('SearchController',function($scope,searchService){
	$scope.search= function() {
		this.searchresult = searchService.Search.get({query:this.query});
	}  
});






  
