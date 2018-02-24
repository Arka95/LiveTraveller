-- phpMyAdmin SQL Dump
-- version 4.5.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Feb 24, 2018 at 04:11 PM
-- Server version: 5.7.11
-- PHP Version: 5.6.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `livetraveller`
--

-- --------------------------------------------------------

--
-- Table structure for table `friend_list`
--

CREATE TABLE `friend_list` (
  `user_id` varchar(50) NOT NULL,
  `friend_id` varchar(50) NOT NULL,
  `status` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `friend_list`
--

INSERT INTO `friend_list` (`user_id`, `friend_id`, `status`) VALUES
('arka95', 'arkaSa', 2),
('arka95', 't', 2),
('arkaSa', 't', 2),
('heritage', 'arka95', 2),
('heritage', 'arkaSa', 2),
('heritage', 'luvlee', 2),
('heritage', 't', 2),
('luvlee', 'arka95', 2),
('luvlee', 'arkaSa', 2),
('luvlee', 't', 2);

-- --------------------------------------------------------

--
-- Table structure for table `login_master`
--

CREATE TABLE `login_master` (
  `user_id` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `role` varchar(6) NOT NULL DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `login_master`
--

INSERT INTO `login_master` (`user_id`, `password`, `email`, `role`) VALUES
('arka95', '1234', 'arka.bhowmik95@gmail.com', 'user'),
('arkaSa', '1234', 'saha@email.com', 'user'),
('heritage', '1234', 'heritageit.edu', 'user'),
('luvlee', '1234', 'loveme@gmail.com', 'user'),
('t', '1234', 't', 'user');

-- --------------------------------------------------------

--
-- Table structure for table `member_details`
--

CREATE TABLE `member_details` (
  `user_id` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL DEFAULT 'NotSet',
  `last_name` varchar(50) NOT NULL DEFAULT 'NotSet',
  `dob` date NOT NULL DEFAULT '1995-07-29',
  `country` varchar(60) NOT NULL DEFAULT 'NotSet',
  `state` varchar(60) NOT NULL DEFAULT 'NotSet',
  `city` varchar(60) NOT NULL DEFAULT 'NotSet',
  `pro_pic` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `member_details`
--

INSERT INTO `member_details` (`user_id`, `first_name`, `last_name`, `dob`, `country`, `state`, `city`, `pro_pic`) VALUES
('arka95', 'Arka', 'Bhowmik', '1995-07-29', 'India', 'Maharashtra', 'Mumbai', 'uploads//arka95.png'),
('arkaSa', 'Arka', 'Saha', '1994-06-09', 'India', 'West Bengal', 'Kolkata', 'uploads//arkaSa.png'),
('heritage', 'Heritage', 'Institute', '1995-03-09', 'India', 'WestBengal', 'Kolkata', 'uploads//heritage.png'),
('luvlee', 'Luv', 'Mehta', '1995-01-26', 'Japan', 'Hokkaido', 'Tokyo', 'uploads//luvlee.png'),
('t', 'Jayati', 'Dev', '1995-10-26', 'U.S.A', 'New York', 'Manhattan', 'uploads//t.png');

-- --------------------------------------------------------

--
-- Table structure for table `member_session`
--

CREATE TABLE `member_session` (
  `is_online` int(1) NOT NULL,
  `current_location_lat` float DEFAULT '0',
  `current_location_long` float DEFAULT '0',
  `last_location_lat` float DEFAULT '0',
  `last_location_long` float DEFAULT '0',
  `user_id` varchar(20) NOT NULL,
  `broadcast_id` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `member_session`
--

INSERT INTO `member_session` (`is_online`, `current_location_lat`, `current_location_long`, `last_location_lat`, `last_location_long`, `user_id`, `broadcast_id`) VALUES
(2, 0, 0, 22.6196, 88.4167, 'arka95', 'happy'),
(1, 0, 0, 22.6196, 88.4167, 'arkaSa', NULL),
(0, 22.6194, 88.4167, 22.6194, 88.4167, 'heritage', NULL),
(0, 22.6194, 88.4167, 22.6194, 88.4167, 'luvlee', NULL),
(0, 0, 0, 0, 0, 't', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

CREATE TABLE `notification` (
  `not_id` int(20) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `friend_id` varchar(50) NOT NULL,
  `type` varchar(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `notification`
--

INSERT INTO `notification` (`not_id`, `user_id`, `friend_id`, `type`) VALUES
(35, 't', 'arka95', '3'),
(36, 'heritage', 'arka95', '3'),
(37, 'luvlee', 'arka95', '3'),
(38, 't', 'arkaSa', '3'),
(40, 'heritage', 'arkaSa', '3'),
(41, 'luvlee', 'arkaSa', '3'),
(42, 't', 'arkaSa', '4'),
(44, 'heritage', 'arkaSa', '4'),
(45, 'luvlee', 'arkaSa', '4'),
(47, 't', 'arka95', '3'),
(48, 'heritage', 'arka95', '3'),
(49, 'luvlee', 'arka95', '3'),
(51, 't', 'arka95', '4'),
(52, 'heritage', 'arka95', '4'),
(53, 'luvlee', 'arka95', '4'),
(54, 't', 'arkaSa', '3'),
(55, 'arka95', 'arkaSa', '3'),
(56, 'heritage', 'arkaSa', '3'),
(57, 'luvlee', 'arkaSa', '3'),
(58, 't', 'arkaSa', '4'),
(59, 'arka95', 'arkaSa', '4'),
(60, 'heritage', 'arkaSa', '4'),
(61, 'luvlee', 'arkaSa', '4'),
(62, 'arkaSa', 'arka95', '3'),
(63, 't', 'arka95', '3'),
(64, 'heritage', 'arka95', '3'),
(65, 'luvlee', 'arka95', '3');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `friend_list`
--
ALTER TABLE `friend_list`
  ADD PRIMARY KEY (`user_id`,`friend_id`),
  ADD KEY `friends_list_linker` (`friend_id`);

--
-- Indexes for table `login_master`
--
ALTER TABLE `login_master`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `member_details`
--
ALTER TABLE `member_details`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `member_session`
--
ALTER TABLE `member_session`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD UNIQUE KEY `broadcast_id` (`broadcast_id`),
  ADD KEY `user_id_2` (`user_id`);

--
-- Indexes for table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`not_id`,`user_id`,`friend_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `friend_id` (`friend_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `notification`
--
ALTER TABLE `notification`
  MODIFY `not_id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=66;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `friend_list`
--
ALTER TABLE `friend_list`
  ADD CONSTRAINT `friend_list_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `login_master` (`user_id`),
  ADD CONSTRAINT `friends_list_linker` FOREIGN KEY (`friend_id`) REFERENCES `login_master` (`user_id`);

--
-- Constraints for table `member_details`
--
ALTER TABLE `member_details`
  ADD CONSTRAINT `user` FOREIGN KEY (`user_id`) REFERENCES `login_master` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `member_session`
--
ALTER TABLE `member_session`
  ADD CONSTRAINT `session_id` FOREIGN KEY (`user_id`) REFERENCES `login_master` (`user_id`);

--
-- Constraints for table `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `login_master` (`user_id`),
  ADD CONSTRAINT `notification_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `login_master` (`user_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
