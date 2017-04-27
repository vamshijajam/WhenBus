<?php
$servername = "localhost";
$username = "root";
$password = "ponni2008";
$dbname = "myDB";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "INSERT INTO dynamic (user_ID,Bus_no,user_latitude,user_longitude,time,Destination)
VALUES ('21233ght56','23c',12.333,13.456,'13:0:1','chennai')";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>