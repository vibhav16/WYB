<?php

//$loc=$_POST['name'];

$loc='delhi';

$city='curl -X GET --header "Accept: application/json" --header "user-key: 5042dc756da4b6839dcc3bab1c8cb822" "https://developers.zomato.com/api/v2.1/locations?query="'.$loc;

$d=exec($city);
$di=json_decode($d,true);

$city_id= $di['location_suggestions']['0']['city_id'];
$entity_type= $di['location_suggestions']['0']['entity_type'];
$entity_id= $di['location_suggestions']['0']['entity_id'];

echo $city_id;
echo "<br>";
echo $entity_id;
echo "<br>";
$c='curl -X GET --header "Accept: application/json" --header "user-key: 5042dc756da4b6839dcc3bab1c8cb822" "https://developers.zomato.com/api/v2.1/search?entity_id="'.$entity_id.'"&entity_type="'.$entity_type.'"&count=20&sort=rating"';

$res=exec($c);
$re=json_decode($res,true);


$connect=mysql_connect("localhost","root","");
$db=mysql_select_db("wyb");
for($i=0;$i<20;$i++)
{

  $name=$re['restaurants'][$i]['restaurant']['name'];
  $address=$re['restaurants'][$i]['restaurant']['location']['address'];
  $image=$re['restaurants'][$i]['restaurant']['featured_image'];
  $lat=$re['restaurants'][$i]['restaurant']['location']['latitude'];
  $long=$re['restaurants'][$i]['restaurant']['location']['longitude'];
  $cu=$re['restaurants'][$i]['restaurant']['cuisines'];

$sp="&nbsp";
echo $name;
echo $sp;
echo $address;
echo $sp;
echo "<br>";
$name=mysql_real_escape_string($name);
$image=mysql_real_escape_string($image);
$address=mysql_real_escape_string($address);

$query=mysql_query("insert into res(res_name,res_image,location,addr,lat,lon,cuisine) values('$name','$image','$loc','$address','$lat','$long','$cu')");

}

?>
  
