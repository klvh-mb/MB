'use strict';

var minibean = angular.module('minibean');

minibean.controller('CreateArticleController',function($scope,$http, articleCategoryService){
	$scope.article;
	//Refer to http://www.tinymce.com/
	$scope.tinymceOptions = {
			selector: "textarea",
		    plugins: [
		        "advlist autolink lists link image charmap print preview anchor",
		        "searchreplace visualblocks code fullscreen",
		        "insertdatetime media table contextmenu paste"
		    ],
		    toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
	}
	
	var range = [];
	for(var i=0;i<100;i++) {
		  range.push(i);
	}
	$scope.targetAge = range;
	
	$scope.articleCategories = articleCategoryService.getAllArticleCategory.get();
	
	$scope.select_category = function(id, name, pn) {
		$scope.category_id = id;
		$scope.category_picture = pn;
		$scope.category_name = name;
		$scope.formData.category_id= id;
		$scope.isChosen = true;
	}
	$scope.submit = function() {
				$http.post('/createArticle', $scope.formData);
	}
});

minibean.service('articleCategoryService',function($resource){
	this.getAllArticleCategory = $resource(
			'/getAllArticleCategory',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
});


minibean.service('allArticlesService',function($resource){
	this.AllArticles = $resource(
			'/get-all-Articles',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
});

minibean.service('getDescriptionService',function($resource){
	this.GetDescription = $resource(
			'/getDescriptionOdArticle/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('deleteArticleService',function($resource){
	this.DeleteArticle = $resource(
			'/deleteArticle/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('ShowArticleController',function($scope, $modal, deleteArticleService, allArticlesService, getDescriptionService){
	$scope.result = allArticlesService.AllArticles.get();
	
	$scope.open = function (id) {
	    var modalInstance = $modal.open({
	      templateUrl: 'myModalContent.html',
	    });
	    var msg = getDescriptionService.GetDescription.get({id:id}	, function(data) {
	    	console.log(data.description);
	    	$('.modal-body').html(data.description);
	    });
	    
	  };
	 
	  
	  $scope.deleteArticle = function (id){
		  deleteArticleService.DeleteArticle.get({id :id}, function(data){
			  
			  angular.forEach($scope.result, function(request, key){
					if(request.id == id) {
						$scope.result.splice($scope.result.indexOf(request),1);
					}
				});
			  
		  });
	  }
	  
});


minibean.service('ArticleService',function($resource){
	this.ArticleInfo = $resource(
			'/Article/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});


minibean.controller('EditArticleController',function($scope,$routeParams, ArticleService,articleCategoryService,$http,usSpinnerService){
	$scope.submitBtn = "Save";
	$scope.article = ArticleService.ArticleInfo.get({id:$routeParams.id});
	$scope.articleCategorys = articleCategoryService.getAllArticleCategory.get();
	
	var range = [];
	for(var i=0;i<100;i++) {
		  range.push(i);
	}
	$scope.targetAge = range;
	
	$scope.tinymceOptions = {
			selector: "textarea",
		    plugins: [
		        "advlist autolink lists link image charmap print preview anchor",
		        "searchreplace visualblocks code fullscreen",
		        "insertdatetime media table contextmenu paste"
		    ],
		    toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
	}
	
	$scope.select_category = function(id, name, pn) {
		$scope.article.category.id = id;
		$scope.article.category.name = name;
		$scope.article.category.pictureName = pn;
		$scope.isChosen = true;
	}
	
	$scope.updateArticleData = function(data) {
		usSpinnerService.spin('loading...');
		return $http.post('/editArticle', $scope.article).success(function(data){
			$scope.submitBtn = "Done";
			usSpinnerService.stop('loading...');
		});
	}
});

	  




