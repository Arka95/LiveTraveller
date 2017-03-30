<?php

require_once 'include/DB_SessionFunctionsClass.php';
$db = new DB_SessionFunctions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['user_id']) && isset($_POST['type'])) {

    // receiving the post params
    $type = $_POST['type'];
    $uid = $_POST['user_id'];

    if ($type == 1) {
        $location = $db->retrieveLocation($uid);
        if ($location) {
            //location retrieved successfully
            $response["error"] = FALSE;
            $response["location"]["curr_lati"] = $location["current_location_lat"];
            $response["location"]["curr_longi"] = $location["current_location_long"];
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "Unknown error occurred while fetching location";
            echo json_encode($response);
        }
    } 
	else if ($type == 2) {
      

	  $lat=$_POST['lat'];
        $longi=$_POST['longi'];
        $response['success']=$db->updateLocation($uid, $lat, $longi);
		echo json_encode($response);
    }
	else if($type==3)
	{
		$result=$db->logOut($uid);
		if($result)
		{
			$response["error"] = FALSE;
			$response["error_msg"]="You have successfully logged out!";
			echo json_encode($response);
		}
		else
		{
		$response["error"] = TRUE;
			$response["error_msg"]="You did not log out!";
			echo json_encode($response);
		}
	}
	else if($type==4)
	{
		$vid_id=$_POST["broadcast_id"];
		$result=$db->isBroadcasting($uid,$vid_id);
		if($result)
		{
			$response["error"] = FALSE;
			$response["error_msg"]="You have started Broadcasting";
			echo json_encode($response);
		}
		else
		{
		$response["error"] = TRUE;
			$response["error_msg"]="Unable to start broadcast!";
			echo json_encode($response);
		}
	}
	else if($type==5)
	{
		
		$result=$db->isNotBroadcasting($uid);
		if($result)
		{
			$response["error"] = FALSE;
			$response["error_msg"]="You have stopped Broadcasting";
			echo json_encode($response);
		}
		else
		{
		$response["error"] = TRUE;
			$response["error_msg"]="Unable to stop broadcast!";
			echo json_encode($response);
		}
	}
		
		
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Invalid Username or type ISSET. Check sessions.PHP";
    echo json_encode($response);
}
?>