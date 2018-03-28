<?php


$loc='pune';
$city='curl -X GET --header "Accept: application/json" --header "user-key: 5042dc756da4b6839dcc3bab1c8cb822" "https://developers.zomato.com/api/v2.1/locations?query="'.$loc;

$d=exec($city);
$di=json_decode($d,true);

$city_id= $di['location_suggestions']['0']['city_id'];
$entity_type= $di['location_suggestions']['0']['entity_type'];
$entity_id= $di['location_suggestions']['0']['entity_id'];

$loc='curl -X GET --header "Accept: application/json" --header "user-key: 5042dc756da4b6839dcc3bab1c8cb822" "https://developers.zomato.com/api/v2.1/search?entity_id="'.$entity_id.'"&entity_type="'.$entity_type;
$l=exec($loc);
$de=json_decode($l,true);

$res_id=array();
for($i=0;$i<20;$i++)
{

 $res_id[$i]=$de['restaurants'][$i]['restaurant']['R']['res_id'];
 echo $res_id[$i];
 echo "<br>";
}


$res=array();
$r=array();
$du=array();


for($i=0;$i<20;$i++)
{
  $res[$i]='curl -X GET --header "Accept: application/json" --header "user-key: 5042dc756da4b6839dcc3bab1c8cb822" "https://developers.zomato.com/api/v2.1/reviews?res_id="'.$res_id[$i];
  $r[$i]=exec($res[$i]);
  $du[$i]=json_decode($r[$i],true);
   for($j=0;$j<5;$j++)
  {
    echo $j." ".$du[$i]['user_reviews'][$j]['review']['review_text'];
    echo "<br>";
     echo "<br>";
 
  }
 echo "<br>";
 echo "<br>";
 echo "<br>";
 echo "--------------------------------------------------------------------------------------------------------------------------------------------------";
echo "<br>";
}





?>