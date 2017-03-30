<?php

/* checks if a user is friend or not */

class DB_FriendsFunctions {

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

    public function checkFriend($uid) {

        $stmt = $this->conn->prepare("select count(*) from friend_list where user_id= ? or friend_id= ? ") ;
        $stmt->bind_param("ssss", $uid, $fid, $fid,$uid);
        $result = $stmt->execute();
        $result = $stmt->num_rows;
        $stmt->close();
        if ($result > 0)
            return true;
        else
            return false;
    }

    public function sendFriendRequest($uid, $fid) {
		$status=1;
        $stmt = $this->conn->prepare("insert into friend_list (user_id,friend_id,status) values(?,?,?)");
        $stmt->bind_param("ssi", $uid, $fid,$status);
        $result = $stmt->execute();
        $stmt->close();
        if ($result )
            return true;
        else
            return false;
    }
	public function deleteFriendRequest($uid, $fid) {
        $stmt = $this->conn->prepare('delete from friend_list where (user_id= ? and friend_id=?) or (user_id= ? and friend_id=?)');
        $stmt->bind_param("ssss", $uid, $fid,$fid,$uid);
        $result = $stmt->execute();
        $stmt->close();
        if ($result)
            return true;
        else
            return false;
    }

    public function acceptFriendRequest($uid, $fid) {
        $stmt = $this->conn->prepare('update friend_list set status=2 where user_id= ? and friend_id=? and status=1');
        $stmt->bind_param("ss", $fid, $uid);
        $result = $stmt->execute();
        $stmt->close();
        if ($result )
            return true;
        else
            return false;
    }


    public function deleteFriend($uid, $fid) {

        $stmt = $this->conn->prepare("delete from friend_list  where (user_id=? and friend_id=?) or (friend_id=? and user_id=?)");
        $stmt->bind_param("ssss", $uid, $fid, $fid, $uid);
        $result = $stmt->execute();
        $stmt->close();
        if ($result)
            return true;
        else
            return false;
    }

    public function showFriends($uid) {
      // 
        $stmt = $this->conn->prepare("select p.user_id,p.first_name,p.last_name,p.pro_pic ,s.is_online,s.current_location_lat,s.current_location_long from member_details p, member_session s where p.user_id in (select distinct friend_id from friend_list where user_id=? and status=2 union select distinct user_id from friend_list where friend_id=?  and status=2) and p.user_id=s.user_id");
        $stmt->bind_param("ss", $uid, $uid);
        $result = $stmt->execute();
        $result = $stmt->get_result();
         $i=0;
        $friends=array();
        while ($row = $result->fetch_assoc()) {
			$friends[]=$row;
				
        }
         $stmt->close();
        return $friends;
    }
     public function showBroadcasters() {

      
        $stmt = $this->conn->prepare("select p.user_id,p.first_name,p.last_name,p.pro_pic ,s.current_location_lat,s.current_location_long from member_details p, member_session s where s.is_online=2 and p.user_id=s.user_id") ;
        //$stmt->bind_param("ss", $uid, $uid);
        $result = $stmt->execute();
        $result = $stmt->get_result();
         $i=0;
          $friends=array();
        while ($row = $result->fetch_assoc()) {
			$friends[]=$row;
			 
        }
         $stmt->close();
        return $friends;
    }
	public function searchFriends($search)
	{
		$search="%".$search."%";
		$stmt = $this->conn->prepare("select p.user_id,p.first_name,p.last_name, p.pro_pic,s.is_online,s.current_location_lat,s.current_location_long from member_details p, member_session s where (p.user_id LIKE ? or p.first_name like ? or p.last_name like ?) and p.user_id=s.user_id ");
        $stmt->bind_param("sss", $search, $search, $search );
        $result = $stmt->execute();
        $result = $stmt->get_result();
         $i=0;
        $friends=array();
        while ($row = $result->fetch_assoc()) {
			$friends[]=$row;		
        }
         $stmt->close();
        return $friends;
	
	}

}

?>