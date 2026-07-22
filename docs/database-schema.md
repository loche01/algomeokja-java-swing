# 데이터베이스 스키마와 DAO 매핑

이 문서는 `sql/schema.sql`과 현재 DAO의 SQL을 정적으로 대조한 결과를 정리합니다. 실제 데이터베이스 접속이나 SQL 실행 결과가 아니라, 저장소에 포함된 코드와 스키마 파일을 기준으로 합니다.

## 스키마 구성

`schema.sql`은 `almja` 데이터베이스와 다음 11개 테이블을 정의합니다.

| 영역 | 테이블 | 주요 용도 |
|---|---|---|
| 인증·회원 | `admin`, `user` | 관리자·사용자 인증과 회원정보 |
| 신체·목표 | `bodyinfo`, `target` | 신체 측정 이력과 체중 목표 |
| 음식·식단 | `food`, `meal`, `meal_log` | 음식 영양정보와 식사별 기록 |
| 운동 | `exercise`, `exercise_log` | 운동 정보와 소모 칼로리 기록 |
| 공지 | `notice`, `notice_files` | 공지 본문과 첨부파일 메타데이터 |

공개 스키마에는 관리자 계정이나 비밀번호 seed를 포함하지 않습니다. 음식과 운동의 기본 조회 데이터만 중복 삽입을 방지하는 형태로 포함합니다.

## DAO별 매핑

| DAO | 참조 테이블 | 정적 대조 결과 |
|---|---|---|
| `LoginDAO`, `JoinDAO`, `UserDAO` | `admin`, `user` | 인증·회원가입·회원정보 SQL의 테이블과 컬럼 존재 |
| `BodyInfoDAO` | `user`, `bodyinfo` | 신체정보 저장 및 최신 측정값 조회 컬럼 존재 |
| `GoalDAO` | `user`, `target`, `bodyinfo`, `meal`, `meal_log` | 목표 저장·조회와 체중·섭취 열량 조회 컬럼 존재 |
| `FoodDAO` | `food` | 음식명과 영양소 조회 컬럼 존재 |
| `MealDAO`, `MealLogDAO`, `MealSaveService` | `meal`, `meal_log`, `food` | 식단 헤더·상세 저장 및 영양소 합계 컬럼 존재 |
| `ExerciseDAO`, `ExerciseLogDAO` | `exercise`, `exercise_log` | 운동 검색·수정 및 운동 기록 저장·조회 컬럼 존재 |
| `CalendarDAO` | `exercise_log`, `meal`, `meal_log`, `food` | 선택 날짜의 운동·식단 조회 컬럼 존재 |
| `NoticeDAO`, `NoticeFileDAO` | `notice`, `notice_files` | 공지 CRUD와 첨부파일 이름·저장 경로 컬럼 존재 |

현재 DAO가 직접 사용하는 테이블과 컬럼은 모두 `schema.sql`에 정의되어 있습니다.

## 사용자 필드 통합 확인

과거 보조 스크립트의 `user_birthdate` 추가 내용은 최종 `user` 테이블에 반영되어 있습니다. 현재 회원가입 SQL이 사용하는 필드는 다음과 같습니다.

- `user_id`
- `user_pwd`
- `user_name`
- `user_phone`
- `user_email`
- `user_createdtime`
- `user_birthdate`
- `user_gender`

따라서 별도의 사용자 컬럼 추가 스크립트는 필요하지 않습니다.

## 목표 테이블 통합 확인

`GoalDAO`가 사용하는 목표 필드는 다음과 같습니다.

- `user_id`
- `start_weight`
- `target_weight`
- `target_duration`
- `created_at`

최종 `target` 테이블은 위 필드와 자동 갱신되는 `updated_at`을 포함합니다. 과거의 백업·테이블 교체용 보조 스크립트가 의도했던 최종 구조가 이미 반영되어 있으므로 신규 설치에는 별도 목표 마이그레이션 스크립트가 필요하지 않습니다.

## 구현 경계

- `ExerciseLogDAO`는 운동 시간과 입력 체중을 칼로리 계산 입력으로 받지만, 현재 영구 저장 계약은 운동 코드·운동명·소모 칼로리입니다. `exercise_log`는 이 저장 계약과 일치합니다.
- 공지 첨부파일은 DB에 원본 파일을 저장하지 않고, 앱 전용 저장소의 UUID 파일명과 표시 이름을 `notice_files`에 기록합니다.
- 테이블 간 관계는 DAO가 키 값으로 관리하며, 현재 공개 스키마에는 외래 키 제약이 선언되어 있지 않습니다.
- 기존 로컬 데이터베이스를 이전해야 하는 경우에는 현재 상태를 별도로 확인한 뒤 전용 마이그레이션을 작성해야 합니다. `schema.sql`은 신규 개발 환경 구성을 기준으로 합니다.

## 정적 점검 범위

- DAO의 `SELECT`, `INSERT`, `UPDATE`, `DELETE` 대상 테이블 확인
- SQL에서 직접 참조하는 컬럼과 `schema.sql` 정의 비교
- 사용자 추가 컬럼과 목표 테이블 보조 SQL의 최종 반영 여부 확인
- 실제 DB 접속 및 SQL 실행 제외
