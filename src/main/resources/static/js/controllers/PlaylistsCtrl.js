(function() {

'use strict';

var module = angular.module('ionicspotify.controllers');

module.controller('PlaylistsCtrl', function ($scope, $rootScope, Spotify) {

    $scope.getUserPlaylists = function () {
      Spotify.getUserPlaylists($rootScope.userId).then(function (data) {
        $scope.playlists = data.items;
      });
    };

    ionic.Platform.ready(function () {
      $scope.getUserPlaylists();
    });

  })

})();
