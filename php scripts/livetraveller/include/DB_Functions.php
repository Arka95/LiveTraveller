<?php

/**
 * @author Arka Bhowmik
 * @credits http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/
 */
class DB_Functions {

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
    public function storeMemberDetails($user_id, $dob, $country, $city, $state, $first_name, $last_name, $email, $password) {


        $stmt = $this->conn->prepare("INSERT INTO login_master(user_id, email,password) VALUES(?, ?, ?)");
        $stmt->bind_param("sss", $user_id, $email, $password);
        $result2 = $stmt->execute();
        $stmt->close();
		
		
        $stmt = $this->conn->prepare("insert into member_details(user_id, first_name, last_name, dob, country, state, city) values(?,?,?,?,?,?,?)");
        $stmt->bind_param("sssssss", $user_id, $first_name, $last_name, $dob, $country, $state, $city);
        $result1 = $stmt->execute();
        $stmt->close();

        $stmt = $this->conn->prepare("INSERT INTO member_session(user_id,is_online) VALUES(?,0)");
        $stmt->bind_param("s", $user_id);
        $result2 = $stmt->execute();
        $stmt->close();

        // check for successful store
       
	   if ($result1 && $result2) {
            $stmt = $this->conn->prepare("SELECT * FROM login_master WHERE user_id = ?");
            $stmt->bind_param("s", $user_id);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return false;
        }
    }
	
	
	
	 public function updateMemberDetails($user_id, $dob, $country, $city, $state, $first_name, $last_name, $password,$pro_pic) {

	 
		$path = "uploads//$user_id.png";
        $stmt = $this->conn->prepare("UPDATE login_master set password=? where user_id=?");
        $stmt->bind_param("ss",$password,$user_id);
        $result1 = $stmt->execute();
        $stmt->close();
		
		
        $stmt = $this->conn->prepare("update member_details set first_name=?, last_name=?, dob=?, country=?, state=?, city=? , pro_pic=? where user_id=?");
        $stmt->bind_param("ssssssss", $first_name, $last_name, $dob, $country, $state, $city,$path,$user_id);
        $result2 = $stmt->execute();
        $stmt->close();

		if($result2)
			file_put_contents($path,base64_decode($pro_pic));
       
	   if ($result1 && $result2) 
            return true;
         else 
            return false;
        
    }


    public function storeUser($user_id, $email, $password) {

        $stmt = $this->conn->prepare("INSERT INTO login_master(user_id, email,password) VALUES(?, ?, ?)");
        $stmt->bind_param("sss", $user_id, $email, $password);
        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM login_master WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {

        $stmt = $this->conn->prepare("SELECT * FROM login_master WHERE user_id= ? or email = ?");

        $stmt->bind_param("ss", $email, $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password

            $saved_password = $user['password'];

            if ($saved_password == $password) {
                // user authentication details are correct
                $stmt = $this->conn->prepare("update member_session set is_online=1 WHERE user_id= ? ");
                $stmt->bind_param("s", $email);
                $stmt->execute();
                return $user;
            }
        } else {
            return NULL;
        }
    }

    /**
     * Check user is existed or not
     */
    public function isUserExisted($email, $user_id) {

        $stmt = $this->conn->prepare("SELECT user_id,email from login_master WHERE email = ? or user_id = ?");
        $stmt->bind_param("ss", $email, $user_id);
        $stmt->execute();
        $stmt->store_result();
        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }

   

}

?>