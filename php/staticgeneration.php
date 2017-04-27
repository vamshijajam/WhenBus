<?php
$servername = "localhost";
$username = "root";
$password = "ponni2008";
$dbname = "myDB";

try {
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    // set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // sql to create table
    $sql = "CREATE TABLE static (
    Bus_no VARCHAR(16),
    from_latitude FLOAT,
    from_longitude FLOAT,
    destination_latitude FLOAT,
    destination_longitude FLOAT,
    distance FLOAT,
    time TIME
    )";

    // use exec() because no results are returned
    $conn->exec($sql);
    echo "Table static created successfully";
    }
catch(PDOException $e)
    {
    echo $sql . "<br>" . $e->getMessage();
    }

$conn = null;
?>