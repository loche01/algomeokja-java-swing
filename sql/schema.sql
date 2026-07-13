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

-- 관리자 계정은 공개 저장소의 고정 seed로 생성하지 않습니다.
-- 로컬 환경에서 PBKDF2 인코딩 값을 사용해 별도로 준비하세요.

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
SELECT '인클라인벤치프레스', '가슴', '근력운동', 5.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '인클라인벤치프레스');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '덤벨플라이', '가슴', '근력운동', 4.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '덤벨플라이');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '딥스', '가슴', '근력운동', 5.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '딥스');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '스쿼트', '하체', '근력운동', 5.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '스쿼트');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '런지', '하체', '근력운동', 4.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '런지');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '레그프레스', '하체', '근력운동', 5.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '레그프레스');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '레그컬', '하체', '근력운동', 3.5
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '레그컬');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '카프레이즈', '하체', '근력운동', 3.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '카프레이즈');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '런닝', '유산소', '유산소운동', 8.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '런닝');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '빠르게걷기', '유산소', '유산소운동', 4.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '빠르게걷기');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '자전거', '유산소', '유산소운동', 6.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '자전거');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '플랭크', '코어', '근력운동', 3.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '플랭크');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '크런치', '코어', '근력운동', 3.5
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '크런치');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '레그레이즈', '코어', '근력운동', 3.5
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '레그레이즈');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '랫풀다운', '등', '근력운동', 4.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '랫풀다운');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '바벨로우', '등', '근력운동', 5.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '바벨로우');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '데드리프트', '등', '근력운동', 6.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '데드리프트');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '풀업', '등', '근력운동', 8.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '풀업');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '숄더프레스', '팔&어깨', '근력운동', 5.0
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '숄더프레스');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '사이드레터럴레이즈', '팔&어깨', '근력운동', 3.5
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '사이드레터럴레이즈');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '덤벨컬', '팔&어깨', '근력운동', 3.5
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '덤벨컬');

INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET)
SELECT '트라이셉스익스텐션', '팔&어깨', '근력운동', 3.5
WHERE NOT EXISTS (SELECT 1 FROM exercise WHERE exercise_name = '트라이셉스익스텐션');

-- 공지 seed는 관리자 계정에 의존하므로 포함하지 않습니다.
