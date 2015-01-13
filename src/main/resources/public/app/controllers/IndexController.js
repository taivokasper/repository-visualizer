angular.module('rv').controller('IndexController', [
    '$scope', '$location', 'RepoListResource',
    function ($scope, $location, RepoListResource) {
        'use strict';

        $scope.repos = [];

        RepoListResource.getRepos().$promise.then(function (data) {
            $scope.repos = data;
        });

        $scope.isActiveRepo = function (repoName) {
            return $location.url() === '//video/' + repoName;
        };
    }
]);