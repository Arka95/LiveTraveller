<?php
 
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['user_id']) && isset($_POST['password'])  && isset($_POST['dob'])  && isset($_POST['city'])  && isset($_POST['country'])  && isset($_POST['state'])  && isset($_POST['first_name'])  && isset($_POST['last_name'])) 
{
 
    // receiving the post params
    $user_id = $_POST['user_id'];
    $password = $_POST['password'];
	 $dob=$_POST['dob'];
	 $country=$_POST['country'];
	 $city=$_POST['city'];
	 $state=$_POST['state'];
	 $first_name=$_POST['first_name'];
	 $last_name=$_POST['last_name'];
	 $pro_pic = $_POST['pro_pic'];
	
 
        $user = $db->updateMemberDetails($user_id,$dob,$country,$city,$state,$first_name,$last_name,$password,$pro_pic);
        if ($user) {
            // user stored successfully
            $response["error"] = FALSE;     
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "Unknown error occurred in updation!";
            echo json_encode($response);
        }
    }
	else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters are  missing!";
    echo json_encode($response);
}
?>