-- 기존 테이블 백업
CREATE TABLE IF NOT EXISTS target_backup AS SELECT * FROM target;

-- 테이블 구조 확인
DESCRIBE target;

-- 테이블 구조 수정 (필요한 경우)
-- 이 부분은 테이블 구조 확인 후 필요에 따라 실행하세요
/*
ALTER TABLE target DROP COLUMN age;
ALTER TABLE target DROP COLUMN height;
ALTER TABLE target DROP COLUMN gender;

-- 새로운 구조로 테이블 생성
CREATE TABLE target_new (
    user_id VARCHAR(20) PRIMARY KEY,
    start_weight DECIMAL(5,2) NOT NULL,
    target_weight DECIMAL(5,2) NOT NULL,
    target_duration INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 백업 데이터에서 필요한 필드만 복원
INSERT INTO target_new (user_id, start_weight, target_weight, target_duration)
SELECT user_id, start_weight, target_weight, target_duration FROM target;

-- 테이블 교체
DROP TABLE target;
RENAME TABLE target_new TO target;
*/ 