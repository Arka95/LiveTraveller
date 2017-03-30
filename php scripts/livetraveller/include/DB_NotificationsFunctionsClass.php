<?php

/**
 * @author Arka Bhowmik
 * @credits http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/
 */
class DB_NotificationsFunctions {

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
    public function createNotification($uid, $fid, $type) {

        $stmt = $this->conn->prepare("insert into notification(user_id, friend_id, type) values(?,?,?)");
        $stmt->bind_param("sss", $fid, $uid, $type);
//friend reqstreceived=1 ,rqstaccptd=2,someoneisbroadcasting= 3,
        $result1 = $stmt->execute();
        $stmt->close();

        if ($result1) {
            return true;
        } else {
            return false;
        }
    }

    public function deleteNotification($not_number) {

        $stmt = $this->conn->prepare("delete from notification where not_id=?");
        $stmt->bind_param("i", $not_number);
        $sol = $stmt->execute();


        // check for successful store
        if ($sol) {
           
            return true;
        } else
            return false;
    }

    public function deleteNotifications($uid, $fid, $type) {

        $stmt = $this->conn->prepare("delete from notification where user_id=?, friend_id=? and type=?");
        $stmt->bind_param("ssi", $fid, $uid, $type);
        $sol = $stmt->execute();


        // check for successful store
        if ($sol)
            return true;
        else
            return false;
    }

    /**
     * Get notifications
     */
    public function showNotifications($uid) {

        $stmt = $this->conn->prepare("SELECT * FROM notification WHERE user_id= ? ");
        $stmt->bind_param("s", $uid);
        $stmt->execute();
		$noti=array();
        $result = $stmt->get_result();
        $i = 0;
    
        while ($row = $result->fetch_assoc()) {
           
			$noti[] = $row; 
        }
        $stmt->close();
        return $noti;
    }
	
	 public function tester() {

        $stmt = $this->conn->prepare("SELECT * FROM member_details ");
        $stmt->execute();
		$noti=array();
        $result = $stmt->get_result();
        $i = 0;
    
        while ($row = $result->fetch_assoc()) {
          
			$noti[] = $row;           
        }
        $stmt->close();
        return $noti;
    }

}

?>