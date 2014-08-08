'use strict';

var minibeanPromo = angular.module('minibeanPromo');

minibeanPromo.controller('PromoController', function($scope, $routeParams, $location) {
    log("PromoController starts");

    log("PromoController completed");
});

minibeanPromo.controller('PNController',function($scope, $routeParams, $http, pnService) {
    log("PNController starts");

    if ($routeParams.region == 'hk') {
        $scope.selectedTab = 1;
    } else if ($routeParams.region == 'kl') {
        $scope.selectedTab = 2;
    } else if ($routeParams.region == 'nt') {
        $scope.selectedTab = 3;
    } else if ($routeParams.region == 'is') {
        $scope.selectedTab = 4;
    } else {
        $scope.selectedTab = 1;
    }
    
    // TODO: hardcoded!!!
    //var hk_commId = 38;
    //var kl_commId = 39;
    //var nt_commId = 40;
    //var is_commId = 41;
    
    var hk_commId = 49;
    var kl_commId = 50;
    var nt_commId = 51;
    var is_commId = 53;
    
    $scope.tagDistricts = function(data) {
        var curDistrict = '';
        var tagColorIndex = -1;
        angular.forEach(data, function(request, key){
            if (curDistrict == '' || curDistrict != request.dis) {
                curDistrict = request.dis;
                tagColorIndex++;
                //log(curDistrict + ":" + DefaultValues.tagColors[tagColorIndex]);
            }
            request.tagc = DefaultValues.tagColors[tagColorIndex];
        });
    }
            
    $scope.hk_pns = pnService.PNs.get({id:hk_commId}, $scope.tagDistricts);
    $scope.kl_pns = pnService.PNs.get({id:kl_commId}, $scope.tagDistricts);
    $scope.nt_pns = pnService.PNs.get({id:nt_commId}, $scope.tagDistricts);
    $scope.is_pns = pnService.PNs.get({id:is_commId}, $scope.tagDistricts);
    
    log("PNController completed");
});

minibeanPromo.service('pnService',function($resource){
    this.PNCommunities = $resource(
            '/get-pn-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', isArray:true}
            }
    );
    this.PNs = $resource(
            '/getPNs/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'},isArray:true}
            }
    );
});