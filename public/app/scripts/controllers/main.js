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

minibean.controller('UIController', function($scope, $location, $anchorScroll, $window) {
    $scope.gotoTop = function() {
    console.log("top");
        // set the location.hash to the id of
        // the element you wish to scroll to
    	$window.scrollTo($window.pageXOffset, 0);
    };
});

minibean.controller('AnnouncementsWidgetController',function($scope, $http, announcementsWidgetService) {
    log("AnnouncementsWidgetController starts");
    
    $scope.announcements = announcementsWidgetService.getAnnouncements.get();
    
    log("AnnouncementsWidgetController completed");
});

minibean.service('announcementsWidgetService',function($resource) {
    this.getAnnouncements = $resource(
            '/get-announcements',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

minibean.controller('TodayWeatherInfoController',function($scope, $http, todayWeatherInfoService) {
    log("TodayWeatherInfoController starts");
    
    $scope.todayWeatherInfo = todayWeatherInfoService.getTodayWeatherInfo.get();
    
    log("TodayWeatherInfoController completed");
});

minibean.service('todayWeatherInfoService',function($resource) {
    this.getTodayWeatherInfo = $resource(
            '/get-today-weather-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.service('locationService',function($resource){
    this.getAllDistricts = $resource(
            '/get-all-districts',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

minibean.service('postLandingService',function($resource){
    this.postLanding = $resource(
            '/post-landing/:id/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{id:'@id',communityId:'@communityId'}}
            }
    );
});

minibean.service('qnaLandingService',function($resource){
    this.qnaLanding = $resource(
            '/qna-landing/:id/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{id:'@id',communityId:'@communityId'}}
            }
    );
});

///////////////////////// Search Service Start //////////////////////////////////
minibean.service('searchService',function($resource){
	this.userSearch = $resource(
			'/user-search?query=:q',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{q:'@q'}, isArray:true}
			}
	);
});

minibean.service('sendInvitation',function($resource){
	this.inviteFriend = $resource(
			'/send-invite?id=:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{id:'@id'}}
			}
	);
});

minibean.service('unFriendService',function($resource){
	this.doUnfriend = $resource(
			'/un-friend?id=:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{id:'@id'}}
			}
	);
});

minibean.controller('SearchController',function($scope, searchService){
    log("SearchController starts");

	$scope.search_result = function(query) {
		if(query != undefined) {
			this.result = searchService.userSearch.get({q:query});
		}
	}
	
	log("SearchController completed");
});
///////////////////////// Search Service End //////////////////////////////////


///////////////////////// User Info Service Start //////////////////////////////////
minibean.service('applicationInfoService',function($resource){
    this.ApplicationInfo = $resource(
            '/get-application-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

minibean.service('userInfoService',function($resource){
	this.UserInfo = $resource(
			'/get-user-info',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET'}
			}
	);
	
	this.UserTargetProfile = $resource(
            '/get-user-target-profile',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

minibean.service('userNotification',function($resource){
	this.getAllFriendRequests = $resource(
			'/get-friend-requests',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', isArray:true}
			}
	);
});

minibean.service('acceptFriendRequestService',function($resource){
	this.acceptFriendRequest = $resource(
			'/accept-friend-request?friend_id=:id&notify_id=:notify_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{id:'@id',notify_id:'@notify_id'}, isArray:true}
			}
	);
});

minibean.service('userSimpleNotifications',function($resource){
	this.getAllJoinRequests = $resource(
			'/get-join-requests',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', isArray:true}
			}
	);
});

minibean.service('userMessageNotifications',function($resource){
	this.getUnreadMsgCount = $resource(
			'/get-unread-msg-count',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET'}
			}
	);
});


minibean.service('acceptJoinRequestService',function($resource){
	this.acceptJoinRequest = $resource(
			'/accept-join-request/:member_id/:group_id/:notify_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
			}
	);
	
	this.acceptInviteRequest = $resource(
			'/accept-invite-request/:member_id/:group_id/:notify_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
			}
	);
});

minibean.service('notificationMarkReadService',function($resource){
	this.markAsRead = $resource(
			'/mark-as-read/:notify_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
			}
	);
});

minibean.controller('UserInfoServiceController',function($scope,userInfoService){
    log("UserInfoServiceController starts");

    $scope.userInfo = userInfoService.UserInfo.get();
    
    log("UserInfoServiceController completed");
});

minibean.controller('ApplicationController',function($scope, $location, $interval, applicationInfoService, userInfoService, userNotification, userSimpleNotifications,
	acceptJoinRequestService, acceptFriendRequestService, userMessageNotifications, notificationMarkReadService, usSpinnerService){

    log("ApplicationController starts");

    window.isBrowserTabActive = true;

    $scope.applicationInfo = applicationInfoService.ApplicationInfo.get();
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.userTargetProfile = userInfoService.UserTargetProfile.get();
	
	$scope.set_background_image = function() {
		return { background: 'url(/image/get-thumbnail-cover-image-by-id/'+$scope.userInfo.id+') center center no-repeat'};
	} 
	$scope.unread_msg_count = 0;
	$scope.friend_requests = userNotification.getAllFriendRequests.get();
	$scope.join_requests = userSimpleNotifications.getAllJoinRequests.get();
	$scope.get_unread_msg_count = function() {
		$scope.unread_msg_count = userMessageNotifications.getUnreadMsgCount.get();
	}
	$scope.isFRreaded = true;
	$scope.isNOreaded = true;
	
	$scope.accept_friend_request = function(id, notify_id) {
		
		angular.forEach($scope.friend_requests, function(request, key){
			if(request.id == id) {
				request.isLoadingEnable = true;
			}
		});
		
		this.acceptFriendRequest = acceptFriendRequestService.acceptFriendRequest.get({id:id, notify_id:notify_id}, 
				//success
				function() {
					angular.forEach($scope.friend_requests, function(request, key){
						if(request.id == id) {
							$scope.friend_requests.splice($scope.friend_requests.indexOf(request),1);
						}
					});
				}
		);
	};
	$scope.accept_join_request = function(member_id,group_id, notification_id) {
		log(notification_id);
		var spinner = new Spinner().spin();
		
		$(".a_" + member_id + "_" + group_id).append(spinner.el);    
		this.accept_join_request = acceptJoinRequestService.acceptJoinRequest.get({"member_id":member_id, "group_id":group_id, "notify_id":notification_id},
			function() {
				$(".a_" + member_id + "_" + group_id).html("member");
				$(".a_" + member_id + "_" + group_id).removeClass("btn-success");
				$(".a_" + member_id + "_" + group_id).addClass("btn-default");
				$(".a_" + member_id + "_" + group_id).attr("disabled", true)
				spinner.stop();
				
				angular.forEach($scope.join_requests, function(request, key){
					if(request.id == member_id) {
						$scope.join_requests.splice($scope.join_requests.indexOf(request),1);
					}
				});
			}
		);
	}
	
	$scope.accept_invite_request = function(member_id,group_id, notification_id) {
		
		var spinner = new Spinner().spin();
		
		$(".a_" + member_id + "_" + group_id).append(spinner.el);    
		this.accept_invite_request = acceptJoinRequestService.acceptInviteRequest.get({"member_id":member_id, "group_id":group_id, "notify_id":notification_id},
			function() {
				$(".a_" + member_id + "_" + group_id).html("member");
				$(".a_" + member_id + "_" + group_id).removeClass("btn-success");
				$(".a_" + member_id + "_" + group_id).addClass("btn-default");
				$(".a_" + member_id + "_" + group_id).attr("disabled", true)
				spinner.stop();
				
				angular.forEach($scope.join_requests, function(request, key){
					if(request.id == member_id) {
						$scope.join_requests.splice($scope.join_requests.indexOf(request),1);
					}
				});
			}
		);
	}
	
	$scope.mark_as_read = function(notification_id) {
		notificationMarkReadService.markAsRead.get({"notify_id":notification_id});
	}

	$scope.reset_fr_count = function() {
		$scope.isFRreaded = false;
	}
	
	$scope.reset_notify_count = function() {
		$scope.isNOreaded = false;
	}
	
	$scope.$on('$locationChangeSuccess', function() {
		if($location.path()=='/about') {
			$scope.selectedTab = 1;
		}
		else if($location.path()=='/friends') {
			$scope.selectedTab = 2;
		}
		else if($location.path()=='/messages') {
			$scope.selectedTab = 3;
		}
	});
   
    $scope.showImage = function(imageId) {
        $scope.img_id = imageId;
    }

    $scope.toggleMenu = function() {
        if ($("#wrapper").hasClass("toggled")) {
            $("#slider-menu-backdrop").removeClass("modal-backdrop");
        } else {
            $("#slider-menu-backdrop").addClass("modal-backdrop");
        }
        
        //e.preventDefault;
        $("#wrapper").toggleClass("toggled");
    }
    
    log("ApplicationController completed");
});

///////////////////////// User Info Service End //////////////////////////////////



///////////////////////// User Notification Service Start //////////////////////////////////
minibean.service('userAboutService',function($resource){
	this.UserAbout = $resource(
			'/about-user',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

// TODO: I dont like way i am defining PhotoModalController
var PhotoModalController = function( $scope, $http, $timeout, $upload, profilePhotoModal, usSpinnerService) {
	$scope.fileReaderSupported = window.FileReader != null;
	$scope.uploadRightAway = true;

	$scope.hasUploader = function(index) {
		return $scope.upload[index] != null;
	};
	
	$scope.abort = function(index) {
		$scope.upload[index].abort(); 
		$scope.upload[index] = null;
	};
	
	$scope.close = function() {
		profilePhotoModal.CloseModal();
	}
	
	$scope.onFileSelect = function($files) {
		
		$scope.selectedFiles = [];
		$scope.progress = [];
		if ($scope.upload && $scope.upload.length > 0) {
			for (var i = 0; i < $scope.upload.length; i++) {
				if ($scope.upload[i] != null) {
					$scope.upload[i].abort();
				}
			}
		}
		$scope.upload = [];
		$scope.uploadResult = [];
		$scope.selectedFiles = $files;
		$scope.dataUrls = [];
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			
			if (window.FileReader && $file.type.indexOf('image') > -1) {
			  	var fileReader = new FileReader();
		        fileReader.readAsDataURL($files[i]);
		        $scope.setPreview(fileReader, i);
			}
			
			$scope.progress[i] = -1;
			if ($scope.uploadRightAway) {
				$scope.start(i);
			}
		}
	} // End of onSelect
	
	$scope.setPreview = function(fileReader, index) {
	    fileReader.onload = function(e) {
	        $timeout(function() {
	        	$scope.dataUrls[index] = e.target.result;
	        });
	    }
	}

	$scope.start = function(index) {
		$scope.progress[index] = 0;
		usSpinnerService.spin('loading..');
		$scope.upload[index] = $upload.upload({
			url : PhotoModalController.url,
			method: $scope.httpMethod,
			//headers: {'myHeaderKey': 'myHeaderVal'},
			//data : {
			//	myModel : $scope.myModel
			//},
			file: $scope.selectedFiles[index],
			fileFormDataName: 'profile-photo'
		}).success(function(data, status, headers, config) {
			usSpinnerService.stop('loading..');
			profilePhotoModal.CloseModal();
		});
	} // End of start
	
}

minibean.service('profilePhotoModal',function( $modal){
	
	this.OpenModal = function(arg, successCallback) {
		this.instance = $modal.open(arg);
		this.onSuccess = successCallback;
	}
	
	this.CloseModal = function() {
		this.instance.dismiss('close');
		this.onSuccess();
	}
});

minibean.controller('UserAboutController',function($routeParams, $scope, $http, userAboutService, locationService, profilePhotoModal){
	log("UserAboutController starts");
	
	var tab = $routeParams.tab;
	
	$scope.get_unread_msg_count();

    $scope.selectedSubTab = 1;	
	if (tab == 'activities' || tab == undefined) {
		$scope.selectedTab = 1;
	}
	
	if (tab == 'communities' ) {
		$scope.selectedTab = 2;
	}
	
	if (tab == 'myCommunities' ) {
		$scope.selectedTab = 2;
	}
	
	if (tab == 'friends' ) {
        $scope.selectedTab = 3;
    }
    
    if (tab == 'bookmarks' ) {
        $scope.selectedTab = 4;
    }
    
	var profileImage = "/image/get-profile-image";
	var coverImage = "/image/get-cover-image";
	$scope.isEdit = true;
	$scope.result = userAboutService.UserAbout.get();
	$scope.profileImage = profileImage;
	$scope.coverImage = coverImage;
	
	$scope.genders = DefaultValues.genders;
	
	$scope.years = DefaultValues.years;
    
    $scope.locations = locationService.getAllDistricts.get();
    
	$scope.updateUserDisplayName = function(data) {
		return $http.post('/updateUserDisplayName', {"displayName" : data});
	}
	
	$scope.updateUserProfileData = function(data) {
		return $http.post('/updateUserProfileData', $scope.result);
	}
	
	$scope.isProfileOn = true; 
	$scope.isCoverOn = !$scope.isProfileOn;
	$scope.openProfilePhotoModal = function() {
		PhotoModalController.url = "image/upload-profile-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		},function() {
			$scope.profileImage = profileImage + "?q="+ Math.random();
		});
		PhotoModalController.isProfileOn = true;
	}
	
	$scope.openCoverPhotoModal = function() {
		PhotoModalController.url = "image/upload-cover-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		},function() {
			$scope.coverImage = coverImage + "?q="+ Math.random();
		});
		PhotoModalController.isProfileOn = false;
	}

    $scope.showImage = function(imageId) {
        $scope.img_id = imageId;
    }
    
    log("UserAboutController completed");
});


///////////////////////// Create community Home Page  //////////////////////////////////
minibean.service('editCommunityPageService',function($resource){
	this.EditCommunityPage = $resource(
			'/editCommunity/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
});

minibean.controller('EditCommunityController',function($scope,$q, $location,$routeParams, $http, usSpinnerService, iconsService, editCommunityPageService, $upload, profilePhotoModal){
    log("EditCommunityController starts");
    
	$scope.submitBtn = "儲存";
	$scope.community = editCommunityPageService.EditCommunityPage.get({id:$routeParams.id}, 
			function(response) {
				
			},
			function(rejection) {
				if(rejection.status === 500) {
					$location.path('/error');
				}
				return $q.reject(rejection);
			}
	);

	$scope.community.typ = DefaultValues.communityType;
	
	$scope.tagetDistrict = DefaultValues.districts;
	
	$scope.icons = iconsService.getCommunityIcons.get();

	$scope.select_icon = function(img, text) {
		$scope.icon_chosen = img;
		$scope.icon_text = text;
		$scope.isChosen = true;
		$scope.community.icon = img;
	}
	
	$scope.updateGroupProfileData = function(data) {
		usSpinnerService.spin('loading...');
		return $http.post('/updateGroupProfileData', $scope.community).success(function(data){
			$scope.submitBtn = "完成";
			usSpinnerService.stop('loading...');
		});
	}

	$scope.openGroupCoverPhotoModal = function(id) {
		PhotoModalController.url = "image/upload-cover-photo-group/"+id;
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		},function() {
		
		});
	}
	
	log("EditCommunityController completed");
});

minibean.controller('CreateCommunityController',function($scope, $location, $http, $upload, $validator, iconsService, usSpinnerService){
	log("CreateCommunityController starts");
	
	$scope.formData = {};
	$scope.selectedFiles =[];
	$scope.submitBtn = "建立社群";
	$scope.submit = function() {
		 $validator.validate($scope, 'formData')
		    .success(function () {
		    	usSpinnerService.spin('載入中...');
		    	$upload.upload({
					url: '/createCommunity',
					method: 'POST',
					file: $scope.selectedFiles[0],
					data: $scope.formData,
					fileFormDataName: 'cover-photo'
				}).progress(function(evt) {
					$scope.submitBtn = "請稍候...";
					usSpinnerService.stop('載入中...');
			    }).success(function(data, status, headers, config) {
			    	$scope.submitBtn = "完成";
			    	usSpinnerService.stop('loading...');
			    	$location.path('/community/'+data+'/question');
			    	$("#myModal").modal('hide');
			    }).error(function(data, status, headers, config) {
			    	if( status == 505 ) {
			    		$scope.uniqueName = true;
			    		usSpinnerService.stop('載入中...');
			    		$scope.submitBtn = "再試一次";
			    	}  
			    });
		    })
		    .error(function () {
		        log('error');
		    });
	}
	
	$scope.icons = iconsService.getCommunityIcons.get();

	$scope.select_icon = function(img, text) {
		$scope.icon_chosen = img;
		$scope.icon_text = text;
		$scope.isChosen = true;
		$scope.formData.icon = img;
	}
	
	$scope.onFileSelect = function($files) {
		$scope.selectedFiles = $files;
		$scope.formData.photo = 'cover-photo';
	}
	
	log("CreateCommunityController completed");
});

///////////////////////// Suggested Friends Widget Service Start //////////////////////////////////

minibean.controller('SuggestedFriendsUtilityController',function($scope, unFriendService, usSpinnerService, sendInvitation, friendsService, userInfoService, $http){
    log("SuggestedFriendsUtilityController starts");

	$scope.result = friendsService.SuggestedFriends.get();
	$scope.isLoadingEnabled = false;
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.send_invite = function(id) {
		$scope.isLoadingEnabled = true;
		this.invite = sendInvitation.inviteFriend.get({id:id}, function(data) {
			$scope.isLoadingEnabled = false;
			angular.forEach($scope.result.friends, function(request, key){
				if(request.id == id) {
					request.isP = true;
				}
			});
		});
	}
	
	$scope.un_friend = function(id) {
		$scope.isLoadingEnabled = true;
		this.unFriendHim = unFriendService.doUnfriend.get({id:id}, function(data) {
			$scope.isLoadingEnabled = false;
			angular.forEach($scope.result.friends, function(request, key){
				if(request.id == id) {
					request.isF = true;
				}
			});
		});
	}
	
	log("SuggestedFriendsUtilityController completed");
});

///////////////////////// User Friends Widget End //////////////////////////////////

///////////////////////// Community Members Widget Service Start //////////////////////////////////
minibean.service('membersWidgetService',function($resource){
	this.CommunityMembers = $resource(
			'/get-community-members/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('CommunityMembersController',function($scope, $routeParams, membersWidgetService, $http){
    log("CommunityMembersController starts");

	$scope.result = membersWidgetService.CommunityMembers.get({id:$routeParams.id});
	$scope.showMembers = true;
	$scope.showAdmin = false;
	$scope.getAllMembers = function() {
		$scope.showMembers = true;
		$scope.showAdmin = false;
	}
	$scope.getAdmin = function() {
		$scope.showMembers = false;
		$scope.showAdmin = true;
	}
	
	log("CommunityMembersController completed");
});

///////////////////////// Community Members Widget Service Ends //////////////////////////////////


///////////////////////// Community PN Service Start //////////////////////////////////
minibean.service('pnService',function($resource){
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

minibean.controller('PNCommunitiesUtilityController', function($scope, $routeParams, pnService, sendJoinRequest, $http){
    log("PNCommunitiesUtilityController starts");

    $scope.pnCommunities = pnService.PNCommunities.get();
    $scope.send_request = function(id) {
        this.invite = sendJoinRequest.sendRequest.get({id:id},
                function(data) {
                    angular.forEach($scope.pnCommunities, function(request, key){
                        if(request.id == id) {
                            request.isP = true;
                        }
                    });
                }
        );
    }
    
    log("PNCommunitiesUtilityController completed");
});

minibean.controller('CommunityPNController',function($scope, $routeParams, pnService, $http){
    log("CommunityPNController starts");

    $scope.myDistrictPNs = [];
    $scope.otherPNs = [];
    var curDistrict = '';
    var tagColorIndex = -1;
	$scope.pns = pnService.PNs.get({id:$routeParams.id}, 
	       function(data) {
                angular.forEach($scope.pns, function(request, key){
                    if (curDistrict == '' || curDistrict != request.dis) {
                        curDistrict = request.dis;
                        tagColorIndex++;
                        //log(curDistrict + ":" + DefaultValues.tagColors[tagColorIndex]);
                    }
                    request.tagc = DefaultValues.tagColors[tagColorIndex];
                    if (request.myd) {
                        $scope.myDistrictPNs.push(request);
                    } else {
                        $scope.otherPNs.push(request);
                    }
                });
            }
	);
	
	log("CommunityPNController completed");
});
///////////////////////// Community PN Ends //////////////////////////////////


///////////////////////// User UnJoined Communities Widget Service Start //////////////////////////////////
minibean.service('unJoinedCommunityWidgetService',function($resource){
	this.UserCommunitiesNot = $resource(
			'/get-not-join-community',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('sendJoinRequest',function($resource){
	this.sendRequest = $resource(
			'/send-request?id=:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{id:'@id'}}
			}
	);
});

minibean.controller('RecommendedCommunityWidgetController',function($scope, usSpinnerService, sendJoinRequest, unJoinedCommunityWidgetService, userInfoService, $http){
    log("RecommendedCommunityWidgetController starts");

	$scope.result = unJoinedCommunityWidgetService.UserCommunitiesNot.get();
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.send_request = function(id) {
		this.invite = sendJoinRequest.sendRequest.get({id:id},
				function(data) {
					angular.forEach($scope.result.communities, function(request, key){
						if(request.id == id) {
							request.isP = true;
						}
					});
				}
		);
	}
	
	log("RecommendedCommunityWidgetController completed");
});

///////////////////////// User UnJoined Communities Widget End //////////////////////////////////

minibean.service('friendsService',function($resource){
    this.MyFriendsForUtility = $resource(
            '/get-my-friends-for-utility',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
	this.UserFriendsForUtility = $resource(
            '/get-user-friends-for-utility/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
    
    this.MyFriends = $resource(
			'/get-all-my-friends',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
	
	this.UserFriends = $resource(
            '/get-all-user-friends/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
    
    this.SuggestedFriends = $resource(
            '/get-suggested-friends',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

minibean.controller('MyFriendsUtilityController',function($scope, userInfoService, friendsService, $http){
    log("MyFriendsUtilityController starts");

    $scope.userInfo = userInfoService.UserInfo.get();
    $scope.result = friendsService.MyFriendsForUtility.get();
    
    log("MyFriendsUtilityController completed");
});

minibean.controller('UserFriendsUtilityController',function($scope, $routeParams, userInfoService, friendsService, $http){
    log("UserFriendsUtilityController starts");

    $scope.userInfo = userInfoService.UserInfo.get();
    $scope.result = friendsService.UserFriendsForUtility.get({id:$routeParams.id});
    
    log("UserFriendsUtilityController completed");
});

minibean.controller('MyFriendsController',function($scope, friendsService, $http){
    log("MyFriendsController starts");

	$scope.result = friendsService.MyFriends.get();
	
	log("MyFriendsController completed");
});

minibean.controller('UserFriendsController',function($scope, $routeParams, friendsService, $http){
    log("UserFriendsController starts");

    $scope.result = friendsService.UserFriends.get({id:$routeParams.id});
    
    log("UserFriendsController completed");
});

///////////////////////// User All Recommend Communities  //////////////////////////////////
minibean.service('sendJoinRequest',function($resource){
	this.sendRequest = $resource(
			'/send-request?id=:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{id:'@id'}}
			}
	);
});

minibean.controller('CommunityWidgetController',function($scope, $routeParams, usSpinnerService, communityWidgetService, sendJoinRequest, $http, userInfoService){
	log("CommunityWidgetController starts");
	
	$scope.userInfo = userInfoService.UserInfo.get();
	
	$scope.myAdminCommunities = [];
    $scope.myJoinedCommunities = [];
	$scope.myCommunities = communityWidgetService.UserCommunities.get(
        function(data) {
            angular.forEach(data.communities, function(community, key) {
            if (community.isO)
                $scope.myAdminCommunities.push(community);
            else
                $scope.myJoinedCommunities.push(community);
            })
        }
	);

	$scope.send_request = function(id) {
		usSpinnerService.spin('loading...');
		this.invite = sendJoinRequest.sendRequest.get({id:id},
				function(data) {
					angular.forEach($scope.result.communities, function(request, key){
						if(request.id == id) {
							request.isP = true;
						}
					});
					usSpinnerService.stop('loading...');
				}
		);
	}
	
	log("CommunityWidgetController completed");
});

///////////////////////// User All Recommend Communities End //////////////////////////////////


///////////////////////// User All Communities  //////////////////////////////////
minibean.service('communityWidgetService',function($resource){
	this.UserCommunities = $resource(
			'/get-my-communities',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('UserCommunityWidgetController',function($scope, communityWidgetService){
	log("UserCommunityWidgetController starts");
	
	$scope.sysCommunities = [];
	$scope.myCommunities = [];
	$scope.result = communityWidgetService.UserCommunities.get({}, 
        	function(data) {
                angular.forEach($scope.result.communities, function(request, key){
                    if (request.sys) {
                        $scope.sysCommunities.push(request);
                    } else {
                        $scope.myCommunities.push(request);
                    }
                });
            }
	);

	$scope.selectedTab = 1;
	
	log("UserCommunityWidgetController completed");
});

///////////////////////// User All Communities End //////////////////////////////////


///////////////////////// User All Communities  //////////////////////////////////
minibean.service('communityWidgetByUserService',function($resource){
	this.UserCommunities = $resource(
			'/get-user-communities/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('allCommunityWidgetByUserService',function($resource){
	this.UserAllCommunities = $resource(
			'/get-user-all-communities/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});


minibean.controller('CommunityWidgetByUserIDController',function($scope, $routeParams, usSpinnerService, sendJoinRequest, communityJoinService, allCommunityWidgetByUserService, communityWidgetByUserService , $http, userInfoService){
    log("CommunityWidgetByUserIDController starts");

	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.result = communityWidgetByUserService.UserCommunities.get({id:$routeParams.id});
	$scope.allResult = allCommunityWidgetByUserService.UserAllCommunities.get({id:$routeParams.id});
	
	$scope.send_request = function(id) {
		usSpinnerService.spin('loading...');
		this.invite = sendJoinRequest.sendRequest.get({id:id},
				function(data) {
					angular.forEach($scope.allResult.communities, function(request, key){
						if(request.id == id) {
							request.isP = true;
						}
					});
					usSpinnerService.stop('loading...');
				}
		);
	}
	
	log("CommunityWidgetByUserIDController completed");
});

///////////////////////// User All Communities End //////////////////////////////////



///////////////////////// User Profile Start //////////////////////////////////

minibean.service('profileService',function($resource){
	this.Profile = $resource(
			'/profile/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
});

minibean.controller('ProfileController',function($scope, $routeParams, $location, profileService, friendsService, sendInvitation, unFriendService){
	log("ProfileController starts");
	
	$scope.$watch($routeParams.id, function (navigateTo) {
		if( $routeParams.id  == $scope.userInfo.id){
			 $location.path("about/activities");
		}
	});
	
	$scope.get_unread_msg_count();
	
	$scope.isLoadingEnabled = false;
	$scope.selectedTab = 1;
	$scope.navigateTo = function (navigateTo) {
		$scope.active = navigateTo;
		if(navigateTo === 'friends') {
			$scope.friends = friendsService.UserFriends.get({id:$routeParams.id});
		}
		
	}
	$scope.send_invite = function(id) {
		$scope.isLoadingEnabled = true;
		this.invite = sendInvitation.inviteFriend.get({id:id}, function(data) {
			$scope.isLoadingEnabled = false;
			$scope.profile.isP = true;
		});
	}
	
	$scope.un_friend = function(id) {
		$scope.isLoadingEnabled = true;
		this.unFriendHim = unFriendService.doUnfriend.get({id:id}, function(data) {
			$scope.isLoadingEnabled = false;
			$scope.profile.isf = false;
		});
	}
	
	$scope.active = "about";
	$scope.profile = profileService.Profile.get({id:$routeParams.id});

    $scope.showImage = function(imageId) {
        $scope.img_id = imageId;
    }
    
    log("CommunityWidgetByUserIDController completed");
});

///////////////////////// User Profile End //////////////////////////////////

minibean.service('communitySearchPageService',function($resource){
	this.GetPostsFromIndex = $resource(
			'/searchForPosts/index/:query/:community_id/:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{community_id:'@community_id',query:'@query',offset:'@offset'},isArray:true}
			}
	);
});

minibean.controller('SearchPageController', function($scope, $routeParams, likeFrameworkService, communityPageService, $http, communitySearchPageService, usSpinnerService){
    log("SearchPageController starts");

	$scope.highlightText="";
	$scope.highlightQuery = "";
	$scope.community = communityPageService.Community.get({id:$routeParams.id}, function(){
		usSpinnerService.stop('loading...');
	});
	
	$scope.comment_on_post = function(id, commentText) {
        // first convert to links
        commentText = convertToLinks(commentText);

		var data = {
			"post_id" : id,
			"commentText" : commentText
		};
		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(comment_id) {
				$('.commentBox').val('');
				
				$scope.commentText = "";
				angular.forEach($scope.community.searchPosts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var comment = {"oid" : $scope.userInfo.id, "commentText" : commentText, "on" : $scope.userInfo.displayName,
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
						post.cs.push(comment);
				}
				usSpinnerService.stop('loading...');	
			});
		});
	};
	
	$scope.$watch('search_trigger', function(query) {
	       if(query != undefined) {
	    	   $scope.search_and_highlight(query);
	       }
	   });

    $scope.showImage = function(imageId) {
        $scope.img_id = imageId;
    }
	
	var offset = 0;
	var searchPost = true;
	$scope.search_and_highlight = function(query) {
		if ($scope.isBusy) return;
		var id = $routeParams.id;
		$scope.isBusy = true;
		if(searchPost){
			$scope.community.searchPosts = [];
			searchPost = false;
		}
		
		communitySearchPageService.GetPostsFromIndex.get({community_id : id , query : query, offset:offset}, function( data ) {
			var posts = data;
			
			if(posts.length == 0) {
				$scope.community.searchPosts.length=0;
				$scope.noresult = "No Results Found";
			}
			if(data.length < DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT) {
				offset = -1;
				$scope.community.searchPosts.length=0;
				$scope.isBusy = false;
			}
			
			for (var i = 0; i < posts.length; i++) {
				$scope.community.searchPosts.push(posts[i]);
		    }
			$scope.isBusy = false;
			offset++;
			$scope.highlightText = query;
		});
	};
	
	$scope.like_post = function(post_id) {
		likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.community.searchPosts, function(post, key){
				if(post.id == post_id) {
					post.isLike=true;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.community.searchPosts, function(post, key){
				if(post.id == post_id) {
					post.nol--;
					post.isLike=false;
				}
			})
		});
	}
	
	$scope.like_comment = function(post_id,comment_id) {
		likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.community.searchPosts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol++;
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
	$scope.unlike_comment = function(post_id,comment_id) {
		likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.community.searchPosts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol--;
							comment.isLike=false;
						}
					})
				}
			})
		});
	}
	
	log("SearchPageController completed");
});

///////////////////////// Community Page Start //////////////////////////////////

minibean.service('communityPageService',function($resource){
	this.Community = $resource(
			'/community/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
	
	this.Posts = $resource(
            '/community/posts/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
	this.GetPosts = $resource(
			'/posts?id=:id&offset=:offset&time=:time',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id',offset:'@offset',time:'@time'},isArray:true}
			}
	);
	
	this.isNewsfeedEnabled = $resource(
            '/is-newsfeed-enabled-for-community/:community_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{community_id:'@community_id'}}
            }
    );
    
	this.toggleNewsfeedEnabled = $resource(
            '/toggle-newsfeed-enabled-for-community/:community_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{community_id:'@community_id'}}
            }
    );
});

minibean.service('postManagementService',function($resource){
    this.deletePost = $resource(
            '/delete-post/:postId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{postId:'@postId'}}
            }
    );
    
    this.deleteComment = $resource(
            '/delete-comment/:commentId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{commentId:'@commentId'}}
            }
    );
});

minibean.service('allCommentsService',function($resource){
	this.comments = $resource(
			'/comments/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'},isArray:true}
			}
	);
});

minibean.service('communityJoinService',function($resource){
	this.sendJoinRequest = $resource(
			'/community/join/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
	
	this.leaveCommunity = $resource(
			'/community/leave/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
});

minibean.service('iconsService',function($resource){
	this.getCommunityIcons = $resource(
			'/image/getCommunityIcons',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
	
	this.getEmoticons = $resource(
            '/image/getEmoticons',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

minibean.service('searchMembersService',function($resource){
	this.getUnjoinedUsers = $resource(
			'/getAllUnjoinedMembers/:id/:query',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id',query : '@query'}, isArray:true}
			}
	);
	
	this.sendInvitationToNonMember = $resource(
			'/inviteToCommunity/:group_id/:user_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{group_id:'@group_id',user_id : '@user_id'}, isArray:true}
			}
	);
});

minibean.service('bookmarkPostService', function($resource) {
	this.bookmarkPost = $resource(
			'/bookmark-post/:post_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{post_id:'@post_id'}}
			}
	);
	
	this.unbookmarkPost = $resource(
			'/unbookmark-post/:post_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{post_id:'@post_id'}}
			}
	);
	
	this.bookmarkArticle = $resource(
			'/bookmark-article/:article_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{article_id:'@article_id'}}
			}
	);
	
	this.unbookmarkArticle = $resource(
			'/unbookmark-article/:article_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{article_id:'@article_id'}}
			}
	);
});
	
minibean.service('likeFrameworkService', function($resource) {
	this.hitLikeOnPost = $resource(
			'/like-post/:post_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{post_id:'@post_id'}}
			}
	);
	
	this.hitUnlikeOnPost = $resource(
			'/unlike-post/:post_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{post_id:'@post_id'}}
			}
	);
	
	this.hitLikeOnComment = $resource(
			'/like-comment/:comment_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{post_id:'@post_id'}}
			}
	);
	
	this.hitUnlikeOnComment = $resource(
			'/unlike-comment/:comment_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{post_id:'@post_id'}}
			}
	);
	
	this.hitLikeOnArticle = $resource(
			'/like-article/:article_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{article_id:'@article_id'}}
			}
	);
	
	this.hitUnlikeOnArticle = $resource(
			'/unlike-article/:article_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{article_id:'@article_id'}}
			}
	);
});

minibean.controller('PostLandingController', function($scope, $routeParams, $http, $timeout, $upload, $validator, 
    postLandingService, communityPageService, allCommentsService, showImageService, bookmarkPostService, likeFrameworkService, usSpinnerService) {
    
    log("PostLandingController starts");

	$scope.get_unread_msg_count();
	
    $scope.$on('$viewContentLoaded', function() {
        usSpinnerService.spin('loading...');
    });
    
    $scope.community = communityPageService.Community.get({id:$routeParams.communityId});
    
    $scope.posts = postLandingService.postLanding.get({id:$routeParams.id,communityId:$routeParams.communityId}, function(response) {
        if (response[0] == 'NO_RESULT'){
            $scope.noResult = true;
        }
        $scope.noResult = false;
        usSpinnerService.stop('loading...');
    });
    
    //
    // Below is copied completely from CommunityPageController
    // for js functions to handle comment, comment photo, like, bookmark etc
    //
    
    $scope.showImage = function(imageId) {
        $scope.img_id = imageId;
    }
    
    $scope.isLoadingEnabled = false;
    $scope.show = false;
    $scope.postPhoto = function() {
        $("#post-photo-id").click();
    }
    $scope.selectedFiles = [];
    $scope.tempSelectedFiles = [];
    $scope.dataUrls = [];
    
    $scope.get_all_comments = function(id) {
        angular.forEach($scope.posts.posts, function(post, key){
            if(post.id == id) {
                post.cs = allCommentsService.comments.get({id:id});
                post.ep = true;
            }
        });
    }
    
    $scope.onFileSelect = function($files) {
        
        if($scope.selectedFiles.length == 0) {
            $scope.tempSelectedFiles = [];
        }
        
        $scope.selectedFiles.push($files);
        $scope.tempSelectedFiles.push($files);
        for ( var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.dataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }
    
    $scope.comment_on_post = function(id, commentText) {
        // first convert to links
        commentText = convertToLinks(commentText);

        var data = {
            "post_id" : id,
            "commentText" : commentText,
            "withPhotos" : $scope.commentSelectedFiles.length != 0
        };
        var post_data = data;
        usSpinnerService.spin('loading...');
        $http.post('/community/post/comment', data) 
            .success(function(comment_id) {
                $('.commentBox').val('');
                
                $scope.commentText = "";
                angular.forEach($scope.posts.posts, function(post, key){
                        if(post.id == data.post_id) {
                            post.n_c++;
                            post.ut = new Date();
                            var comment = {"oid" : $scope.posts.lu, "d" : commentText, "on" : $scope.posts.lun,
                                    "isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
                            post.cs.push(comment);
                            
                            if($scope.commentSelectedFiles.length == 0) {
                                return;
                            }
                            
                            $scope.commentSelectedFiles = [];
                            $scope.commentDataUrls = [];
                            
                            // when post is done in BE then do photo upload
                            log($scope.commentTempSelectedFiles.length);
                            for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
                                usSpinnerService.spin('loading...');
                                $upload.upload({
                                    url : '/image/uploadCommentPhoto',
                                    method: $scope.httpMethod,
                                    data : {
                                        commentId : comment_id
                                    },
                                    file: $scope.commentTempSelectedFiles[i],
                                    fileFormDataName: 'comment-photo'
                                }).success(function(data, status, headers, config) {
                                    $scope.commentTempSelectedFiles.length = 0;
                                    if(post.id == post_data.post_id) {
                                        angular.forEach(post.cs, function(cmt, key){
                                            if(cmt.id == comment_id) {
                                                cmt.hasImage = true;
                                                if(cmt.imgs) {
                                                    
                                                } else {
                                                    cmt.imgs = [];
                                                }
                                                cmt.imgs.push(data);
                                            }
                                        });
                                    }
                                });
                        }
                    }
            });
            usSpinnerService.stop('loading...');    
        });
    };
    
    $scope.remove_image_from_comment = function(index) {
        $scope.commentSelectedFiles.splice(index, 1);
        $scope.commentTempSelectedFiles.splice(index, 1);
        $scope.commentDataUrls.splice(index, 1);
    }
    
    $scope.post_on_community = function(id, postText) {
        
        usSpinnerService.spin('loading...');
        var data = {
            "community_id" : id,
            "postText" : postText,
            "withPhotos" : $scope.selectedFiles.length != 0
        };
        
        $scope.postText="";
        
        $http.post('/community/post', data)// first create post with post text.
            .success(function(post_id) {
                usSpinnerService.stop('loading...');
                $('.postBox').val('');
                $scope.postText = "";
                var post = {"oid" : $scope.posts.lu, "pt" : postText, "cn" : $scope.community.n,
                        "isLike" : false, "nol" : 0, "p" : $scope.posts.lun, "t" : new Date(), "n_c" : 0, "id" : post_id, "cs": []};
                $scope.posts.posts.unshift(post);
                
                if($scope.selectedFiles.length == 0) {
                    return;
                }
                
                $scope.selectedFiles = [];
                $scope.dataUrls = [];
                
                // when post is done in BE then do photo upload
                for(var i=0 ; i<$scope.tempSelectedFiles.length ; i++) {
                    usSpinnerService.spin('loading...');
                    $upload.upload({
                        url : '/image/uploadPostPhoto',
                        method: $scope.httpMethod,
                        data : {
                            postId : post_id
                        },
                        file: $scope.tempSelectedFiles[i],
                        fileFormDataName: 'post-photo'
                    }).success(function(data, status, headers, config) {
                        usSpinnerService.stop('loading...');
                        angular.forEach($scope.posts.posts, function(post, key){
                            if(post.id == post_id) {
                                post.hasImage = true;
                                if(post.imgs) { 
                                } else {
                                    post.imgs = [];
                                }
                                post.imgs.push(data);
                            }
                        });
                    });
                    
                }
        });
    };

    $scope.send_join = function(id) {
        usSpinnerService.spin('loading...');
        this.send_join_request = communityJoinService.sendJoinRequest.get({"id":id}, function(data) {
            usSpinnerService.stop('loading...');
            log($scope.community.typ);
            $scope.community.isP = $scope.community.typ == 'CLOSE' ?  true : false;
            $scope.community.isM = $scope.community.typ == 'OPEN'? true : false;
        });
    }
    
    $scope.leave_community = function(id) {
        usSpinnerService.spin('loading...');
        this.leave_this_community = communityJoinService.leaveCommunity.get({"id":id}, function(data) {
            usSpinnerService.stop('loading...');
            $scope.community.isM = false;
        });
    }
    
    $scope.remove_image = function(index) {
        $scope.selectedFiles.splice(index, 1);
        $scope.tempSelectedFiles.splice(index, 1);
        $scope.dataUrls.splice(index, 1);
    }
    
    $scope.openGroupCoverPhotoModal = function(id) {
        PhotoModalController.url = "image/upload-cover-photo-group/"+id;
        profilePhotoModal.OpenModal({
             templateUrl: 'change-profile-photo-modal.html',
             controller: PhotoModalController
        },function() {
            $scope.coverImage = coverImage + "?q="+ Math.random();
        });
    }
    
    $scope.bookmarkPost = function(post_id) {
        bookmarkPostService.bookmarkPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.posts.posts, function(post, key){
                if(post.id == post_id) {
                    post.isBookmarked = true;
                }
            })
        });
    }
    
    $scope.unBookmarkPost = function(post_id) {
        bookmarkPostService.unbookmarkPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.posts.posts, function(post, key){
                if(post.id == post_id) {
                    post.isBookmarked = false;
                }
            })
        });
    }
        
    $scope.like_post = function(post_id) {
        likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.posts.posts, function(post, key){
                if(post.id == post_id) {
                    post.isLike=true;
                    post.nol++;
                }
            })
        });
    }
    
    $scope.unlike_post = function(post_id) {
        likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.posts.posts, function(post, key){
                if(post.id == post_id) {
                    post.nol--;
                    post.isLike=false;
                }
            })
        });
    }
    
    $scope.like_comment = function(post_id,comment_id) {
        likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
            angular.forEach($scope.posts.posts, function(post, key){
                if(post.id == post_id) {
                    angular.forEach(post.cs, function(comment, key){
                        if(comment.id == comment_id) {
                            comment.nol++;
                            comment.isLike=true;
                        }
                    })
                }
            })
        });
    }
    
    $scope.unlike_comment = function(post_id,comment_id) {
        likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
            angular.forEach($scope.posts.posts, function(post, key){
                if(post.id == post_id) {
                    angular.forEach(post.cs, function(comment, key){
                        if(comment.id == comment_id) {
                            comment.nol--;
                            comment.isLike=false;
                        }
                    })
                }
            })
        });
    }
    
    $scope.commentPhoto = function(post_id) {
        $("#comment-photo-id").click();
        $scope.commentedOnPost = post_id ;
    } 
    
    $scope.commentSelectedFiles = [];
    $scope.commentTempSelectedFiles = [];
    $scope.commentDataUrls = [];
    
    $scope.onCommentFileSelect = function($files) {
        log($scope.commentSelectedFiles.length);
        if($scope.commentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
            $scope.commentTempSelectedFiles = [];
        }
        
        $scope.commentSelectedFiles.push($files);
        log($scope.commentSelectedFiles);
        $scope.commentTempSelectedFiles.push($files);
        for ( var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.commentDataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }
    
    log("PostLandingController completed");
});
    
minibean.controller('QnALandingController', function($scope, $routeParams, $http, $timeout, $upload, $validator, 
    qnaLandingService, communityPageService, allAnswersService, showImageService, bookmarkPostService, likeFrameworkService, usSpinnerService) {

    log("QnALandingController starts");

    $scope.get_unread_msg_count();

    $scope.$on('$viewContentLoaded', function() {
        usSpinnerService.spin('loading...');
    });
    
    $scope.community = communityPageService.Community.get({id:$routeParams.communityId});
    
    $scope.QnAs = qnaLandingService.qnaLanding.get({id:$routeParams.id,communityId:$routeParams.communityId}, function(response) {
        if (response[0] == 'NO_RESULT'){
            $scope.noResult = true;
        }
        $scope.noResult = false;
        usSpinnerService.stop('loading...');
    });
    
    //
    // Below is copied completely from CommunityQnAController
    // for js functions to handle comment, comment photo, like, bookmark etc
    //
    
    $scope.showImage = function(imageId) {
        $scope.img_id = imageId;
    }
    
    $scope.postPhoto = function() {
        $("#QnA-photo-id").click();
    }
    
    $scope.get_all_answers = function(id) {
        angular.forEach($scope.QnAs.posts, function(post, key){
            if(post.id == id) {
                post.cs = allAnswersService.answers.get({id:id});
                post.ep = true;
            }
        });
    }
    
    // !!!NOTE: Since we reuse qna-bar.html for landing page, and qna-bar.html is 
    // being used in home-news-feed-section.html, "view all" is using 
    // CommunityPageController.get_all_comments() instead of 
    // CommunityQnAController.get_all_answers(). Hence we need to define 
    // below to make get_all_comments() available in QnALandingController
    
    $scope.get_all_comments = $scope.get_all_answers;
    
    $scope.QnASelectedFiles = [];
    $scope.dataUrls = [];
    $scope.tempSelectedFiles = [];
    
    $scope.commentPhoto = function(post_id) {
        $("#qna-comment-photo-id").click();
        $scope.commentedOnPost = post_id ;
    };
    
    $scope.qnaCommentSelectedFiles = [];
    $scope.qnaTempCommentSelectedFiles = [];
    $scope.qnaCommentDataUrls = [];
    
    $scope.onQnACommentFileSelect = function($files) {
        log($scope.qnaCommentSelectedFiles.length);
        if($scope.qnaCommentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
            $scope.qnaTempCommentSelectedFiles = [];
        }
        
        $scope.qnaCommentSelectedFiles.push($files);
        log($scope.qnaCommentSelectedFiles);
        $scope.qnaTempCommentSelectedFiles.push($files);
        for ( var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.qnaCommentDataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }
    
    $scope.ask_question_community = function(id, questionTitle, questionText) {
        // first convert to links
        questionText = convertToLinks(questionText);

        usSpinnerService.spin('loading...');
        var data = {
                "community_id" : id,
                "questionTitle" : questionTitle,
                "questionText" : questionText,
                "withPhotos" : $scope.QnASelectedFiles.length != 0
            };
        
        $http.post('/communityQnA/question/post', data)// first create post with question text.
            .success(function(post_id) {
                usSpinnerService.stop('loading...');
                $('.postBox').val('');
                var post = {"oid" : $scope.QnAs.lu, "ptl" : questionTitle, "pt" : questionText, "cn" : $scope.community.n, 
                        "isLike" : false, "nol" : 0, "p" : $scope.QnAs.lun, "t" : new Date(), "n_c" : 0, "id" : post_id, "cs": []};
                $scope.QnAs.posts.unshift(post);
                
                if($scope.QnASelectedFiles.length == 0) {
                    return;
                }
                
                $scope.QnASelectedFiles = [];
                $scope.dataUrls = [];
                
                
                // when post is done in BE then do photo upload
                for(var i=0 ; i<$scope.tempSelectedFiles.length ; i++) {
                    usSpinnerService.spin('loading...');
                    $upload.upload({
                        url : '/image/uploadPostPhoto',
                        method: $scope.httpMethod,
                        data : {
                            postId : post_id
                        },
                        file: $scope.tempSelectedFiles[i],
                        fileFormDataName: 'post-photo'
                    }).success(function(data, status, headers, config) {
                        usSpinnerService.stop('loading...');
                        angular.forEach($scope.QnAs.posts, function(post, key){
                            if(post.id == post_id) {
                                post.hasImage = true;
                                if(post.imgs) { 
                                } else {
                                    post.imgs = [];
                                }
                                post.imgs.push(data);
                            }
                        });
                    });
                }
        });
    };
    
    $scope.remove_image_from_qna_comment = function(index) {
        $scope.qnaCommentSelectedFiles.splice(index, 1);
        $scope.qnaTempCommentSelectedFiles.splice(index, 1);
        $scope.qnaCommentDataUrls.splice(index, 1);
    };
    
    $scope.answer_to_question = function(question_post_id, answerText) {
        // first convert to links
        answerText = convertToLinks(answerText);

        var data = {
            "post_id" : question_post_id,
            "answerText" : answerText,
            "withPhotos" : $scope.qnaCommentSelectedFiles.length != 0
        };
        
        var post_data = data;
        $http.post('/communityQnA/question/answer', data) 
            .success(function(answer_id) {
                $('.commentBox').val('');
                angular.forEach($scope.QnAs.posts, function(post, key){
                    if(post.id == data.post_id) {
                        post.n_c++;
                        post.ut = new Date();
                        var answer = {"oid" : $scope.QnAs.lu, "d" : answerText, "on" : $scope.QnAs.lun, 
                                "isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : answer_id};
                        post.cs.push(answer);
                    
                        if($scope.qnaCommentSelectedFiles.length == 0) {
                            return;
                        }
                        
                        $scope.qnaCommentSelectedFiles = [];
                        $scope.qnaCommentDataUrls = [];
                        
                    
                        // when post is done in BE then do photo upload
                        log($scope.qnaTempCommentSelectedFiles.length);
                        for(var i=0 ; i<$scope.qnaTempCommentSelectedFiles.length ; i++) {
                            usSpinnerService.spin('loading...');
                            $upload.upload({
                                url : '/image/uploadQnACommentPhoto',
                                method: $scope.httpMethod,
                                data : {
                                    commentId : answer_id
                                },
                                file: $scope.qnaTempCommentSelectedFiles[i],
                                fileFormDataName: 'comment-photo'
                            }).success(function(data, status, headers, config) {
                                $scope.qnaTempCommentSelectedFiles.length = 0;
                                if(post.id == post_data.post_id) {
                                    angular.forEach(post.cs, function(cmt, key){
                                        if(cmt.id == answer_id) {
                                            cmt.hasImage = true;
                                            if(cmt.imgs) {
                                                
                                            } else {
                                                cmt.imgs = [];
                                            }
                                            cmt.imgs.push(data);
                                        }
                                    });
                                }
                            });
                    }
                }
            
                usSpinnerService.stop('loading...');
            });
        });
    }
    
    $scope.onQnAFileSelect = function($files) {
        if($scope.QnASelectedFiles.length == 0) {
            $scope.tempSelectedFiles = [];
        }
        $scope.tempSelectedFiles.push($files);
        $scope.QnASelectedFiles.push($files);
        for ( var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.dataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }
    
    $scope.remove_image = function(index) {
        $scope.QnASelectedFiles.splice(index, 1);
        $scope.dataUrls.splice(index, 1);
    }
    
    $scope.like_post = function(post_id) {
        likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.QnAs.posts, function(post, key){
                if(post.id == post_id) {
                    post.isLike=true;
                    post.nol++;
                }
            })
        });
    }
    
    $scope.unlike_post = function(post_id) {
        likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.QnAs.posts, function(post, key){
                if(post.id == post_id) {
                    post.isLike=false;
                    post.nol--;
                }
            })
        });
    }

    $scope.like_comment = function(post_id,comment_id) {
        likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
            angular.forEach($scope.QnAs.posts, function(post, key){
                if(post.id == post_id) {
                    angular.forEach(post.cs, function(comment, key){
                        if(comment.id == comment_id) {
                            comment.nol++;
                            comment.isLike=true;
                        }
                    })
                }
            })
        });
    }
    
    $scope.unlike_comment = function(post_id,comment_id) {
        likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
            angular.forEach($scope.QnAs.posts, function(post, key){
                if(post.id == post_id) {
                    angular.forEach(post.cs, function(comment, key){
                        if(comment.id == comment_id) {
                            comment.nol--;
                            comment.isLike=false;
                        }
                    })
                }
            })
        });
    }
    
    $scope.bookmarkPost = function(post_id) {
        bookmarkPostService.bookmarkPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.QnAs.posts, function(post, key){
                if(post.id == post_id) {
                    post.isBookmarked = true;
                }
            })
        });
    }
    
    $scope.unBookmarkPost = function(post_id) {
        bookmarkPostService.unbookmarkPost.get({"post_id":post_id}, function(data) {
            angular.forEach($scope.QnAs.posts, function(post, key){
                if(post.id == post_id) {
                    post.isBookmarked = false;
                }
            })
        });
    }
    
    log("QnALandingController completed");
});

minibean.controller('CommunityPageController', function($scope, $routeParams, $http, profilePhotoModal, iconsService,
        communityPageService, communityJoinService, userInfoService, searchMembersService, $upload, $timeout, usSpinnerService){
    
    log("CommunityPageController starts");

    $scope.show = false;
    
    $scope.selectedTab = 1;
    $scope.selectedSubTab = 1;
    var tab = $routeParams.tab;
    if(tab == 'question'){
        $scope.selectedSubTab = 1;
    }
    if(tab == 'moment'){
        $scope.selectedSubTab = 2;
    }
    if(tab == 'members'){
        $scope.selectedTab = 2;
    }
    if(tab == 'details'){
        $scope.selectedTab = 3;
    }
    
    $scope.$on('$viewContentLoaded', function() {
        usSpinnerService.spin('loading...');
    });
    
    $scope.community = communityPageService.Community.get({id:$routeParams.id}, function(data){
        usSpinnerService.stop('loading...');
        
        // special handling - select details tab for PN
        if (data.ttyp == 'PRE_NURSERY') {
            $scope.selectedTab = 3;
        }
    });
    
    communityPageService.isNewsfeedEnabled.get({community_id:$routeParams.id}, function(data) {
        $scope.newsfeedEnabled = data.newsfeedEnabled; 
    });
    
    $scope.toggleNewsfeedEnabled = function(community_id) {
        communityPageService.toggleNewsfeedEnabled.get({"community_id":community_id}, function(data) {
            $scope.newsfeedEnabled = data.newsfeedEnabled; 
        });
    }
    
    $scope.showImage = function(imageId) {
        $scope.img_id = imageId;
    }
    
    $scope.coverImage = "/image/get-cover-community-image-by-id/" + $routeParams.id;
    
    $scope.openGroupCoverPhotoModal = function(id) {
        PhotoModalController.url = "image/upload-cover-photo-group/"+id;
        profilePhotoModal.OpenModal({
             templateUrl: 'change-profile-photo-modal.html',
             controller: PhotoModalController
        },function() {
            $scope.coverImage = coverImage + "?q="+ Math.random();
        });
    }
    
    $scope.nonMembers = [];
    $scope.search_unjoined_users = function(comm_id, query) {
        if(query.length >1){
            $scope.nonMembers = searchMembersService.getUnjoinedUsers.get({id : comm_id, query: query});
        }
    }
    
    $scope.send_invite_to_join = function(group_id, user_id) {
        searchMembersService.sendInvitationToNonMember.get({group_id : group_id, user_id: user_id}, function() {
            angular.forEach($scope.nonMembers, function(member, key){
                if(member.id == user_id) {
                    $scope.nonMembers.splice($scope.nonMembers.indexOf(member),1);
                }
            });
        });
    }
    
    $scope.send_join = function(id) {
        usSpinnerService.spin('loading...');
        this.send_join_request = communityJoinService.sendJoinRequest.get({"id":id}, function(data) {
            usSpinnerService.stop('loading...');
            log($scope.community.typ);
            $scope.community.isP = $scope.community.typ == 'CLOSE' ?  true : false;
            $scope.community.isM = $scope.community.typ == 'OPEN'? true : false;
        });
    }
    
    $scope.leave_community = function(id) {
        usSpinnerService.spin('loading...');
        this.leave_this_community = communityJoinService.leaveCommunity.get({"id":id}, function(data) {
            usSpinnerService.stop('loading...');
            $scope.community.isM = false;
        });
    }
    
    //$scope.icons = iconsService.getCommunityIcons.get();
    
    log("CommunityPageController completed");
});

minibean.controller('CommunityPostController', function($scope, $routeParams, $http, profilePhotoModal, iconsService,
		allCommentsService, communityPageService, postManagementService, likeFrameworkService, bookmarkPostService, communityJoinService, userInfoService, $upload, $timeout, usSpinnerService){
	
	log("CommunityPostController starts");
	
    var firstBatchLoaded = false;
    var offset = 0;
    var time = 0;
    var noMore = false;
    
	$scope.posts = communityPageService.Posts.get({id:$routeParams.id}, function(){
        //log("===> get first batch posts completed");
        firstBatchLoaded = true;
        usSpinnerService.stop('loading...');
    });
    
    $scope.nextPosts = function() {
        //log("===> nextPosts:isBusy="+$scope.isBusy+"|offsetq="+offsetq+"|time="+time+"|firstBatchLoaded="+firstBatchLoaded+"|noMore="+noMore);
        if ($scope.isBusy) return;
        if (!firstBatchLoaded) return;
        if (noMore) return;
        $scope.isBusy = true;
        if(angular.isObject($scope.posts.posts) && $scope.posts.posts.length > 0) {
            time = $scope.posts.posts[$scope.posts.posts.length - 1].t;
            //log("===> set time:"+time);
        }
        communityPageService.GetPosts.get({id:$routeParams.id,offset:offset,time:time}, function(data){
            var posts = data;
            if(data.length == 0) {
                noMore = true;
            }
            
            for (var i = 0; i < posts.length; i++) {
                $scope.posts.posts.push(posts[i]);
            }
            $scope.isBusy = false;
            offset++;
        });
    }
    
    $scope.displayLink = function(link) {
        var link = $scope.applicationInfo.baseUrl + link;
        
        bootbox.dialog({
            message: 
                "<input style='width:85%;padding:3px;' type='text' name='post-link' id='post-link' value="+link+" readonly></input>" + 
                "<a style='margin-left:5px;padding:2px 7px;font-size:14px;' class='toolsbox toolsbox-single' onclick='highlightLink(\"post-link\")'><i class='glyphicon glyphicon-link'></i></a>",
            title: "",
            className: "post-bootbox-modal post-copy-link-modal",
        });
    }
    
	$scope.deletePost = function(postId) {
        //log("deletePost:"+postId);
        postManagementService.deletePost.get({"postId":postId}, function(data) {
            angular.forEach($scope.posts.posts, function(post, key){
                if(post.id == postId) {
                    //log("remove post:"+post.id);
                    $scope.posts.posts.splice($scope.posts.posts.indexOf(post),1);
                }
            })
        });
    }
		
	$scope.postPhoto = function() {
		$("#post-photo-id").click();
	}
	$scope.selectedFiles = [];
	$scope.tempSelectedFiles = [];
	$scope.dataUrls = [];
	
	$scope.get_all_comments = function(id) {
		angular.forEach($scope.posts.posts, function(post, key){
			if(post.id == id) {
				post.cs = allCommentsService.comments.get({id:id});
				post.ep = true;
			}
		});
	}
	
	$scope.onFileSelect = function($files) {
		
		if($scope.selectedFiles.length == 0) {
			$scope.tempSelectedFiles = [];
		}
		
		$scope.selectedFiles.push($files);
		$scope.tempSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.dataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}
	
	$scope.comment_on_post = function(id, commentText) {
        // first convert to links
        commentText = convertToLinks(commentText);

		var data = {
			"post_id" : id,
			"commentText" : commentText,
			"withPhotos" : $scope.commentSelectedFiles.length != 0
		};
		var post_data = data;
		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(comment_id) {
				$('.commentBox').val('');
				
				$scope.commentText = "";
				angular.forEach($scope.posts.posts, function(post, key){
						if(post.id == data.post_id) {
							post.n_c++;
							post.ut = new Date();
							var comment = {"oid" : $scope.posts.lu, "d" : commentText, "on" : $scope.posts.lun,
									"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
							post.cs.push(comment);
							
							if($scope.commentSelectedFiles.length == 0) {
								return;
							}
							
							$scope.commentSelectedFiles = [];
							$scope.commentDataUrls = [];
							
							// when post is done in BE then do photo upload
							log($scope.commentTempSelectedFiles.length);
							for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
								usSpinnerService.spin('loading...');
								$upload.upload({
									url : '/image/uploadCommentPhoto',
									method: $scope.httpMethod,
									data : {
										commentId : comment_id
									},
									file: $scope.commentTempSelectedFiles[i],
									fileFormDataName: 'comment-photo'
								}).success(function(data, status, headers, config) {
									$scope.commentTempSelectedFiles.length = 0;
									if(post.id == post_data.post_id) {
										angular.forEach(post.cs, function(cmt, key){
											if(cmt.id == comment_id) {
												cmt.hasImage = true;
												if(cmt.imgs) {
													
												} else {
													cmt.imgs = [];
												}
												cmt.imgs.push(data);
											}
										});
									}
								});
						}
					}
			});
			usSpinnerService.stop('loading...');	
		});
	};
	
	$scope.remove_image_from_comment = function(index) {
		$scope.commentSelectedFiles.splice(index, 1);
		$scope.commentTempSelectedFiles.splice(index, 1);
		$scope.commentDataUrls.splice(index, 1);
	}
	
	$scope.post_on_community = function(id, postText) {
        // first convert to links
		postText = convertToLinks(postText);

		usSpinnerService.spin('loading...');
		var data = {
			"community_id" : id,
			"postText" : postText,
			"withPhotos" : $scope.selectedFiles.length != 0
		};
		
		$scope.postText="";
		
		$http.post('/community/post', data)// first create post with post text.
			.success(function(post_id) {
				usSpinnerService.stop('loading...');
				$('.postBox').val('');
				$scope.postText = "";
				var post = {"oid" : $scope.posts.lu, "pt" : postText, "cn" : $scope.posts.n,
						"isLike" : false, "nol" : 0, "p" : $scope.posts.lun, "t" : new Date(), "n_c" : 0, "id" : post_id, "cs": []};
				$scope.posts.posts.unshift(post);
				
				if($scope.selectedFiles.length == 0) {
					return;
				}
				
				$scope.selectedFiles = [];
				$scope.dataUrls = [];
				
				// when post is done in BE then do photo upload
				for(var i=0 ; i<$scope.tempSelectedFiles.length ; i++) {
					usSpinnerService.spin('loading...');
					$upload.upload({
						url : '/image/uploadPostPhoto',
						method: $scope.httpMethod,
						data : {
							postId : post_id
						},
						file: $scope.tempSelectedFiles[i],
						fileFormDataName: 'post-photo'
					}).success(function(data, status, headers, config) {
						usSpinnerService.stop('loading...');
						angular.forEach($scope.posts.posts, function(post, key){
							if(post.id == post_id) {
								post.hasImage = true;
								if(post.imgs) { 
								} else {
									post.imgs = [];
								}
								post.imgs.push(data);
							}
						});
					});
					
				}
		});
	};

	$scope.remove_image = function(index) {
		$scope.selectedFiles.splice(index, 1);
		$scope.tempSelectedFiles.splice(index, 1);
		$scope.dataUrls.splice(index, 1);
	}
	
	$scope.bookmarkPost = function(post_id) {
		bookmarkPostService.bookmarkPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = true;
				}
			})
		});
	}
	
	$scope.unBookmarkPost = function(post_id) {
		bookmarkPostService.unbookmarkPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = false;
				}
			})
		});
	}
		
	$scope.like_post = function(post_id) {
		likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=true;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					post.nol--;
					post.isLike=false;
				}
			})
		});
	}
	
	$scope.like_comment = function(post_id,comment_id) {
		likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol++;
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
	$scope.unlike_comment = function(post_id,comment_id) {
		likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol--;
							comment.isLike=false;
						}
					})
				}
			})
		});
	}
	
	$scope.commentPhoto = function(post_id) {
		$("#comment-photo-id").click();
		$scope.commentedOnPost = post_id ;
	} 
	
	$scope.commentSelectedFiles = [];
	$scope.commentTempSelectedFiles = [];
	$scope.commentDataUrls = [];
	
	$scope.onCommentFileSelect = function($files) {
		log($scope.commentSelectedFiles.length);
		if($scope.commentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.commentTempSelectedFiles = [];
		}
		
		$scope.commentSelectedFiles.push($files);
		log($scope.commentSelectedFiles);
		$scope.commentTempSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.commentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}
	
	log("CommunityPostController completed");
});
///////////////////////// Community Page  End ////////////////////////////////

///////////////////////// Community QnA Page Start ////////////////////////////////
minibean.service('communityQnAPageService',function($resource){
	this.QnAs = $resource(
			'/communityQnA/questions/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
	this.GetQnAs = $resource(
			'/questions?id=:id&offset=:offset&time=:time',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id',offset:'@offset',time:'@time'},isArray:true}
			}
	);
});

minibean.service('allAnswersService',function($resource){
	this.answers = $resource(
			'/answers/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'},isArray:true}
			}
	);
});

minibean.controller('CommunityQnAController',function($scope, postManagementService, bookmarkPostService, likeFrameworkService, allAnswersService, communityQnAPageService, usSpinnerService ,$timeout, $routeParams, $http,  $upload, $validator){
    log("CommunityQnAController starts");

    var firstBatchLoaded = false;
    var offsetq = 0;
    var time = 0;
    var noMore = false;
    
    $scope.QnAs = communityQnAPageService.QnAs.get({id:$routeParams.id}, function(){
        //log("===> get first batch QnAs completed");
        firstBatchLoaded = true;
        usSpinnerService.stop('loading...');
    });
	
    $scope.nextPosts = function() {
        //log("===> nextPosts:isBusy="+$scope.isBusy+"|offsetq="+offsetq+"|time="+time+"|firstBatchLoaded="+firstBatchLoaded+"|noMore="+noMore);
        if ($scope.isBusy) return;
        if (!firstBatchLoaded) return;
        if (noMore) return;
        $scope.isBusy = true;
        if(angular.isObject($scope.QnAs.posts) && $scope.QnAs.posts.length > 0) {
            time = $scope.QnAs.posts[$scope.QnAs.posts.length - 1].t;
            //log("===> set time:"+time);
        }
        communityQnAPageService.GetQnAs.get({id:$routeParams.id,offset:offsetq,time:time}, function(data){
            var posts = data;
            if(data.length == 0) {
                noMore = true;
            }
            
            for (var i = 0; i < posts.length; i++) {
                $scope.QnAs.posts.push(posts[i]);
            }
            $scope.isBusy = false;
            offsetq++;
        });
        
    }
    
	$scope.displayLink = function(link) {
        var link = $scope.applicationInfo.baseUrl + link;
        
        bootbox.dialog({
            message: 
                "<input style='width:85%;padding:3px;' type='text' name='post-link' id='post-link' value="+link+" readonly></input>" + 
                "<a style='margin-left:5px;padding:2px 7px;font-size:14px;' class='toolsbox toolsbox-single' onclick='highlightLink(\"post-link\")'><i class='glyphicon glyphicon-link'></i></a>",
            title: "",
            className: "post-bootbox-modal post-copy-link-modal",
        });
    }
    
	$scope.deletePost = function(postId) {
        postManagementService.deletePost.get({"postId":postId}, function(data) {
            angular.forEach($scope.QnAs.posts, function(post, key){
                if(post.id == postId) {
                    $scope.QnAs.posts.splice($scope.QnAs.posts.indexOf(post),1);
                }
            })
        });
    }
    
	$scope.postPhoto = function() {
		$("#QnA-photo-id").click();
	}
	
	$scope.get_all_answers = function(id) {
		angular.forEach($scope.QnAs.posts, function(post, key){
			if(post.id == id) {
				post.cs = allAnswersService.answers.get({id:id});
				post.ep = true;
			}
		});
	}
	
	// Right now community-qna-bar.html > qna-bar.html is using CommunityQnAController
	// and home-news-feed.html > qna-bar.html is using CommunityPageController
	// and qna-bar.html is calling get_all_comments() instead of get_all_answers() 
	// such that it works in all places
	// Assign the dummy get_all_comments here... needs refactoring... 
	 
	$scope.get_all_comments = $scope.get_all_answers;
	
	$scope.QnASelectedFiles = [];
	$scope.dataUrls = [];
	$scope.tempSelectedFiles = [];
	
	$scope.qnaCommentPhoto = function(post_id) {
		$("#qna-comment-photo-id").click();
		$scope.commentedOnPost = post_id ;
	};
	
	$scope.qnaCommentSelectedFiles = [];
	$scope.qnaTempCommentSelectedFiles = [];
	$scope.qnaCommentDataUrls = [];
	
	$scope.onQnACommentFileSelect = function($files) {
		log($scope.qnaCommentSelectedFiles.length);
		if($scope.qnaCommentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.qnaTempCommentSelectedFiles = [];
		}
		
		$scope.qnaCommentSelectedFiles.push($files);
		log($scope.qnaCommentSelectedFiles);
		$scope.qnaTempCommentSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.qnaCommentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}
	
	$scope.ask_question_community = function(id, questionTitle, questionText) {
        // first convert to links
        questionText = convertToLinks(questionText);

		usSpinnerService.spin('loading...');
		var data = {
				"community_id" : id,
				"questionTitle" : questionTitle,
				"questionText" : questionText,
				"withPhotos" : $scope.QnASelectedFiles.length != 0
			};
		
		$http.post('/communityQnA/question/post', data)// first create post with question text.
			.success(function(post_id) {
				usSpinnerService.stop('loading...');
				$('.postBox').val('');
				var post = {"oid" : $scope.QnAs.lu, "ptl" : questionTitle, "pt" : questionText, "cn" : $scope.community.n, 
						"isLike" : false, "nol" : 0, "p" : $scope.QnAs.lun, "t" : new Date(), "n_c" : 0, "id" : post_id, "cs": []};
				$scope.QnAs.posts.unshift(post);
				
				if($scope.QnASelectedFiles.length == 0) {
					return;
				}
				
				$scope.QnASelectedFiles = [];
				$scope.dataUrls = [];
				
				
				// when post is done in BE then do photo upload
				for(var i=0 ; i<$scope.tempSelectedFiles.length ; i++) {
					usSpinnerService.spin('loading...');
					$upload.upload({
						url : '/image/uploadPostPhoto',
						method: $scope.httpMethod,
						data : {
							postId : post_id
						},
						file: $scope.tempSelectedFiles[i],
						fileFormDataName: 'post-photo'
					}).success(function(data, status, headers, config) {
						usSpinnerService.stop('loading...');
						angular.forEach($scope.QnAs.posts, function(post, key){
							if(post.id == post_id) {
								post.hasImage = true;
								if(post.imgs) { 
								} else {
									post.imgs = [];
								}
								post.imgs.push(data);
							}
						});
					});
					
				}
				
				
		});
	};
	
	$scope.remove_image_from_qna_comment = function(index) {
		$scope.qnaCommentSelectedFiles.splice(index, 1);
		$scope.qnaTempCommentSelectedFiles.splice(index, 1);
		$scope.qnaCommentDataUrls.splice(index, 1);
	};
	
	$scope.answer_to_question = function(question_post_id, answerText) {
		// first convert to links
        answerText = convertToLinks(answerText);

        var data = {
			"post_id" : question_post_id,
			"answerText" : answerText,
			"withPhotos" : $scope.qnaCommentSelectedFiles.length != 0
		};
		
		var post_data = data;
		$http.post('/communityQnA/question/answer', data) 
			.success(function(answer_id) {
				$('.commentBox').val('');
				angular.forEach($scope.QnAs.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var answer = {"oid" : $scope.QnAs.lu, "d" : answerText, "on" : $scope.QnAs.lun, 
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : answer_id};
                        post.cs.push(answer);
					  
						if($scope.qnaCommentSelectedFiles.length == 0) {
							return;
						}
						
						$scope.qnaCommentSelectedFiles = [];
						$scope.qnaCommentDataUrls = [];
					
						// when post is done in BE then do photo upload
						log($scope.qnaTempCommentSelectedFiles.length);
						for(var i=0 ; i<$scope.qnaTempCommentSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadQnACommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : answer_id
								},
								file: $scope.qnaTempCommentSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.qnaTempCommentSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == answer_id) {
											cmt.hasImage = true;
											if(cmt.imgs) {
												
											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
					}
				}
			
				usSpinnerService.stop('loading...');
			});
		});
	}
	
	$scope.onQnAFileSelect = function($files) {
		if($scope.QnASelectedFiles.length == 0) {
			$scope.tempSelectedFiles = [];
		}
		$scope.tempSelectedFiles.push($files);
		$scope.QnASelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.dataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}
	
	$scope.remove_image = function(index) {
		$scope.QnASelectedFiles.splice(index, 1);
		$scope.dataUrls.splice(index, 1);
	}
	
	$scope.like_post = function(post_id) {
		likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.QnAs.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=true;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.QnAs.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=false;
					post.nol--;
				}
			})
		});
	}

	$scope.like_comment = function(post_id,comment_id) {
		likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.QnAs.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol++;
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
	$scope.unlike_comment = function(post_id,comment_id) {
		likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.QnAs.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol--;
							comment.isLike=false;
						}
					})
				}
			})
		});
	}
	
	$scope.bookmarkPost = function(post_id) {
		bookmarkPostService.bookmarkPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.QnAs.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = true;
				}
			})
		});
	}
	
	$scope.unBookmarkPost = function(post_id) {
		bookmarkPostService.unbookmarkPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.QnAs.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = false;
				}
			})
		});
	}
	
	log("CommunityQnAController completed");
});


///////////////////////// Community QnA Page End ////////////////////////////////
minibean.controller('CreateArticleController',function($scope,$http,usSpinnerService, articleCategoryService){
    log("CreateArticleController starts");

	$scope.article;
	$scope.submitBtn = "Save";
	
	var range = [];
	for(var i=0;i<100;i++) {
		  range.push(i);
	}
	$scope.targetAge = range;
	//Refer to http://www.tinymce.com/
	$scope.tinymceOptions = {
			selector: "textarea",
		    plugins: [
						"advlist autolink lists link image charmap print preview anchor",
						"searchreplace visualblocks code fullscreen",
						"insertdatetime media table contextmenu paste"
		    ],
		    toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
	}
	
	$scope.articleCategories = articleCategoryService.getAllArticleCategory.get();
	
	$scope.select_category = function(id, name, pn) {
		$scope.category_id = id;
		$scope.category_picture = pn;
		$scope.category_name = name;
		$scope.formData.category_id= id;
		$scope.isChosen = true;
	}
	$scope.submit = function() {
		usSpinnerService.spin('loading...');
		$http.post('/createArticle', $scope.formData).success(function(data){
			$scope.submitBtn = "Complete";
			usSpinnerService.stop('loading...');
		});
	}
	
	log("CreateArticleController completed");
});

minibean.service('articleCategoryService',function($resource){
	this.getAllArticleCategory = $resource(
			'/getAllArticleCategory',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
});


minibean.service('allArticlesService',function($resource){
	this.AllArticles = $resource(
			'/get-all-Articles',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
	this.NewArticles = $resource(
			'/get-new-Articles',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
	this.HotArticles = $resource(
			'/get-hot-Articles',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
    this.RecommendedArticles = $resource(
            '/get-recommended-Articles',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );	
	this.ArticleCategorywise = $resource(
			'/get-Articles-Categorywise/:id/:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
	this.SixArticles = $resource(
			'/get-Six-Articles',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('getDescriptionService',function($resource){
	this.GetDescription = $resource(
			'/getDescriptionOfArticle/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('allRelatedArticlesService',function($resource){
	this.getRelatedArticles = $resource(
			'/get-Related-Articles/:id/:category_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get',isArray:true}
			}
	);
});

minibean.service('deleteArticleService',function($resource){
	this.DeleteArticle = $resource(
			'/deleteArticle/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('showImageService',function($resource){
	this.getImage = $resource(
			'/get-image-url/:id',
			{alt:'json',callback:'JSON_CALLBACK', },
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('ArticleSliderController', function($scope, $modal, $routeParams, showImageService, usSpinnerService, allArticlesService){
    log("ArticleSliderController starts");
  
    $scope.resultSlider = allArticlesService.SixArticles.get({}, function() {
        $scope.image_source = $scope.resultSlider.la[0].img_url;
        $scope.description = $scope.resultSlider.la[0].lds;
        $scope.title = $scope.resultSlider.la[0].nm;
        $scope.category_id = $scope.resultSlider.la[0].id;
    });
    
    $scope.changeInsideImage = function(article_id) {
        angular.forEach($scope.resultSlider.la, function(element, key){
            if(element.id == article_id) {
                $scope.image_source = element.img_url;
                $scope.description = element.lds;
                $scope.title = element.nm;
                $scope.category_id = element.id;
            }
        })
        angular.forEach($scope.resultSlider.ra, function(element, key){
            if(element.id == article_id) {
                $scope.image_source = element.img_url;
                $scope.description = element.lds;
                $scope.title = element.nm;
                $scope.category_id = element.id;
            }
        })
    };
    
    log("ArticleSliderController completed");
});

minibean.controller('ShowArticleController',function($scope, $modal, $routeParams, bookmarkPostService, likeFrameworkService, usSpinnerService, articleService, allArticlesService, allRelatedArticlesService){
    log("ShowArticleController starts");
    
    $scope.hotArticles = allArticlesService.HotArticles.get();
    $scope.recommendedArticles = allArticlesService.RecommendedArticles.get();
    $scope.newArticles = allArticlesService.NewArticles.get();
    
    $scope.article = articleService.ArticleInfo.get({id:$routeParams.id}, function(response) {
        if(response[0] == 'NO_RESULT'){
            $location.path('/article/show/0');
        }
        $scope.relatedResult = allRelatedArticlesService.getRelatedArticles.get({id:$routeParams.id, category_id:response.ct.id});
    });
    
    $scope.like_article = function(article_id) {
        likeFrameworkService.hitLikeOnArticle.get({"article_id":article_id}, function(data) {
            $scope.article.nol++;
            $scope.article.isLike=true;
        });
    }

    $scope.unlike_article = function(article_id) {
        likeFrameworkService.hitUnlikeOnArticle.get({"article_id":article_id}, function(data) {
            $scope.article.nol--;
            $scope.article.isLike=false;
        });
    }
    
    $scope.bookmarkArticle = function(article_id) {
        bookmarkPostService.bookmarkArticle.get({"article_id":article_id}, function(data) {
            $scope.article.isBookmarked = true;
        });
    }
    
    $scope.unBookmarkArticle = function(article_id) {
        bookmarkPostService.unbookmarkArticle.get({"article_id":article_id}, function(data) {
            $scope.article.isBookmarked = false;
        });
    }
    
    log("ShowArticleController completed");
});

minibean.controller('ShowArticleControllerNew',function($scope, $modal,$routeParams, bookmarkPostService, articleCategoryService, showImageService, usSpinnerService, deleteArticleService, allArticlesService, getDescriptionService) {
    log("ShowArticleControllerNew starts");

	$scope.result = [];
	
	$scope.hotArticles = allArticlesService.HotArticles.get();
	$scope.recommendedArticles = allArticlesService.RecommendedArticles.get();
	$scope.newArticles = allArticlesService.NewArticles.get();
	
	var offset = 0;
	var noMore = false;
	$scope.articleCategories = articleCategoryService.getAllArticleCategory.get();
	$scope.get_result = function(catId) {
		usSpinnerService.spin('loading...');
		$scope.isBusy = true;
		$scope.result = allArticlesService.ArticleCategorywise.get({id:catId, offset: offset}, function(data) {
			var count = 1;
			offset++;
			angular.forEach($scope.result, function(element, key){
					if(count == 1) {
						$scope.desc = element.ds;
						$scope.article1 = element;
					}
					if(count == 2) {
						$scope.article2 = element;
					}
					if(count == 3) {
						$scope.article3 = element;
					}
					count++;
				})
				if ($scope.result.length == 0){
					noMore = true;
				}
			$scope.categoryImage = $scope.result[0].category_url;
			$scope.categoryName = $scope.result[0].ct.name;
				if(catId == 0) {
					$scope.allCategory = true;
					$scope.oneCategory = false;
				}
				else
				{
					$scope.allCategory = false;
					$scope.oneCategory = true;
				}
			$scope.threeCategory = true;
			$scope.isBusy = false;
			usSpinnerService.stop('loading...');
	    });
	    
	 };

    $scope.bookmarkArticle = function(article_id) {
        bookmarkPostService.bookmarkArticle.get({"article_id":article_id}, function(data) {
            angular.forEach($scope.result, function(article, key){
                if(article.id == article_id) {
                    article.isBookmarked = true;
                }
            })
        });
    }
		
    $scope.unBookmarkArticle = function(article_id) {
    	bookmarkPostService.unbookmarkArticle.get({"article_id":article_id}, function(data) {
    		angular.forEach($scope.result, function(article, key){
    			if(article.id == article_id) {
    				article.isBookmarked = false;
    			}
    		})
    	});
    }
	 
	var catId = $routeParams.catid;
	
	if(catId == 0 || catId == undefined) {
		$scope.get_result(catId);
	}
	else{
		$scope.get_result(catId);
	}
	$scope.next_result = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		$scope.isBusy = true;
		log($scope.result);
		usSpinnerService.spin('loading...');
		allArticlesService.ArticleCategorywise.get({id:catId, offset: offset},
			function(data){
			log(data);
				var posts = data;
				if(posts.length == 0) {
					noMore = true;
				}
				
				for (var i = 0; i < posts.length; i++) {
					$scope.result.push(posts[i]);
			    }
				log($scope.result);
			    $scope.isBusy = false;
			    offset++;
			    usSpinnerService.stop('loading...');
			}
		);
	}
	
	log("ShowArticleControllerNew completed");
});

minibean.service('articleService',function($resource){
	this.ArticleInfo = $resource(
			'/article/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('articleCommentsService',function($resource){
	this.comments = $resource(
			'/ArticleComments/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
});

minibean.controller('EditArticleController',function($scope,$routeParams,$location, bookmarkPostService, likeFrameworkService, articleCommentsService, userInfoService, usSpinnerService, articleService, articleCategoryService,allRelatedArticlesService,$http){
	log("EditArticleController starts");
	
	$scope.submitBtn = "Save";
	$scope.userInfo = userInfoService.UserInfo.get();
	
	$scope.article = articleService.ArticleInfo.get({id:$routeParams.id}, function(response) {
		if(response[0] == 'NO_RESULT'){
			$location.path('/article/show/0');
		}
		$scope.relatedResult = allRelatedArticlesService.getRelatedArticles.get({id:$routeParams.id, category_id:response.ct.id});
	});
	$scope.articleCategories = articleCategoryService.getAllArticleCategory.get();
	
    $scope.open = function(id) {
        var modalInstance = $modal.open({
            templateUrl: 'myModalContent.html',
        });
        var msg = getDescriptionService.GetDescription.get({id:id}, function(data) {
            log(data.description);
            $('.modal-body').html(data.description);
        });
    };
	
	$scope.tinymceOptions = {
			selector: "textarea",
		    plugins: [
		        "advlist autolink lists link image charmap print preview anchor",
		        "searchreplace visualblocks code fullscreen",
		        "insertdatetime media table contextmenu paste"
		    ],
		    toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
	}
	
	$scope.select_category = function(id, name, pn) {
		$scope.article.ct.id = id;
		$scope.article.ct.name = name;
		$scope.article.ct.pictureName = pn;
		$scope.isChosen = true;
	}
	
	$scope.updateArticleData = function(data) {
		usSpinnerService.spin('loading...');
		return $http.post('/editArticle', $scope.article).success(function(data){
			$scope.submitBtn = "Complete";
			usSpinnerService.stop('loading...');
		});
	}
	
    $scope.get_all_comments = function(id) {
        $scope.article.cs = articleCommentsService.comments.get({id:id});
    }
	
	$scope.comment_on_article = function(id, commentText) {
		var data = {
			"article_id" : id,
			"commentText" : commentText
		};
		usSpinnerService.spin('loading...');
		$http.post('/article/comment', data) 
			.success(function(comment_id) {
				$scope.article.n_c++;
				var comment = {"oid" : $scope.userInfo.id, "d" : commentText, "on" : $scope.userInfo.displayName, 
						"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" :$scope.article.n_c, "id" : comment_id};
				$scope.article.cs.push(comment);
				usSpinnerService.stop('loading...');	
		});
	};
	
    $scope.like_article = function(article_id) {
        likeFrameworkService.hitLikeOnArticle.get({"article_id":article_id}, function(data) {
            $scope.article.nol++;
            $scope.article.isLike=true;
        });
    }

    $scope.unlike_article = function(article_id) {
        likeFrameworkService.hitUnlikeOnArticle.get({"article_id":article_id}, function(data) {
            $scope.article.nol--;
            $scope.article.isLike=false;
        });
    }
	
	$scope.bookmarkArticle = function(article_id) {
		bookmarkPostService.bookmarkArticle.get({"article_id":article_id}, function(data) {
			$scope.article.isBookmarked = true;
		});
	}
	
	$scope.unBookmarkArticle = function(article_id) {
		bookmarkPostService.unbookmarkArticle.get({"article_id":article_id}, function(data) {
			$scope.article.isBookmarked = false;
		});
	}
	
	log("EditArticleController completed");
});

minibean.service('newsFeedService',function($resource){
	this.NewsFeeds = $resource(
			'/get-newsfeeds/:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{offset:'@offset'}}
			}
	);
});

minibean.controller('NewsFeedController', function($scope, postManagementService, bookmarkPostService, likeFrameworkService, $timeout, $upload, $http, allCommentsService, usSpinnerService, newsFeedService, iconsService) {
	log("NewsFeedController starts");
	
	$scope.newsFeeds = { posts: [] };
	
    $scope.displayLink = function(link) {
        var link = $scope.applicationInfo.baseUrl + link;
        
        bootbox.dialog({
            message: 
                "<input type='text' name='post-link' id='post-link' value="+link+"></input>" + 
                "<a style='margin-left:5px;padding:2px 7px;font-size:14px;' class='toolsbox toolsbox-single' onclick='highlightLink(\"post-link\")'><i class='glyphicon glyphicon-link'></i></a>",
            title: "",
            className: "post-bootbox-modal post-copy-link-modal",
        });
    }
    
	$scope.deletePost = function(postId) {
        postManagementService.deletePost.get({"postId":postId}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == postId) {
                    $scope.newsFeeds.posts.splice($scope.newsFeeds.posts.indexOf(post),1);
                }
            })
        });
    }
    
	$scope.get_unread_msg_count();
	
	/*
	$scope.emoticons = iconsService.getEmoticons.get();

    $scope.commentText = "";

    $scope.select_emoticon = function(code) {
        $scope.commentText += code;
        //$("#message-inputfield").val($("#message-inputfield").val() + code);
        //$("#message-inputfield").focus();
    }
    */
    
	$scope.comment_on_post = function(id, commentText) {
        // first convert to links
        commentText = convertToLinks(commentText);

		var data = {
			"post_id" : id,
			"commentText" : commentText,
			"withPhotos" : $scope.commentSelectedFiles.length != 0
		};
		var post_data = data;
		
		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(comment_id) {
				$('.commentBox').val('');
				
				$scope.commentText = "";
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var comment = {"oid" : $scope.userInfo.id, "d" : commentText, "on" : $scope.userInfo.displayName,
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
						post.cs.push(comment);
						
						if($scope.commentSelectedFiles.length == 0) {
							return;
						}
						
						$scope.commentSelectedFiles = [];
						$scope.commentDataUrls = [];
						
						// when post is done in BE then do photo upload
						log($scope.commentTempSelectedFiles.length);
						for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadCommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : comment_id
								},
								file: $scope.commentTempSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.commentTempSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == comment_id) {
											cmt.hasImage = true;
											if(cmt.imgs) {
												
											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
					}
				}
			});
			usSpinnerService.stop('loading...');	
		});
	};
	
	$scope.bookmarkPost = function(post_id) {
		bookmarkPostService.bookmarkPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = true;
				}
			})
		});
	}
	
	$scope.unBookmarkPost = function(post_id) {
		bookmarkPostService.unbookmarkPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = false;
				}
			})
		});
	}
	
	$scope.like_post = function(post_id) {
		likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=true;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=false;
					post.nol--;
				}
			})
		});
	}

	$scope.like_comment = function(post_id,comment_id) {
		likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol++;
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
	$scope.unlike_comment = function(post_id,comment_id) {
		likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol--;
							comment.isLike=false;
						}
					})
				}
			})
		});
	}
	
	$scope.get_all_comments = function(id) {
		angular.forEach($scope.newsFeeds.posts, function(post, key){
			if(post.id == id) {
				post.cs = allCommentsService.comments.get({id:id});
				post.ep = true;
			}
		});
	}

	var noMore = false;
	var offset = 0;
	$scope.nextNewsFeeds = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		$scope.isBusy = true;
		newsFeedService.NewsFeeds.get({offset:offset},
			function(data){
				var posts = data.posts;
				if(posts.length == 0) {
					noMore = true;
				}
				
				for (var i = 0; i < posts.length; i++) {
					$scope.newsFeeds.posts.push(posts[i]);
			    }
			    $scope.isBusy = false;
				offset=offset + 1;
			}
		);
	}

	/***QnA Community ***/
	
	$scope.qnaCommentPhoto = function(post_id) {
		$("#qna-comment-photo-id").click();
		$scope.commentedOnPost = post_id ;
	};
	
	$scope.qnaCommentSelectedFiles = [];
	$scope.qnaTempCommentSelectedFiles = [];
	$scope.qnaCommentDataUrls = [];
	
	$scope.onQnACommentFileSelect = function($files) {
		log($scope.qnaCommentSelectedFiles.length);
		if($scope.qnaCommentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.qnaTempCommentSelectedFiles = [];
		}
		
		$scope.qnaCommentSelectedFiles.push($files);
		log($scope.qnaCommentSelectedFiles);
		$scope.qnaTempCommentSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.qnaCommentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	};
	
	$scope.answer_to_question = function(question_post_id, answerText) {
		// first convert to links
        answerText = convertToLinks(answerText);

        var data = {
			"post_id" : question_post_id,
			"answerText" : answerText,
			"withPhotos" : $scope.qnaCommentSelectedFiles.length != 0
		};
		
		var post_data = data;
		$http.post('/communityQnA/question/answer', data) 
			.success(function(answer_id) {
				$('.commentBox').val('');
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					log(post);
					
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var answer = {"oid" : $scope.userInfo.id, "d" : answerText, "on" : $scope.userInfo.displayName, 
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : answer_id};
						post.cs.push(answer);
                        
						if($scope.qnaCommentSelectedFiles.length == 0) {
							return;
						}
						
						$scope.qnaCommentSelectedFiles = [];
						$scope.qnaCommentDataUrls = [];
					
						// when post is done in BE then do photo upload
						log($scope.qnaTempCommentSelectedFiles.length);
						for(var i=0 ; i<$scope.qnaTempCommentSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadQnACommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : answer_id
								},
								file: $scope.qnaTempCommentSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.qnaTempCommentSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == answer_id) {
											cmt.hasImage = true;
											if(cmt.imgs) {
												
											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
					}
				}
			
				usSpinnerService.stop('loading...');
			});
		});
	};
	
	$scope.remove_image_from_qna_comment = function(index) {
		$scope.qnaCommentSelectedFiles.splice(index, 1);
		$scope.qnaTempCommentSelectedFiles.splice(index, 1);
		$scope.qnaCommentDataUrls.splice(index, 1);
	};
	
	/** images **/
	
	$scope.commentPhoto = function(post_id) {
		$("#comment-photo-id").click();
		$scope.commentedOnPost = post_id;
	}
	$scope.commentSelectedFiles = [];
	$scope.commentTempSelectedFiles = [];
	$scope.commentDataUrls = [];
	
	$scope.onCommentFileSelect = function($files) {
		log($scope.commentSelectedFiles.length);
		if($scope.commentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.commentTempSelectedFiles = [];
		}
		
		$scope.commentSelectedFiles.push($files);
		log($scope.commentSelectedFiles);
		$scope.commentTempSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.commentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}
	
	$scope.remove_image_from_comment = function(index) {
		$scope.commentSelectedFiles.splice(index, 1);
		$scope.commentTempSelectedFiles.splice(index, 1);
		$scope.commentDataUrls.splice(index, 1);
	}
	
	log("NewsFeedController completed");
});
	  
minibean.service('userNewsFeedService',function($resource){

	this.NewsFeedsPosts = $resource(
			'/get-user-newsfeeds-posts/:offset/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{offset:'@offset',id:'@id'}}
			}
	);
	
	this.NewsFeedsComments = $resource(
			'/get-user-newsfeeds-comment/:offset/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{offset:'@offset',id:'@id'}}
			}
	);
});

minibean.controller('UserNewsFeedController', function($scope, $routeParams, $timeout, $upload, postManagementService, bookmarkPostService, likeFrameworkService, userInfoService, $http, allCommentsService, usSpinnerService, userNewsFeedService) {
	log("UserNewsFeedController starts");
	
	$scope.newsFeeds = { posts: [] };
	
	$scope.displayLink = function(link) {
        var link = $scope.applicationInfo.baseUrl + link;
        
        bootbox.dialog({
            message: 
                "<input style='width:85%;padding:3px;' type='text' name='post-link' id='post-link' value="+link+" readonly></input>" + 
                "<a style='margin-left:5px;padding:2px 7px;font-size:14px;' class='toolsbox toolsbox-single' onclick='highlightLink(\"post-link\")'><i class='glyphicon glyphicon-link'></i></a>",
            title: "",
            className: "post-bootbox-modal post-copy-link-modal",
        });
    }
    
	$scope.deletePost = function(postId) {
        postManagementService.deletePost.get({"postId":postId}, function(data) {
            angular.forEach($scope.newsFeeds.posts, function(post, key){
                if(post.id == postId) {
                    $scope.newsFeeds.posts.splice($scope.newsFeeds.posts.indexOf(post),1);
                }
            })
        });
    }
    
	$scope.comment_on_post = function(id, commentText) {
        // first convert to links
        commentText = convertToLinks(commentText);

		var data = {
			"post_id" : id,
			"commentText" : commentText,
			"withPhotos" : $scope.commentSelectedFiles.length != 0
		};
		var post_data = data;
		
		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(comment_id) {
				$('.commentBox').val('');
				
				$scope.commentText = "";
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var comment = {"oid" : $scope.userInfo.id, "d" : commentText, "on" : $scope.userInfo.displayName,
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
						post.cs.push(comment);
						
						if($scope.commentSelectedFiles.length == 0) {
							return;
						}
						
						$scope.commentSelectedFiles = [];
						$scope.commentDataUrls = [];
						
						// when post is done in BE then do photo upload
						log($scope.commentTempSelectedFiles.length);
						for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadCommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : comment_id
								},
								file: $scope.commentTempSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.commentTempSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == comment_id) {
											cmt.hasImage = true;
											if(cmt.imgs) {
												
											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
					}
				}
				usSpinnerService.stop('loading...');	
			});
		});
	};
	
	$scope.qnaCommentPhoto = function(post_id) {
		$("#qna-comment-photo-id").click();
		$scope.commentedOnPost = post_id ;
	};
	
	$scope.qnaCommentSelectedFiles = [];
	$scope.qnaTempCommentSelectedFiles = [];
	$scope.qnaCommentDataUrls = [];
	
	$scope.onQnACommentFileSelect = function($files) {
		log($scope.qnaCommentSelectedFiles.length);
		if($scope.qnaCommentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.qnaTempCommentSelectedFiles = [];
		}
		
		$scope.qnaCommentSelectedFiles.push($files);
		log($scope.qnaCommentSelectedFiles);
		$scope.qnaTempCommentSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.qnaCommentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	};
	
	$scope.answer_to_question = function(question_post_id, answerText) {
		// first convert to links
        answerText = convertToLinks(answerText);

        var data = {
			"post_id" : question_post_id,
			"answerText" : answerText,
			"withPhotos" : $scope.qnaCommentSelectedFiles.length != 0
		};
		
		var post_data = data;
		$http.post('/communityQnA/question/answer', data) 
			.success(function(answer_id) {
				$('.commentBox').val('');
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var answer = {"oid" : $scope.userInfo.id, "d" : answerText, "on" : $scope.userInfo.displayName, 
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : answer_id};
                        post.cs.push(answer);
                        
						if($scope.qnaCommentSelectedFiles.length == 0) {
							return;
						}
						
						$scope.qnaCommentSelectedFiles = [];
						$scope.qnaCommentDataUrls = [];
					
						// when post is done in BE then do photo upload
						log($scope.qnaTempCommentSelectedFiles.length);
						for(var i=0 ; i<$scope.qnaTempCommentSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadQnACommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : answer_id
								},
								file: $scope.qnaTempCommentSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.qnaTempCommentSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == answer_id) {
											cmt.hasImage = true;
											if(cmt.imgs) {
												
											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
					}
				}
			
				usSpinnerService.stop('loading...');
			});
		});
	};
	
	$scope.remove_image_from_qna_comment = function(index) {
		$scope.qnaCommentSelectedFiles.splice(index, 1);
		$scope.qnaTempCommentSelectedFiles.splice(index, 1);
		$scope.qnaCommentDataUrls.splice(index, 1);
	};
	/**images **/
	
	$scope.commentPhoto = function(post_id) {
		$("#comment-photo-id").click();
		$scope.commentedOnPost = post_id;
	}
	$scope.commentSelectedFiles = [];
	$scope.commentTempSelectedFiles = [];
	$scope.commentDataUrls = [];
	
	$scope.onCommentFileSelect = function($files) {
		log($scope.commentSelectedFiles.length);
		if($scope.commentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.commentTempSelectedFiles = [];
		}
		
		$scope.commentSelectedFiles.push($files);
		log($scope.commentSelectedFiles);
		$scope.commentTempSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.commentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}
	
	$scope.remove_image_from_comment = function(index) {
		$scope.commentSelectedFiles.splice(index, 1);
		$scope.commentTempSelectedFiles.splice(index, 1);
		$scope.commentDataUrls.splice(index, 1);
	}
	
	$scope.get_all_comments = function(id) {
		angular.forEach($scope.newsFeeds.posts, function(post, key){
			if(post.id == id) {
				post.cs = allCommentsService.comments.get({id:id});
				post.ep = true;
			}
		});
	}
	
	$scope.bookmarkPost = function(post_id) {
		bookmarkPostService.bookmarkPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = true;
				}
			})
		});
	}
	
	$scope.unBookmarkPost = function(post_id) {
		bookmarkPostService.unbookmarkPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = false;
				}
			})
		});
	}
	
	$scope.like_post = function(post_id) {
		likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=true;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=false;
					post.nol--;
				}
			})
		});
	}
	

	$scope.like_comment = function(post_id,comment_id) {
		likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol++;
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
	$scope.unlike_comment = function(post_id,comment_id) {
		likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol--;
							comment.isLike=false;
						}
					})
				}
			})
		});
	}
	var noMoreC = false;
	var offsetC = 0;
	var noMoreP = false;
	var offsetP = 0;
	
	$scope.setSelectedSubTab = function (iTab) {
        if ($scope.selectedSubTab == iTab)
            return;
            
		$scope.selectedSubTab = iTab;
		$scope.newsFeeds = { posts: [] };
		$scope.isBusyP = false;
		$scope.isBusyC = false;
		noMoreC = false;
		offsetC = 0;
		noMoreP = false;
		offsetP = 0;
	}
	
	var id = $scope.userInfo.id;
	
	// nextNewsFeeds section starts
	$scope.nextNewsFeeds = function() {
		if($routeParams.id != undefined){
			id = $routeParams.id;
		}
		
		if ($scope.isBusyP) return;
		if (noMoreP) return;
		$scope.isBusyP = true;
			userNewsFeedService.NewsFeedsPosts.get({offset:offsetP,id:id},
				function(data){
					
					var posts = data.posts;
					if(posts.length < DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT) {
						noMoreP = true;
						$scope.isBusyP = false;
					}
					
					for (var i = 0; i < posts.length; i++) {
						$scope.newsFeeds.posts.push(posts[i]);
				    }
				    $scope.isBusyP = false;
					offsetP++;
				}
			);
	}
	// nextNewsFeeds section ends
	
	$scope.nextNewsFeedsComments = function() {
		if ($scope.isBusyC) return;
		if (noMoreC) return;
		$scope.isBusyC = true;
		userNewsFeedService.NewsFeedsComments.get({offset:offsetC,id:id},
			function(data){
				
				var posts = data.posts;
				if(posts.length < DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT) {
					noMoreC = true;
					$scope.isBusyC = false;
				}
				
				for (var i = 0; i < posts.length; i++) {
					$scope.newsFeeds.posts.push(posts[i]);
			    }
			    $scope.isBusy = false;
				offsetC++;
			}
		);
	}
	
	log("UserNewsFeedController completed");
});


minibean.service('bookmarkService',function($resource){
	this.bookmarkPost = $resource(
			'/get-bookmark-post/:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{offset:'@offset'}, isArray:true}
			}
	);
	
	this.bookmarkArticle = $resource(
			'/get-bookmark-article/:offsetA',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{offsetA:'@offsetA'}, isArray:true}
			}
	);
	
	this.bookmarkSummary = $resource(
            '/get-bookmark-summary',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

minibean.controller('MyBookmarkController', function($scope, bookmarkPostService, likeFrameworkService, postManagementService, $http, allCommentsService, usSpinnerService, bookmarkService) {
    log("MyBookmarkController starts");
    
    $scope.bookmarkSummary = bookmarkService.bookmarkSummary.get();
    
	$scope.posts = { posts: [] };
	
	$scope.articles = { article: [] };
	
	$scope.selectedSubTab = 1;
	
	$scope.displayLink = function(link) {
        var link = $scope.applicationInfo.baseUrl + link;
        
        bootbox.dialog({
            message: 
                "<input style='width:85%;padding:3px;' type='text' name='post-link' id='post-link' value="+link+" readonly></input>" + 
                "<a style='margin-left:5px;padding:2px 7px;font-size:14px;' class='toolsbox toolsbox-single' onclick='highlightLink(\"post-link\")'><i class='glyphicon glyphicon-link'></i></a>",
            title: "",
            className: "post-bootbox-modal post-copy-link-modal",
        });
    }
    
	$scope.deletePost = function(postId) {
        postManagementService.deletePost.get({"postId":postId}, function(data) {
            angular.forEach($scope.posts.posts, function(post, key){
                if(post.id == postId) {
                    $scope.posts.posts.splice($scope.posts.posts.indexOf(post),1);
                }
            })
        });
    }
    
	$scope.comment_on_post = function(id, commentText) {
        // first convert to links
        commentText = convertToLinks(commentText);

		var data = {
			"post_id" : id,
			"commentText" : commentText
		};
		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(comment_id) {
				$('.commentBox').val('');
				
				$scope.commentText = "";
				angular.forEach($scope.posts.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var comment = {"oid" : $scope.userInfo.id, "d" : commentText, "on" : $scope.userInfo.displayName,
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
						post.cs.push(comment);
				}
				usSpinnerService.stop('loading...');	
			});
		});
	};
	
	$scope.answer_to_question = $scope.comment_on_post;
	
	$scope.unBookmarkPost = function(post_id) {
		bookmarkPostService.unbookmarkPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = false;
					$scope.posts.posts.splice($scope.posts.posts.indexOf(post),1);
					if (post.type == 'QUESTION')
    					$scope.bookmarkSummary.qc--;
    			     else if (post.type == 'SIMPLE')
                        $scope.bookmarkSummary.pc--;
				}
			})
		});
	}
	
	$scope.unBookmarkArticle = function(article_id) {
		bookmarkPostService.unbookmarkArticle.get({"article_id":article_id}, function(data) {
			angular.forEach($scope.articles.article, function(article, key){
				if(article.id == article_id) {
					article.isBookmarked = false;
					$scope.articles.article.splice($scope.articles.article.indexOf(article),1);
                    $scope.bookmarkSummary.ac--;
				}
			})
		});
	}
	
	$scope.like_post = function(post_id) {
		likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=true;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=false;
					post.nol--;
				}
			})
		});
	}

	$scope.like_comment = function(post_id,comment_id) {
		likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol++;
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
	$scope.unlike_comment = function(post_id,comment_id) {
		likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol--;
							comment.isLike=false;
						}
					})
				}
			})
		});
	}
	
	$scope.get_all_comments = function(id) {
		angular.forEach($scope.posts.posts, function(post, key){
			if(post.id == id) {
				post.cs = allCommentsService.comments.get({id:id});
				post.ep = true;
			}
		});
	}

	var offset = 0;
	var noMore = false;
	$scope.nextPosts = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		$scope.isBusy = true;
		bookmarkService.bookmarkPost.get({offset:offset},
            function(data){
    			var posts = data;
    			if(data.length == 0) {
    				noMore = true;
    			}
    			
    			for (var i = 0; i < posts.length; i++) {
    				$scope.posts.posts.push(posts[i]);
    		    }
    			$scope.isBusy = false;
    			offset++;
    		});
	}
	
	var offsetA = 0;
	var noMoreA = false;
	$scope.nextArticles = function() {
		if ($scope.isBusyA) return;
		if (noMoreA) return;
		$scope.isBusyA = true;
		bookmarkService.bookmarkArticle.get({offsetA:offsetA},
            function(data){
    			var articleData = data;
    			if(data.length == 0) {
    				noMoreA = true;
    			}
    			
    			for (var i = 0; i < articleData.length; i++) {
    				$scope.articles.article.push(articleData[i]);
    		    }
    			$scope.isBusyA = false;
    			offsetA++;
    		});
	}
	
	log("MyBookmarkController completed");
});

///////////////////////// User  Converstion //////////////////////////////////

minibean.service('allConversationService',function($resource){
	this.UserAllConversation = $resource(
		'/get-all-Conversation',
		{alt:'json',callback:'JSON_CALLBACK'},
		{
			get: {method:'get',isArray:true}
		}
	);
	
	this.startConversation = $resource(
		'/start-Conversation/:id',
		{alt:'json',callback:'JSON_CALLBACK'},
		{
			get: {method:'get', isArray:true}
		}
	);
	
	this.deleteConversation = $resource(
		'/delete-Conversation/:id',
		{alt:'json',callback:'JSON_CALLBACK'},
		{
			get: {method:'get', isArray:true}
		}
	);
	
});

minibean.service('getMessageService',function($resource){
	this.getMessages = $resource(
		'/get-messages/:id/:offset',
		{alt:'json',callback:'JSON_CALLBACK'},
		{
			get: {method:'get'}
		}
	);
});


minibean.service('searchFriendService',function($resource){
	this.userSearch = $resource(
			'/user-friend-search?query=:q',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{q:'@q'}, isArray:true}
			}
	);
});

minibean.controller('UserConversationController',function($scope, $http, $filter, $timeout, $upload, $routeParams, searchFriendService, usSpinnerService, getMessageService, allConversationService, iconsService) {
    log("UserConversationController starts");

    $scope.emoticons = iconsService.getEmoticons.get();

    $scope.messageText = "";

    $scope.select_emoticon = function(code) {
        $scope.messageText += code;
        //$("#message-inputfield").val($("#message-inputfield").val() + code);
        $("#message-inputfield").focus();
    }
    
	if($routeParams.id == 0){
		$scope.conversations = allConversationService.UserAllConversation.get(function(){
			if($scope.conversations.length > 0){
				$scope.getMessages($scope.conversations[0].id, $scope.conversations[0].uid);
			}
		});
	} else {
		$scope.conversations = allConversationService.startConversation.get({id: $routeParams.id} ,function(){
			$scope.getMessages($scope.conversations[0].id, $scope.conversations[0].uid);
		});
	}
	
	$scope.messages = [];
	$scope.receiverId;
	$scope.currentConversation;
	
	var offset = 0;
	
	$scope.search_friend = function(query) {
		if(query != undefined && query.trim() != '') {
			$scope.searchResult = searchFriendService.userSearch.get({q:query});
		}
	}
	
	$scope.searchReset = function() {
	   $scope.searchResult = [];
	}
	
	$scope.sendPhoto = function() {
        $("#send-photo-id").click();
    }
    $scope.selectedFiles = [];
    $scope.dataUrls = [];
    $scope.tempSelectedFiles = [];
    $scope.onFileSelect = function($files) {
        if($scope.selectedFiles.length == 0) {
            $scope.tempSelectedFiles = [];
        }
        
        $scope.selectedFiles.push($files);
        $scope.tempSelectedFiles.push($files);
        for ( var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.dataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }
    
    $scope.remove_image = function(index) {
    	$scope.selectedFiles = [];
        $scope.dataUrls = [];
        $scope.tempSelectedFiles = [];
    }
	
	$scope.startConversation = function(uid) {
		$scope.receiverId = uid;
		usSpinnerService.spin('loading...');
		allConversationService.startConversation.get({id: uid},
				function(data){
			$scope.conversations = data;
			$scope.getMessages($scope.conversations[0].id, $scope.conversations[0].uid);
			usSpinnerService.stop('loading...');
		});
	}
	
	$scope.deleteConversation = function(cid) {
		usSpinnerService.spin('loading...');
		allConversationService.deleteConversation.get({id: cid},
				function(data){
			$scope.conversations = data;
			$scope.messages = 0;
			$scope.noMore = false;
			usSpinnerService.stop('loading...');
		});
	}
	
    $scope.loadMore = false;
    $scope.getMessages = function(cid, uid) {
        offset = 0;
        $scope.receiverId = uid;
        $scope.currentConversation = cid;
        usSpinnerService.spin('loading...');
        getMessageService.getMessages.get({id: cid,offset: offset},
            function(data){
                $scope.loadMore = true;
                $scope.messages = data.message;
                $scope.unread_msg_count.count = data.counter;
                usSpinnerService.stop('loading...');
                if($scope.messages.length < DefaultValues.CONVERSATION_MESSAGE_COUNT){
                    $scope.loadMore = false;
                }
                offset++;
                $timeout(function(){
                    var objDiv = document.getElementById('message-area');
                    objDiv.scrollTop = objDiv.scrollHeight;
                });
            });
    }
	
	$scope.showImage = function(imageId) {
        $scope.img_id = imageId;
    }
    
	$scope.selectedindex = 0; 
	$scope.setSelectedIndex = function($index) {
		$scope.selectedIndex = $index ;
	}
	
	$scope.nextMessages = function() {
        usSpinnerService.spin('loading...');
        getMessageService.getMessages.get({id: $scope.currentConversation,offset: offset},
            function(data){
                $scope.loadMore = true;
                //log(data);
                var objDiv = document.getElementById('message-area');
                var height = objDiv.scrollHeight;
                //log("nextMessages() - height:"+height);
                var messages = data.message;
                $scope.unread_msg_count.count = data.counter;
                for (var i = 0; i < messages.length; i++) {
                    $scope.messages.push(messages[i]);
                }
                if(data.message.length < DefaultValues.CONVERSATION_MESSAGE_COUNT){
                    $scope.loadMore = false;
                }
                usSpinnerService.stop('loading...');
                offset++;
                $timeout(function(){
                    var objDiv = document.getElementById('message-area');
                    objDiv.scrollTop = objDiv.scrollHeight - height;
                    //log("nextMessages() - message-area.scrollTop:"+objDiv.scrollTop);
                });
            });
    }
	
	$scope.sendMessage = function(msgText) {
        // first convert to links
        msgText = convertToLinks(msgText);

		var data = {
			"receiver_id" : $scope.receiverId,
			"msgText" : msgText,
			"withPhotos" : $scope.selectedFiles.length != 0
		};
		usSpinnerService.spin('loading...');
		$http.post('/Message/sendMsg', data) 
			.success(function(messagedata) {
				//log(messagedata);
				$scope.messages = messagedata.message;
				$scope.conversations = allConversationService.UserAllConversation.get();
				usSpinnerService.stop('loading...');	
				
				$timeout(function(){
        			var objDiv = document.getElementById('message-area');
        			objDiv.scrollTop = objDiv.scrollHeight;
        	    });
				
				if($scope.selectedFiles.length == 0) {
                    return;
                }
                
                $upload.upload({
                    url: '/image/sendMessagePhoto',
                    method: $scope.httpMethod,
                    data: {
                    	messageId : $scope.messages[0].id
                    },
                    file: $scope.tempSelectedFiles[0],
                    fileFormDataName: 'send-photo'
                }).success(function(data, status, headers, config) {
                    usSpinnerService.stop('loading...');
                    angular.forEach($scope.messages, function(message, key){
                        if(message.id == $scope.messages[0].id) {
                        	message.hasImage = true;
                            message.imgs = data;
                        }
                    });
                    $timeout(function(){
            			var objDiv = document.getElementById('message-area');
            			objDiv.scrollTop = objDiv.scrollHeight;
            	    });
            	    $scope.remove_image(0);
                });
		});
	};
	
	$scope.currentHeader = "";
	$scope.createDateHeader = function(msgDate) {
		var date = $filter('date')(new Date(msgDate), 'dd/MM/yyyy');
	    var showHeader = (date != $scope.currentHeader); 
	    $scope.currentHeader = date;
	    log("createDateHeader()");
	    return showHeader;
	}
	
	log("UserConversationController completed");
});
