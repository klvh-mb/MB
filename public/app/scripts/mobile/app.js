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
        templateUrl: '/assets/app/views/mobile/home.html'
      })
      .when('/about/:tab',{
    	templateUrl: '/assets/app/views/mobile/about-me.html',
    	controller: 'UserAboutController'
      })
      .when('/profile/:id',{
    	templateUrl: '/assets/app/views/mobile/visit-profile.html',
    	controller : 'ProfileController'  
      })
      .when('/community/:id/:tab',{
    	templateUrl: '/assets/app/views/mobile/community-page.html',
    	controller : 'CommunityPageController'  
      })
      .when('/post-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/post-landing.html',
        controller : 'PostLandingController'  
      })
      .when('/qna-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/qna-landing.html',
        controller : 'QnALandingController'  
      })
      .when('/article/id/:id',{
    	templateUrl: '/assets/app/views/mobile/articlePage.html',
    	controller : 'ShowArticleController'  
      })
      .when('/article/show/:catid',{
    	templateUrl: '/assets/app/views/mobile/showArticlesPage.html',
    	controller : 'ShowArticleControllerNew'  
      })
      .when('/message/:id',{
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

//
// noCache for browser
//

var minibean = angular.module('minibean');

var URL_IGNORE = [
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