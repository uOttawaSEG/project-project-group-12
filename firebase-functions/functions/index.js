const admin = require("firebase-admin");
const functions = require("firebase-functions");

// Initialize Firebase Admin SDK
admin.initializeApp({
  credential: admin.credential.applicationDefault(),
  databaseURL: "https://eams-68bc8-default-rtdb.firebaseio.com/", //    check if last backslah messs up stuff
});


// Create a new user and store additional information in Realtime Database
exports.createUser = functions.https.onRequest((req, res) => {
  const {email, password, user, id} = req.body;

  admin.auth().createUser({email, password})
      .then((userRecord) => {
        return admin.database().ref(`/users/${userRecord.uid}`).set(user)
            .then(() => {
              // then Delete regitrationPending from DN
              const deleteRegistration = `https://<region>-<project-id>.cloudfunctions.net/deleteRegistration`;
              return fetch(deleteRegistration, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({registrationId: id}),
              });
            })
            .then((response) => response.json())
            .then((data) => res.status(200).send(userRecord))
            .catch((error) => res.status(500).send(error));
      })
      .catch((error) => res.status(400).send(error));
});


exports.deleteRegistration = functions.https.onRequest((req, res) => {
  const {registrationId} = req.body;

  if (!registrationId) {
    return res.status(400).send("Registration Id is required");
  }


  const path = `/resgistration/${registrationId}`;
  // may need to execute promise differenlty
  admin.database().ref(path).remove()
      .then(() =>{
        const msg=`Registration with Id ${registrationId} deleted successfully`;
        res.status(200).send(msg);
      })
      .catch((error) => {
        const msg = `Error deleting registration: ${error.message}`;
        res.status(500).send(msg);
      });
});

/*
// Example of another function - for teammates
exports.helloWorld = functions.https.onRequest((req, res) => {
  res.send("Hello, World!");
});
*/
