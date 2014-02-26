'use strict';

angular.module('htdocsApp', [
  'infinite-scroll',
  'ngResource',
  'ngRoute'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/app/views/main.html',
        controller: 'MainCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
