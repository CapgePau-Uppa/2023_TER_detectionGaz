const express = require('express');
const app = express();
const mongoClient = require('mongodb').MongoClient;

const url = "mongodb://localhost:27017"

app.use(express.json());

mongoClient.connect(url, (err, db) => {
    if (err) {
        console.log("Error connecting to Mongo Client");
    } else {

        const arangarciaDB = db.db('arangarciaDB');
        const collection = arangarciaDB.collection('alertTable');

        app.post('/addAlert', (req, res) => {

            const newAlert = {
                longitude: req.body.longitude,
                latitude: req.body.latitude,
                danger: req.body.danger
            }

            collection.insertOne(newAlert, (err, result) => {
                res.status(200).send();
            })
        })

        app.post('/getAlerts', (req, res) => {

            collection.find().sort({'id': 1}).toArray(function(err, result) {
                if(result == [] || result == ''){
                 res.status(404).send();
                }
                else{
                 output.contents = result;
                 res.status(200).send(output);
                }
            });

            /*                GET specific alerts
            let query = { danger: "Urgent" };
            
            collection.find(query, (err, result)=>{
                if(result != null){
                    const objToSend = {
                        longitude: req.body.longitude,
                        latitude: req.body.latitude,
                        danger: req.body.danger
                    }

                    res.status(200).send();
                }
            } );*/
            
        });

        
        app.listen(3000, () => {
            console.log("listening on port 3000");
        })
    }
})