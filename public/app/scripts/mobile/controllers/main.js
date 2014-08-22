'use strict';

var minibean = angular.module('minibean');

minibean.controller('SlidingMenuController', function($scope, $routeParams, $location, userInfoService, articleCategoryService){
    log("SlidingMenuController starts");
    
    //
    // sliding menu control
    // http://startbootstrap.com/templates/simple-sidebar/#
    //
    
    $scope.toggleMenu = function() {
        if ($("#wrapper").hasClass("toggled")) {
            $("#slider-menu-backdrop").removeClass("modal-backdrop");
        } else {
            $("#slider-menu-backdrop").addClass("modal-backdrop");
        }
        
        //e.preventDefault;
        $("#wrapper").toggleClass("toggled");
    }
    
    //
    // user info
    //
    
    //$scope.userInfo = userInfoService.UserInfo.get();
    //$scope.userTargetProfile = userInfoService.UserTargetProfile.get();
    
    $scope.set_background_image = function() {
        return { background: 'url(/image/get-thumbnail-cover-image-by-id/'+$scope.userInfo.id+') center center no-repeat'};
    } 
    
    //
    // article categories
    //
    
    $scope.articleCategories = articleCategoryService.getAllArticleCategory.get();
    
    log("SlidingMenuController completed");
});