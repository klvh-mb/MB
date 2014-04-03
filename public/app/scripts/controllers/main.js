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
			'/accept-friend-request?friend_id=:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{id:'@id'}, isArray:true}
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
			'/accept-join-request/:member_id/:group_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id'}, isArray:true}
			}
	);
});




minibean.controller('UserInfoServiceController',function($scope,userInfoService){
		$scope.userInfo = userInfoService.UserInfo.get();
	  
});

minibean.controller('ApplicationController',function($scope, userInfoService, userNotification, userSimpleNotifications, acceptJoinRequestService, acceptFriendRequestService){
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.friend_requests = userNotification.getAllFriendRequests.get();
	$scope.join_requests = userSimpleNotifications.getAllJoinRequests.get();
	$scope.isFRreaded = true;
	$scope.isNOreaded = true;
	
	$scope.accept_friend_request = function(id) {
		
		angular.forEach($scope.friend_requests, function(request, key){
			if(request.id == id) {
				request.isLoadingEnable = true;
			}
		});
		
		this.acceptFriendRequest = acceptFriendRequestService.acceptFriendRequest.get({id:id}, 
				//success
				function() {
					angular.forEach($scope.friend_requests, function(request, key){
						if(request.id == id) {
							request.isLoadingEnable = false;
							request.isFriendAccepted = true;
						}
					});
				}
		);
	};
	$scope.accept_join_request = function(member_id,group_id) {
		
		angular.forEach($scope.join_requests, function(request, key){
			if(request.id == member_id) {
				request.isLoadingEnable = true;
			}
		});
		
		this.accept_join_request = acceptJoinRequestService.acceptJoinRequest.get({"member_id":member_id, "group_id":group_id},
			function() {
				angular.forEach($scope.join_requests, function(request, key){
					if(request.id == member_id) {
						request.isLoadingEnable = false;
						request.isRequestAccepted = true;
					}
				});
			}
		);
	}
	
	$scope.reset_fr_count = function() {
		$scope.isFRreaded = false;
	}
	
	$scope.reset_notify_count = function() {
		$scope.isNOreaded = false;
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
	
	this.OpenModal = function(arg, successCallback) {
		this.instance = $modal.open(arg);
		this.onSuccess = successCallback;
	}
	
	this.CloseModal = function() {
		this.instance.dismiss('close');
		this.onSuccess();
	}
	
	
});




minibean.controller('UserAboutController',function($scope, userAboutService, $http, profilePhotoModal){
	var profileImage = "/get-profile-image";
	var coverImage = "/get-cover-image";
	
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
	
	$scope.openProfilePhotoModal = function() {
		PhotoModalController.url = "upload-profile-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		},function() {
			$scope.profileImage = profileImage + "?q="+ Math.random();
		});
	}
	
	$scope.openCoverPhotoModal = function() {
		PhotoModalController.url = "upload-cover-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal.html',
			 controller: PhotoModalController
		},function() {
			$scope.coverImage = coverImage + "?q="+ Math.random();
		});
	}
	
});


///////////////////////// Create community Home Page  //////////////////////////////////
minibean.service('groupAboutService',function($resource){
	this.UserAbout = $resource(
			'/about-group',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
	
});

minibean.service('communityPageService',function($resource){
	this.CommunityPage = $resource(
			'/community/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
			}
	);
});

minibean.controller('GroupController',function($scope, $routeParams, $http, communityPageService, $upload, profilePhotoModal){
	
	$scope.community = communityPageService.CommunityPage.get({id:$routeParams.id});
	
	$scope.communityType = [
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
	
	$scope.updateGroupProfileData = function(data) {
		return $http.post('/updateGroupProfileData', $scope.community);
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



minibean.controller('CreateCommunityController',function($scope,  $http,  $upload, $validator){
	
	$scope.formData = {};
	$scope.selectedFiles =[];
	$scope.submitBtn = "Save";
	$scope.submit = function() {
		 $validator.validate($scope, 'formData')
		    .success(function () {
		    	$upload.upload({
					url: '/createCommunity',
					method: 'POST',
					file: $scope.selectedFiles[0],
					data: $scope.formData,
					fileFormDataName: 'cover-photo'
				}).progress(function(evt) {
					$scope.submitBtn = "Please Wait";
			    }).success(function(data, status, headers, config) {
			    	$scope.submitBtn = "Done";
			    }).error(function(data, status, headers, config) {
			    	if( status == 505 ) {
			    		$scope.uniqueName = true;
			    	}  
			    	$scope.submitBtn = "Try Again";
			    });
		    })
		    .error(function () {
		        console.log('error');
		    });
		
	
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

minibean.service('sendJoinRequest',function($resource){
	this.sendRequest = $resource(
			'/send-request?id=:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{id:'@id'}}
			}
	);
});

minibean.controller('UnknownCommunityWidgetController',function($scope, sendJoinRequest, unJoinedCommunityWidgetService, userInfoService, $http){
	$scope.result = unJoinedCommunityWidgetService.UserCommunitiesNot.get();
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.send_request = function(id) {
		this.invite = sendJoinRequest.sendRequest.get({id:id});
	}
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

minibean.controller('CommunityWidgetController',function($scope,$routeParams, communityService, allCommunityWidgetService, sendJoinRequest , $http, userInfoService){
	
	$scope.mygroups = $routeParams.type == "myGroups" ? null : "active" ;
	
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.result = communityService.UserCommunitiesNot.get();
	$scope.allResult = allCommunityWidgetService.UserAllCommunities.get();
	
	$scope.send_request = function(id) {
		this.invite = sendJoinRequest.sendRequest.get({id:id});
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

minibean.controller('UserCommunityWidgetController',function($scope, allCommunityWidgetService, communityWidgetService , $http, userInfoService){
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.result = communityWidgetService.UserCommunities.get();
	$scope.allResult = allCommunityWidgetService.UserAllCommunities.get();
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

minibean.controller('ProfileController',function($scope, $routeParams, profileService, friendsService,sendInvitation, unFriendService){
	$scope.isLoadingEnabled = false;
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



///////////////////////// Community Page Start //////////////////////////////////

minibean.service('communityPageService',function($resource){
	this.CommunityPage = $resource(
			'/community/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id'}}
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

minibean.controller('CommunityPageController', function($scope, $routeParams, $http, communityPageService, communityJoinService, $upload, $timeout){
	$scope.community = communityPageService.CommunityPage.get({id:$routeParams.id});
	
	$scope.isLoadingEnabled = false;
	
	$scope.postPhoto = function() {
		$("#post-photo-id").click();
	}
	$scope.selectedFiles = [];
	$scope.dataUrls = [];
	
	$scope.onFileSelect = function($files) {
		
		$scope.selectedFiles.push($files);
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
		
		console.log($scope.dataUrls);
	}
	
	
	$scope.comment_on_post = function(id, commentText) {
		var data = {
			"post_id" : id,
			"commentText" : commentText
		};
		
		$http.post('/community/post/comment', data) 
			.success(function(post_id) {
				angular.forEach($scope.community.posts, function(post, key){
					if(post.id == post_id) {
						post.n_c++;
						var comment = {"oid" : $scope.community.lu, "d" : commentText, "on" : $scope.community.lun, 
							"cd" : new Date(), "n_c" : post.n_c};
					post.cs.push(comment);
				}
			});
		});
	};
	
	$scope.post_on_community = function(id, postText) {
		
		var data = {
			"community_id" : id,
			"postText" : postText
		};
		$http.post('/community/post', data)// first create post with post text.
			.success(function(post_id) {
				
				// when post is done in BE then do photo upload
				for(var i=0 ; i<$scope.selectedFiles.length ; i++) {
					$upload.upload({
						url : '/uploadPostPhoto',
						method: $scope.httpMethod,
						data : {
							postId : post_id
						},
						file: $scope.selectedFiles[i],
						fileFormDataName: 'post-photo'
					}).success(function(data, status, headers, config) {
						
					});
					
				}
				
				
				
				var post = {"oid" : $scope.community.lu, "pt" : postText, 
							"p" : $scope.community.lun, "t" : new Date(), "n_c" : 0, "id" : post_id, "cs": []};
				$scope.community.posts.unshift(post);
		});
	};

	$scope.send_join = function(id) {
		$scope.isLoadingEnabled = true;
		this.send_join_request = communityJoinService.sendJoinRequest.get({"id":id}, function(data) {
			$scope.isLoadingEnabled = false;
			$scope.community.isP = true;
		});
	}
	
	$scope.leave_community = function(id) {
		$scope.isLoadingEnabled = true;
		this.leave_this_community = communityJoinService.leaveCommunity.get({"id":id}, function(data) {
			$scope.isLoadingEnabled = false;
			$scope.community.isM = false;
		});
	}
});




///////////////////////// Community Page  End //////////////////////////////////
