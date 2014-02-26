'use strict';

angular.module('htdocsApp')
  .controller('MainCtrl', function ($scope, Posts) {
    $scope.post = new Posts();
  })
  .factory('Reddit', function($http) {
    var Reddit = function() {
    this.items = [];
    this.busy = false;
    this.after = '';
    };

    Reddit.prototype.nextPage = function() {
      if (this.busy) return;
      this.busy = true;

      var url = "http://api.reddit.com/hot?after=" + this.after + "&jsonp=JSON_CALLBACK";
      $http.jsonp(url).success(function(data) {
        var items = data.data.children;
        for (var i = 0; i < items.length; i++) {
          this.items.push(items[i].data);
        }
        this.after = "t3_" + this.items[this.items.length - 1].id;
        this.busy = false;
      }.bind(this));
    };

    return Reddit;
  })
  .factory('Posts', function($http) {
    var Posts = function() {
      this.items = [];
      this.busy = false;
      this.offset = 0;
      this.limit = 15;
    };

    Posts.prototype.nextPage = function() {
      if (this.busy) return;
      this.busy = true;

      var url = "get-post?offset=" + this.offset + "&limit=" + this.limit ;
      $http.get(url).success(function(data) {
        console.log(data);
        for (var i = 0; i < data.length; i++) {
          this.items.push(data[i]);
        }
        this.offset = this.offset + this.items.length;
        this.busy = false;
      }.bind(this));
    };

    return Posts;
  })
  
