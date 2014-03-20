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

minibean.controller('SearchController',function($scope, searchService, sendInvitation){
	$scope.search_result = function(query) {
		if(query != undefined) {
			this.result = searchService.userSearch.get({q:query});
		}
	}
	
	$scope.send_invite = function(id) {
		this.invite = sendInvitation.inviteFriend.get({id:id});
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
			'/accept-friend-request?friend_id=:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{id:'@id'}, isArray:true}
			}
	);
});

minibean.controller('UserInfoServiceController',function($scope,userInfoService){
		$scope.userInfo = userInfoService.UserInfo.get();
	  
});

minibean.controller('ApplicationController',function($scope, userInfoService, userNotification,acceptFriendRequestService){
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.friend_requests = userNotification.getAllFriendRequests.get();
	
	$scope.accept_friend_request = function(id) {
		this.acceptFriendRequest = acceptFriendRequestService.acceptFriendRequest.get({id:id});
	}
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
	$scope.uploadRightAway = true;
	
	$scope.hasUploader = function(index) {
		return $scope.upload[index] != null;
	};
	
	$scope.abort = function(index) {
		$scope.upload[index].abort(); 
		$scope.upload[index] = null;
	};
	
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
			profilePhotoModal.CloseModal();
		});
	} // End of start
	
}

minibean.service('profilePhotoModal',function( $modal){
	
	this.OpenModal = function(arg) {
		this.instance = $modal.open(arg);
	}
	
	this.CloseModal = function() {
		this.instance.dismiss('close');
	}
	
	
});




minibean.controller('UserAboutController',function($scope, userAboutService, $http, profilePhotoModal){
	$scope.result = userAboutService.UserAbout.get();
	
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
	
	$scope.openProfilePhotoModal = function() {
		PhotoModalController.prototype.url = "upload-profile-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		});
	}
	
	$scope.openCoverPhotoModal = function() {
		PhotoModalController.url = "upload-cover-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		});
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

///////////////////////// User Friends Widget End //////////////////////////////////


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

minibean.controller('UnknownCommunityWidgetController',function($scope, unJoinedCommunityWidgetService, userInfoService, $http){
	$scope.result = unJoinedCommunityWidgetService.UserCommunitiesNot.get();
	$scope.userInfo = userInfoService.UserInfo.get();
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
	$scope.result = friendService.UserFriends.get();
});


///////////////////////// User All Communities  //////////////////////////////////
minibean.service('communityService',function($resource){
	this.UserCommunitiesNot = $resource(
			'/get-all-communities',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('CommunityController',function($scope, communityService , $http, userInfoService){
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.result = communityService.UserCommunitiesNot.get();
});

///////////////////////// User All Communities End //////////////////////////////////