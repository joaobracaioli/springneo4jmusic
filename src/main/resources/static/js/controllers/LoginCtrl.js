(function() {

'use strict';

var module = angular.module('ionicspotify.controllers');

  module.controller('LoginCtrl', function ($scope, $rootScope, $state, $ionicHistory, $cordovaOauth, Spotify, spotifySetting,apiREST, Api, $ionicLoading) {

    $scope.loginFromBrowser = function () {
      Spotify.login().then(function (result) {
        console.log(result);
        Api.setToken(result);
        var token = Api.getToken();

        //window.localStorage.getItem('spotify-token');
        $scope.completeLogin(token);
      });
    };

    $scope.loginFromCordova = function () {
      var scopeArray = spotifySetting.scope.split(' ');
      $cordovaOauth.spotify(spotifySetting.clientId, scopeArray).then(function (result) {
        Api.setToken(result.access_token);
        //window.localStorage.setItem('spotify-token', result.access_token);

        $scope.completeLogin(result.access_token);
      }, function (error) {
        console.log("Error -> " + error);
      });
    };

    $scope.completeLogin = function (token) {

      Spotify.setAuthToken(token);
      console.log("Entrou no spotify");


      Spotify.getCurrentUser().then(

        function sucesso (dataUser) {
              $ionicLoading.show({
                template: 'Recuperando informações do Spotify...'
              });
                  $rootScope.userId = dataUser.id;
                 Api.setId_spotify(dataUser.id);
                 //Api.setUser(dataUser);

                    Spotify.getSavedUserTracks({ limit: 10 }).then(function (data) {
                       $ionicLoading.hide();
                      console.log("entreou");
                      $ionicLoading.show({
                        template: '<p class="item-icon-left">Gerando seu perfil para recomendação...<ion-spinner icon="lines"/></p>Pode demorar 2 mim ou mais',
                        duration: 30000,
                        noBackdrop: true
                      });

                        data.items = data.items.concat(dataUser);
                        console.log(data.items);
                        apiREST.createUser(data.items, token).then(
                                                  function sucesso(data) {
                                                    console.log(data);
                                                        $ionicLoading.hide();
                                                  },
                                                  function error(errResponse){
                                                      console.error('Error ao gerar seu perfil');
                                                  });


                    });


                $ionicHistory.nextViewOptions({
                  disableBack: true
                });
        /*
        $state.go('app.playlists', {}, {location: 'replace'});
        */
          $state.go('app.main', {}, {location: 'replace'});
      
      }, function erro (erro){

          console.log("erro ao logar");
          $scope.login();
          }
      );

    };

    $scope.login = function () {
      if (ionic.Platform.isWebView())
        $scope.loginFromCordova();
      else
        $scope.loginFromBrowser();
    };

    ionic.Platform.ready(function () {
      var token = Api.getToken();

          if (token !== null) {
            Spotify.setAuthToken(token);
            Spotify.getCurrentUser().then(function (data) {

            }, function(error) {
              $scope.login();
            });

            $rootScope.userId = Api.getId_spotify();
            //$scope.completeLogin(token);
            $ionicHistory.nextViewOptions({
              disableBack: true
            });
            /*
            $state.go('app.playlists', {}, {location: 'replace'});
            */
              $state.go('app.main', {}, {location: 'replace'});

      }
    });

  })
})();