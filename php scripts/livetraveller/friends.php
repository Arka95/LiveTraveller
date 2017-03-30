<?php

require_once 'include/DB_NotificationsFunctionsClass.php';

require_once 'include/DB_FriendsFunctionsClass.php';
$db = new DB_NotificationsFunctions();
$dbf = new DB_FriendsFunctions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['type'])) {

    // receiving the post params
	if(isset($_POST['uid']))
			$uid = $_POST['uid'];

    $type = $_POST['type'];


    if ($type == 1) {        
		
		//friend request send and notify friend
        $fid = $_POST['friend_id'];
        $result = $dbf->sendFriendRequest($uid, $fid);
        if ($result) {

				if($db->createNotification($uid, $fid, $type))
				{
					$response["error"] = FALSE;
					$response["message"] = "request sent !";
				}
				else {
					$response["error"] = TRUE;
					$response["message"] = "error notifying person!";
				}
		} 
		else {
            $response["error"] = TRUE;
            $response["message"] = "error sending request!";
        }
		echo json_encode($response);
		
    } 
	
	else if ($type == 2) {
	
		//friend request accept and notify friend
        $fid = $_POST['friend_id'];
        $result = $dbf->acceptFriendRequest($uid, $fid);
        $response["error"] = FALSE;
        if ($result) {
				
				if($db->createNotification($uid, $fid, $type))
				{
					$response["error"] = FALSE;
					$response["message"] = "request accepted !";
				}
				else {
					$response["error"] = TRUE;
					$response["message"] = "error notifying person!";
				}
		} 
		else {
            $response["error"] = TRUE;
            $response["message"] = "error accepting request!";
        }
		
		echo json_encode($response);
        
    }

	else if ($type == 3) {//friend request delete
        $fid = $_POST['friend_id'];

        if ($dbf->deleteFriendRequest($uid, $fid)) {

          	if($db->deleteNotifications($uid, $fid, 1))
				{
					$response["error"] = FALSE;
					$response["message"] = "notification deleted !";
				}
				else {
					$response["error"] = TRUE;
					$response["message"] = "error deleting Notification !";
				}
		} 
		else {
            $response["error"] = TRUE;
            $response["message"] = "error rejecting request!";
        }
		echo json_encode($response);

    } 
	
	else if ($type == 4) {//unfollow
        $fid = $_POST['friend_id'];
        $result = $dbf->deleteFriend($uid, $fid);

        if ($result) {
            $response["error"] = FALSE;
            $response["message"] = "you are no longer " + fid + "'s friend !";
        } else {
            $response["error"] = TRUE;
            $response["message"] = "error deleting friendship!";
        }
    } else if ($type == 5) {
       
      
		$friends= $dbf->showFriends($uid);
        $i = 0;
		$limit=count($friends);
        if ($friends) {
            // user stored successfully
            $response["error"] = FALSE;
            while ($i<$limit) {
                $response["friends"][$i]["user_id"] = $friends[$i]["user_id"];
                $response["friends"][$i]["first_name"] = $friends[$i]["first_name"];
                $response["friends"][$i]["last_name"] = $friends[$i]["last_name"];
                $response["friends"][$i]["is_online"] = $friends[$i]["is_online"];
                $response["friends"][$i]["curr_lat"] = $friends[$i]["current_location_lat"];
                $response["friends"][$i]["curr_long"] = $friends[$i]["current_location_long"];
                $response["friends"][$i]["pro_pic"] = $friends[$i]["pro_pic"];
                $i++;
            }
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "You have no friends!";
            echo json_encode($response);
        }
    }
    
    else if ($type == 6) {//HOMEPAGE FEATURED CONTENT
       
      
		$friends= $dbf->showBroadcasters($uid);
        $i = 0;
		$limit=count($friends);
        if ($friends) {
            // user stored successfully
            $response["error"] = FALSE;
            while ($i<$limit) {
                $response["friends"][$i]["user_id"] = $friends[$i]["user_id"];
                $response["friends"][$i]["first_name"] = $friends[$i]["first_name"];
                $response["friends"][$i]["last_name"] = $friends[$i]["last_name"];
                $response["friends"][$i]["curr_lat"] = $friends[$i]["current_location_lat"];
                $response["friends"][$i]["curr_long"] = $friends[$i]["current_location_long"];
                $response["friends"][$i]["pro_pic"] = $friends[$i]["pro_pic"];
                $i++;
            }
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "There are no broadcasters :( !";
            echo json_encode($response);
        }
    }
	else if($type==7)
	{
		$search=$_POST['search'];
		$friends= $dbf->searchFriends($search);
        $i = 0;
		$limit=count($friends);
        if ($friends) {
            // user stored successfully
            $response["error"] = FALSE;
            while ($i<$limit) {
                $response["friends"][$i]["user_id"] = $friends[$i]["user_id"];
                $response["friends"][$i]["first_name"] = $friends[$i]["first_name"];
                $response["friends"][$i]["last_name"] = $friends[$i]["last_name"];
                $response["friends"][$i]["is_online"] = $friends[$i]["is_online"];
                $response["friends"][$i]["curr_lat"] = $friends[$i]["current_location_lat"];
                $response["friends"][$i]["curr_long"] = $friends[$i]["current_location_long"];
                $response["friends"][$i]["pro_pic"] = $friends[$i]["pro_pic"];
                $i++;
            }
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "You have no friends!";
            echo json_encode($response);
        }
		
	}
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Invalid Username or Friend or type ISSET. Check friend.PHP";
    echo json_encode($response);
}
?>