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
  'wu.masonry'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/app/views/magazine/home.html'
      })
      .when('/view', {
        templateUrl: '/assets/app/views/magazine/home.html'
      })
      .when('/view/:cat', {
        templateUrl: '/assets/app/views/magazine/home.html'
      })
      .when('/article/show/:catId',{
        templateUrl: '/assets/app/views/magazine/showArticlesPage.html',
        controller : 'ShowArticlesController'  
      })
      .when('/article/:id/:catId',{
        templateUrl: '/assets/app/views/magazine/articlePage.html',
        controller : 'ArticlePageController'  
      })
      .when('/business/community/:id',{
        templateUrl: '/assets/app/views/business-community-page.html',
        controller : 'BusinessCommunityPageController'  
      })
      .when('/business/community/:id/:tab',{
        templateUrl: '/assets/app/views/business-community-page.html',
        controller : 'BusinessCommunityPageController'  
      })
      .when('/business-post-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/business-post-landing.html',
        controller : 'PostLandingController'  
      })
      .when('/error', {
          templateUrl: '/assets/app/views/errorPage.html',
      })
      .when('/ads', {
        templateUrl: '/assets/app/views/magazine/ads.html'
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
