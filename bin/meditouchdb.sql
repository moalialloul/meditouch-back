-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Host: mysql-111646-db.mysql-111646:10322
-- Generation Time: Feb 23, 2023 at 02:39 AM
-- Server version: 8.0.26
-- PHP Version: 7.2.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `meditouchdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `appointments_table`
--

CREATE TABLE `appointments_table` (
  `appointmentId` int NOT NULL,
  `slotFk` int NOT NULL,
  `businessAccountFk` int NOT NULL,
  `userFk` int NOT NULL,
  `serviceFk` int NOT NULL,
  `appointmentActualStartTime` timestamp NULL DEFAULT NULL,
  `appointmentActualEndTime` timestamp NULL DEFAULT NULL,
  `appointmentStatus` enum('ACCEPTED','REJECTED','PENDING') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING',
  `isApproved` tinyint(1) NOT NULL DEFAULT '0',
  `isCancelled` tinyint(1) NOT NULL DEFAULT '0',
  `cancelledBy` enum('PATIENT','HEALTH_PROFESSIONAL') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `appointments_table`
--

INSERT INTO `appointments_table` (`appointmentId`, `slotFk`, `businessAccountFk`, `userFk`, `serviceFk`, `appointmentActualStartTime`, `appointmentActualEndTime`, `appointmentStatus`, `isApproved`, `isCancelled`, `cancelledBy`) VALUES
(1, 24, 9, 42, 5, '2023-02-21 16:00:00', '2023-02-21 16:20:00', 'ACCEPTED', 0, 0, NULL),
(2, 25, 9, 42, 5, NULL, NULL, 'PENDING', 0, 0, NULL),
(3, 24, 9, 44, 5, NULL, NULL, 'PENDING', 0, 0, NULL),
(4, 23, 9, 42, 5, NULL, NULL, 'PENDING', 1, 1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `appointment_prescriptions_table`
--

CREATE TABLE `appointment_prescriptions_table` (
  `prescriptionId` int NOT NULL,
  `appointmentFk` int NOT NULL,
  `prescriptionDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `appointment_prescriptions_table`
--

INSERT INTO `appointment_prescriptions_table` (`prescriptionId`, `appointmentFk`, `prescriptionDescription`) VALUES
(13, 1, 'sss'),
(14, 1, 'sss');

-- --------------------------------------------------------

--
-- Table structure for table `appointment_result_table`
--

CREATE TABLE `appointment_result_table` (
  `resultId` int NOT NULL,
  `appointmentFk` int NOT NULL,
  `resultDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `resultDocument` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `appointment_result_table`
--

INSERT INTO `appointment_result_table` (`resultId`, `appointmentFk`, `resultDescription`, `resultDocument`) VALUES
(2, 1, 'sss', 'ccc');

-- --------------------------------------------------------

--
-- Table structure for table `blogs_table`
--

CREATE TABLE `blogs_table` (
  `blogId` int NOT NULL,
  `blogType` varchar(255) NOT NULL,
  `blogDate` timestamp(6) NOT NULL,
  `blogTitle` varchar(255) NOT NULL,
  `blogUrl` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `blogs_table`
--

INSERT INTO `blogs_table` (`blogId`, `blogType`, `blogDate`, `blogTitle`, `blogUrl`) VALUES
(1, 'sss', '2023-02-01 02:00:00.000000', 'sss', 'sss');

-- --------------------------------------------------------

--
-- Table structure for table `business_accounts_services_table`
--

CREATE TABLE `business_accounts_services_table` (
  `serviceId` int NOT NULL,
  `businessAccountFk` int NOT NULL,
  `servicePrice` double NOT NULL,
  `serviceName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `currencyUnit` enum('USD','LBP') NOT NULL DEFAULT 'USD'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `business_accounts_services_table`
--

INSERT INTO `business_accounts_services_table` (`serviceId`, `businessAccountFk`, `servicePrice`, `serviceName`, `currencyUnit`) VALUES
(5, 9, 15, 'Test15', 'LBP');

-- --------------------------------------------------------

--
-- Table structure for table `business_account_blockings_table`
--

CREATE TABLE `business_account_blockings_table` (
  `blockId` int NOT NULL,
  `businessAccountFk` int NOT NULL,
  `userFk` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `business_account_blockings_table`
--

INSERT INTO `business_account_blockings_table` (`blockId`, `businessAccountFk`, `userFk`) VALUES
(4, 9, 42),
(5, 9, 44);

-- --------------------------------------------------------

--
-- Table structure for table `business_account_promo_codes_table`
--

CREATE TABLE `business_account_promo_codes_table` (
  `promo_code_id` int NOT NULL,
  `user_fk` int NOT NULL,
  `business_account_fk` int NOT NULL,
  `promo_code_key` varchar(255) NOT NULL,
  `promo_code_value` int NOT NULL,
  `promo_code_unit` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `business_account_referrals_table`
--

CREATE TABLE `business_account_referrals_table` (
  `referralId` int NOT NULL,
  `userFk` int NOT NULL,
  `appointmentFk` int NOT NULL,
  `referralDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `referredByBusinessAccountFk` int NOT NULL,
  `referredToBusinessAccountFk` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `business_account_referrals_table`
--

INSERT INTO `business_account_referrals_table` (`referralId`, `userFk`, `appointmentFk`, `referralDescription`, `referredByBusinessAccountFk`, `referredToBusinessAccountFk`) VALUES
(8, 42, 1, 'sss', 9, 9);

-- --------------------------------------------------------

--
-- Table structure for table `business_account_schedule_slots_table`
--

CREATE TABLE `business_account_schedule_slots_table` (
  `slotId` int NOT NULL,
  `scheduleFk` int NOT NULL,
  `slotDate` date NOT NULL,
  `slotStartTime` timestamp NOT NULL,
  `slotEndTime` timestamp NOT NULL,
  `isLocked` tinyint(1) NOT NULL DEFAULT '0',
  `serviceFk` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `business_account_schedule_slots_table`
--

INSERT INTO `business_account_schedule_slots_table` (`slotId`, `scheduleFk`, `slotDate`, `slotStartTime`, `slotEndTime`, `isLocked`, `serviceFk`) VALUES
(23, 6, '2023-02-21', '2023-02-21 16:00:00', '2023-02-21 16:10:00', 1, 5),
(24, 6, '2023-02-22', '2023-02-22 16:15:00', '2023-02-22 16:25:00', 0, 5),
(25, 6, '2023-02-22', '2023-02-22 16:30:00', '2023-02-22 16:40:00', 0, 5),
(26, 6, '2023-02-23', '2023-02-23 16:45:00', '2023-02-23 16:55:00', 0, 5);

-- --------------------------------------------------------

--
-- Table structure for table `business_account_schedule_table`
--

CREATE TABLE `business_account_schedule_table` (
  `scheduleId` int NOT NULL,
  `businessAccountFk` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `business_account_schedule_table`
--

INSERT INTO `business_account_schedule_table` (`scheduleId`, `businessAccountFk`) VALUES
(6, 9);

-- --------------------------------------------------------

--
-- Table structure for table `business_account_table`
--

CREATE TABLE `business_account_table` (
  `businessAccountId` int NOT NULL,
  `userFk` int NOT NULL,
  `specialityFk` int DEFAULT '-1',
  `biography` varchar(255) NOT NULL,
  `clinicLocation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `clinicLocationLongitude` double DEFAULT NULL,
  `clinicLocationLatitude` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `business_account_table`
--

INSERT INTO `business_account_table` (`businessAccountId`, `userFk`, `specialityFk`, `biography`, `clinicLocation`, `clinicLocationLongitude`, `clinicLocationLatitude`) VALUES
(9, 43, 1, 'text', 'text', 33.456, 33.555);

-- --------------------------------------------------------

--
-- Table structure for table `community_posts_table`
--

CREATE TABLE `community_posts_table` (
  `postId` int NOT NULL,
  `userFk` int NOT NULL,
  `postService` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `postDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `community_posts_table`
--

INSERT INTO `community_posts_table` (`postId`, `userFk`, `postService`, `postDescription`) VALUES
(2, 42, 'test', 'post'),
(3, 42, 'test2', 'post2'),
(4, 42, 'test22', 'post22'),
(5, 42, 'test222', 'post222'),
(6, 42, 'test2222', 'post2222'),
(7, 42, 'test22222', 'post22222'),
(8, 42, 'test222222', 'post222222');

-- --------------------------------------------------------

--
-- Table structure for table `contact_us_table`
--

CREATE TABLE `contact_us_table` (
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `message` varchar(255) NOT NULL,
  `contactId` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `contact_us_table`
--

INSERT INTO `contact_us_table` (`firstName`, `lastName`, `subject`, `message`, `contactId`) VALUES
('sss', 'vvv', 'vvv', 'vvv', 1);

-- --------------------------------------------------------

--
-- Table structure for table `favorites_table`
--

CREATE TABLE `favorites_table` (
  `favoriteId` int NOT NULL,
  `businessAccountFk` int NOT NULL,
  `userFk` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `feedbacks_table`
--

CREATE TABLE `feedbacks_table` (
  `feedbackId` int NOT NULL,
  `businessAccountFk` int NOT NULL,
  `userFk` int NOT NULL,
  `feedbackDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `feedbacks_table`
--

INSERT INTO `feedbacks_table` (`feedbackId`, `businessAccountFk`, `userFk`, `feedbackDescription`) VALUES
(1, 9, 42, 'tst'),
(2, 9, 42, 'tst'),
(3, 9, 42, 'tst'),
(4, 9, 42, 'tst'),
(5, 9, 42, 'tst'),
(6, 9, 42, 'tst'),
(7, 9, 42, 'tst');

-- --------------------------------------------------------

--
-- Table structure for table `notifications_table`
--

CREATE TABLE `notifications_table` (
  `notificationId` int NOT NULL,
  `userToFk` int NOT NULL,
  `userFromFk` int NOT NULL,
  `notificationText` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `notificationType` enum('RESERVED_APPOINTMENT','COMMENT','REFERRAL','PROMO_CODE','FAVORITE','APPOINTMENT_STATUS','APPOINTMENT_RESULT','APPOINTMENT_PRESCRIPTION') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `isOpen` tinyint(1) NOT NULL DEFAULT '0',
  `notificationUrl` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `password_tokens_table`
--

CREATE TABLE `password_tokens_table` (
  `tokenId` int NOT NULL,
  `userFk` int NOT NULL,
  `tokenValue` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `expiryDate` timestamp NULL DEFAULT ((now() + interval 5 minute))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `password_tokens_table`
--

INSERT INTO `password_tokens_table` (`tokenId`, `userFk`, `tokenValue`, `expiryDate`) VALUES
(1, 42, 'VN6YHB', '2023-02-21 03:06:58');

-- --------------------------------------------------------

--
-- Table structure for table `posts_comments_table`
--

CREATE TABLE `posts_comments_table` (
  `commentId` int NOT NULL,
  `userFk` int DEFAULT NULL,
  `postFk` int NOT NULL,
  `commentDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `posts_comments_table`
--

INSERT INTO `posts_comments_table` (`commentId`, `userFk`, `postFk`, `commentDescription`) VALUES
(2, 42, 2, 'wow'),
(3, 42, 2, 'wow'),
(4, 42, 2, 'wow22'),
(5, 42, 2, 'wow2222'),
(6, 42, 2, 'woddw2222'),
(7, 42, 2, 'wovvvddw2222');

-- --------------------------------------------------------

--
-- Table structure for table `reserved_slots_chosen_table`
--

CREATE TABLE `reserved_slots_chosen_table` (
  `reservationId` int NOT NULL,
  `slotFk` int NOT NULL,
  `userFk` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `reserved_slots_chosen_table`
--

INSERT INTO `reserved_slots_chosen_table` (`reservationId`, `slotFk`, `userFk`) VALUES
(4, 23, 42),
(16, 23, 42),
(17, 23, 42),
(18, 23, 42),
(19, 23, 42),
(20, 23, 42);

-- --------------------------------------------------------

--
-- Table structure for table `specialities_table`
--

CREATE TABLE `specialities_table` (
  `specialityId` int NOT NULL,
  `specialityName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `specialityDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `specialities_table`
--

INSERT INTO `specialities_table` (`specialityId`, `specialityName`, `specialityDescription`) VALUES
(1, 'Allergey and Immunology', 'description');

-- --------------------------------------------------------

--
-- Table structure for table `subscriptions_table`
--

CREATE TABLE `subscriptions_table` (
  `subscriptionId` int NOT NULL,
  `userEmail` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `subscriptions_table`
--

INSERT INTO `subscriptions_table` (`subscriptionId`, `userEmail`) VALUES
(1, 'test@test.com'),
(3, 'test@tests.com');

-- --------------------------------------------------------

--
-- Table structure for table `surveys_table`
--

CREATE TABLE `surveys_table` (
  `surveyId` int NOT NULL,
  `surveyName` varchar(255) NOT NULL,
  `surveyDescription` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `surveys_table`
--

INSERT INTO `surveys_table` (`surveyId`, `surveyName`, `surveyDescription`) VALUES
(2, 'sss', 'vvv');

-- --------------------------------------------------------

--
-- Table structure for table `survey_answers_table`
--

CREATE TABLE `survey_answers_table` (
  `questionAnswerId` int NOT NULL,
  `questionFk` int NOT NULL,
  `answerFk` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `survey_answers_table`
--

INSERT INTO `survey_answers_table` (`questionAnswerId`, `questionFk`, `answerFk`) VALUES
(7, 30, 16),
(8, 30, 18);

-- --------------------------------------------------------

--
-- Table structure for table `survey_questions_answers_table`
--

CREATE TABLE `survey_questions_answers_table` (
  `answerId` int NOT NULL,
  `questionFk` int NOT NULL,
  `answerText` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `survey_questions_answers_table`
--

INSERT INTO `survey_questions_answers_table` (`answerId`, `questionFk`, `answerText`) VALUES
(15, 30, 'ccc'),
(16, 30, 'ccc'),
(17, 30, 'sss'),
(18, 30, 'vvvv'),
(19, 31, 'ccc'),
(20, 31, 'ccc'),
(21, 31, 'sss'),
(22, 31, 'vvvv');

-- --------------------------------------------------------

--
-- Table structure for table `survey_questions_table`
--

CREATE TABLE `survey_questions_table` (
  `questionId` int NOT NULL,
  `surveyFk` int NOT NULL,
  `questionText` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `survey_questions_table`
--

INSERT INTO `survey_questions_table` (`questionId`, `surveyFk`, `questionText`) VALUES
(30, 2, 'sss'),
(31, 2, 'sss');

-- --------------------------------------------------------

--
-- Table structure for table `tokens_table`
--

CREATE TABLE `tokens_table` (
  `tokenId` int NOT NULL,
  `userFk` int NOT NULL,
  `tokenValue` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `expiryDate` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tokens_table`
--

INSERT INTO `tokens_table` (`tokenId`, `userFk`, `tokenValue`, `expiryDate`) VALUES
(6, 42, '0CA11T', '2023-02-21 03:01:49'),
(7, 43, 'D78H8T', '2023-02-21 03:04:11'),
(8, 44, 'C62WMX', '2023-02-22 16:15:45');

-- --------------------------------------------------------

--
-- Table structure for table `translations_table`
--

CREATE TABLE `translations_table` (
  `translation_id` int NOT NULL,
  `translation_key` varchar(255) NOT NULL,
  `translation_value` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users_table`
--

CREATE TABLE `users_table` (
  `userId` int NOT NULL,
  `firstName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `lastName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userEmail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) NOT NULL,
  `isVerified` tinyint(1) NOT NULL DEFAULT '0',
  `numberOfLoginTrials` int NOT NULL DEFAULT '0',
  `isApproved` tinyint(1) NOT NULL DEFAULT '0',
  `isLocked` tinyint(1) NOT NULL DEFAULT '0',
  `userRole` enum('PATIENT','HEALTH_PROFESSIONAL') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `userLanguage` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `profilePicture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `registrationDate` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users_table`
--

INSERT INTO `users_table` (`userId`, `firstName`, `lastName`, `userEmail`, `password`, `isVerified`, `numberOfLoginTrials`, `isApproved`, `isLocked`, `userRole`, `userLanguage`, `profilePicture`, `registrationDate`) VALUES
(42, 'Mohammad', 'Patient', 'test@tests.com', '3fb0a50e69a3bd10bd006726cf744fa50e779bd652b0dda9733137d78af42de5', 1, 0, 0, 0, 'PATIENT', 'en', 'jjj', '2023-02-21 02:56:49'),
(43, 'Mohammad', 'Doctor', 'mhmd2@gmail.com', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 1, 0, 1, 0, 'HEALTH_PROFESSIONAL', 'en', '', '2023-02-21 02:59:10'),
(44, 'Mohammad', 'Mohammad', 'mohmd22@gmail.com', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5', 0, 0, 0, 0, 'PATIENT', 'en', '', '2023-02-22 16:10:45');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `appointments_table`
--
ALTER TABLE `appointments_table`
  ADD PRIMARY KEY (`appointmentId`),
  ADD KEY `appointment_slot_relation` (`slotFk`),
  ADD KEY `appointment_business_account_relation` (`businessAccountFk`),
  ADD KEY `appointment_user_relation` (`userFk`),
  ADD KEY `appointment_service_relation` (`serviceFk`);

--
-- Indexes for table `appointment_prescriptions_table`
--
ALTER TABLE `appointment_prescriptions_table`
  ADD PRIMARY KEY (`prescriptionId`),
  ADD KEY `prescription_appointment_relation` (`appointmentFk`);

--
-- Indexes for table `appointment_result_table`
--
ALTER TABLE `appointment_result_table`
  ADD PRIMARY KEY (`resultId`),
  ADD KEY `result_appointment_relation` (`appointmentFk`);

--
-- Indexes for table `blogs_table`
--
ALTER TABLE `blogs_table`
  ADD PRIMARY KEY (`blogId`);

--
-- Indexes for table `business_accounts_services_table`
--
ALTER TABLE `business_accounts_services_table`
  ADD PRIMARY KEY (`serviceId`),
  ADD KEY `business_account_service_relation` (`businessAccountFk`);

--
-- Indexes for table `business_account_blockings_table`
--
ALTER TABLE `business_account_blockings_table`
  ADD PRIMARY KEY (`blockId`),
  ADD KEY `block_business_account_relation` (`businessAccountFk`),
  ADD KEY `block_user_relation` (`userFk`);

--
-- Indexes for table `business_account_promo_codes_table`
--
ALTER TABLE `business_account_promo_codes_table`
  ADD PRIMARY KEY (`promo_code_id`),
  ADD KEY `promo_code_user_relation` (`user_fk`),
  ADD KEY `promo_code_business_account_relation` (`business_account_fk`);

--
-- Indexes for table `business_account_referrals_table`
--
ALTER TABLE `business_account_referrals_table`
  ADD PRIMARY KEY (`referralId`),
  ADD KEY `referral_user_relation` (`userFk`),
  ADD KEY `referral_referred_by_relation` (`referredByBusinessAccountFk`),
  ADD KEY `referral_referred_to_relation` (`referredToBusinessAccountFk`),
  ADD KEY `referral_appointment_relation` (`appointmentFk`);

--
-- Indexes for table `business_account_schedule_slots_table`
--
ALTER TABLE `business_account_schedule_slots_table`
  ADD PRIMARY KEY (`slotId`),
  ADD KEY `schedule_slot_relation` (`scheduleFk`),
  ADD KEY `service_slot_relation` (`serviceFk`);

--
-- Indexes for table `business_account_schedule_table`
--
ALTER TABLE `business_account_schedule_table`
  ADD PRIMARY KEY (`scheduleId`),
  ADD KEY `business_account_schedule_relation` (`businessAccountFk`);

--
-- Indexes for table `business_account_table`
--
ALTER TABLE `business_account_table`
  ADD PRIMARY KEY (`businessAccountId`),
  ADD KEY `business_account_user_relation` (`userFk`),
  ADD KEY `business_account_speciality_relation` (`specialityFk`);

--
-- Indexes for table `community_posts_table`
--
ALTER TABLE `community_posts_table`
  ADD PRIMARY KEY (`postId`),
  ADD KEY `post_user_relation` (`userFk`);

--
-- Indexes for table `contact_us_table`
--
ALTER TABLE `contact_us_table`
  ADD PRIMARY KEY (`contactId`);

--
-- Indexes for table `favorites_table`
--
ALTER TABLE `favorites_table`
  ADD PRIMARY KEY (`favoriteId`),
  ADD KEY `favorite_user_relation` (`userFk`),
  ADD KEY `favorite_business_account_relation` (`businessAccountFk`);

--
-- Indexes for table `feedbacks_table`
--
ALTER TABLE `feedbacks_table`
  ADD PRIMARY KEY (`feedbackId`),
  ADD KEY `feedback_user_relation` (`userFk`),
  ADD KEY `feedback_business_account_relation` (`businessAccountFk`);

--
-- Indexes for table `notifications_table`
--
ALTER TABLE `notifications_table`
  ADD PRIMARY KEY (`notificationId`),
  ADD KEY `notification_user_from_relation` (`userFromFk`),
  ADD KEY `notification_user_to_relation` (`userToFk`);

--
-- Indexes for table `password_tokens_table`
--
ALTER TABLE `password_tokens_table`
  ADD PRIMARY KEY (`tokenId`),
  ADD KEY `user_password_token_relation` (`userFk`);

--
-- Indexes for table `posts_comments_table`
--
ALTER TABLE `posts_comments_table`
  ADD PRIMARY KEY (`commentId`),
  ADD KEY `comment_user_relation` (`userFk`),
  ADD KEY `comment_post_relation` (`postFk`);

--
-- Indexes for table `reserved_slots_chosen_table`
--
ALTER TABLE `reserved_slots_chosen_table`
  ADD PRIMARY KEY (`reservationId`),
  ADD KEY `reservation_slot_relation` (`slotFk`),
  ADD KEY `reservation_user_relation` (`userFk`);

--
-- Indexes for table `specialities_table`
--
ALTER TABLE `specialities_table`
  ADD PRIMARY KEY (`specialityId`);

--
-- Indexes for table `subscriptions_table`
--
ALTER TABLE `subscriptions_table`
  ADD PRIMARY KEY (`subscriptionId`),
  ADD UNIQUE KEY `unique_email` (`userEmail`);

--
-- Indexes for table `surveys_table`
--
ALTER TABLE `surveys_table`
  ADD PRIMARY KEY (`surveyId`);

--
-- Indexes for table `survey_answers_table`
--
ALTER TABLE `survey_answers_table`
  ADD PRIMARY KEY (`questionAnswerId`),
  ADD KEY `survey_question_answer_relation` (`answerFk`),
  ADD KEY `survey_question_relation` (`questionFk`);

--
-- Indexes for table `survey_questions_answers_table`
--
ALTER TABLE `survey_questions_answers_table`
  ADD PRIMARY KEY (`answerId`),
  ADD KEY `question_answer_relation` (`questionFk`);

--
-- Indexes for table `survey_questions_table`
--
ALTER TABLE `survey_questions_table`
  ADD PRIMARY KEY (`questionId`),
  ADD KEY `question_survey_relation` (`surveyFk`);

--
-- Indexes for table `tokens_table`
--
ALTER TABLE `tokens_table`
  ADD PRIMARY KEY (`tokenId`),
  ADD KEY `user_token_relation` (`userFk`);

--
-- Indexes for table `translations_table`
--
ALTER TABLE `translations_table`
  ADD PRIMARY KEY (`translation_id`);

--
-- Indexes for table `users_table`
--
ALTER TABLE `users_table`
  ADD PRIMARY KEY (`userId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `appointments_table`
--
ALTER TABLE `appointments_table`
  MODIFY `appointmentId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `appointment_prescriptions_table`
--
ALTER TABLE `appointment_prescriptions_table`
  MODIFY `prescriptionId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `appointment_result_table`
--
ALTER TABLE `appointment_result_table`
  MODIFY `resultId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `blogs_table`
--
ALTER TABLE `blogs_table`
  MODIFY `blogId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `business_accounts_services_table`
--
ALTER TABLE `business_accounts_services_table`
  MODIFY `serviceId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `business_account_blockings_table`
--
ALTER TABLE `business_account_blockings_table`
  MODIFY `blockId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `business_account_promo_codes_table`
--
ALTER TABLE `business_account_promo_codes_table`
  MODIFY `promo_code_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `business_account_referrals_table`
--
ALTER TABLE `business_account_referrals_table`
  MODIFY `referralId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `business_account_schedule_slots_table`
--
ALTER TABLE `business_account_schedule_slots_table`
  MODIFY `slotId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `business_account_schedule_table`
--
ALTER TABLE `business_account_schedule_table`
  MODIFY `scheduleId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `business_account_table`
--
ALTER TABLE `business_account_table`
  MODIFY `businessAccountId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `community_posts_table`
--
ALTER TABLE `community_posts_table`
  MODIFY `postId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `contact_us_table`
--
ALTER TABLE `contact_us_table`
  MODIFY `contactId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `favorites_table`
--
ALTER TABLE `favorites_table`
  MODIFY `favoriteId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `feedbacks_table`
--
ALTER TABLE `feedbacks_table`
  MODIFY `feedbackId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `notifications_table`
--
ALTER TABLE `notifications_table`
  MODIFY `notificationId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `password_tokens_table`
--
ALTER TABLE `password_tokens_table`
  MODIFY `tokenId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `posts_comments_table`
--
ALTER TABLE `posts_comments_table`
  MODIFY `commentId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `reserved_slots_chosen_table`
--
ALTER TABLE `reserved_slots_chosen_table`
  MODIFY `reservationId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `specialities_table`
--
ALTER TABLE `specialities_table`
  MODIFY `specialityId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `subscriptions_table`
--
ALTER TABLE `subscriptions_table`
  MODIFY `subscriptionId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `surveys_table`
--
ALTER TABLE `surveys_table`
  MODIFY `surveyId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `survey_answers_table`
--
ALTER TABLE `survey_answers_table`
  MODIFY `questionAnswerId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `survey_questions_answers_table`
--
ALTER TABLE `survey_questions_answers_table`
  MODIFY `answerId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `survey_questions_table`
--
ALTER TABLE `survey_questions_table`
  MODIFY `questionId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `tokens_table`
--
ALTER TABLE `tokens_table`
  MODIFY `tokenId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `translations_table`
--
ALTER TABLE `translations_table`
  MODIFY `translation_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users_table`
--
ALTER TABLE `users_table`
  MODIFY `userId` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `appointments_table`
--
ALTER TABLE `appointments_table`
  ADD CONSTRAINT `appointment_business_account_relation` FOREIGN KEY (`businessAccountFk`) REFERENCES `business_account_table` (`businessAccountId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `appointment_service_relation` FOREIGN KEY (`serviceFk`) REFERENCES `business_accounts_services_table` (`serviceId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `appointment_slot_relation` FOREIGN KEY (`slotFk`) REFERENCES `business_account_schedule_slots_table` (`slotId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `appointment_user_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `appointment_prescriptions_table`
--
ALTER TABLE `appointment_prescriptions_table`
  ADD CONSTRAINT `prescription_appointment_relation` FOREIGN KEY (`appointmentFk`) REFERENCES `appointments_table` (`appointmentId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `appointment_result_table`
--
ALTER TABLE `appointment_result_table`
  ADD CONSTRAINT `result_appointment_relation` FOREIGN KEY (`appointmentFk`) REFERENCES `appointments_table` (`appointmentId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `business_accounts_services_table`
--
ALTER TABLE `business_accounts_services_table`
  ADD CONSTRAINT `business_account_service_relation` FOREIGN KEY (`businessAccountFk`) REFERENCES `business_account_table` (`businessAccountId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `business_account_blockings_table`
--
ALTER TABLE `business_account_blockings_table`
  ADD CONSTRAINT `block_business_account_relation` FOREIGN KEY (`businessAccountFk`) REFERENCES `business_account_table` (`businessAccountId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `block_user_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `business_account_promo_codes_table`
--
ALTER TABLE `business_account_promo_codes_table`
  ADD CONSTRAINT `promo_code_business_account_relation` FOREIGN KEY (`business_account_fk`) REFERENCES `business_account_table` (`businessAccountId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `promo_code_user_relation` FOREIGN KEY (`user_fk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `business_account_referrals_table`
--
ALTER TABLE `business_account_referrals_table`
  ADD CONSTRAINT `referral_appointment_relation` FOREIGN KEY (`appointmentFk`) REFERENCES `appointments_table` (`appointmentId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `referral_referred_by_relation` FOREIGN KEY (`referredByBusinessAccountFk`) REFERENCES `business_account_table` (`businessAccountId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `referral_referred_to_relation` FOREIGN KEY (`referredToBusinessAccountFk`) REFERENCES `business_account_table` (`businessAccountId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `referral_user_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `business_account_schedule_slots_table`
--
ALTER TABLE `business_account_schedule_slots_table`
  ADD CONSTRAINT `schedule_slot_relation` FOREIGN KEY (`scheduleFk`) REFERENCES `business_account_schedule_table` (`scheduleId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `service_slot_relation` FOREIGN KEY (`serviceFk`) REFERENCES `business_accounts_services_table` (`serviceId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `business_account_schedule_table`
--
ALTER TABLE `business_account_schedule_table`
  ADD CONSTRAINT `business_account_schedule_relation` FOREIGN KEY (`businessAccountFk`) REFERENCES `business_account_table` (`businessAccountId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `business_account_table`
--
ALTER TABLE `business_account_table`
  ADD CONSTRAINT `business_account_speciality_relation` FOREIGN KEY (`specialityFk`) REFERENCES `specialities_table` (`specialityId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `business_account_user_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `community_posts_table`
--
ALTER TABLE `community_posts_table`
  ADD CONSTRAINT `post_user_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `favorites_table`
--
ALTER TABLE `favorites_table`
  ADD CONSTRAINT `favorite_business_account_relation` FOREIGN KEY (`businessAccountFk`) REFERENCES `business_account_table` (`businessAccountId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `favorite_user_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `feedbacks_table`
--
ALTER TABLE `feedbacks_table`
  ADD CONSTRAINT `feedback_business_account_relation` FOREIGN KEY (`businessAccountFk`) REFERENCES `business_account_table` (`businessAccountId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `feedback_user_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `password_tokens_table`
--
ALTER TABLE `password_tokens_table`
  ADD CONSTRAINT `user_password_token_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `posts_comments_table`
--
ALTER TABLE `posts_comments_table`
  ADD CONSTRAINT `comment_post_relation` FOREIGN KEY (`postFk`) REFERENCES `community_posts_table` (`postId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `comment_user_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `reserved_slots_chosen_table`
--
ALTER TABLE `reserved_slots_chosen_table`
  ADD CONSTRAINT `reservation_slot_relation` FOREIGN KEY (`slotFk`) REFERENCES `business_account_schedule_slots_table` (`slotId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `reservation_user_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `survey_answers_table`
--
ALTER TABLE `survey_answers_table`
  ADD CONSTRAINT `survey_question_answer_relation` FOREIGN KEY (`answerFk`) REFERENCES `survey_questions_answers_table` (`answerId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `survey_question_relation` FOREIGN KEY (`questionFk`) REFERENCES `survey_questions_table` (`questionId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `survey_questions_answers_table`
--
ALTER TABLE `survey_questions_answers_table`
  ADD CONSTRAINT `question_answer_relation` FOREIGN KEY (`questionFk`) REFERENCES `survey_questions_table` (`questionId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `survey_questions_table`
--
ALTER TABLE `survey_questions_table`
  ADD CONSTRAINT `question_survey_relation` FOREIGN KEY (`surveyFk`) REFERENCES `surveys_table` (`surveyId`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `tokens_table`
--
ALTER TABLE `tokens_table`
  ADD CONSTRAINT `user_token_relation` FOREIGN KEY (`userFk`) REFERENCES `users_table` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
