CREATE DATABASE IF NOT EXISTS almja
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_general_ci;

USE almja;

CREATE TABLE IF NOT EXISTS admin (
    admin_id VARCHAR(50) PRIMARY KEY,
    admin_pwd VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS user (
    user_id VARCHAR(50) PRIMARY KEY,
    user_pwd VARCHAR(100) NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    user_phone VARCHAR(30),
    user_email VARCHAR(100),
    user_createdtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_birthdate VARCHAR(20),
    user_gender VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS bodyinfo (
    bodyinfo_code INT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(50) NOT NULL,
    height FLOAT DEFAULT NULL,
    weight FLOAT DEFAULT NULL,
    fat_rate FLOAT DEFAULT NULL,
    fat_mass FLOAT DEFAULT NULL,
    muscle_mass FLOAT DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (bodyinfo_code),
    INDEX idx_bodyinfo_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS target (
    user_id VARCHAR(50) NOT NULL,
    start_weight DECIMAL(5,2) NOT NULL,
    target_weight DECIMAL(5,2) NOT NULL,
    target_duration INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS food (
    food_code INT PRIMARY KEY AUTO_INCREMENT,
    food_name VARCHAR(100) NOT NULL,
    food_kcal DOUBLE DEFAULT 0,
    carb DOUBLE DEFAULT 0,
    protein DOUBLE DEFAULT 0,
    fat DOUBLE DEFAULT 0
);

CREATE TABLE IF NOT EXISTS exercise (
    exercise_code INT PRIMARY KEY AUTO_INCREMENT,
    exercise_name VARCHAR(100) NOT NULL,
    exercise_category VARCHAR(50) NOT NULL,
    exercise_type VARCHAR(50),
    exercise_MET FLOAT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS exercise_log (
    exercise_log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(50) NOT NULL,
    exercise_code INT,
    exercise_name VARCHAR(100),
    exercise_calories INT DEFAULT 0,
    exercise_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notice (
    notice_num INT PRIMARY KEY AUTO_INCREMENT,
    admin_id VARCHAR(50) NOT NULL,
    notice_title VARCHAR(200) NOT NULL,
    notice_coment TEXT,
    notice_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notice_files (
    file_id INT PRIMARY KEY AUTO_INCREMENT,
    notice_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500),
    uploaded_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS meal (
    meal_code INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(50) NOT NULL,
    meal_date DATE NOT NULL,
    meal_time TIME NOT NULL,
    meal_type VARCHAR(20),
    meal_image_path VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS meal_log (
    meal_log_code INT PRIMARY KEY AUTO_INCREMENT,
    meal_code INT NOT NULL,
    food_code INT NOT NULL,
    weight_g DOUBLE DEFAULT 0,
    meal_kcal DOUBLE DEFAULT 0,
    meal_carb DOUBLE DEFAULT 0,
    meal_protein DOUBLE DEFAULT 0,
    meal_fat DOUBLE DEFAULT 0
);

-- historical admin seed removed

INSERT INTO food (food_name, food_kcal, carb, protein, fat)
SELECT '닭가슴살', 165, 0, 31, 3.6
WHERE NOT EXISTS (SELECT 1 FROM food WHERE food_name = '닭가슴살');

INSERT INTO food (food_name, food_kcal, carb, protein, fat)
SELECT '현미밥', 360, 76, 7, 2.7
WHERE NOT EXISTS (SELECT 1 FROM food WHERE food_name = '현미밥');

INSERT INTO food (food_name, food_kcal, carb, protein, fat)
SELECT '삶은계란', 78, 0.6, 6.3, 5.3
WHERE NOT EXISTS (SELECT 1 FROM food WHERE food_name = '삶은계란');

INSERT INTO food (food_name, food_kcal, carb, protein, fat)
SELECT '바나나', 89, 22.8, 1.1, 0.3
WHERE NOT EXISTS (SELECT 1 FROM food WHERE food_name = '바나나');

INSERT INTO food (food_name, food_kcal, carb, protein, fat)
SELECT '고구마', 128, 30, 1.4, 0.2
WHERE NOT EXISTS (SELECT 1 FROM food WHERE food_name = '고구마');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '푸쉬업', '가슴', '근력운동', 3.8
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '푸쉬업');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '벤치프레스', '가슴', '근력운동', 6.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '벤치프레스');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '스쿼트', '하체', '근력운동', 5.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '스쿼트');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '런닝', '유산소', '유산소운동', 8.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '런닝');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '플랭크', '코어', '근력운동', 3.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '플랭크');

INSERT INTO notice (admin_id, notice_title, notice_coment, notice_time)
SELECT 'admin', '테스트 공지사항', '프로젝트 실행 확인용 테스트 공지입니다.', NOW()
WHERE NOT EXISTS (SELECT 1 FROM notice WHERE notice_title = '테스트 공지사항');
