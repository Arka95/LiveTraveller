<?php
 
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['user_id']) && isset($_POST['email']) && isset($_POST['password'])  && isset($_POST['dob'])  && isset($_POST['city'])  && isset($_POST['country'])  && isset($_POST['state'])  && isset($_POST['first_name'])  && isset($_POST['last_name'])) 
{
 
    // receiving the post params
    $user_id = $_POST['user_id'];
    $email = $_POST['email'];
    $password = $_POST['password'];
	 $dob=$_POST['dob'];
	 $country=$_POST['country'];
	 $city=$_POST['city'];
	 $state=$_POST['state'];
	 $first_name=$_POST['first_name'];
	 $last_name=$_POST['last_name'];
    // check if user is already existed with the same email
    if ($db->isUserExisted($email,$user_id)) {
        // user already existed
        $response["error"] = TRUE;
        $response["error_msg"] = "User already existed with that email or user_id";
        echo json_encode($response);
    } else {
        // create a new user
        $user = $db->storeMemberDetails($user_id,$dob,$country,$city,$state,$first_name,$last_name,$email,$password);
        if ($user) {
            // user stored successfully
            $response["error"] = FALSE;     
            $response["user"]["user_id"] = $user["user_id"];
            $response["user"]["email"] = $user["email"];
			$response["user"]["password"]=$user["password"];
           
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "Unknown error occurred in registration!";
            echo json_encode($response);
        }
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (user_id, email or password) is missing!";
    echo json_encode($response);
}
?>