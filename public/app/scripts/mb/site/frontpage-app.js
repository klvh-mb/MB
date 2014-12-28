'use strict';

angular.module('minibean', [
  'infinite-scroll',
  'ngResource',
  'ngRoute',
  'xeditable',
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
  'pasvaz.bindonce',
  'ui.utils'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/app/views/frontpage/frontpage.html', 
        controller : 'FrontpageController' 
      })
      .when('/campaign',{
        templateUrl: '/assets/app/views/frontpage/campaign-page.html',
        controller : 'CampaignPageController' 
      })
      .when('/campaign/:id',{
        templateUrl: '/assets/app/views/frontpage/campaign-page.html',
        controller : 'CampaignPageController' 
      })
      .when('/campaign/joiners/:id',{
        templateUrl: '/assets/app/views/frontpage/campaign-page-joiners.html',
        controller : 'CampaignPageJoinersController' 
      })
      .when('/communities-discover',{
        templateUrl: '/assets/app/views/frontpage/communities-discover-page.html'
      })
      .when('/communities-discover/:tab',{
        templateUrl: '/assets/app/views/frontpage/communities-discover-page.html'
      })
      .when('/community/:id',{
        templateUrl: '/assets/app/views/home/community-page.html',
        controller: 'CommunityPageController'  
      })
      .when('/community/:id/:tab',{
        templateUrl: '/assets/app/views/home/community-page.html',
        controller: 'CommunityPageController'  
      })
      .when('/post-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/home/post-landing.html',
        controller: 'PostLandingController'  
      })
      .when('/qna-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/home/qna-landing.html',
        controller: 'QnALandingController'  
      })
      .when('/error', {
          templateUrl: '/assets/app/views/error-page.html',
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
                            //console.log(config.method + " " + config.url);
                        }
                    }
                    return config;
               }
           };
    });
