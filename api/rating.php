<?php
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
	echo "&nbsp";
	$arr[$i][0]=$row[0];
	$arr[$i][1]=$row[1];
	//echo $row[1];
	//echo "<br>";
	$i++;
}

$q2=mysql_query("select * from food");
while($row1=mysql_fetch_array($q2))
{
	echo $row1[0];
	echo "&nbsp";
	$arr1[$j][0]=$row1[0];
	$arr1[$j][1]=$row1[1];
	echo $row1[1];
	echo "<br>";
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
?>