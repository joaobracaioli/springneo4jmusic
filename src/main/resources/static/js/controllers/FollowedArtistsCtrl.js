(function() {

'use strict';

var module = angular.module('ionicspotify.controllers');

module.controller('FollowedArtistsCtrl', function ($scope, Spotify) {

    $scope.getFollowedArtists = function () {
      Spotify.following('artist', {limit: 50}).then(function (data) {
        $scope.artists = data.artists.items;
      });
    };

    ionic.Platform.ready(function () {
      $scope.getFollowedArtists();
    });
  });

  
})();
