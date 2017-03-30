<html>
<body>
<?php

require_once 'include/DB_SessionFunctionsClass.php';
$db = new DB_SessionFunctions();


// json response array

		$profile = $db->isBroadcasting("arka95","happy");
	
if($profile){
				
        
           
			echo "<h1>".json_encode($profile)."</h1>";
        }
    
       
?>
		</body>
		</html>