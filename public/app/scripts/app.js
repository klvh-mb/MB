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
      .when('/post-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/postLandingPage.html',
        controller : 'PostLandingController'  
      })
      .when('/qna-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/qnaLandingPage.html',
        controller : 'QnALandingController'  
      })
      .when('/article/id/:id',{
    	templateUrl: '/assets/app/views/articlePage.html',
    	controller : 'ShowArticleController'  
      })
      .when('/article/show/:catid',{
    	templateUrl: '/assets/app/views/showArticlesPage.html',
    	controller : 'ShowArticleControllerNew'  
      })
      .when('/message',{
    	templateUrl: '/assets/app/views/message.html',
    	controller : 'UserConversationController'  
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

var minibean = angular.module('minibean');

//minibean.config(['$httpProvider', function($httpProvider) {
    //initialize get if not there
//    if (!$httpProvider.defaults.headers.get) {
//        $httpProvider.defaults.headers.get = {};    
//    }
    //disable IE ajax request caching
//    $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';
//    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache'; 
//    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
//}]);

minibean.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('noCacheInterceptor');
    }]).factory('noCacheInterceptor', function () {
            return {
                request: function (config) {
                    //console.log(config.method);
                    //console.log(config.url);
                    if(config.method=='GET'){
                        if (config.url.indexOf('template') === -1) {
                            var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                            config.url = config.url+separator+'noCache=' + new Date().getTime();
                        }
                    }
                    //console.log(config.method);
                    //console.log(config.url);
                    return config;
               }
           };
    });
    
DefaultValues = function() {



};
