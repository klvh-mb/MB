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
                      className: "post-unfavorite-modal",
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
