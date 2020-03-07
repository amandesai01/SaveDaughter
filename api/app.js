const db = require('mysql');
const express = require('express');
const jwt = require('jsonwebtoken');
const uid = require('uniqid');
const request = require('request');
const app = express()
const connection = db.createPool({
    host: 'localhost',
    user: 'root',
    password: 'root@1441',
    database: 'codeshastra'
});

const geocoder = (address, callback) => {
    url = "https://api.mapbox.com/geocoding/v5/mapbox.places/";
    params = ".json?access_token=pk.eyJ1IjoiYW1hbmRlc2FpMDEiLCJhIjoiY2s2N3c1czZwMDZ5YzNqcGdwMWlidndtaiJ9._ZfmHWHgOaWcanGrdMBmTg&limit=1";
    request({ url: url + address + params, json: true}, (error, response) => {
        if(error || response.body.features.length === 0) {
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
    var latitude;
    var longitude;
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
            latitude = response.latitude;
            longitude = response.longitude;
        }
    });

    values = [id.toString(), fullname, imagelink, phoneno, address];
    var response = {};
    try{
        connection.query("INSERT INTO `codeshastra`.`policeofficials` (`uid`, `fullname`, `imagelink`, `phoneno`, `address`, `latitude`, `longitude`) VALUES (?, ?, ?, ?, ?, ?, ?) ", values, (error) => {
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
            assigneduserid: id,
            latitude: latitude,
            longitude: longitude
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


app.listen(3000, () => {
    console.log("Listening on Port 3000");
});