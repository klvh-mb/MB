'use strict';

var minibean = angular.module('minibean');

/**
 * e.g.
 * <input ng:model="filter.firstName"/>
 * <input ng:model="filter.lastName"/>
 * <input ng:model="filter.email"/>
 * 
 * <ul ng:repeat="cust in customers | objFilter: filter">
 *     <li>
 *         <a href="mailto:{{cust.email}}">{{cust.firstName}} {{cust.lastName}}</a>
 *     </li>
 * </ul>
 */
minibean.filter('objFilter', function() {
  return function(items, filter) {
      if (!filter){
          return items;
      }  
      var result = {};
        angular.forEach( filter, function(filterVal, filterKey) {
          angular.forEach(items, function(item, key) {
              var fieldVal = item[filterKey];
              if (fieldVal && fieldVal.toLowerCase().indexOf(filterVal.toLowerCase()) > -1){
                  result[key] = item;
              }
          });
        });
        return result;
    };
});

/**
 * trustAsHtml
 */
minibean.filter('to_trusted', ['$sce', function($sce){
    return function(text) {
        return $sce.trustAsHtml(text);
    };
}]);

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
