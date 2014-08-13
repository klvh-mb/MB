'use strict';

var minibeanPromo = angular.module('minibeanPromo');

minibeanPromo.controller('PromoController', function($scope, $routeParams, $location) {
    log("PromoController starts");

    log("PromoController completed");
});

minibeanPromo.controller('PNController',function($scope, $routeParams, $http, pnService) {
    log("PNController starts");

    $scope.pnCommunities = pnService.PNCommunities.get();

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
            
    $scope.hk_pns = pnService.PNs.get({region:"hk"}, $scope.tagDistricts);
    $scope.kl_pns = pnService.PNs.get({region:"kl"}, $scope.tagDistricts);
    $scope.nt_pns = pnService.PNs.get({region:"nt"}, $scope.tagDistricts);
    $scope.is_pns = pnService.PNs.get({region:"is"}, $scope.tagDistricts);
    
    log("PNController completed");
});

minibeanPromo.service('pnService',function($resource){
    this.PNCommunities = $resource(
            '/get-promo-pn-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', isArray:true}
            }
    );
    this.PNs = $resource(
            '/get-promo-pns/:region',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{region:'@region'},isArray:true}
            }
    );
});