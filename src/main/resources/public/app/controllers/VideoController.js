angular.module('rv').controller('VideoController', [
	'$scope', '$stateParams', '$sce', 'RepoResource', 'GenerateVideoResource',
	function ($scope, $stateParams, $sce, RepoResource, GenerateVideoResource) {
		'use strict';

		$scope.repoName = $stateParams.repoName;

		$scope.flags = {
			generationInProgress: false,
			videoNotFound: true
		};

		function setVideoUrl(data) {
			$scope.flags.generationInProgress = false;
			$scope.flags.videoNotFound = data.videoPath === null;

			if ($scope.flags.videoNotFound) {
				return;
			}
			$scope.videoUrl = data.videoPath;
		}

		RepoResource.get({ repoName: $scope.repoName }).$promise.then(setVideoUrl);

		$scope.generateNewVideo = function () {
			$scope.flags.generationInProgress = true;

			GenerateVideoResource.generate({ repoName: $scope.repoName }).$promise.then(setVideoUrl);
		};
	}
]);