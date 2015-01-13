angular.module('rv').directive('repoVideo', ['$sce', function ($sce) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            sourceUrl: '='
        },
        templateUrl: '/app/directives/views/video.html',
        controller: function ($scope) {
            $scope.config = {
                sources: [
                    {
                        src: $sce.trustAsResourceUrl($scope.sourceUrl),
                        type: 'video/mp4'
                    }
                ],
                theme: '/lib/videogular-themes-default/videogular.css',
                autohide: true,
                autohideTime: 1500
            };
        }
    };
}]);