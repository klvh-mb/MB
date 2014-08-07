'use strict';

var minibean = angular.module('minibean');

/**
 * A generic confirmation for risky actions.
 * Usage: Add attributes: ng-really-message="Are you sure"? ng-really-click="takeAction()" function
 */
minibean.directive('ngConfirmClick', [function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('click', function() {
                var message = attrs.ngConfirmMessage;
                if (message) {
                    /*
                    bootbox.confirm(message, function(result) {
                        if (result) {
                            scope.$apply(attrs.ngConfirmClick);
                        }
                    });
                    */
                    
                    bootbox.dialog({
                      message: message,
                      title: "",
                      className: "post-bootbox-modal",
                      buttons: {
                        cancel: {
                          label: "取消",
                          className: "btn-default",
                          callback: function() {
                            
                          }
                        },
                        success: {
                          label: "確認",
                          className: "btn-primary",
                          callback: function() {
                            scope.$apply(attrs.ngConfirmClick);
                          }
                        }
                      }
                    });
                    
                } else {
                    scope.$apply(attrs.ngConfirmClick);
                }
            });
        }
    }
}]);

/**
 * Use this directive 'valid-file' together with 'required' for input type='file'. 
 */
minibean.directive('validFile',function(){
    return {
        require:'ngModel',
        link:function(scope,el,attrs,ngModel){
            //change event is fired when file is selected
            el.bind('change',function(){
                scope.$apply(function(){
                    ngModel.$setViewValue(el.val());
                    ngModel.$render();
                });
            });
        }
    }
});
