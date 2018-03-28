<?php 
//connect and select the database 
$connect = mysql_connect("localhost","root","") or die('Database Not Connected. Please Fix the Issue! ' . mysql_error());
 mysql_select_db("wyb", $connect); 
// get the contents of the JSON file
 $jsonCont = file_get_contents('res.json'); 

//decode JSON data to PHP array

 $content = json_decode($jsonCont, true); 
//Fetch the details of Student
 $std_id = $content['res_id']; 

//Insert the fetched Data into Database 
$query = "INSERT INTO res VALUES($std_id)"; 
if(!mysql_query($query,$connect))
 {
 die('Error : Query Not Executed. Please Fix the Issue! ' . mysql_error()); 
}
 else
{ 
echo "Data Inserted Successully!!!"; 
} 

?> 

