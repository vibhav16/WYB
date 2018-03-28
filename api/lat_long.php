<?php 
	//Creating a connection
	$con = mysqli_connect("localhost:3306","root","","wyb");
	 
    if (mysqli_connect_errno())
    {
       echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }

	$loc= $_GET["location"];
	$sql= "Select lat,lon,res_name,addr,location from res where location='$loc'";
	
	$result = mysqli_query($con ,$sql);
	
	while ($row = mysqli_fetch_assoc($result)) {
		
		$array[] = $row;
		
	}
	header('Content-Type:Application/json');
	
	echo json_encode($array);
 
    mysqli_free_result($result);
 
    mysqli_close($con);
  
 ?>