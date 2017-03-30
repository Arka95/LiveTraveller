<?php

require_once 'include/DB_NotificationsFunctionsClass.php';
$db = new DB_NotificationsFunctions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['type'])) {

    // receiving the post params
    $type = $_POST['type'];
    if ($type == 1) {
        $user_id = $_POST['uid'];

        $i = 0;
		$notifs= $db->showNotifications($user_id);
		$limit=count($notifs);
		$response["uid"] =$user_id;
        if (!empty($notifs)) {
            // user stored successfully
            $response["error"] = FALSE;
            while ($i<$limit) {
                $response["notifs"][$i]["to"] = $notifs[$i]["user_id"];
                $response["notifs"][$i]["from"] = $notifs[$i]["friend_id"];
                $response["notifs"][$i]["type"] = $notifs[$i]["type"];
                $response["notifs"][$i]["not_id"] = $notifs[$i]["not_id"];
                $i++;
            }
            echo json_encode($response);
        } else {
            // no notifications
            $response["error"] = TRUE;
            $response["error_msg"] = "no notifications";
            echo json_encode($response);
        }
    } 
 else if ($type == 2) {
    $not_id=$_POST['not_id'];
    $success = $db->deleteNotification($not_id);
    if ($success) {
        $response["error"] = FALSE;
        $response["error_msg"] = "Deleted Notification";
        echo json_encode($response);
    } else {
        $response["error"] = TRUE;
        $response["error_msg"] = "Unknown error occurred while fetching notifications";
        echo json_encode($response);
    }
}
}
else {
        $response["error"] = TRUE;
        $response["error_msg"] = "Invalid Username ISSET. Check notifications.PHP";
        echo json_encode($response);
    }
?>