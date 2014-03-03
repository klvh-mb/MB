'use strict';

angular.module('minibean', [
  'infinite-scroll',
  'ngResource',
  'ngRoute'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '/assets/app/views/home.html'
      })
  });
