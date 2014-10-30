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
  'angularMoment',
  'wu.masonry',
  'ui.utils'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/app/views/home.html'
      })
      .when('/my-magazine', {
        templateUrl: '/assets/app/views/my-magazine.html'
      })
      .when('/about',{
        templateUrl: '/assets/app/views/about-me.html',
        controller: 'UserAboutController'
      })
      .when('/about/:tab',{
    	templateUrl: '/assets/app/views/about-me.html',
    	controller: 'UserAboutController'
      })
      .when('/about-edit',{
        templateUrl: '/assets/app/views/about-me-edit.html',
        controller: 'UserAboutController'
      })
      .when('/profile/:id',{
    	templateUrl: '/assets/app/views/visit-profile.html',
    	controller: 'UserProfileController'  
      })
      .when('/communities-discover',{
        templateUrl: '/assets/app/views/communities-discover-page.html',
      })
      .when('/community/:id',{
        templateUrl: '/assets/app/views/community-page.html',
        controller: 'CommunityPageController'  
      })
      .when('/community/:id/:tab',{
    	templateUrl: '/assets/app/views/community-page.html',
    	controller: 'CommunityPageController'  
      })
      .when('/editCommunity/:id',{
        templateUrl: '/assets/app/views/edit-community.html',
        controller: 'EditCommunityController'
      })
      .when('/post-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/post-landing.html',
        controller: 'PostLandingController'  
      })
      .when('/qna-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/qna-landing.html',
        controller: 'QnALandingController'  
      })
      .when('/business/community/:id',{
        templateUrl: '/assets/app/views/business-community-page.html',
        controller: 'BusinessCommunityPageController'  
      })
      .when('/business/community/:id/:tab',{
        templateUrl: '/assets/app/views/business-community-page.html',
        controller: 'BusinessCommunityPageController'  
      })
      .when('/business-post-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/business-post-landing.html',
        controller: 'PostLandingController'  
      })
      .when('/article/:id/:catId',{
    	templateUrl: '/assets/app/views/magazine/articlePage.html',
    	controller: 'ArticlePageController'  
      })
      .when('/article/show/:catId',{
    	templateUrl: '/assets/app/views/magazine/showArticlesPage.html',
    	controller: 'ShowArticlesController'  
      })
      .when('/message/:id',{
    	templateUrl: '/assets/app/views/message.html',
    	controller: 'UserConversationController'  
      })
      .when('/bpoints',{
        templateUrl: '/assets/app/views/bpoints.html',
        controller: 'GameController'
      })
      .when('/bpoints/rules',{
        templateUrl: '/assets/app/views/bpoints-rules.html',
        controller: 'GameController'
      })
      .when('/edm-subscription-settings',{
        templateUrl: '/assets/app/views/edm-subscription-settings.html',
        controller : 'SubscriptionController'  
      })
      .when('/privacy-settings',{
        templateUrl: '/assets/app/views/privacy-settings.html',
        controller : 'PrivacySettingsController'  
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

//
// noCache for browser
//

var minibean = angular.module('minibean');

var URL_IGNORE = [
    "tracking",
    "template", 
    "assets", 
    "image", 
    "photo", 
    "modal"
];

minibean.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('noCacheInterceptor');
    }]).factory('noCacheInterceptor', function () {
            return {
                request: function (config) {
                    //console.log(config.method + " " + config.url);
                    if(config.method=='GET'){
                        var url = config.url.toLowerCase();
                        var containsUrlIgnore = false;
                        for (var i in URL_IGNORE) {
                            if (url.indexOf(URL_IGNORE[i]) != -1) {
                                containsUrlIgnore = true;
                            }
                        }
                        if (!containsUrlIgnore) {
                            var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                            config.url = config.url+separator+'noCache=' + new Date().getTime();
                            console.log(config.method + " " + config.url);
                        }
                    }
                    return config;
               }
           };
    });
    
//minibean.config(['$httpProvider', function($httpProvider) {
//    if (!$httpProvider.defaults.headers.get) {
//        $httpProvider.defaults.headers.get = {};    
//    }
//    $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';
//    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache'; 
//    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
//}]);