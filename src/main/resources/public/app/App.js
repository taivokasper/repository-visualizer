angular.module('rv',
        [
            'ui.router',
            'ngResource',
            'lumx',
            'com.2fdevs.videogular',
            'com.2fdevs.videogular.plugins.controls',
            'com.2fdevs.videogular.plugins.overlayplay',
            'com.2fdevs.videogular.plugins.poster'
        ]
    ).config(function ($stateProvider, $urlRouterProvider) {
        'use strict';

        $urlRouterProvider.otherwise('/');

        $stateProvider
            .state('index', {
                url: '',
                templateUrl: 'views/index.html',
                controller: 'IndexController'
            })
            .state('index.video', {
                url: '/video/:repoName',
                templateUrl: 'views/video.html',
                controller: 'VideoController',
                resolve: {
                    repo: ['$stateParams', 'RepoResource', function ($stateParams, RepoResource) {
                        return RepoResource.get({ repoName: $stateParams.repoName }).$promise;
                    }]
                }
            });
    });