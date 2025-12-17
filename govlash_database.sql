-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 15, 2025 at 08:19 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `govlash_database`
--

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `NotificationID` int(11) NOT NULL,
  `RecipientID` int(11) NOT NULL,
  `TransactionID` int(11) NOT NULL,
  `NotificationMessage` varchar(255) NOT NULL,
  `CreatedAt` datetime NOT NULL,
  `IsRead` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `services`
--

CREATE TABLE `services` (
  `ServiceID` int(11) NOT NULL,
  `ServiceName` varchar(50) NOT NULL,
  `ServiceDescription` varchar(250) NOT NULL,
  `ServicePrice` double NOT NULL,
  `ServiceDuration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `TransactionID` int(11) NOT NULL,
  `ServiceID` int(11) NOT NULL,
  `CustomerID` int(11) NOT NULL,
  `ReceptionistID` int(11) DEFAULT NULL,
  `LaundryStaffID` int(11) DEFAULT NULL,
  `TransactionDate` datetime NOT NULL,
  `TransactionStatus` varchar(20) NOT NULL,
  `TotalWeight` double NOT NULL,
  `TransactionNotes` varchar(250) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `UserID` int(11) NOT NULL,
  `UserName` varchar(50) DEFAULT NULL,
  `UserEmail` varchar(50) DEFAULT NULL,
  `UserPassword` varchar(50) DEFAULT NULL,
  `UserGender` char(10) DEFAULT NULL,
  `UserDOB` date DEFAULT NULL,
  `UserRole` char(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`UserID`, `UserName`, `UserEmail`, `UserPassword`, `UserGender`, `UserDOB`, `UserRole`) VALUES
(1, 'admin', 'admin@email.com', 'admin123', 'Male', '2000-01-01', 'ADMIN'),

--
-- Indexes for dumped tables
--

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`NotificationID`),
  ADD KEY `RecipientID` (`RecipientID`),
  ADD KEY `TransactionID` (`TransactionID`);

--
-- Indexes for table `services`
--
ALTER TABLE `services`
  ADD PRIMARY KEY (`ServiceID`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`TransactionID`),
  ADD KEY `ServiceID` (`ServiceID`),
  ADD KEY `CustomerID` (`CustomerID`),
  ADD KEY `ReceptionistID` (`ReceptionistID`),
  ADD KEY `LaundryStaffID` (`LaundryStaffID`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`UserID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `NotificationID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `services`
--
ALTER TABLE `services`
  MODIFY `ServiceID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `TransactionID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `UserID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`RecipientID`) REFERENCES `users` (`UserID`),
  ADD CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`TransactionID`) REFERENCES `transactions` (`TransactionID`);

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`ServiceID`) REFERENCES `services` (`ServiceID`),
  ADD CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`CustomerID`) REFERENCES `users` (`UserID`),
  ADD CONSTRAINT `transactions_ibfk_3` FOREIGN KEY (`ReceptionistID`) REFERENCES `users` (`UserID`),
  ADD CONSTRAINT `transactions_ibfk_4` FOREIGN KEY (`LaundryStaffID`) REFERENCES `users` (`UserID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
