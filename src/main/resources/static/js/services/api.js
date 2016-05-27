(function() {

'use strict';

    var module = angular.module('ionicspotify.Api', [])

      module.factory('Api', function(localStorageService) {


          return {
                getToken: function() {
                 var token = localStorageService.get('spotify-token');
                 return token;
               },
               setToken: function(token) {
                 localStorageService.set('spotify-token', token);
               },
               getId_spotify: function() {
                   var id_spotify = localStorageService.get('id_spotify');
                   return id_spotify;
               },
               setId_spotify: function(id_spotify) {
                    localStorageService.set('id_spotify', id_spotify);
               },
               getUser: function() {
                   var username = localStorageService.get('user');
                   return username;
               },
               setUser: function(user) {
                    localStorageService.set('user', user);
               }


          }

      })

  })();
