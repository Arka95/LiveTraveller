<?php

require_once 'include/DB_ProfileFunctionsClass.php';
$db = new DB_ProfileFunctions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['type']) && isset($_POST['uid'])) {

// receiving the post params
    $type = $_POST['type'];
    $uid = $_POST['uid'];
	 if ($type == 0) {
        $profile = $db->getAccountData($uid);
        if ($profile) {
            $response["error"] = FALSE;
            $response["user_id"] = $profile["user_id"];
            $response["profile"]["first_name"] = $profile["first_name"];
            $response["profile"]["last_name"] = $profile["last_name"];  
            $response["profile"]["dob"] = $profile["dob"];           
            $response["profile"]["country"] = $profile["country"];
            $response["profile"]["city"] = $profile["city"];
            $response["profile"]["state"] = $profile["state"];
			if(!$profile["pro_pic"])
				$response["profile"]["pro_pic"] = "not set";
			else
            $response["profile"]["pro_pic"] = $profile["pro_pic"];
            echo json_encode($response);
        } else {
            $response["error"] = FALSE;
            $response["error_message"] = "cannot retrieve profile";
        }
    }
    if ($type == 1) {
        $profile = $db->getData($uid);
        if ($profile) {
            $response["error"] = FALSE;
            $response["user_id"] = $profile["user_id"];
            $response["profile"]["first_name"] = $profile["first_name"];
            $response["profile"]["last_name"] = $profile["last_name"];
            $response["profile"]["curr_lat"] = $profile["current_location_lat"];
            $response["profile"]["curr_long"] = $profile["current_location_long"];
            $response["profile"]["last_lat"] = $profile["last_location_lat"];
            $response["profile"]["last_long"] = $profile["last_location_long"];
            $response["profile"]["dob"] = $profile["dob"];
            $response["profile"]["is_online"] = $profile["is_online"];
            $response["profile"]["country"] = $profile["country"];
            $response["profile"]["city"] = $profile["city"];
            $response["profile"]["state"] = $profile["state"];
			 $response["profile"]["count"] = $profile["count"];
			if(!$profile["pro_pic"])
				$response["profile"]["pro_pic"] = "not set";
			else
            $response["profile"]["pro_pic"] = $profile["pro_pic"];
            echo json_encode($response);
        } else {
            $response["error"] = FALSE;
            $response["error_message"] = "cannot retrieve profile";
        }
    } else if ($type == 2) {
        $fid = $_POST['fid'];
        $profile = $db->getFriendData($uid, $fid);
        if ($profile) {
            $response["error"] = FALSE;
            $response["user_id"] = $profile["user_id"];
            $response["profile"]["first_name"] = $profile["first_name"];
            $response["profile"]["last_name"] = $profile["last_name"];
            $response["profile"]["curr_lat"] = $profile["current_location_lat"];
            $response["profile"]["curr_long"] = $profile["current_location_long"];
            $response["profile"]["last_lat"] = $profile["last_location_lat"];
            $response["profile"]["last_long"] = $profile["last_location_long"];
            $response["profile"]["dob"] = $profile["dob"];
            $response["profile"]["is_online"] = $profile["is_online"];
            $response["profile"]["country"] = $profile["country"];
            $response["profile"]["city"] = $profile["city"];
            $response["profile"]["state"] = $profile["state"];
			$response["profile"]["count"] = $profile["count"];
            $response["profile"]["pro_pic"] = $profile["pro_pic"];
            $response["profile"]["is_friend"] = ($profile["is_friend"]?$profile["is_friend"]:0);
            echo json_encode($response);
        } else {
            $response["error"] = FALSE;
            $response["error_message"] = "cannot retrieve profile";
        }
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (user_id, email or password) is missing!";
    echo json_encode($response);
}
?>