# DB 스키마·DAO 3자 대조 보고서

## 1. 분석 목적

실제 로컬 MySQL `almja` 데이터베이스 구조, `sql/schema.sql`, DAO SQL, 관련 UI 전달값을 대조해 핵심 불일치 원인을 정리한다.

분석 대상은 `bodyinfo`, `target`, `exercise_log`, `notice_files` 네 항목이다. 이 문서는 수정 방향만 제안하며, 실제 Java·SQL 파일 수정이나 DB 변경은 포함하지 않는다.

## 2. 실제 로컬 MySQL 확인 결과

사용자가 직접 확인한 실제 `almja` 테이블은 다음 9개뿐이다.

- `admin`
- `exercise`
- `exercise_log`
- `food`
- `meal`
- `meal_log`
- `notice`
- `notice_files`
- `user`

실제 DB에는 `bodyinfo`, `target` 테이블이 없다.

전체 컬럼 검색 결과에서도 신체정보·목표 관리를 위한 `height`, `weight`, `bmi`, `goal`, `target`, `activity`, `calorie` 관련 저장 구조는 확인되지 않았다. 검색 결과는 `exercise_log.exercise_calories`, `meal_log.weight_g`, `user.user_birthdate`, `user.user_gender`뿐이다.

## 3. `bodyinfo` 대조 결과

실제 DB 상태: `bodyinfo` 테이블이 없다.

`schema.sql` 상태: `bodyinfo` 생성문이 없다.

DAO 기대 구조: `BodyInfoDAO.saveBodyInfo()`는 `bodyinfo(user_id, height, weight, fat_rate, fat_mass, muscle_mass)` INSERT를 수행한다. `BodyInfoDAO.getLatestBodyInfo()`와 `GoalDAO.getLatestWeight()`는 `bodyinfo_code DESC` 기준 최신 행 조회를 기대한다.

UI 전달값: `BodyInfoSetPanel`과 `MyBodyPanel`은 키, 몸무게, 체지방률, 체지방량, 골격근량을 DAO로 전달한다. `MyPagePanel`, `HomeTargetPanel`, `MymeGoalPanel`도 신체정보 조회 결과에 의존한다.

정확한 불일치: 실제 DB와 `schema.sql`에는 테이블이 없지만 DAO와 UI는 이미 `bodyinfo`를 핵심 데이터 저장소로 사용한다.

예상 실행 오류: 신체정보 저장·조회 시 `Table 'almja.bodyinfo' doesn't exist` 계열 SQL 예외가 발생한다.

권장 기준 구조:

- `bodyinfo_code INT PRIMARY KEY AUTO_INCREMENT`
- `user_id VARCHAR(50) NOT NULL`
- `height FLOAT`
- `weight FLOAT`
- `fat_rate FLOAT`
- `fat_mass FLOAT`
- `muscle_mass FLOAT`
- `created_at DATETIME DEFAULT CURRENT_TIMESTAMP`
- `INDEX(user_id)`

수정 방향: 테이블 추가가 필요하다. DAO는 `ResultSet`을 반환한 뒤 연결을 닫지 못하는 구조도 함께 개선하는 것이 좋다.

## 4. `target` 대조 결과

실제 DB 상태: `target` 테이블이 없다.

`schema.sql` 상태: `target` 생성문이 없다.

보조 SQL 상태: `update_target_table.sql`은 기존 `target`을 백업하고 `target_new(user_id, start_weight, target_weight, target_duration)`로 교체하려는 목적의 스크립트다. 현재 실제 DB에는 적용된 결과가 없다.

DAO 기대 구조: `GoalDAO.saveOrUpdateGoal()`은 `target(user_id, start_weight, target_weight, target_duration)`에 대해 존재 확인, INSERT, UPDATE를 수행한다. `GoalDAO.getUserGoal()`은 같은 컬럼을 조회한다.

UI 전달값: `MymeGoalPanel`은 시작 체중, 목표 체중, 목표 기간을 `UserGoal`로 만들어 `GoalDAO`에 전달한다. `HomeTargetPanel`은 이 값을 읽어 목표 달성 화면을 표시한다.

정확한 불일치: 실제 DB와 `schema.sql`에는 테이블이 없지만 DAO와 UI는 목표 관리 기능에서 `target`을 사용한다.

예상 실행 오류: 목표 저장·조회 시 `Table 'almja.target' doesn't exist` 계열 SQL 예외가 발생한다.

권장 기준 구조:

- `user_id VARCHAR(50) PRIMARY KEY`
- `start_weight DECIMAL(5,2) NOT NULL`
- `target_weight DECIMAL(5,2) NOT NULL`
- `target_duration INT NOT NULL`
- `created_at DATETIME DEFAULT CURRENT_TIMESTAMP`
- `updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`

수정 방향: 테이블 추가가 필요하다. 목표 시작일을 별도 컬럼으로 둘지 여부는 추가 결정이 필요하다.

## 5. `exercise_log` 대조 결과

실제 DB 상태와 `schema.sql` 상태는 같은 방향이다.

현재 구조:

- `exercise_log_id INT PRIMARY KEY AUTO_INCREMENT`
- `user_id VARCHAR(50) NOT NULL`
- `exercise_code INT`
- `exercise_name VARCHAR(100)`
- `exercise_calories INT DEFAULT 0`
- `exercise_date DATETIME DEFAULT CURRENT_TIMESTAMP`

DAO 기대 구조: `ExerciseLogDAO.saveExerciseLog()`는 `exercise_log_code`, `exercise_log_runtime`, `weight_input`, `exercise_kcal` 컬럼을 기대한다. 실제 DB와 `schema.sql`에는 이 컬럼들이 없다.

UI 전달값: `ExerciseCaloriePanel`은 운동 시간, 체중, 계산된 칼로리를 `ExerciseLogDAO`로 전달한다. 다만 운동 선택 정보 전달 코드가 주석 처리되어 있어 현재는 기본 MET `6.0`, 기본 운동 코드 `1`이 사용될 가능성이 있다.

정확한 불일치:

- 실제 DB와 `schema.sql`: `exercise_log_id`, `exercise_calories` 중심
- DAO: `exercise_log_code`, `exercise_log_runtime`, `weight_input`, `exercise_kcal` 중심
- UI: 운동 시간, 체중, 계산 칼로리를 갖고 있으나 현재 DB 구조에는 시간·체중·MET 저장 컬럼이 없다.

예상 실행 오류: 현재 DAO 그대로 저장하면 `Unknown column 'exercise_log_code'` 또는 `exercise_log_runtime`, `weight_input`, `exercise_kcal` 컬럼 없음 오류가 발생한다.

현재 DB 구조로 DAO를 맞출 경우 손실되는 정보:

- 운동 시간
- 입력 체중
- 계산 당시 MET
- 계산 근거

DB를 DAO/UI에 맞춰 확장할 경우 영향:

- 저장은 UI 계산값을 보존할 수 있다.
- `ExerciseDAO.getTotalBurnedCalories()`는 `exercise_calories`를 조회하므로 DAO 저장 컬럼명도 `exercise_calories`로 통일해야 한다.
- 운동 선택 시 `ExerciseBean`의 code/name/MET가 `ExerciseCaloriePanel`에 전달되도록 UI 흐름도 같이 보완해야 한다.

권장 기준 구조:

- `exercise_log_id INT PRIMARY KEY AUTO_INCREMENT`
- `user_id VARCHAR(50) NOT NULL`
- `exercise_code INT`
- `exercise_name VARCHAR(100)`
- `exercise_log_runtime INT`
- `weight_input DOUBLE`
- `exercise_met DOUBLE`
- `exercise_calories INT DEFAULT 0`
- `exercise_date DATETIME DEFAULT CURRENT_TIMESTAMP`

수정 방향: DB 확장과 DAO 수정이 함께 필요하다. 운동 시간, 체중, MET, 운동 코드 보존 여부를 기준으로 최종 구조를 결정해야 한다.

## 6. `notice_files` 대조 결과

실제 DB 상태와 `schema.sql` 상태는 `file_path` 방식이다.

현재 구조:

- `file_id INT PRIMARY KEY AUTO_INCREMENT`
- `notice_id INT NOT NULL`
- `file_name VARCHAR(255) NOT NULL`
- `file_path VARCHAR(500)`
- `uploaded_time DATETIME DEFAULT CURRENT_TIMESTAMP`

DAO 기대 구조: `NoticeFileDAO.uploadFile()`은 `notice_id`, `admin_id`, `file_name`, `file_data`, `upload_time`에 INSERT한다. `downloadFile()`은 `file_data` BLOB을 읽어 사용자 Downloads 폴더에 저장한다.

UI 전달값: `NoticeWritePanel`과 `NoticeEditPanel`은 `JFileChooser`에서 선택한 `File` 객체를 `NoticeFileDAO.uploadFile()`로 전달한다. 실제 파일을 프로젝트 내부나 별도 저장소로 복사하는 로직은 확인되지 않았다.

`NoticeDAO`와 `NoticeFileDAO`의 차이:

- `NoticeDAO.getFilesByNoticeId()`는 `file_id`, `file_name` 목록만 조회한다.
- `NoticeFileDAO`도 목록 조회는 같은 방식이지만, 업로드·다운로드는 BLOB 방식이다.
- 삭제 시 `notice_files` 정리 로직은 확인되지 않았다.

정확한 불일치:

- 실제 DB와 `schema.sql`: `file_path`, `uploaded_time`
- DAO: `admin_id`, `file_data`, `upload_time`
- UI: 파일 객체를 전달하지만 path 저장을 위한 파일 복사 로직은 없음

예상 실행 오류: 첨부파일 저장 시 `admin_id`, `file_data`, `upload_time` 컬럼 없음 오류가 발생한다. 다운로드 시에도 `file_data` 컬럼 없음 오류가 발생한다.

권장 판단: 현재 코드 최소 수정 기준으로는 BLOB 방식이 더 자연스럽다. 이미 `NoticeFileDAO`가 파일 내용을 `FileInputStream`으로 읽고, 다운로드도 DB BLOB에서 읽는 흐름으로 작성되어 있기 때문이다.

대안:

- BLOB 방식: `file_data LONGBLOB`, `admin_id`, `uploaded_time` 구조로 스키마와 DAO를 맞춘다.
- file_path 방식: 파일 복사 위치, 파일명 충돌 정책, 삭제 정책, 다운로드 로직을 새로 구현한다.

수정 방향: BLOB 방식과 file_path 방식 중 하나를 선택해야 한다. 단, 첨부파일 기능은 핵심 복구 순서에서는 `bodyinfo`, `target`, `exercise_log`보다 후순위다.

## 7. 네 대상별 권장 수정 방향

| 대상 | 권장 방향 | 이유 |
|---|---|---|
| `bodyinfo` | 테이블 추가, DAO 연결 관리 개선 | 실제 DB와 `schema.sql`에 없지만 DAO·UI가 이미 사용 |
| `target` | 테이블 추가, 목표 시작일 여부 결정 | 실제 DB와 `schema.sql`에 없지만 목표 관리 DAO·UI가 사용 |
| `exercise_log` | DB 확장과 DAO 수정 병행 | 실제 DB와 `schema.sql`은 일치하나 DAO가 다른 컬럼 기대 |
| `notice_files` | BLOB/file_path 중 택일, 현 코드 기준 BLOB 우선 | 실제 DB와 `schema.sql`은 path 방식, DAO·다운로드는 BLOB 방식 |

## 8. 수정 우선순위

1. `bodyinfo` 테이블 추가
2. `target` 테이블 추가
3. `exercise_log` 저장 구조 결정 및 DAO/UI 수정
4. 목표·신체정보·운동 기록 실제 실행 검증
5. `notice_files` 저장 방식 결정
6. 공지 첨부파일 업로드·다운로드·삭제 흐름 정리

첨부파일은 중요하지만 핵심 복구 순서에서는 신체정보, 목표, 운동 기록보다 후순위로 둔다.

## 9. 아직 결정이 필요한 사항

- `bodyinfo`를 사용자당 1행으로 유지할지, 이력 테이블로 누적할지
- `bodyinfo`에 BMI, 활동량, 목표 칼로리까지 포함할지 별도 테이블로 분리할지
- `target`에 목표 시작일을 저장할지
- `exercise_log`에 운동 시간, 체중, MET를 반드시 저장할지
- 운동 선택 시 `ExerciseBean` 전체를 기록 화면에 전달할지
- `notice_files`를 BLOB 방식으로 갈지, 파일 경로 방식으로 갈지
- 공지 삭제·수정 시 첨부파일 DB 레코드와 실제 파일을 어떻게 정리할지

## 10. 다음 작업 제안

1. `bodyinfo`, `target`의 최종 컬럼을 확정한다.
2. `schema.sql`에 두 테이블을 추가한다.
3. `ExerciseLogDAO`를 실제 저장 목적에 맞게 수정하고, 운동 선택 정보 전달을 복구한다.
4. 신체정보 저장, 목표 저장, 운동 기록 저장을 실제 실행으로 검증한다.
5. 이후 `notice_files` 저장 방식을 확정하고 첨부파일 기능을 별도 작업으로 정리한다.
