const db = require('mysql');
const express = require('express');
const jwt = require('jsonwebtoken');
const uid = require('uniqid');
const request = require('request');
const geolib = require('geolib')
const mathjs = require('mathjs')

const app = express()
const connection = db.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'root@1441',
    database: 'codeshastra'
});

const appendObjTo = (thatArray, newObj) => {
    const frozenObj = Object.freeze(newObj);
    return Object.freeze(thatArray.concat(frozenObj));
}

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

    var qlat = parseFloat(latitude).toFixed(2);
    var qlon = parseFloat(longitude).toFixed(2);
    console.log(typeof(qlat)+ " " + typeof(qlon));
    var qlat1 = (mathjs.evaluate(qlat + '+ 0.01')).toFixed(2);
    var qlat2 = mathjs.evaluate(qlat + '- 0.01').toFixed(2);
    var qlon1 = mathjs.evaluate(qlon + '+ 0.01').toFixed(2);
    var qlon2 = mathjs.evaluate(qlon + '- 0.01').toFixed(2);


    var selectedStations = new Array();
    console.log(qlat+ " " + qlon);

    // var sql = "SELECT * FROM policeofficials WHERE latitude LIKE `" + qlat.toString() + "` AND longitude LIKE ` " + qlon.toString() + " `";
    connection.connect();
    sql = "SELECT * FROM policeofficials WHERE latitude = " + qlat1 + " AND longitude = " + qlon1 + "";
    connection.query(sql, undefined, (error, results, fields) => {
        if(results != undefined){
            console.log("In");
            selectedStations.push(results[0]);
            console.log(results[0]);
        }
        sql1 = "SELECT * FROM policeofficials WHERE latitude = " + qlat2 + " AND longitude = " + qlon2 + "";
        connection.query(sql1, undefined, (error, results, fields) => {
            if(results != undefined){
                console.log("In1");
                selectedStations.push(results[0]);
                console.log(results[0]);
            }
            sql2 = "SELECT * FROM policeofficials WHERE latitude = " + qlat + " AND longitude = " + qlon + "";
            connection.query(sql2, undefined, (error, results, fields) => {
                if(results != undefined){
                    console.log("In2");
                    selectedStations.push(results[0]);
                    console.log(results[0]);
                }
                sql3 = "SELECT * FROM policeofficials WHERE latitude = " + qlat2 + " AND longitude = " + qlon1 + "";
                connection.query(sql3, undefined, (error, results, fields) => {
                    if(results != undefined){
                        console.log("In3");
                        selectedStations.push(results[0]);
                        console.log(results[0]);
                    }
                    sql4 = "SELECT * FROM policeofficials WHERE latitude = " + qlat1 + " AND longitude = " + qlon2 + "";
                    connection.query(sql4, undefined, (error, results, fields) => {
                        if(!(results === undefined)){
                            console.log("In4");
                            selectedStations.push(results[0]);
                            console.log(results[0]);
                        }
                        sql5 = "SELECT * FROM policeofficials WHERE latitude = " + qlat + " AND longitude = " + qlon1 + "";
                        connection.query(sql5, undefined, (error, results, fields) => {
                            if(!(results === undefined)){
                                console.log("In5");
                                selectedStations.push(results[0]);
                                console.log(results[0]);
                            }
                            sql6 = "SELECT * FROM policeofficials WHERE latitude = " + qlat + " AND longitude = " + qlon2 + "";
                            connection.query(sql6, undefined, (error, results, fields) => {
                                if(!(results === undefined)){
                                    console.log("In6");
                                    selectedStations.push(results[0]);
                                    console.log(results[0]);
                                }
                                sql7 = "SELECT * FROM policeofficials WHERE latitude = " + qlat1 + " AND longitude = " + qlon + "";
                                connection.query(sql7, undefined, (error, results, fields) => {
                                    if(!(results === undefined)){
                                        console.log("In7");
                                        selectedStations.push(results[0]);
                                        console.log(results[0]);
                                    }
                                    sql8 = "SELECT * FROM policeofficials WHERE latitude = " + qlat2 + " AND longitude = " + qlon + "";
                                    connection.query(sql8, undefined, (error, results, fields) => {
                                        if(!(results === undefined)){
                                            console.log("In8");
                                            selectedStations.push(results[0]);
                                            console.log(results[0]);
                                        }
                                        console.log(selectedStations);
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    });



    connection.end();

    res.send("OK")
});

app.listen(3000, () => {
    console.log("Listening on Port 3000");
});