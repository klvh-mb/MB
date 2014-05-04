'use strict';

angular.module('minibean', [
  'infinite-scroll',
  'ngResource',
  'ngRoute',
  'xeditable',
  'ngAnimate',
  'ui.bootstrap',
  'ui.bootstrap.tpls',
  'angularFileUpload',
  'ui.bootstrap.datetimepicker',
  'validator',
  'validator.rules',
  'angularSpinner',
  'truncate',
  'ui.tinymce'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/app/views/home.html'
      })
      .when('/article/id/:id',{
    	templateUrl: '/assets/app/views/articlePage.html',
    	controller : 'ViewArticleController'  
      })
      .when('/article/create',{
    	templateUrl: '/assets/app/views/createArticlePage.html',
    	controller : 'CreateArticleController'  
      })
      .when('/article/edit/:id',{
    	templateUrl: '/assets/app/views/editArticlePage.html',
    	controller : 'EditArticleController'  
      })
      .when('/article/show',{
    	templateUrl: '/assets/app/views/showArticlesPage.html',
    	controller : 'ShowArticleController'  
      })
      .otherwise({
          redirectTo: '/'
      });
  })
  .run(function(editableOptions) {
  editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});

