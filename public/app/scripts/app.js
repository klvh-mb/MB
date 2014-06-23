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
      .when('/about/:tab',{
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
      .when('/community/:id/:tab',{
    	templateUrl: '/assets/app/views/communityPage.html',
    	controller : 'CommunityPageController'  
      })
      .when('/article/id/:id',{
    	templateUrl: '/assets/app/views/articlePage.html',
    	controller : 'ShowArticleController'  
      })
      .when('/article/show/:catid',{
    	templateUrl: '/assets/app/views/showArticlesPage.html',
    	controller : 'ShowArticleControllerNew'  
      })
      .when('/error', {
    	  templateUrl: '/assets/app/views/errorPage.html',
      })
      .otherwise({
          redirectTo: '/'
      });
  })
  .run(function(editableOptions) {
  editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
  });

