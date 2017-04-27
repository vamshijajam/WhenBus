<?php
$servername = "localhost";
$username = "root";
$password = "ponni2008";
$dbname = "myDB";

$fin = array();
$fin2 = array();
$maxentriesforquery = 2;
$count = 0;
// Create connection
function cmp($a, $b)
{
    if ($a["time"] == $b["time"]) {
        return 0;
    }
    return ($a["time"] > $b["time"]) ? -1 : 1;
}

$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 
echo "hey\n";
echo sizeof($_GET)."\n";
foreach ($_GET as $key => $value) {
    echo "$key: $value\n";
}
if(empty($_POST['username']) || empty($_POST['password'])) {
	echo("hey2\n");
}
$from = $_GET["from"];
$destination = $_GET["destination"];
$busnumber = $_GET["busnumber"];

$sql = "SELECT busname,location,time FROM realtime where busname = '$busnumber'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
    	array_push($fin, $row);
        //echo "location: " .$row["location"]. "   time: " . $row["time"]. "<br>";
    }
} 
else {
    echo "0 results";
}
usort($fin,"cmp");

if(sizeof($fin)>$maxentriesforquery)
{
	while($count<$maxentriesforquery)
	{
		array_push($fin2,$fin[$count]);
		$count += 1;
	}
}
else
{
	$fin2 = $fin;
}	
//echo $fin[0]["time"]."\n".$fin[0]["time"]."\n";
echo json_encode($fin2);
// echo "\n";
$conn->close();
?>