(function() {

'use strict';

var module = angular.module('ionicspotify.controllers');

module.controller('PlaylistCtrl', function ($scope, $stateParams, Spotify) {
  console.log("Entrei PlaylistCtrl");
  $scope.listname = $stateParams.listname;

  $scope.audio = new Audio();

  $scope.getPlaylist = function () {
    Spotify.getPlaylist($stateParams.userId, $stateParams.playlistId).then(function (data) {
      $scope.tracks = data.tracks.items;
    });
  };

  $scope.playTrack = function (trackInfo) {
    $scope.audio.src = trackInfo.track.preview_url;
    $scope.audio.play();
  };

  $scope.openSpotify = function (link) {
    window.open(link, '_blank', 'location=yes');
  };

  $scope.stop = function () {
    if ($scope.audio.src) {
      $scope.audio.pause();
    }
  };

  $scope.play = function () {
    if ($scope.audio.src) {
      $scope.audio.play();
    }
  };

  ionic.Platform.ready(function () {
    $scope.getPlaylist();
  });
})


})();
