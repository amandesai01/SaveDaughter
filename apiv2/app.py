from flask import Flask, request, jsonify
import uuid
import mysql.connector
import requests

app = Flask(__name__)

def connect():
    return mysql.connector.connect(user = 'root', password = 'root@1441', host = 'localhost', database = 'codeshastra')

@app.route('/register')
def register():
    connection = connect()
    cursor = connection.cursor()
    uid = str(uuid.uuid4())[:9]
    name = request.args.get('name')
    aadhar = request.args.get('aadhar')
    imagelink = request.args.get('imagelink')
    phoneno = request.args.get('phoneno')
    password = request.args.get('password')

    values = (uid, name, password, aadhar, imagelink, phoneno)
    response = {}
    try:
        cursor.execute("INSERT INTO `codeshastra`.`users` (`uid`, `name`, `password` ,`aadhar`, `imagelink`, `phoneno`) VALUES (%s, %s, %s, %s, %s, %s) ", values)
        connection.commit()
        response = {
            "message": "Registered!",
            "status" : "OK",
            "assigneduserid": uid
        }

        return jsonify(response)
    except Exception as e:
        response = {
            "error": str(e),
            "message": "Not Registered",
            "status" : "FAIL"
        }
        return jsonify(response)
    finally:
        connection.close()

@app.route('/registerpolice')
def registerpolice():
    id = str(uuid.uuid4())[:9]
    fullname = request.args.get('fullname')
    imagelink = request.args.get('imagelink')
    phoneno = request.args.get('phoneno')
    address = request.args.get('address')
    latitude = None
    longitude = None
    url = "https://api.mapbox.com/geocoding/v5/mapbox.places/"
    params = ".json?access_token=pk.eyJ1IjoiZGV2NDQiLCJhIjoiY2s3aG5mM3JvMDduNTNucHJrM2U1OXU1NiJ9.W0bBWZgSPXA-WFeXJ9Qqeg&limit=1"
    try:
        conn = connect()
        cursor = conn.cursor()
        data = requests.get(url + address + params).json()
        latitude = data['features'][0]['center'][1]
        longitude = data['features'][0]['center'][0]
        sql = "INSERT INTO `codeshastra`.`policeofficials` (`id`, `fullname`, `imagelink`, `phoneno`, `address`, `latitude`, `longitude`) VALUES (%s, %s, %s, %s, %s, %s, %s) "
        values = (id, fullname, imagelink, phoneno, address, latitude, longitude)
        cursor.execute(sql, values)
        conn.commit()
        return jsonify({
            "message": "Registered!",
            "status" : "OK",
            "assigneduserid" : str(id),
            "latitude" : str(latitude),
            "longitude" : str(longitude)
        })
    except Exception as e:
        return jsonify({
            "error" : str(e),
            "status" : "FAIL",
            "message" : "Not Registered"
        })

@app.route('/loginuser')
def loginuser():
    phoneno = request.args.get('phoneno')
    password = request.args.get('password')
    con = connect()
    cursor = con.cursor()
    sql = "select password from users where phoneno = %s"
    cursor.execute(sql, [phoneno])
    out = cursor.fetchall()
    flag = False
    for i in out:
        for x in i:
            if x == password:
                flag = True
                break
        if flag:
            break
    if not flag:
        return jsonify({ "status" : "FAIL", "message" : "WRONG PASSWORD OR PHONENO"})
    return jsonify({ "status" : "OK", "message": "LOGGEDIN"})

@app.route('/nearestps')
def nearestps():
    latitude = request.args.get('latitude')
    longitude = request.args.get('longitude')

    qlat = round(float(latitude), 2)
    qlon = round(float(longitude), 2)

    qlat1 = round((qlat + 0.01), 2)
    qlat2 = round((qlat - 0.01), 2)
    qlon1 = round((qlon + 0.01), 2)
    qlon2 = round((qlon - 0.01), 2)

    selectedStations = []

    con = connect()
    cursor = con.cursor()
    sql = "SELECT id FROM policeofficials WHERE latitude = " + str(qlat1 )+ " AND longitude = " + str(qlon1 )+ ""
    sql1 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat2) + " AND longitude = " +str( qlon2) + ""
    sql2 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat )+ " AND longitude = " + str(qlon ) + ""
    sql3 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat2) + " AND longitude = " +str( qlon1) + ""
    sql4 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat1) + " AND longitude = " +str( qlon2) + ""
    sql5 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat )+ " AND longitude = " + str(qlon1 )+ ""
    sql6 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat )+ " AND longitude = " + str(qlon2 )+ ""
    sql7 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat1) + " AND longitude = " +str( qlon )+ ""
    sql8 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat2) + " AND longitude = " +str( qlon )+ ""
    
    cursor.execute(sql)
    res = cursor.fetchall()
    if len(res) > 0:
        selectedStations.append(res[0][0])
    cursor.execute(sql1)
    res1 = cursor.fetchall()
    if len(res1) > 0:
        selectedStations.append(res1[0][0])
    cursor.execute(sql2)
    res2 = cursor.fetchall()
    if len(res2) > 0:
        selectedStations.append(res2[0][0])
    cursor.execute(sql3)
    res3 = cursor.fetchall()
    if len(res3) > 0:
        selectedStations.append(res3[0][0])
    cursor.execute(sql4)
    res4 = cursor.fetchall()
    if len(res4) > 0:
        selectedStations.append(res4[0][0])
    cursor.execute(sql5)
    res5 = cursor.fetchall()
    if len(res5) > 0:
        selectedStations.append(res5[0][0])
    cursor.execute(sql6)
    res6 = cursor.fetchall()
    if len(res6) > 0:
        selectedStations.append(res6[0][0])
    cursor.execute(sql7)
    res7 = cursor.fetchall()
    if len(res7) > 0:
        selectedStations.append(res7[0][0])
    cursor.execute(sql8)
    res8 = cursor.fetchall()
    if len(res8) > 0:
        selectedStations.append(res8[0][0])
    if len(selectedStations) == 0:
        return jsonify({})
    selectedNames = []
    for i in selectedStations:
        quer = "SELECT address FROM policeofficials WHERE id = %s"
        cursor.execute(quer, (i, ))
        res = cursor.fetchone()
        tdic = {
            "phone":res[0]
        }
        selectedNames.append(res[0])
    print(selectedNames)
    return jsonify({"values": selectedNames})


@app.route('/generatealert')
def generatealert():
    targetid = request.args.get('id')
    latitude = request.args.get('latitude')
    longitude = request.args.get('longitude')

    qlat = round(float(latitude), 2)
    qlon = round(float(longitude), 2)

    qlat1 = round((qlat + 0.01), 2)
    qlat2 = round((qlat - 0.01), 2)
    qlon1 = round((qlon + 0.01), 2)
    qlon2 = round((qlon - 0.01), 2)

    selectedStations = []

    con = connect()
    cursor = con.cursor()
    sql = "SELECT id FROM policeofficials WHERE latitude = " + str(qlat1 )+ " AND longitude = " + str(qlon1 )+ ""
    sql1 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat2) + " AND longitude = " +str( qlon2) + ""
    sql2 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat )+ " AND longitude = " + str(qlon ) + ""
    sql3 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat2) + " AND longitude = " +str( qlon1) + ""
    sql4 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat1) + " AND longitude = " +str( qlon2) + ""
    sql5 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat )+ " AND longitude = " + str(qlon1 )+ ""
    sql6 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat )+ " AND longitude = " + str(qlon2 )+ ""
    sql7 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat1) + " AND longitude = " +str( qlon )+ ""
    sql8 = "SELECT id FROM policeofficials WHERE latitude = " +str( qlat2) + " AND longitude = " +str( qlon )+ ""
    
    cursor.execute(sql)
    res = cursor.fetchall()
    if len(res) > 0:
        selectedStations.append(res[0][0])
    cursor.execute(sql1)
    res1 = cursor.fetchall()
    if len(res1) > 0:
        selectedStations.append(res1[0][0])
    cursor.execute(sql2)
    res2 = cursor.fetchall()
    if len(res2) > 0:
        selectedStations.append(res2[0][0])
    cursor.execute(sql3)
    res3 = cursor.fetchall()
    if len(res3) > 0:
        selectedStations.append(res3[0][0])
    cursor.execute(sql4)
    res4 = cursor.fetchall()
    if len(res4) > 0:
        selectedStations.append(res4[0][0])
    cursor.execute(sql5)
    res5 = cursor.fetchall()
    if len(res5) > 0:
        selectedStations.append(res5[0][0])
    cursor.execute(sql6)
    res6 = cursor.fetchall()
    if len(res6) > 0:
        selectedStations.append(res6[0][0])
    cursor.execute(sql7)
    res7 = cursor.fetchall()
    if len(res7) > 0:
        selectedStations.append(res7[0][0])
    cursor.execute(sql8)
    res8 = cursor.fetchall()
    if len(res8) > 0:
        selectedStations.append(res8[0][0])
    if len(selectedStations) == 0:
        return jsonify({})
    for i in selectedStations:
        sql = "UPDATE policeofficials SET CrimeCount = CrimeCount + 1 WHERE id = %s"
        cursor.execute(sql, (i,))
        con.commit()
    selectedNos = []
    for i in selectedStations:
        quer = "SELECT phoneno FROM policeofficials WHERE id = %s"
        cursor.execute(quer, (i, ))
        res = cursor.fetchone()
        tdic = {
            "phone":res[0]
        }
        selectedNos.append(res[0])
    print(selectedNos)
    return jsonify({"values": selectedNos})
            
@app.route('/getcrimes')
def getcrimes():
    conn = connect()
    cursor = conn.cursor()
    quer = "SELECT address, CrimeCount FROM policeofficials";
    cursor.execute(quer)
    result = cursor.fetchall();
    ret = []
    for i in result:
        dic = {}
        dic['address'] = i[0]
        dic['count'] = i[1]
        ret.append(dic)
    print(ret)
    return jsonify(ret)


if __name__ == '__main__':
    app.run(port=3000, debug=True)
    
    