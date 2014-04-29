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
  'ui.tinymce',
  'ui.utils',
  'ngSanitize',
  'angularMoment'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/app/views/home.html'
      })
      .when('/about',{
    	templateUrl: '/assets/app/views/about.html',
    	controller: 'UserAboutController'
      })
      .when('/friends',{
    	templateUrl: '/assets/app/views/allFriends.html',
    	controller : 'FriendsController'
      })
      .when('/myGroups/:type',{
    	templateUrl: '/assets/app/views/myAllCommunities.html',
    	controller : 'CommunityWidgetController'
      })
      .when('/editCommunity/:id',{
    	templateUrl: '/assets/app/views/edit-community.html',
    	controller : 'GroupController'
      })
      .when('/profile/:id',{
    	templateUrl: '/assets/app/views/visit-profile.html',
    	controller : 'ProfileController'  
      })
      .when('/community/:id',{
    	templateUrl: '/assets/app/views/communityPage.html',
    	controller : 'CommunityPageController'  
      })
      .when('/article/id/:id',{
    	templateUrl: '/assets/app/views/articlePage.html',
    	controller : 'EditArticleController'  
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

