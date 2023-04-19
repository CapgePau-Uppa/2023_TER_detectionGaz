const express = require('express');
const app = express();

const url = "mongodb://127.0.0.1:27017"
const {MongoClient} = require('mongodb');
const client = new MongoClient(url)


app.use(express.urlencoded({extended:true}));
app.use(express.json());


const addAlert = async (req, res) => {
    try{
        
        console.log("add alert request received");
        const newAlert = {
            longitude: req.body.longitude,
            latitude: req.body.latitude,
            danger: req.body.danger
        }
        const arangarciaDB = client.db('arangarciaDB');
        const collection = arangarciaDB.collection('alertTable');
        let result = await collection.insertOne(newAlert)

        res.status(200).json(result);
    } catch(error){
        console.log(error);
        res.status(500).json(error);
    }
}

const getAlert = async (req, res) => {
    try{
        console.log("get alert request received ");
        const arangarciaDB = client.db('arangarciaDB');
        const collection = arangarciaDB.collection('alertTable');
        let cursor = await collection.find()
        let result = await cursor.toArray();
        if(result.length>0){
            res.status(200).json(result);
        } else{
            res.status(204).json({msg:"No alerts"});
        }

    } catch(error){
        console.log(error);
        res.status(500).json(error);
    }
}

const router = express.Router();
router.route("/addAlert").post( addAlert );
router.route("/getAlert").post( getAlert );
app.use("/arangarcia", router);


app.listen(3000, () => {
    console.log("listening on port 3000");
})