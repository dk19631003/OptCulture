importScripts('https://www.gstatic.com/firebasejs/7.14.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/7.14.2/firebase-messaging.js');
/*Update this config*/
var config = {
  apiKey: 'AIzaSyAzfUY5m6iXOWqmTWB5ceCsmXA_Smk7IaM',
  authDomain: 'wakefiled-web-pushnotification.firebaseapp.com',
  databaseURL: 'https://wakefiled-web-pushnotification.firebaseio.com',
  projectId: 'wakefiled-web-pushnotification',
  storageBucket: 'wakefiled-web-pushnotification.appspot.com',
  messagingSenderId: '637245927687',
  appId: '1:637245927687:web:33404e980ffa22fbb315a2',
  measurementId: 'G-QXSGXQ4SJL',
  };
  firebase.initializeApp(config);

const messaging = firebase.messaging();
messaging.setBackgroundMessageHandler(function(payload) {
  console.log('[firebase-messaging-sw.js] Received background message ', payload);
  // Customize notification here
  const notificationTitle = payload.data.title;
  const notificationOptions = {
    body: payload.data.body,
    icon: 'http://localhost/gcm-push/img/icon.png',
    image: 'http://localhost/gcm-push/img/d.png'
  };

  return self.registration.showNotification(notificationTitle,
      notificationOptions);
});