
angular.module('ionicspotify', ['ionic', 'ionicspotify.controllers','ionicspotify.apiREST',
                                'ionicspotify.Api', 'ngCordova', 'ngCordovaOauth', 'spotify',
                                'LocalStorageModule'])

  .constant('spotifySetting', {
    clientId: '391333d8687241cb806121b668f43a5c',
    scope: 'playlist-read-private playlist-read-collaborative user-follow-modify user-follow-read user-library-read user-read-email user-library-read user-top-read',
  })

  .run(function ($ionicPlatform, $ionicPopup,$ionicHistory) {
    $ionicPlatform.ready(function () {

      if (window.cordova && window.cordova.plugins.Keyboard) {
        cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
        cordova.plugins.Keyboard.disableScroll(true);

      }
      if (window.StatusBar) {
        StatusBar.styleDefault();
      }
    });

    $ionicPlatform.registerBackButtonAction(function(event) {
       if (true) { // your check here

         /*$ionicPopup.confirm({
           title: 'System warning',
           template: 'are you sure you want to exit?'
         }).then(function(res) {
           if (res) {
             ionic.Platform.exitApp();
           }
         })
         */
         $ionicHistory.goBack();
       }

     }, 100);
  })

  .config(function ($stateProvider, $urlRouterProvider, SpotifyProvider, spotifySetting) {

    SpotifyProvider.setClientId(spotifySetting.clientId);
    SpotifyProvider.setRedirectUri('http://localhost:8100/templates/callback.html');
    SpotifyProvider.setScope(spotifySetting.scope);

    $stateProvider

      .state('app', {
        url: '/app',
        abstract: true,
        templateUrl: 'templates/menu.html',
        controller: 'AppCtrl'
      })

      .state('app.login', {
        url: '/login',
        views: {
          'menuContent': {
            templateUrl: 'templates/login.html',
            controller: 'LoginCtrl'
          }
        }
      })

      .state('app.search', {
        url: '/search',
        views: {
          'menuContent': {
            templateUrl: 'templates/search.html',
            controller: 'SearchCtrl'
          }
        }
      })

      .state('app.all_group', {
        url: '/all_group',
        views: {
          'menuContent': {
            templateUrl: 'templates/all_group.html',
            controller: 'AllGroupCtrl'
          }
        }
      })
      .state('app.followed-artists', {
        url: '/followed-artists',
        views: {
          'menuContent': {
            templateUrl: 'templates/followed-artists.html',
            controller: 'FollowedArtistsCtrl'
          }
        }
      })
      .state('app.playlists', {
        url: '/playlists',
        views: {
          'menuContent': {
            templateUrl: 'templates/playlists.html',
            controller: 'PlaylistsCtrl'
          }
        }
      })
      .state('app.main', {
        url: '/main',
        views: {
          'menuContent': {
            templateUrl: 'templates/main.html',
            controller: 'MainCtrl'
          }
        }
      })
      .state('app.createGroup', {
        url: '/createGroup',
        views: {
          'menuContent': {
            templateUrl: 'templates/create_context.html',
            controller: 'CreateGroupCtrl'
          }
        }
      })
      .state('app.group', {
        url: '/group/:groupId/:userId',
        views: {
          'menuContent': {
            templateUrl: 'templates/group.html',
            controller: 'GroupCtrl'
          }
        }
      })
      .state('app.participants', {
        url: '/group/participants',
        views: {
          'tab-participants': {
            templateUrl: 'templates/group.html',
            controller: 'GroupCtrl'
          }
        }
      })
      .state('app.playlist', {
        url: '/group/playlist',
        views: {
          'tab-playlist': {
            templateUrl: 'templates/group.html',
            controller: 'GroupCtrl'
          }
        }
      })
      .state('app.genere', {
        url: '/group/genere',
        views: {
          'tab-genere': {
            templateUrl: 'templates/group.html',
            controller: 'GroupCtrl'
          }
        }
      })
      .state('app.single', {
        url: '/playlists/:playlistId/:userId/:playlistName',
        views: {
          'menuContent': {
            templateUrl: 'templates/playlist.html',
            controller: 'PlaylistCtrl'
          }
        }
      });

    $urlRouterProvider.otherwise('/app/login');
  });
