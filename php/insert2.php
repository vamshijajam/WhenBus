<?php
$servername = "localhost";
$username = "root";
$password = "ponni2008";
$dbname = "myDB";
$busname = $argv[1];
$latitude = $argv[2];
$longitude = $argv[3];
$time = $argv[4];
$location = $argv[5];
// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "INSERT INTO realtime (busname,latitude,longitude,time,location)
VALUES ('$busname','$latitude','$longitude','$time','$location')";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>