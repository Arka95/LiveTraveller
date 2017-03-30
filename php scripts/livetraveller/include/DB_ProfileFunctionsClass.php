<?php

/* checks if a user is friend or not */

class DB_ProfileFunctions {

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
		public function getAccountData($uid) {
        $stmt = $this->conn->prepare("select * from member_details where user_id =?");
        $stmt->bind_param("s", $uid);
        $result = $stmt->execute();
        $result = $stmt->get_result();
        $profile = $result->fetch_assoc();
        $stmt->close();
        if ($result)
            return $profile;
        else
            return false;
    }
	
    public function getData($uid) {

        $stmt = $this->conn->prepare("select * from member_details p, member_session s where p.user_id =? and p.user_id=s.user_id");
        $stmt->bind_param("s", $uid);
        $result = $stmt->execute();
        $result = $stmt->get_result();
        $profile = $result->fetch_assoc();
        $stmt->close();


        $stmt = $this->conn->prepare("select distinct user_id from friend_list where status=2 and user_id=? UNION select distinct friend_id from friend_list where status=2 and friend_id=?");
        $stmt->bind_param("ss", $uid, $uid);
        $result = $stmt->execute();
        $count = $stmt->num_rows;
        $stmt->close();
		
		$profile['count']=$count;
		
        if ($result)
            return $profile;
        else
            return false;
    }
	

    public function getFriendData($uid, $fid) {


        $stmt = $this->conn->prepare("select p.*,s.* from member_details p, member_session s where p.user_id =? and p.user_id=s.user_id ");
        $stmt->bind_param("s", $fid);
        $result3 = $stmt->execute();
        $result = $stmt->get_result();
        $profile = $result->fetch_assoc();
        $stmt->close();

        $stmt = $this->conn->prepare("select friend_id from friend_list as g where status=2 and user_id=? UNION select user_id as g from friend_list where status=2 and friend_id=?");
        $stmt->bind_param("ss", $fid, $fid);
        $result2 = $stmt->execute();
        $count = $stmt->num_rows;
		 $stmt->close();
		$profile['count']=$count;
		
		$stmt = $this->conn->prepare("select status from friend_list f where (f.user_id=? and f.friend_id=? )or (f.user_id=? and f.friend_id=? )");
		$stmt->bind_param("ssss", $fid,$uid,$uid,$fid);
        $result1 = $stmt->execute();
        $result = $stmt->get_result()->fetch_assoc();
        $stmt->close();
		
		$profile['is_friend']=$result['status'];

        if ($result1 && $result2 && $result3)
            return $profile;
        else
            return false;
    }

}
/*

 $path = "pro_pics/$id.png";
 
 $actualpath = "http://192.168.0.101/livetraveller/$path";
 
 $sql = "INSERT INTO member_details (pro_pic) VALUES ('$actualpath','$name')";
 
 if(mysqli_query($con,$sql)){
 file_put_contents($path,base64_decode($image));
 echo "Successfully Uploaded";
 }
 
 mysqli_close($con);
 }else{
 echo "Error";
 }
*/

?>