'use strict'

const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/Notifications/{project_id}/{notification_id}').onWrite((data, context) => {
	const project_id = context.params.project_id;
	const notification_id = context.params.notification_id;


	const deviceToken = admin.database().ref(`/Notifications/${project_id}/${notification_id}/device`).once('value');
	return deviceToken.then(result => {
		const token_id = result.val();

		const orgid = admin.database().ref(`/Notifications/${project_id}/${notification_id}/organization`).once('value');
		return orgid.then(result2=>{
			const org = result2.val();
			const payload = {
				notification:{
					title: `${project_id}`,
					body: 'User has joined the meeting panel',
					icon: "default",
					sound: "default",
					click_action : `${org}`

				}
			}
			return admin.messaging().sendToDevice(token_id,payload).then(response =>{
				return console.log('test ',project_id);

			});
		});
	});
});
