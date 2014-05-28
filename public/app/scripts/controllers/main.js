'use strict';

var minibean = angular.module('minibean');

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
	$scope.search_result = function(query) {
		if(query != undefined) {
			this.result = searchService.userSearch.get({q:query});
		}
	}
	
	
});
///////////////////////// Search Service End //////////////////////////////////


///////////////////////// User Info Service Start //////////////////////////////////
minibean.service('userInfoService',function($resource){
	this.UserInfo = $resource(
			'/get-user-info',
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
		$scope.userInfo = userInfoService.UserInfo.get();
	  
});

minibean.controller('ApplicationController',function($scope,$location, userInfoService, userNotification, userSimpleNotifications,
	acceptJoinRequestService, acceptFriendRequestService, notificationMarkReadService, usSpinnerService){
	
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.set_background_image = function() {
		return {background : 'url(/get-thumbnail-cover-image-by-id/'+$scope.userInfo.id+')'};
	} 
	$scope.friend_requests = userNotification.getAllFriendRequests.get();
	$scope.join_requests = userSimpleNotifications.getAllJoinRequests.get();
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
		console.log(notification_id);
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
var PhotoModalController = function( $scope, $http, $timeout, $upload, profilePhotoModal) {
	$scope.fileReaderSupported = window.FileReader != null;
	$scope.uploadRightAway = false;
	$scope.isProfileOn = PhotoModalController.isProfileOn;
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
	
	$scope.selectedCoords = function(cords) {
		$scope.cords = cords;	
	}

	$scope.start = function(index) {
		$scope.progress[index] = 0;
		$scope.upload[index] = $upload.upload({
			url : PhotoModalController.url,
			method: $scope.httpMethod,
			data: $scope.cords,
			file: $scope.selectedFiles[index],
			fileFormDataName: 'profile-photo'
		}).success(function(data, status, headers, config) {
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




minibean.controller('UserAboutController',function($routeParams, $scope, userAboutService, $http, profilePhotoModal){
	
	var tab = $routeParams.tab;
	
	if (tab == 'activities' || tab == undefined) {
		$scope.selectedTab = 1;
	}
	
	if (tab == 'friends' ) {
		$scope.selectedTab = 3;
	}
	
	if (tab == 'communities' ) {
		$scope.selectedTab = 2;
	}
	
	if (tab == 'myCommunities' ) {
		$scope.selectedTab = 2;
	}
	
	
	var profileImage = "/get-profile-image";
	var coverImage = "/get-cover-image";
	$scope.isEdit = true;
	$scope.result = userAboutService.UserAbout.get();
	$scope.profileImage = profileImage;
	$scope.coverImage = coverImage;
	
	$scope.genders = [
	                   {value: 'Male', text: 'Male'},
	                   {value: 'Female', text: 'Female'}
	                   ];
	
	
	$scope.updateUserDisplayName = function(data) {
		return $http.post('/updateUserDisplayName', {"displayName" : data});
	}
	
	$scope.updateUserProfileData = function(data) {
		return $http.post('/updateUserProfileData', $scope.result);
	}
	
	
	$scope.isProfileOn = true; 
	$scope.isCoverOn = !$scope.isProfileOn;
	$scope.openProfilePhotoModal = function() {
		PhotoModalController.url = "upload-profile-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		},function() {
			$scope.profileImage = profileImage + "?q="+ Math.random();
		});
		PhotoModalController.isProfileOn = true;
	}
	
	$scope.openCoverPhotoModal = function() {
		PhotoModalController.url = "upload-cover-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		},function() {
			$scope.coverImage = coverImage + "?q="+ Math.random();
		});
		PhotoModalController.isProfileOn = false;
	}
	
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


minibean.controller('GroupController',function($scope,$q, $location,$routeParams, $http, usSpinnerService, iconsService, editCommunityPageService, $upload, profilePhotoModal){
   
	$scope.submitBtn = "Save";
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

	$scope.community.typ = [
	 	                   {value: 'OPEN', text: 'Open'},
	 	                   {value: 'CLOSE', text: 'Close'}
	 	                   ];
	
	$scope.tagetDistrict = [
 		                   {value: 'Pune', text: 'Pune'},
 		                   {value: 'Mumbai', text: 'Mumbai'},
 		                   {value: 'Kolakata', text: 'Kolakata'},
 		                   {value: 'Nagpur', text: 'Nagpur'},
 		                   {value: 'Delhi', text: 'Delhi'},
 		                   {value: 'Surat', text: 'Surat'}
 		                   ];
	 
	
	$scope.IconsToSelects = iconsService.getAllIcons.get();

	$scope.select_icon = function(img, text) {
		$scope.icon_chosen = img;
		$scope.icon_text = text;
		$scope.isChosen = true;
		$scope.community.iconName = img;
	}
	
	$scope.updateGroupProfileData = function(data) {
		usSpinnerService.spin('loading...');
		return $http.post('/updateGroupProfileData', $scope.community).success(function(data){
			$scope.submitBtn = "Done";
			usSpinnerService.stop('loading...');
		});
	}

	$scope.openGroupCoverPhotoModal = function(id) {
		PhotoModalController.url = "upload-cover-photo-group/"+id;
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		},function() {
		
		});
	}
	
});



minibean.controller('CreateCommunityController',function($scope, $location, $http, $upload, $validator, iconsService, usSpinnerService){
	
	$scope.formData = {};
	$scope.selectedFiles =[];
	$scope.submitBtn = "Save";
	$scope.submit = function() {
		 $validator.validate($scope, 'formData')
		    .success(function () {
		    	usSpinnerService.spin('loading...');
		    	$upload.upload({
					url: '/createCommunity',
					method: 'POST',
					file: $scope.selectedFiles[0],
					data: $scope.formData,
					fileFormDataName: 'cover-photo'
				}).progress(function(evt) {
					$scope.submitBtn = "Please Wait";
					usSpinnerService.stop('loading...');
			    }).success(function(data, status, headers, config) {
			    	$scope.submitBtn = "Done";
			    	usSpinnerService.stop('loading...');
			    	$location.path('/community/'+data);
			    	$("#myModal").modal('hide');
			    }).error(function(data, status, headers, config) {
			    	if( status == 505 ) {
			    		$scope.uniqueName = true;
			    		usSpinnerService.stop('loading...');
			    		$scope.submitBtn = "Try Again";
			    	}  
			    });
		    })
		    .error(function () {
		        console.log('error');
		    });
		
	
	}
	
	$scope.IconsToSelects = iconsService.getAllIcons.get();

	$scope.select_icon = function(img, text) {
		$scope.icon_chosen = img;
		$scope.icon_text = text;
		$scope.isChosen = true;
		$scope.formData.iconName = img;
	}
	$scope.showImage = function(imageId) {
		$scope.img_id = imageId;
	}
	$scope.onFileSelect = function($files) {
		$scope.selectedFiles = $files;
		$scope.formData.photo = 'cover-photo';
	}
	
});

///////////////////////// User Friends Widget Service Start //////////////////////////////////
minibean.service('friendWidgetService',function($resource){
	this.UserFriends = $resource(
			'/get-user-friends',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('FriendsWidgetController',function($scope, friendWidgetService,userInfoService, $http){
	$scope.result = friendWidgetService.UserFriends.get();
	$scope.userInfo = userInfoService.UserInfo.get();
});

///////////////////////// My Friends Widget End //////////////////////////////////



///////////////////////// User Friends Widget Service Start //////////////////////////////////
minibean.service('userFriendWidgetService',function($resource){
	this.UserFriends = $resource(
			'/get-user-friends-by-ID/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('UserFriendsWidgetController',function($scope,$routeParams, userFriendWidgetService,userInfoService, $http){
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.result = userFriendWidgetService.UserFriends.get({id:$routeParams.id});
});

///////////////////////// User Friends Widget End //////////////////////////////////


///////////////////////// Suggested Friends Widget Service Start //////////////////////////////////
minibean.service('friendSuggestedWidgetService',function($resource){
	this.UserFriends = $resource(
			'/get-suggested-friends',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('SuggestedFriendsWidgetController',function($scope, unFriendService, usSpinnerService, sendInvitation, friendSuggestedWidgetService,userInfoService, $http){
	$scope.result = friendSuggestedWidgetService.UserFriends.get();
	$scope.isLoadingEnabled = false;
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.send_invite = function(id) {
		$scope.isLoadingEnabled = true;
		this.invite = sendInvitation.inviteFriend.get({id:id}, function(data) {
			$scope.isLoadingEnabled = false;
			angular.forEach($scope.result.fvm, function(request, key){
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
			angular.forEach($scope.result.fvm, function(request, key){
				if(request.id == id) {
					request.isF = true;
				}
			});
		});
	}
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

minibean.controller('CommunityMembersWidgetController',function($scope, $routeParams, membersWidgetService, $http){
	$scope.result = membersWidgetService.CommunityMembers.get({id:$routeParams.id});
});

///////////////////////// Community Members Widget Service Ends //////////////////////////////////

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

minibean.service('getMoreUnknownCommunity',function($resource){
	this.get_next_communities = $resource(
			'/get-next-unknown-communities/:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{offset:'@offset'}, isArray : true}
			}
	);
});

minibean.controller('UnknownCommunityWidgetController',function($scope, usSpinnerService, getMoreUnknownCommunity, sendJoinRequest, unJoinedCommunityWidgetService, userInfoService, $http){
	$scope.result = unJoinedCommunityWidgetService.UserCommunitiesNot.get();
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.send_request = function(id) {
		usSpinnerService.spin('loading...');
		this.invite = sendJoinRequest.sendRequest.get({id:id},
				function(data) {
					usSpinnerService.stop('loading...');
					
					angular.forEach($scope.result.fvm, function(request, key){
						if(request.id == id) {
							request.isP = true;
						}
					});
				}
		);
	}

	var offsetC = 0;
	var noMore = false;
	$scope.nextGroups = function() {
		$scope.noMoreResult = false;
		if ($scope.isBusy) return;
		if (noMore){ $scope.noMoreResult = true; return;}
		
		$scope.isBusy = true;
		getMoreUnknownCommunity.get_next_communities.get({offset:offsetC}, function( response ){
			var groups = response;
			if(groups.length < 3 ) {
				noMore = true;
				$scope.isBusy = false;
			}
			
			for (var i = 0; i < groups.length; i++) {
				$scope.result.fvm.push(groups[i]);
		    }
			$scope.isBusy = false;
			offsetC++;
		});
	};
});

///////////////////////// User UnJoined Communities Widget End //////////////////////////////////



minibean.service('friendService',function($resource){
	this.UserFriends = $resource(
			'/get-all-friends',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('FriendsController',function($scope, friendService , $http){
	$scope.sendMessage = true;
	$scope.result = friendService.UserFriends.get();
});


///////////////////////// User All Recommend Communities  //////////////////////////////////
minibean.service('communityService',function($resource){
	this.UserCommunitiesNot = $resource(
			'/get-all-communities',
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

minibean.service('allCommunityWidgetService',function($resource){
	this.UserAllCommunities = $resource(
			'/get-users-all-communities',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('CommunityWidgetController',function($scope,$routeParams, usSpinnerService, communityService, allCommunityWidgetService, sendJoinRequest , $http, userInfoService){
	
	$scope.mygroups = $routeParams.type == "myGroups" ? null : "active" ;
	var tab = $routeParams.tab;
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.result = communityService.UserCommunitiesNot.get();
	$scope.allResult = allCommunityWidgetService.UserAllCommunities.get();
	if(tab == 'communities'){
		$scope.selectedTab = 2;
	}
	
	if(tab == 'myCommunities'){
		$scope.selectedTab = 1;
	}
	
	$scope.send_request = function(id) {
		usSpinnerService.spin('loading...');
		this.invite = sendJoinRequest.sendRequest.get({id:id},
				function(data) {
					angular.forEach($scope.result.fvm, function(request, key){
						if(request.id == id) {
							request.isP = true;
						}
					});
					usSpinnerService.stop('loading...');
				}
		);
	}
});

///////////////////////// User All Recommend Communities End //////////////////////////////////


///////////////////////// User All Communities  //////////////////////////////////
minibean.service('communityWidgetService',function($resource){
	this.UserCommunities = $resource(
			'/get-users-three-communities',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('allCommunityWidgetService',function($resource){
	this.UserAllCommunities = $resource(
			'/get-users-all-communities',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('myNextCommunitiesService',function($resource){
	this.get_my_next_communities = $resource(
			'/get-my-next-communities/:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', isArray:true, params:{offset:'@offset'}}
			}
	);
});

minibean.controller('UserCommunityWidgetController',function($scope, myNextCommunitiesService, allCommunityWidgetService, communityWidgetService , $http, userInfoService){
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.result = communityWidgetService.UserCommunities.get();
	$scope.allResult = allCommunityWidgetService.UserAllCommunities.get();
	$scope.selectedTab = 1;
	var offsetC = 0;
	var noMore = false;
	
	$scope.nextMyGroups = function() {
		myNextCommunitiesService.get_my_next_communities.get({offset:offsetC}, function( response ){
			$scope.noMoreResult = false;
			if ($scope.isBusy) return;
			if (noMore){ $scope.noMoreResult = true; return;}
			
			var groups = response;
			if(groups.length < 3 ) {
				noMore = true;
				$scope.isBusy = false;
			}
			
			for (var i = 0; i < groups.length; i++) {
				$scope.result.fvm.push(groups[i]);
		    }
			$scope.isBusy = false;
			offsetC++;
		});
	}
});

///////////////////////// User All Communities End //////////////////////////////////


///////////////////////// User All Communities  //////////////////////////////////
minibean.service('communityWidgetByUserService',function($resource){
	this.UserCommunities = $resource(
			'/get-three-communities-userID/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('allCommunityWidgetByUserService',function($resource){
	this.UserAllCommunities = $resource(
			'/get-all-communities-userID/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});


minibean.controller('CommunityWidgetByUserIDController',function($scope, $routeParams, usSpinnerService, sendJoinRequest, communityJoinService, communityService, allCommunityWidgetService, allCommunityWidgetByUserService, communityWidgetByUserService , $http, userInfoService){
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.result = communityWidgetByUserService.UserCommunities.get({id:$routeParams.id});
	$scope.allResult = allCommunityWidgetByUserService.UserAllCommunities.get({id:$routeParams.id});
	
	$scope.send_request = function(id) {
		usSpinnerService.spin('loading...');
		this.invite = sendJoinRequest.sendRequest.get({id:id},
				function(data) {
					angular.forEach($scope.allResult.fvm, function(request, key){
						if(request.id == id) {
							request.isP = true;
						}
					});
					usSpinnerService.stop('loading...');
				}
		);
	}
	
});

///////////////////////// User All Communities End //////////////////////////////////



///////////////////////// User Profile Start //////////////////////////////////

minibean.service('friendsService',function($resource){
	this.Friends = $resource(
			'/friends/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'},isArray:true}
			}
	);
});

minibean.service('profileService',function($resource){
	this.Profile = $resource(
			'/profile/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
});

minibean.controller('ProfileController',function($scope, $routeParams, $location, profileService, friendsService,sendInvitation, unFriendService){
	
	$scope.$watch( $routeParams.id , function (navigateTo) {
		if( $routeParams.id  == $scope.userInfo.id){
			 $location.path("about/activities");
		}
	});
	
	
	$scope.isLoadingEnabled = false;
	$scope.selectedTab = 1;
	$scope.navigateTo = function (navigateTo) {
		$scope.active = navigateTo;
		if(navigateTo === 'friends') {
			$scope.friends = friendsService.Friends.get({id:$routeParams.id});
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
	$scope.highlightText="";
	$scope.highlightQuery = "";
	$scope.community = communityPageService.CommunityPage.get({id:$routeParams.id}, function(){
		usSpinnerService.stop('loading...');
	});
	
	$scope.comment_on_post = function(id, commentText) {
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
					alert("GOT IT searchPosts");
					if(post.id == data.post_id) {
						alert("GOT IT comment");
						post.n_c++;
						var comment = {"oid" : $scope.userInfo.id, "commentText" : commentText, "on" : $scope.userInfo.displayName,
								"isLike" : true, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
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
			
			if(posts.length == 0){
				$scope.community.searchPosts.length=0;
				$scope.noresult = "No Results Found";
			}
			if(data.length < 5 ) {
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
					post.isLike=false;
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
					post.isLike=true;
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
							comment.isLike=false;
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
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
	
});

///////////////////////// Community Page Start //////////////////////////////////

minibean.service('communityPageService',function($resource){
	this.CommunityPage = $resource(
			'/community/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
	
	this.GetPosts = $resource(
			'/posts?id=:id&offset=:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id',offset:'@offset'},isArray:true}
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
	this.getAllIcons = $resource(
			'/getAllIcons',
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

minibean.controller('CommunityPageController', function($scope, $routeParams, $http, profilePhotoModal, searchMembersService, iconsService,
		allCommentsService, communityPageService,likeFrameworkService, communityJoinService, $upload, $timeout, usSpinnerService){
	$scope.$on('$viewContentLoaded', function() {
		usSpinnerService.spin('loading...');
	});
	
	$scope.showImage = function(imageId) {
		$scope.img_id = imageId;
	}
	
	var coverImage = "/get-cover-community-image-by-id/"+$routeParams.id;
	$scope.coverImage = coverImage;
	
	$scope.community = communityPageService.CommunityPage.get({id:$routeParams.id}, function(){
		usSpinnerService.stop('loading...');
	});
	var tab = $routeParams.tab;
	if(tab == 'moment'){
		$scope.selectedTab1 =1;
	}
	if(tab == 'question'){
		$scope.selectedTab1 =2;
	}
	$scope.selectedTab =1;
	
	
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
	
	$scope.IconsToSelects = iconsService.getAllIcons.get();
	
	$scope.isLoadingEnabled = false;
	$scope.show = false;
	$scope.postPhoto = function() {
		$("#post-photo-id").click();
	}
	$scope.selectedFiles = [];
	$scope.tempSelectedFiles = [];
	$scope.dataUrls = [];
	
	$scope.get_all_comments = function(id) {
	
		angular.forEach($scope.community.posts, function(post, key){
			if(post.id == id) {
				post.cs = allCommentsService.comments.get({id:id});
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
	
	var offset = 0;
	var noMore = false;
	$scope.nextPost = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		
		$scope.isBusy = true;
		communityPageService.GetPosts.get({id:$routeParams.id,offset:offset},
				function(data){
			var posts = data;
			if(data.length < 5 ) {
				noMore = true;
				$scope.isBusy = false;
			}
			
			for (var i = 0; i < posts.length; i++) {
				$scope.community.posts.push(posts[i]);
		    }
			$scope.isBusy = false;
			offset++;
		});
		
	}
	
	$scope.comment_on_post = function(id, commentText) {
		var data = {
			"post_id" : id,
			"commentText" : commentText
		};
		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(comment_id) {
				$('.commentBox').val('');
				
				$scope.commentText = "";
				angular.forEach($scope.community.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						var comment = {"oid" : $scope.community.lu, "d" : commentText, "on" : $scope.community.lun,
								"isLike" : true, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
						post.cs.push(comment);
				}
				usSpinnerService.stop('loading...');	
			});
		});
	};
	
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
				var post = {"oid" : $scope.community.lu, "pt" : postText, "cn" : $scope.community.n,
						"isLike" : true, "p" : $scope.community.lun, "t" : new Date(), "n_c" : 0, "id" : post_id, "cs": []};
				$scope.community.posts.unshift(post);
				
				if($scope.selectedFiles.length == 0) {
					return;
				}
				
				$scope.selectedFiles = [];
				$scope.dataUrls = [];
				
				// when post is done in BE then do photo upload
				for(var i=0 ; i<$scope.tempSelectedFiles.length ; i++) {
					usSpinnerService.spin('loading...');
					$upload.upload({
						url : '/uploadPostPhoto',
						method: $scope.httpMethod,
						data : {
							postId : post_id
						},
						file: $scope.tempSelectedFiles[i],
						fileFormDataName: 'post-photo'
					}).success(function(data, status, headers, config) {
						usSpinnerService.stop('loading...');
						angular.forEach($scope.community.posts, function(post, key){
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
			console.log($scope.community.typ);
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
		PhotoModalController.url = "upload-cover-photo-group/"+id;
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		},function() {
			$scope.coverImage = coverImage + "?q="+ Math.random();
		});
	}
	
	$scope.like_post = function(post_id) {
		likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.community.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=false;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.community.posts, function(post, key){
				if(post.id == post_id) {
					post.nol--;
					post.isLike=true;
				}
			})
		});
	}
	
	$scope.like_comment = function(post_id,comment_id) {
		likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.community.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol++;
							comment.isLike=false;
						}
					})
				}
			})
		});
	}
	
	$scope.unlike_comment = function(post_id,comment_id) {
		likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.community.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol--;
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
});
///////////////////////// Community Page  End ////////////////////////////////

///////////////////////// Community QnA Page Start ////////////////////////////////
minibean.service('communityQnAPageService',function($resource){
	this.QnAPosts = $resource(
			'/communityQnA/questions/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
	this.GetQuests = $resource(
			'/questions?id=:id&offset=:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id',offset:'@offset'},isArray:true}
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


minibean.controller('CreateQnACommunityController',function($scope, likeFrameworkService, allAnswersService, communityQnAPageService, usSpinnerService ,$timeout, $routeParams, $http,  $upload, $validator){
	$scope.QnA = communityQnAPageService.QnAPosts.get({id:$routeParams.id}, function(){
		usSpinnerService.stop('loading...');
	});
	
	$scope.postPhoto = function() {
		$("#QnA-photo-id").click();
	}
	
	$scope.get_all_answers = function(id) {
		angular.forEach($scope.QnA.posts, function(post, key){
			if(post.id == id) {
				post.cs = allAnswersService.answers.get({id:id});
			}
		});
	}
	
	$scope.QnASelectedFiles = [];
	$scope.dataUrls = [];
	$scope.tempSelectedFiles = [];
	
	var offsetq = 0;
	var noMore = false;
	$scope.nextPost = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		
		$scope.isBusy = true;
		communityQnAPageService.GetQuests.get({id:$routeParams.id,offset:offsetq},
				function(data){
			var posts = data;
			if(data.length < 5 ) {
				noMore = true;
				$scope.isBusy = false;
			}
			
			for (var i = 0; i < posts.length; i++) {
				$scope.QnA.posts.push(posts[i]);
		    }
			$scope.isBusy = false;
			offsetq++;
		});
		
	}
	
	$scope.ask_question_community = function(id, questionTitle, questionText) {
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
				var post = {"oid" : $scope.QnA.lu, "ptl" : questionTitle, "pt" : questionText, "cn" : $scope.community.n, 
						"p" : $scope.QnA.lun, "t" : new Date(), "n_c" : 0, "id" : post_id, "cs": []};
				$scope.QnA.posts.unshift(post);
				
				if($scope.QnASelectedFiles.length == 0) {
					return;
				}
				
				$scope.QnASelectedFiles = [];
				$scope.dataUrls = [];
				
				
				// when post is done in BE then do photo upload
				for(var i=0 ; i<$scope.tempSelectedFiles.length ; i++) {
					usSpinnerService.spin('loading...');
					$upload.upload({
						url : '/uploadPostPhoto',
						method: $scope.httpMethod,
						data : {
							postId : post_id
						},
						file: $scope.tempSelectedFiles[i],
						fileFormDataName: 'post-photo'
					}).success(function(data, status, headers, config) {
						usSpinnerService.stop('loading...');
						angular.forEach($scope.QnA.posts, function(post, key){
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
	
	$scope.answer_to_question = function(question_post_id, answerText) {
		var data = {
				"post_id" : question_post_id,
				"answerText" : answerText
			};
			
			$http.post('/communityQnA/question/answer', data) 
				.success(function(answer_id) {
					$('.commentBox').val('');
					angular.forEach($scope.QnA.posts, function(post, key){
						if(post.id == data.post_id) {
							post.n_c++;
							var answer = {"oid" : $scope.QnA.lu, "d" : answerText, "on" : $scope.QnA.lun, 
									"isLike" : true, "cd" : new Date(), "n_c" : post.n_c,"id" : answer_id};
						post.cs.push(answer);
					}
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
			angular.forEach($scope.QnA.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=false;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.QnA.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=true;
					post.nol--;
				}
			})
		});
	}
	

	$scope.like_comment = function(post_id,comment_id) {
		likeFrameworkService.hitLikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.QnA.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol++;
							comment.isLike=false;
						}
					})
				}
			})
		});
	}
	
	$scope.unlike_comment = function(post_id,comment_id) {
		likeFrameworkService.hitUnlikeOnComment.get({"comment_id":comment_id}, function(data) {
			angular.forEach($scope.QnA.posts, function(post, key){
				if(post.id == post_id) {
					angular.forEach(post.cs, function(comment, key){
						if(comment.id == comment_id) {
							comment.nol--;
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
});


///////////////////////// Community QnA Page End ////////////////////////////////
minibean.controller('CreateArticleController',function($scope,$http,usSpinnerService, articleCategoryService){
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
					$scope.submitBtn = "Done";
					usSpinnerService.stop('loading...');
				});
	}
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
			'/getDescriptionOdArticle/:id',
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

minibean.controller('ShowArticleController',function($scope, $modal,$routeParams, showImageService, usSpinnerService, deleteArticleService, allArticlesService, getDescriptionService,allRelatedArticlesService){
	$scope.get_result = function(catId) {
		if(catId == "all" || catId == undefined) {
			catId = 0;
		}
		usSpinnerService.spin('loading...');
		$scope.result = [];
		$scope.result = allArticlesService.ArticleCategorywise.get({id:catId}	, function(data) {
			var count = 1;
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
			$scope.categoryImage = $scope.result[0].category_url;
			$scope.categoryName = $scope.result[0].ct.name;
			$scope.allCategory = false;
			$scope.oneCategory = true;
			$scope.seeAllCategory = false;
			$scope.threeCategory = true;
			usSpinnerService.stop('loading...');
	    });
	    
	 };

	 
	 $scope.getAllArticles = function(){
		 usSpinnerService.spin('loading...');
		 	$scope.allCategory = true;
			$scope.oneCategory = false;
			$scope.seeAllCategory = true;
			$scope.threeCategory = false;
			$scope.result = [];
			$scope.result = allArticlesService.AllArticles.get(function(data) {
				usSpinnerService.stop('loading...');
		    });
		};
	var catId = $routeParams.catid;
	$scope.allCategory = true;
	$scope.oneCategory = false;
	
	if(catId == "all" || catId == undefined) {
		$scope.result = allArticlesService.AllArticles.get();
		$scope.seeAllCategory = true;
		$scope.threeCategory = false;
	}
	else{
		$scope.get_result(catId);
	}
	
    $scope.resultSlidder = allArticlesService.SixArticles.get({}, function() {
        $scope.image_source= $scope.resultSlidder.la[0].img_url;
    });
	
	 $scope.changeInsideImage = function(article_id) {
		 angular.forEach($scope.result, function(element, key){
				if(element.id == article_id) {
					$scope.image_source= element.img_url;
					$scope.description = element.lines;
					$scope.title = element.nm;
					$scope.category_id = element.id;
				}
		})
	 };
	 
	 $scope.open = function (id) {
	    var modalInstance = $modal.open({
	      templateUrl: 'myModalContent.html',
	    });
	    var msg = getDescriptionService.GetDescription.get({id:id}	, function(data) {
	    	console.log(data.description);
	    	$('.modal-body').html(data.description);
	    });
	    
	  };
	 
	  
	  $scope.deleteArticle = function (id){
		  deleteArticleService.DeleteArticle.get({id :id}, function(data){
			  
			  angular.forEach($scope.result, function(request, key){
					if(request.id == id) {
						$scope.result.splice($scope.result.indexOf(request),1);
					}
				});
			  
		  });
	  }
	
	
	 $scope.changeInsideImage = function(article_id) {
		 angular.forEach($scope.result, function(element, key){
				if(element.id == article_id) {
					$scope.image_source= element.img_url;
					$scope.description = element.lds;
					$scope.title = element.nm;
					$scope.category_id = element.id;
				}
		})
	 };
	  
});


minibean.controller('ShowArticleControllerNew',function($scope, $modal,$routeParams, articleCategoryService, showImageService, usSpinnerService, deleteArticleService, allArticlesService, getDescriptionService,allRelatedArticlesService){
	$scope.result = [];
	var offset = 0;
	var noMore = false;
	$scope.articleCategorys = articleCategoryService.getAllArticleCategory.get();
	$scope.get_result = function(catId) {
		usSpinnerService.spin('loading...');
		$scope.isBusy = true;
		$scope.result = allArticlesService.ArticleCategorywise.get({id:catId, offset: offset}	, function(data) {
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
				if ($scope.result.length > 5){
					noMore = true;
				}
			$scope.categoryImage = $scope.result[0].category_url;
			$scope.categoryName = $scope.result[0].ct.name;
				if(catId == 0)
				{
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
		console.log($scope.result);
		usSpinnerService.spin('loading...');
		allArticlesService.ArticleCategorywise.get({id:catId, offset: offset},
			function(data){
			console.log(data);
				var posts = data;
				if(posts.length < 5 ) {
					noMore = true;
					$scope.isBusy = false;
				}
				
				for (var i = 0; i < posts.length; i++) {
					$scope.result.push(posts[i]);
			    }
				console.log($scope.result);
			    $scope.isBusy = false;
			    offset++;
			    usSpinnerService.stop('loading...');
			}
		);
	}
		
	$scope.resultSlidder = allArticlesService.SixArticles.get({}, function() {
		$scope.image_source= $scope.resultSlidder.la[0].img_url;
	});
	
	 $scope.changeInsideImage = function(article_id) {
		 angular.forEach($scope.result, function(element, key){
				if(element.id == article_id) {
					$scope.image_source= element.img_url;
					$scope.description = element.lds;
					$scope.title = element.nm;
					$scope.category_id = element.id;
				}
		})
	 };
});



minibean.service('ArticleService',function($resource){
	this.ArticleInfo = $resource(
			'/Article/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.service('ArticleallCommentsService',function($resource){
	this.comments = $resource(
			'/ArticleComments/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get' ,isArray:true}
			}
	);
});


minibean.controller('EditArticleController',function($scope,$routeParams,$location, likeFrameworkService, ArticleallCommentsService, userInfoService, usSpinnerService, ArticleService,articleCategoryService,allRelatedArticlesService,$http){
	$scope.submitBtn = "Save";
	$scope.userInfo = userInfoService.UserInfo.get();
	
	$scope.article = ArticleService.ArticleInfo.get({id:$routeParams.id}, function(response) {
		if(response[0] == '1'){
			$location.path('/article/show/0');
		}
		$scope.relatedResult = allRelatedArticlesService.getRelatedArticles.get({id:$routeParams.id, category_id:response.ct.id});
	});
	$scope.articleCategorys = articleCategoryService.getAllArticleCategory.get();
	
	$scope.open = function (id) {
	    var modalInstance = $modal.open({
	      templateUrl: 'myModalContent.html',
	    });
	    var msg = getDescriptionService.GetDescription.get({id:id}	, function(data) {
	    	console.log(data.description);
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
			$scope.submitBtn = "Done";
			usSpinnerService.stop('loading...');
		});
	}
	
	$scope.get_all_comments = function(id) {
			$scope.article.cs = ArticleallCommentsService.comments.get({id:id});
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
						"isLike" : true, "cd" : new Date(), "n_c" :$scope.article.n_c, "id" : comment_id};
				$scope.article.cs.push(comment);
				usSpinnerService.stop('loading...');	
		});
	};
	
		 $scope.like_article = function(article_id) {
				likeFrameworkService.hitLikeOnArticle.get({"article_id":article_id}, function(data) {
					$scope.article.nol++;
					$scope.article.isLike=false;
				});
			}
			
			$scope.unlike_article = function(article_id) {
				likeFrameworkService.hitUnlikeOnArticle.get({"article_id":article_id}, function(data) {
					$scope.article.nol--;
					$scope.article.isLike=true;
				});
			}
	
});

minibean.service('newsFeedService',function($resource){
	
//	this.getMyUpdates = $resource(
//			'/get-my-updates/:timestamp',
//			{alt:'json',callback:'JSON_CALLBACK'},
//			{
//				get: {method:'GET', params:{timestamp:'@timestamp'}}
//			}
//	);
//	
//	this.getMyLiveUpdates = $resource(
//			'/get-my-live-updates/:timestamp',
//			{alt:'json',callback:'JSON_CALLBACK'},
//			{
//				get: {method:'GET', params:{timestamp:'@timestamp'}}
//			}
//	);
//	
//	this.GetNextNewsFeeds = $resource(
//			'/get-next-news-feeds/:timestamp',
//			{alt:'json',callback:'JSON_CALLBACK'},
//			{
//				get: {method:'GET', params:{timestamp:'@timestamp'}, isArray:true}
//			}
//	);
	
	this.NewsFeeds = $resource(
			'/get-newsfeeds/:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{offset:'@offset'}}
			}
	);
	
	
});

minibean.controller('NewsFeedController', function($scope, likeFrameworkService, $interval, $http, allCommentsService, usSpinnerService, newsFeedService) {
	$scope.newsFeeds = { posts: [] };
	
	$scope.comment_on_post = function(id, commentText) {
		var data = {
			"post_id" : id,
			"commentText" : commentText
		};
		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(comment_id) {
				$('.commentBox').val('');
				
				$scope.commentText = "";
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						var comment = {"oid" : $scope.userInfo.id, "d" : commentText, "on" : $scope.userInfo.displayName,
								"isLike" : true, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
						post.cs.push(comment);
				}
				usSpinnerService.stop('loading...');	
			});
		});
	};
	
	$scope.like_post = function(post_id) {
		likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=false;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=true;
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
							comment.isLike=false;
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
							comment.isLike=true;
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
			}
		});
	}
	
	

	var noMore = false;
	var offset = 0;

	//$scope.newsFeeds = newsFeedService.NewsFeeds.get({offset:0}, function(vm) {
	//	offset++;
	//});
	
	$scope.nextNewsFeeds = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		$scope.isBusy = true;
		newsFeedService.NewsFeeds.get({offset:offset},
			function(data){
				var posts = data.posts;
				if(posts.length < 5 ) {
					noMore = true;
					$scope.isBusy = false;
				}
				
				for (var i = 0; i < posts.length; i++) {
					$scope.newsFeeds.posts.push(posts[i]);
			    }
			    $scope.isBusy = false;
				offset++;
			}
		);
	}
	
	$scope.showImage = function(imageId) {
		$scope.img_id = imageId;
	}
});

	  
minibean.service('userNewsFeedService',function($resource){

	this.NewsFeeds = $resource(
			'/get-user-newsfeeds/:offset/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{offset:'@offset'}}
			}
	);
	
	
});

minibean.controller('UserNewsFeedController', function($scope,$routeParams, $interval, likeFrameworkService, userInfoService, $http, allCommentsService, usSpinnerService, userNewsFeedService) {
	$scope.newsFeeds = { posts: [] };
	
	$scope.comment_on_post = function(id, commentText) {
		var data = {
			"post_id" : id,
			"commentText" : commentText
		};
		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(comment_id) {
				$('.commentBox').val('');
				
				$scope.commentText = "";
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						var comment = {"oid" : $scope.userInfo.id, "d" : commentText, "on" : $scope.userInfo.displayName,
								"isLike" : true, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
						post.cs.push(comment);
				}
				usSpinnerService.stop('loading...');	
			});
		});
	};
	
	$scope.get_all_comments = function(id) {
		
		angular.forEach($scope.newsFeeds.posts, function(post, key){
			if(post.id == id) {
				post.cs = allCommentsService.comments.get({id:id});
			}
		});
	}
	
	

	var noMore = false;
	var offset = 0;
	$scope.userInfo = userInfoService.UserInfo.get();
	

	//$scope.newsFeeds = newsFeedService.NewsFeeds.get({offset:0}, function(vm) {
	//	offset++;
	//});
	
	$scope.like_post = function(post_id) {
		likeFrameworkService.hitLikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=false;
					post.nol++;
				}
			})
		});
	}
	
	$scope.unlike_post = function(post_id) {
		likeFrameworkService.hitUnlikeOnPost.get({"post_id":post_id}, function(data) {
			angular.forEach($scope.newsFeeds.posts, function(post, key){
				if(post.id == post_id) {
					post.isLike=true;
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
							comment.isLike=false;
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
							comment.isLike=true;
						}
					})
				}
			})
		});
	}
	
	
	$scope.nextNewsFeeds = function() {
		var id = $scope.userInfo.id;
		if($routeParams.id != undefined){
			id = $routeParams.id;
		}
		if ($scope.isBusy) return;
		if (noMore) return;
		$scope.isBusy = true;
		userNewsFeedService.NewsFeeds.get({offset:offset,id:id},
			function(data){
				var posts = data.posts;
				if(posts.length < 5 ) {
					noMore = true;
					$scope.isBusy = false;
				}
				
				for (var i = 0; i < posts.length; i++) {
					$scope.newsFeeds.posts.push(posts[i]);
			    }
			    $scope.isBusy = false;
				offset++;
			}
		);
	}
	
	$scope.showImage = function(imageId) {
		$scope.img_id = imageId;
	}
});




