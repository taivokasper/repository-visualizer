angular.module('rv').controller('VideoController', [
    '$scope', '$state', '$stateParams', 'GenerateVideoResource', 'repo',
    function ($scope, $state, $stateParams, GenerateVideoResource, repo) {
        'use strict';

        $scope.repoName = $stateParams.repoName;
        $scope.videoUrl = repo.videoPath;

        $scope.flags = {
            generationInProgress: false,
            videoNotFound: repo.videoPath === null
        };

        $scope.generateNewVideo = function () {
            $scope.flags.generationInProgress = true;

            GenerateVideoResource.generate({repoName: $scope.repoName}).$promise
                .then(function () {
                    $state.reload()
                });
        };
    }
]);