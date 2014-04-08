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
  'angularSpinner'
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
      .otherwise({
          redirectTo: '/'
      });
  })
  .run(function(editableOptions) {
  editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});

