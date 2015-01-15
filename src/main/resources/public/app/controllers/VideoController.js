angular.module('rv').controller('VideoController', [
    '$scope', '$state', '$stateParams', '$interval', 'GenerateVideoResource', 'GenerationInfoResource', 'repo', 'someGenerationInProgress',
    'LxDialogService', 'LxNotificationService',
    function ($scope, $state, $stateParams, $interval, GenerateVideoResource, GenerationInfoResource, repo, someGenerationInProgress,
              LxDialogService, LxNotificationService) {
        'use strict';

        $scope.repoName = $stateParams.repoName;
        $scope.videoUrl = repo.videoPath;

        function isSomeVideoGenerationInProgress(data) {
            return !!data.repoInProgress && data.repoInProgress !== $stateParams.repoName;
        }

        $scope.flags = {
            generationInProgress: repo.isGenerating,
            generateButtonPressed: false,
            someGenerationInProgress: isSomeVideoGenerationInProgress(someGenerationInProgress),
            videoNotFound: repo.videoPath === null
        };

        $interval(function () {
            if (!$scope.flags.generateButtonPressed) {
                GenerationInfoResource.getForRepo({repoName: $scope.repoName}, function (data) {
                    $scope.flags.generationInProgress = !!data.repoInProgress;
                });
            }
        }, 1000);

        $interval(function () {
            if (!$scope.flags.generationInProgress) {
                GenerationInfoResource.get({}, function (data) {
                    $scope.flags.someGenerationInProgress = isSomeVideoGenerationInProgress(data);
                });
            }
        }, 1000);

        $scope.generateNewVideo = function () {
            $scope.flags.generationInProgress = true;
            $scope.flags.generateButtonPressed = true;

            GenerateVideoResource.generate({repoName: $scope.repoName}).$promise.then(function () {
                $state.reload();
            }).catch(function () {
                LxNotificationService.error('Problem with generating a video');
                $state.reload();
            });
        };

        $scope.cancelGeneration = function () {
            // TODO
        };

        $scope.openDialog = function (dialogId) {
            LxDialogService.open(dialogId);
        };
    }
]);