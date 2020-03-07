const db = require('mysql');
const express = require('express');
const jwt = require('jsonwebtoken');
const uid = require('uniqid');

const app = express()
const connection = db.createPool({
    host: 'localhost',
    user: 'root',
    password: 'root@1441',
    database: 'codeshastra'
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