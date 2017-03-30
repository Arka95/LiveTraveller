<?php

/**
 * @author Arka Bhowmik
 * @credits http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/
 */
class DB_SessionFunctions {

    private $conn;

    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }

    // destructor
    function __destruct() {
        
    }

    /**
     * Storing new user
     * returns user details
     */
	 public function logOut($uid)
	 {
		$stmt = $this->conn->prepare("update member_session set is_online=0,current_location_lat=0.0,current_location_long=0.0, broadcast_id=NULL where user_id=?");
        $stmt->bind_param("s", $uid);
        $result1 = $stmt->execute();
        $stmt->close();

        if ($result1) {
           
            return true;
        } else {
            return false;
        }
	 }
    public function retrieveLocation($uid) {


        $stmt = $this->conn->prepare("select  current_location_lat,current_location_long,last_location_lat,last_location_long from session where user_id=?");
        $stmt->bind_param("s", $uid);
//friend reqstreceived=1 ,rqstaccptd=2,someoneisbroadcasting= 3,
        $result1 = $stmt->execute();
        $stmt->close();

        if ($result1) {
            $location = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $location;
        } else {
            return false;
        }
    }

    public function updateLocation($uid,$lat, $longi) {

        $stmt = $this->conn->prepare("update  member_session set current_location_lat=?, current_location_long=? ,last_location_lat=? , last_location_long=?  where user_id=?");
        $stmt->bind_param("sssss", $lat, $longi, $lat, $longi,$uid);
        $sol = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($sol)
            return true;
        else
            return false;
    }

	 public function isBroadcasting($user_id,$vid_id) {
        $type = 3;
        $stmt = $this->conn->prepare("update member_session set is_online=2 , broadcast_id=? where user_id=?");
        $stmt->bind_param("ss", $vid_id,$user_id);
       $result= $stmt->execute();
		$stmt->close();
		
		
		 //Notify all friends
		 $stmt = $this->conn->prepare("SELECT DISTINCT f.friend_id  as g from friend_list f where f.user_id=? UNION SELECT DISTINCT f.user_id as g from friend_list f where f.friend_id=?");
         $stmt->bind_param("ss", $user_id, $user_id);
		 $stmt->execute();
         $friends = $stmt->get_result(); 
		
         while ($row = $friends->fetch_assoc()) {
			$stmt1 = $this->conn->prepare("INSERT INTO notification(user_id,friend_id,type) values(?,?,?)");
			$stmt1->bind_param("sss", $row['g'], $user_id, $type);
			$stmt1->execute();
			$stmt1->close();
			
		 }
		$stmt->close();
		//delete FROM `notification` WHERE type=3
		
        if ($result) {
            return true;
        } else {
            return false;
        }
    }
	
	public function isNotBroadcasting($user_id) {
        $type = 4;
        $stmt = $this->conn->prepare("update member_session set is_online=1 , broadcast_id=NULL where user_id=?");
        $stmt->bind_param("s", $user_id);
        $stmt->execute();
		$stmt->close();
		 
		 //Notify all friends
		 $stmt = $this->conn->prepare("SELECT DISTINCT f.friend_id  as g from friend_list f where f.user_id=? UNION SELECT DISTINCT f.user_id as g from friend_list f where f.friend_id=?");
         $stmt->bind_param("ss", $user_id, $user_id);
		 $stmt->execute();
         $friends = $stmt->get_result(); 
		
         while ($row = $friends->fetch_assoc()) {
			$stmt1 = $this->conn->prepare("INSERT INTO notification(user_id,friend_id,type) values(?,?,?)");
			$stmt1->bind_param("sss", $row['g'], $user_id, $type);
			$stmt1->execute();
			$stmt1->close();
			
		 }
		$stmt->close();
		//delete FROM `notification` WHERE type=4
		
        if ($result) {
            return true;
        } else {
            return false;
        }
    }

}

?>