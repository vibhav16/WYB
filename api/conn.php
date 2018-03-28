<?php

//$loc=$_POST['name'];

$loc='mumbai';

$city='curl -X GET --header "Accept: application/json" --header "user-key: 83b086da868e2bd694e3751a75ba8a41" "https://developers.zomato.com/api/v2.1/locations?query="'.$loc;

$d=exec($city);
$di=json_decode($d,true);

$city_id= $di['location_suggestions']['0']['city_id'];
$entity_type= $di['location_suggestions']['0']['entity_type'];
$entity_id= $di['location_suggestions']['0']['entity_id'];


$rest='curl -X GET --header "Accept: application/json" --header "user-key: 83b086da868e2bd694e3751a75ba8a41" "https://developers.zomato.com/api/v2.1/location_details?entity_id="'.$entity_id.'"&entity_type="'.$entity_type;

$r=exec($rest);
$re=json_decode($r,true);
$res_name=array();
$res_image=array();

$connect=mysql_connect("localhost","root","");
  $db=mysql_select_db("wyb");
for($i=0;$i<10;$i++)
{
	$name=$re['best_rated_restaurant'][$i]['restaurant']['name'];
	$image=$re['best_rated_restaurant'][$i]['restaurant']['featured_image'];
echo $i;
echo $name;
echo "&nbsp&nbsp&nbsp";
	$name=mysql_real_escape_string($name);
	$image=mysql_real_escape_string($image);
  $query=mysql_query("insert into res(res_name,res_image,location) values('$name','$image','$loc')");  
echo $re['best_rated_restaurant'][$i]['restaurant']['featured_image'];

echo"<br>";
}





?>