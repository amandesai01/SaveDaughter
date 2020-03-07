const db = require('mysql');
const express = require('express');
const jwt = require('jsonwebtoken');
const uid = require('uniqid');
const request = require('request');
const geolib = require('geolib')


const app = express()
const connection = db.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'root@1441',
    database: 'codeshastra'
});

const geocoder = (address, callback) => {
    url = "https://api.mapbox.com/geocoding/v5/mapbox.places/";
    params = ".json?access_token=pk.eyJ1IjoiZGV2NDQiLCJhIjoiY2s3aG5mM3JvMDduNTNucHJrM2U1OXU1NiJ9.W0bBWZgSPXA-WFeXJ9Qqeg&limit=1";
    request({ url: url + address + params, json: true}, (error, response) => {
        if(error || response.body.features.length === 0) {
            console.log(error)
            callback(true, undefined)
        }
        else {
            callback(false, { latitude: response.body.features[0].center[1], longitude: response.body.features[0].center[0]});
        }
    });
};

app.get('/registerpolice', (req, res) => {
    const id = uid('plce-');
    const fullname = req.query.fullname;
    const imagelink = req.query.imagelink;
    const phoneno = req.query.phoneno;
    const address = req.query.address;
    geocoder(address, (error, response) => {
        if(error){
            const ret = {
                status: "FAIL",
                error: "",
                message: "Invalid Address"
            }
            res.send(ret)
        }
        else{
            values = [id.toString(), fullname, imagelink, phoneno, address, response.latitude, response.longitude];
            try{
                connection.query("INSERT INTO `codeshastra`.`policeofficials` (`id`, `fullname`, `imagelink`, `phoneno`, `address`, `latitude`, `longitude`) VALUES (?, ?, ?, ?, ?, ?, ?) ", values, (error) => {
                    if(error){
                        response = {
                            error: error.toString(),
                            message: "Not Registered",
                            status : "FAIL"
                        }
                        console.log(error.toString())
                    }
                });
                response = {
                    message: "Registered!",
                    status : "OK",
                    assigneduserid: id,
                    latitude: response.latitude,
                    longitude: response.longitude
                }
            } catch(error) {
                response = {
                    error: error.toString(),
                    message: "Not Registered",
                    status : "FAIL"
                }
            }

            res.send(response);
        }
    });
});

app.get('/register', (req, res) => {
    const id = uid();
    const name = req.query.name;
    const aadhar = req.query.aadhar;
    const imagelink = req.query.imagelink;
    const phoneno = req.query.phoneno;

    values = [id.toString(), name, aadhar, imagelink, phoneno];
    var response = {};
    try{
        connection.query("INSERT INTO `codeshastra`.`users` (`uid`, `name`, `aadhar`, `imagelink`, `phoneno`) VALUES (?, ?, ?, ?, ?) ", values, (error) => {
            if(error){
                response = {
                    error: error.toString(),
                    message: "Not Registered",
                    status : "FAIL"
                }
            }
        });
        response = {
            message: "Registered!",
            status : "OK",
            assigneduserid: id
        }
    } catch(error) {
        response = {
            error: error.toString(),
            message: "Not Registered",
            status : "FAIL"
        }
    }

    res.send(response);
});

app.get('/generatealert', (req, res) => {
    const targetid = req.query.id;
    const latitude = req.query.latitude;
    const longitude = req.query.longitude;

    var qlat = latitude.toString();
    qlat = qlat.slice(0, 5);
    var qlon = longitude.toString();
    qlon = qlon.slice(0, 4);
    sql = "SELECT * FROM policeofficials WHERE latitude LIKE `" + qlat + "` AND longitude LIKE ` " + qlon + " `";
    connection.query(sql, undefined, (error, results, fields) => {
        console.log(results)
    })
    res.send("OK")

});

app.listen(3000, () => {
    console.log("Listening on Port 3000");
});