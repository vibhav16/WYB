<?php
function knapSolve($w,$v,$i,$aW,&$m,$pickedItems) 
{

    global $numcalls;
    $numcalls ++;
    if (isset($m[$i][$aW])) 
     {
        return array( $m[$i][$aW], $m['picked'][$i][$aW] );
     } 
    else 
     {

        if ($i == 0) 
         {
            if ($w[$i] <= $aW) 
             {
                $m[$i][$aW] = $v[$i]; 
                $m['picked'][$i][$aW] = array($i); 
                return array($v[$i],array($i)); 
             } 
            else
            {
                
                $m[$i][$aW] = 0; // Memo zero
                $m['picked'][$i][$aW] = array(); // and a blank array entry...
                return array(0,array()); // Return nothing
            }
        }   

        
        list ($without_i,$without_PI) = knapSolve($w, $v, $i-1, $aW,$m,$pickedItems);

        if ($w[$i] > $aW)
          { 

            $m[$i][$aW] = $without_i; 
            $m['picked'][$i][$aW] = array(); 
            return array($without_i,array()); 
          } 

        else 
         {
            list ($with_i,$with_PI) = knapSolve($w, $v, ($i-1), ($aW - $w[$i]),$m,$pickedItems);
            $with_i += $v[$i];  

            
            if ($with_i > $without_i) 
            {
                $res = $with_i;
                $picked = $with_PI;
                array_push($picked,$i);
            } 
           else 
            {
                $res = $without_i;
                $picked = $without_PI;
            }

            $m[$i][$aW] = $res; 
            $m['picked'][$i][$aW] = $picked; 
            return array ($res,$picked); 
        }   
    }
}

$conn=mysql_connect("localhost","root");
$db=mysql_select_db("wyb");

$q1=mysql_query("select * from test");

$i=0;
$j=0;
$arr[100][100]=0;
$arr1[100][100]=0;
while($row=mysql_fetch_array($q1))
{
	//echo $row[0];
	//echo "&nbsp";
	$arr[$i][0]=$row[0];
	$arr[$i][1]=$row[1];
	//echo $row[2];
	//echo "<br>";
	$i++;
}

$q2=mysql_query("select * from food");
while($row1=mysql_fetch_array($q2))
{
	//echo $row1[0];
	echo "&nbsp";
	$arr1[$j][0]=$row1[0];
	//$arr1[$j][1]=$row1[3];
	//echo $row1[2];
	//echo "<br>";
	$j++;
}

 for($k=0;$k<$i;$k++)
 {
    for($s=0;$s<$j;$s++)
	 {
       if($arr[$k][0]==$arr1[$s][0])
		 {
		    $val=$arr[$k][1];
			$val1=$arr[$k][0];
	        $q3=mysql_query("update food set rating=$val where dish='$val1'");
			break;
		 }
	 }
 }


$qz=mysql_query("select * from food");

$w4=array();
$v4=array();
print_r($v4);
$p=0;
while($ro=mysql_fetch_array($qz))
{
   $items4[$p]=$ro[0];
   $w4[$p]=$ro[2];
   $v4[$p]=$ro[1];
   echo $v4[$p];
   $p++;
   
}
echo sizeof($v4);



## Initialize
$numcalls = 0; $m = array();
 $pickedItems = array();

## Solve
$budget=$_POST["budget"];
$people=$_POST["people"];
$res_name=$_POST["res_name"];
list ($m4,$pickedItems) = knapSolve($w4, $v4, sizeof($v4) -1,$budget,$m,$pickedItems);

# Display Result 
echo "<b>Items:</b><br>".join(", ",$items4)."<br>";
echo "<b>Max Value Found:</b><br>$m4 (in $numcalls calls)<br>";
echo "<b>Array Indices:</b><br>".join(",",$pickedItems)."<br>";
$totalVal=0;
$totalWt=0;

$totalWt_copy=array();
$totalVal_copy=array();
$items_copy=array();
$ii=0;

echo "<b>Chosen Items:</b><br>";
echo "<table border cellspacing=0>";
echo "<tr><td>Item</td><td>Rating</td><td>Cost  </td></tr>";
foreach($pickedItems as $key) 
{
    $totalVal+= $v4[$key];
	$totalVal_copy[$ii]=$v4[$key];
    $totalWt += $w4[$key];
	$totalWt_copy[$ii]=$w4[$key];
	$items_copy[$ii]=$items4[$key];
	$ii++;
    
}

$avg_rat=$totalVal/sizeof($pickedItems);
$min_w=min($totalWt_copy);
$min_w_key=array_keys($totalWt_copy, min($totalWt_copy));

$count=0;
if($totalWt+($min_w*($people-1)) <= $budget )
{
  $totalWt_copy[$min_w_key]= $people*$totalWt_copy[$min_w_key];
  $totalWt=$totalWt+ ($people-1)*$min_w;
  $count=1;
}
 
if($count==1)
{
  for($j=0;$j<$ii;$j++) 
   {
	
     if($totalWt_copy[$j]==$min_w)
       {
		 $items_copy[$j].="(";
		 $items_copy[$j].=$people;
         $items_copy[$j].=")";
	     echo "<tr><td>".$items_copy[$j]."</td><td>".$totalVal_copy[$j]."</td><td>".$totalWt_copy[$j]*$people."</td></tr>";
       }
      else
	  {
     echo "<tr><td>".$items_copy[$j]."</td><td>".$totalVal_copy[$j]."</td><td>".$totalWt_copy[$j]."</td></tr>";
	  }
   }
}
else
{
  for($j=0;$j<$ii;$j++) 
   {
     echo "<tr><td>".$items_copy[$j]."</td><td>".$totalVal_copy[$j]."</td><td>".$totalWt_copy[$j]."</td></tr>";
   }
}


echo "<tr><td align=right><b>Totals</b></td><td>$avg_rat</td><td>$totalWt</td></tr>";
echo "</table><hr>";
$str=" ";




for($j=0;$j<$ii;$j++)
{
   $query=mysql_query("insert into menu values('$res_name',$totalWt_copy[$j],$totalWt,'$items_copy[$j]',$people,$budget)");
  
}


?>

