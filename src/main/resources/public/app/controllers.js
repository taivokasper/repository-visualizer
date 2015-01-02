var app = angular.module('app');

app.controller('IndexController', function ($scope, RepoListResource) {
    $scope.repos = [];

    RepoListResource.getRepos().$promise.then(function (data) {
        $scope.repos = data;
    });
});

app.controller('VideoController', function ($scope, video, ngVideoPlaylist, $stateParams, RepoResource, GenerateVideoResource) {
    var repoName = $stateParams.repoName;
    console.log('Repo name:', repoName);

    $scope.flags = {
        generationInProgress: false,
        videoNotFound: true
    };

    function setVideo(data) {
        $scope.flags.generationInProgress = false;
        $scope.flags.videoNotFound = data.videoPath == null;
        if ($scope.flags.videoNotFound) {
            return;
        }

        console.log('Result:', data);

        ngVideoPlaylist.splice(0, ngVideoPlaylist.length);

        video.addSource('mp4', data.videoPath);
    }

    RepoResource.get({repoName: repoName}).$promise
        .then(setVideo)
        .catch(function (err) {
            console.error(err);
        });


    $scope.generateNewVideo = function () {
        console.log('Starting to generate new video for ' + repoName);
        $scope.flags.generationInProgress = true;

        GenerateVideoResource.generate({repoName: repoName}).$promise
            .then(setVideo)
            .catch(function (err) {
                console.error(err);
            });
    };
});