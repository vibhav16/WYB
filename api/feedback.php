<?php
$name = $_POST['name'];
$useremail = $_POST['email'];
$feedback = $_POST['feedback'];
$user = "root";
$pass = "";
$host= "localhost";
$dbname="wyb";
 
$con = mysqli_connect($host,$user,$pass,$dbname);
$sql="insert into feedback(name,email,feedback) values('".$name."','".$useremail."','".$feedback."');";
if(mysqli_query($con,$sql)){
 echo  "Thank you for the feedback";
 
}else{ 
 echo "Failed";
}
?>