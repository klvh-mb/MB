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
	
	this.acceptInviteRequest = $resource(
			'/accept-invite-request/:member_id/:group_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id'}, isArray:true}
			}
	);
});




minibean.controller('UserInfoServiceController',function($scope,userInfoService){
		$scope.userInfo = userInfoService.UserInfo.get();
	  
});

minibean.controller('ApplicationController',function($scope,$location, userInfoService, userNotification, userSimpleNotifications,
	acceptJoinRequestService, acceptFriendRequestService, usSpinnerService){
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
		
		var spinner = new Spinner().spin();
		
		$(".a_" + member_id + "_" + group_id).append(spinner.el);    
		this.accept_join_request = acceptJoinRequestService.acceptJoinRequest.get({"member_id":member_id, "group_id":group_id},
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
	
	$scope.accept_invite_request = function(member_id,group_id) {
		
		var spinner = new Spinner().spin();
		
		$(".a_" + member_id + "_" + group_id).append(spinner.el);    
		this.accept_invite_request = acceptJoinRequestService.acceptInviteRequest.get({"member_id":member_id, "group_id":group_id},
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



minibean.controller('GroupController',function($scope, $routeParams, $http, usSpinnerService, iconsService, communityPageService, $upload, profilePhotoModal){

	$scope.submitBtn = "Save";
	$scope.community = communityPageService.CommunityPage.get({id:$routeParams.id});

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



minibean.controller('CreateCommunityController',function($scope,  $http,  $upload, $validator, iconsService, usSpinnerService){
	
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
			    }).success(function(data, status, headers, config) {
			    	$scope.submitBtn = "Done";
			    	usSpinnerService.stop('loading...');
			    }).error(function(data, status, headers, config) {
			    	if( status == 505 ) {
			    		$scope.uniqueName = true;
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

minibean.controller('UnknownCommunityWidgetController',function($scope, getMoreUnknownCommunity, sendJoinRequest, unJoinedCommunityWidgetService, userInfoService, $http){
	$scope.result = unJoinedCommunityWidgetService.UserCommunitiesNot.get();
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.send_request = function(id) {
		this.invite = sendJoinRequest.sendRequest.get({id:id});
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

minibean.controller('CommunityWidgetController',function($scope,$routeParams, communityService, allCommunityWidgetService, sendJoinRequest , $http, userInfoService){
	
	$scope.mygroups = $routeParams.type == "myGroups" ? null : "active" ;
	
	$scope.userInfo = userInfoService.UserInfo.get();
	$scope.result = communityService.UserCommunitiesNot.get();
	$scope.allResult = allCommunityWidgetService.UserAllCommunities.get();
	$scope.selectedTab = 1;
	
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
	
	this.GetPosts = $resource(
			'/posts?id=:id&offset=:offset',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{id:'@id',offset:'@offset'},isArray:true}
			}
	);
	
	this.GetPostsFromIndex = $resource(
			'/searchForPosts/index/:query/:community_id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get', params:{community_id:'@community_id',query:'@query'},isArray:true}
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

minibean.controller('CommunityPageController', function($scope, $routeParams, $http, searchMembersService, iconsService,
		allCommentsService, communityPageService, communityJoinService, $upload, $timeout, usSpinnerService){
	$scope.highlightQuery = "";
	$scope.$on('$viewContentLoaded', function() {
		usSpinnerService.spin('loading...');
	});
	
	
	$scope.community = communityPageService.CommunityPage.get({id:$routeParams.id}, function(){
		usSpinnerService.stop('loading...');
	});
	$scope.selectedTab =1;
	$scope.highlightText="";
	$scope.search_and_highlight = function(community_id, query) {
		$scope.community.posts=[];
		communityPageService.GetPostsFromIndex.get({community_id: community_id, query : query}, function( results ) {
			angular.forEach(results, function(post, key){
				$scope.community.posts.push(post);
			});
			$scope.highlightText = query;
		});
	};
	
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
			.success(function(post_id) {
				angular.forEach($scope.community.posts, function(post, key){
					if(post.id == post_id) {
						post.n_c++;
						var comment = {"oid" : $scope.community.lu, "d" : commentText, "on" : $scope.community.lun, 
							"cd" : new Date(), "n_c" : post.n_c};
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
		$http.post('/community/post', data)// first create post with post text.
			.success(function(post_id) {
				usSpinnerService.stop('loading...');
				var post = {"oid" : $scope.community.lu, "pt" : postText, 
				"p" : $scope.community.lun, "t" : new Date(), "n_c" : 0, "id" : post_id, "cs": []};
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


minibean.controller('CreateQnACommunityController',function($scope,allAnswersService, communityQnAPageService, usSpinnerService ,$timeout, $routeParams, $http,  $upload, $validator){
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
	
	$scope.ask_question_community = function(id, questionText) {
		usSpinnerService.spin('loading...');
		var data = {
				"community_id" : id,
				"questionText" : questionText,
				"withPhotos" : $scope.QnASelectedFiles.length != 0
			};
		
		
			$http.post('/communityQnA/question/post', data)// first create post with question text.
				.success(function(post_id) {
					usSpinnerService.stop('loading...');
					var post = {"oid" : $scope.QnA.lu, "pt" : questionText, 
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
				.success(function(post_id) {
					angular.forEach($scope.QnA.posts, function(post, key){
						if(post.id == post_id) {
							post.n_c++;
							var answer = {"oid" : $scope.QnA.lu, "d" : answerText, "on" : $scope.QnA.lun, 
								"cd" : new Date(), "n_c" : post.n_c};
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
	this.EightArticles = $resource(
			'/get-Eight-Articles',
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

minibean.service('deleteArticleService',function($resource){
	this.DeleteArticle = $resource(
			'/deleteArticle/:id',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'get'}
			}
	);
});

minibean.controller('ShowArticleController',function($scope, $modal, deleteArticleService, allArticlesService, getDescriptionService){
	$scope.result = allArticlesService.AllArticles.get();
	
	$scope.resultSlidder = allArticlesService.EightArticles.get();
	
	
	
	
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


minibean.controller('EditArticleController',function($scope,$routeParams, ArticleallCommentsService, userInfoService, usSpinnerService, ArticleService,articleCategoryService,$http){
	$scope.submitBtn = "Save";
	$scope.userInfo = userInfoService.UserInfo.get();
	var range = [];
	for(var i=0;i<100;i++) {
		  range.push(i);
	}
	$scope.targetAge = range;
	$scope.article = ArticleService.ArticleInfo.get({id:$routeParams.id});
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
			.success(function(article_id) {
				$scope.article.n_c++;
				var comment = {"oid" : $scope.userInfo.id, "d" : commentText, "on" : $scope.userInfo.displayName, 
					"cd" : new Date(), "n_c" :$scope.article.n_c};
				$scope.article.cs.push(comment);
				usSpinnerService.stop('loading...');	
		});
	};
	
});

minibean.service('newsFeedService',function($resource){
	this.getMyUpdates = $resource(
			'/get-my-updates/:timestamp',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{timestamp:'@timestamp'}}
			}
	);
	
	this.getMyLiveUpdates = $resource(
			'/get-my-live-updates/:timestamp',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{timestamp:'@timestamp'}}
			}
	);
	
	this.GetNextNewsFeeds = $resource(
			'/get-next-news-feeds/:timestamp',
			{alt:'json',callback:'JSON_CALLBACK'},
			{
				get: {method:'GET', params:{timestamp:'@timestamp'}, isArray:true}
			}
	);
});

minibean.controller('NewsFeedController', function($scope, $interval, $http, allCommentsService, usSpinnerService, newsFeedService) {
	$scope.newsFeeds = [];
	var timestamp = moment().unix();
	$scope.newsFeeds = newsFeedService.getMyUpdates.get({timestamp : timestamp}, function(vm) {
		var posts = vm.posts;
		var lastIndex = posts.length -1;
		if(posts[lastIndex] != undefined) {
			timestamp = posts[lastIndex].ts;
		}
	});
	
	$interval(function() {
		var timestamp = moment().unix();
		newsFeedService.getMyLiveUpdates.get({timestamp : timestamp}, function(result) {
			angular.forEach(result.posts, function(post, key){
				$scope.newsFeeds.posts.splice(key, 0, post);
			});
		});
	}, 60*1000);
	
	$scope.comment_on_post = function(id, commentText) {
		var data = {
			"post_id" : id,
			"commentText" : commentText
		};
		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(post_id) {
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == post_id) {
						post.n_c++;
						var comment = {"oid" : $scope.newsFeeds.lu, "d" : commentText, "on" : $scope.newsFeeds.lun, 
								"cd" : new Date(), "n_c" : post.n_c};
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
	$scope.nextNewsFeeds = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		
		$scope.isBusy = true;
		
		newsFeedService.GetNextNewsFeeds.get({timestamp : timestamp},
			function(data){
				var posts = data;
				if(posts.length < 5 ) {
					noMore = true;
					$scope.isBusy = false;
				}
				
				for (var i = 0; i < posts.length; i++) {
					$scope.newsFeeds.posts.push(posts[i]);
			    }
				
				var lastIndex = posts.length - 1;
				if(posts[lastIndex] != undefined) {
					timestamp = posts[lastIndex].ts;
				}
				$scope.isBusy = false;
			}
		);
	}
});

	  




