# Codex 정적 분석 감사 보고서

## 1. 분석 목적과 범위

이 문서는 Eclipse 기반 Java Swing 식단 관리 프로그램을 포트폴리오 제출 전에 정리하기 위해, 앞서 수행한 프로젝트 정적 분석 결과를 문서화한 것이다.

- 분석 목적: 컴파일 가능성, DB 스키마와 DAO 불일치, 로그인·세션, 화면 전환, 핵심 기능 완성도, 중복 DB 호출, 디버그 로그 잔존 여부를 제출 전 관점에서 정리한다.
- 분석 범위: `src/main`, `src/panel`, `src/DB`, `src/model`, `src/ui_n_utils`, `sql/schema.sql`, 보조 SQL 파일의 구조와 참조 관계.
- 분석 제외: `backup/`, `bin/`, `out/`, `.git/`, `.class`, 로그·임시 파일, `src/db.properties`.
- 검증 구분: 이 문서의 문제 목록은 정적 분석 결과이며, 실제 실행 확인이 필요한 항목은 별도 섹션에 분리했다.

현재까지 실제 실행으로 확인된 사항은 macOS 실행 복구, MySQL 연결과 주요 테이블 생성, 관리자 로그인, 일반 사용자 회원가입·로그인, 신체정보 Skip 화면 전환, 문서·캡처 정리, 일부 DAO 디버그 로그 정리이다. 그 외 기능은 정상 동작으로 단정하지 않는다.

주의: `schema.sql`에 테이블 또는 컬럼이 없다는 정적 분석 결과와, 실제 로컬 MySQL DB에 해당 객체가 없다는 사실은 구분한다. 이 보고서에서 `bodyinfo`, `target`, `exercise_log`, `notice_files` 관련 지적은 우선 “`schema.sql` 기준 누락 또는 불일치”로 표현하며, 실제 DB 존재 여부와 컬럼 구조는 실행 확인 항목으로 남긴다.

## 2. 프로젝트 구조 요약

| 경로 | 역할 | 비고 |
|---|---|---|
| `src/main/MainFrame.java` | 최상위 Swing 프레임, CardLayout 화면 전환 | `showPanel`, `moveToBodyInfoSet` 중심 |
| `src/main/MainUserPanel.java` | 일반 사용자 메인 패널 | 생성자에서 홈, 식단, 목표, 캘린더, 운동, 마이페이지, 공지 패널을 즉시 생성 |
| `src/main/MainAdminPanel.java` | 관리자 메인 패널 | 생성자에서 공지 목록, 작성, 상세, 수정 패널을 즉시 생성 |
| `src/panel/*.java` | 사용자·관리자 화면 UI | 절대 좌표 기반 Swing UI |
| `src/DB/*.java` | DAO와 DB 연결 관리 | MySQL `almja` 스키마 사용 |
| `src/model/*.java` | Bean, 모델 계층 | `LoginManager`가 `src/model/LoginManager.java`에 존재하며 자체 로그인 사용자 상태를 관리 |
| `src/ui_n_utils/*.java` | UI 유틸, 세션 유틸 | `UserSessionManager`가 `src/ui_n_utils/UserSessionManager.java`에 존재하며 `LoginManager`와 다른 패키지에서 별도 세션 상태를 관리 |
| `sql/schema.sql` | 기본 DB 생성 스크립트 | 일부 DAO가 요구하는 테이블·컬럼이 누락됨 |
| `add_user_columns.sql`, `update_target_table.sql` | 보조 마이그레이션 SQL | 기본 스키마와 통합 여부 확인 필요 |
| `docs/` | 포트폴리오 문서와 이미지 | 이번 문서 추가 대상 |

## 3. 사용자 기능 분석표

| 기능 | 관련 파일 | 정적 분석 판단 | 실제 실행 확인 |
|---|---|---|---|
| 일반 사용자 회원가입 | `src/panel/JoinPanel.java`, `src/DB/JoinDAO.java` | 사용자 기본 정보 저장 흐름 존재. `user_birthdate`, `user_gender`는 `sql/schema.sql:19-20`에 존재한다. | 확인됨 |
| 일반 사용자 로그인 | `src/panel/LoginPanel.java`, `src/DB/LoginDAO.java:198-235` | 로그인 성공 시 `UserBean`을 생성하고 역할을 `USER`로 설정한다. | 확인됨 |
| 신체정보 입력 | `src/panel/BodyInfoSetPanel.java:116-124`, `src/DB/BodyInfoDAO.java:53-99` | DAO가 `bodyinfo` 테이블에 저장하지만 기본 스키마에 `bodyinfo` 테이블이 없다. 저장 기능은 스키마 보완 전 실패 가능성이 높다. | Skip 전환만 확인됨 |
| 신체정보 Skip | `src/panel/BodyInfoSetPanel.java:130-133` | `mainFrame.showPanel("mainUser")` 호출 구조가 있다. | 확인됨 |
| 일일 영양 현황 | `src/panel/HomeDailyPanel.java:22-26`, `src/panel/HomeDailyPanel.java:252-276`, `src/DB/MealLogDAO.java:151-211` | 오늘 식단 합산을 조회하지만 목표 칼로리와 영양소가 하드코딩되어 사용자 목표와 연결되지 않는다. | 실행 확인 필요 |
| 식단 기록 | `src/panel/FoodListPanel.java:328-351`, `src/DB/MealDAO.java:18-49`, `src/DB/MealLogDAO.java:20-108` | `meal`, `meal_log`, `food` 기본 흐름은 스키마와 대체로 일치한다. 단, 콘솔 로그가 많이 남아 있다. | 실행 확인 필요 |
| 식단별 칼로리 카드 | `src/panel/HomeMealPanel.java:350-390`, `src/DB/MealDAO.java:84-120` | 아침·점심·저녁·간식을 각각 별도 쿼리로 조회한다. 데이터량 증가 시 비효율 가능성이 있다. | 실행 확인 필요 |
| 운동 검색·목록 | `src/panel/ExerciseListPanel.java`, `src/panel/ExerciseSearchPanel.java`, `src/DB/ExerciseDAO.java` | `exercise` 테이블과 기본 조회 구조가 있다. | 실행 확인 필요 |
| 운동 기록 저장 | `src/DB/ExerciseLogDAO.java:20-77` | 기본 스키마의 `exercise_log` 컬럼과 DAO INSERT 컬럼이 불일치한다. | 실행 확인 필요 |
| 공지사항 조회 | `src/panel/NoticePanel.java`, `src/DB/NoticeDAO.java:61-107` | `notice` 테이블 조회 구조가 있다. DAO에 디버그 로그가 남아 있다. | 실행 확인 필요 |
| 마이페이지 회원정보 수정 | `src/panel/MyMemberPanel.java`, `src/DB/UserDAO.java:28-74`, `src/DB/LoginDAO.java:87-133`, `src/ui_n_utils/UserSessionManager.java` | 회원정보 수정 DAO가 2개 경로로 존재한다. 또한 `MyMemberPanel`이 기존 비밀번호를 세션에서 사용할 경우, `UserSessionManager`의 세션 저장 과정에서 비밀번호가 `null` 처리될 수 있어 비밀번호 변경 여부 판단이 불안정할 가능성이 있다. 정적 분석만으로 오류를 확정하지 않고 실행 확인이 필요하다. | 실행 확인 필요 |
| ID/PW 찾기 | `src/panel/FindIdPwPhone.java:145-155`, `src/panel/FindIdPwEmail.java:127-137` | 인증 전송, 아이디 찾기, 비밀번호 찾기 버튼이 실제 DB 조회·인증 없이 `System.out.println` 중심으로 남아 있다. | 실행 확인 필요 |

## 4. 관리자 기능 분석표

| 기능 | 관련 파일 | 정적 분석 판단 | 실제 실행 확인 |
|---|---|---|---|
| 관리자 로그인 | `src/DB/LoginDAO.java:164-195`, `src/DB/LoginDAO.java:198-203` | `admin` 테이블 조회 후 `UserBean` 역할을 `ADMIN`으로 설정한다. | 확인됨 |
| 관리자 화면 전환 | `src/main/MainFrame.java:47-54` | `UserSessionManager.isAdmin()`이면 요청 패널명과 무관하게 `mainAdmin`을 표시한다. 관리자 화면에서는 의도된 동작일 수 있으나 일반 화면 복귀·로그아웃 시 세션 초기화 검증이 필요하다. | 실행 확인 필요 |
| 공지사항 목록 | `src/DB/NoticeDAO.java:61-107` | `notice_time DESC`로 조회한다. 콘솔 출력이 과다하다. | 실행 확인 필요 |
| 공지사항 작성 | `src/panel/NoticeWritePanel.java:172-223`, `src/DB/NoticeDAO.java:24-59` | 저장 후 `getLastNoticeId()`로 최근 공지 ID를 조회한다. 동시 작성 또는 같은 시각 저장 시 잘못된 ID를 참조할 수 있다. | 실행 확인 필요 |
| 공지사항 수정 | `src/panel/NoticeEditPanel.java:152-174`, `src/DB/NoticeDAO.java:150-187` | `notice_num` 기준 수정 구조가 있다. 첨부파일 처리와 기존 파일 삭제 정책은 추가 검증 필요. | 실행 확인 필요 |
| 공지사항 삭제 | `src/DB/NoticeDAO.java:189-223` | `notice`만 삭제한다. `notice_files` 연계 삭제 또는 FK 정책이 기본 스키마에 없다. | 실행 확인 필요 |
| 첨부파일 | `src/panel/NoticeWritePanel.java:194-209`, `src/DB/NoticeDAO.java:264-305` | `notice_files` 조회 구조는 있으나 파일 저장 위치, 실제 파일 복사, notice 삭제 시 파일 정리 여부 확인 필요. | 실행 확인 필요 |
| 관리자 홈 버튼 | `src/ui_n_utils/HeaderUtil.java:72-77` | 관리자 헤더 타이틀 클릭 시 실제 화면 전환 없이 로그만 출력한다. | 실행 확인 필요 |

## 5. DB·DAO 불일치 목록

| 우선도 | 불일치 | 관련 위치 | 판단 근거 |
|---|---|---|---|
| 높음 | `bodyinfo` 테이블 누락 | `sql/schema.sql:7-83`, `src/DB/BodyInfoDAO.java:69`, `src/DB/BodyInfoDAO.java:110`, `src/DB/GoalDAO.java:220` | `schema.sql` 기준 기본 스키마에는 `admin`, `user`, `food`, `exercise`, `exercise_log`, `notice`, `notice_files`, `meal`, `meal_log`만 생성된다. DAO는 `bodyinfo`를 INSERT/SELECT한다. 실제 로컬 MySQL DB 존재 여부는 별도 실행 확인 필요. |
| 높음 | `target` 테이블 누락 | `sql/schema.sql:7-83`, `src/DB/GoalDAO.java:49-51`, `src/DB/GoalDAO.java:107` | `schema.sql` 기준 목표 저장·조회 DAO가 사용하는 `target` 테이블이 없다. `update_target_table.sql`과 기본 스키마의 관계 및 실제 로컬 MySQL DB 존재 여부는 별도 확인 필요. |
| 높음 | `exercise_log` 기본키 컬럼명 불일치 | `sql/schema.sql:40-47`, `src/DB/ExerciseLogDAO.java:31`, `src/DB/ExerciseLogDAO.java:44-46` | `schema.sql`은 `exercise_log_id`를 정의하지만 DAO는 `exercise_log_code`를 조회·삽입한다. 현재 `schema.sql`만 적용한 DB에서는 실패 가능성이 높으며, 실제 로컬 DB 구조는 실행 확인 필요. |
| 높음 | `exercise_log` 운동 기록 컬럼 누락 | `sql/schema.sql:40-47`, `src/DB/ExerciseLogDAO.java:44-46` | DAO는 `exercise_log_runtime`, `weight_input`, `exercise_kcal`을 INSERT하지만 `schema.sql`에는 없고 `exercise_calories`, `exercise_date`만 있다. 현재 `schema.sql`만 적용한 DB에서는 실패 가능성이 높으며, 실제 로컬 DB 구조는 실행 확인 필요. |
| 높음 | `notice_files` 컬럼 불일치 | `sql/schema.sql:57-63`, `src/DB/NoticeFileDAO.java:44-53`, `src/DB/NoticeFileDAO.java:145-152` | DAO는 `admin_id`, `file_data`, `upload_time` 컬럼을 INSERT/SELECT하지만 현재 `schema.sql`은 `file_path`, `uploaded_time`만 정의한다. 현재 `schema.sql`만 적용한 DB에서는 첨부파일 업로드·다운로드 실패 가능성이 높으며, 실제 로컬 DB 구조는 실행 확인 필요. |
| 중간 | 공지 첨부파일 참조 무결성 부족 | `sql/schema.sql:57-63`, `src/DB/NoticeDAO.java:197`, `src/DB/NoticeDAO.java:281` | `notice_files.notice_id`는 존재하지만 FK와 cascade 정책이 없다. 공지 삭제 시 첨부파일 레코드가 남을 수 있다. |
| 중간 | `getLastNoticeId()`의 최근 공지 조회 방식 | `src/DB/NoticeDAO.java:226-263`, `src/panel/NoticeWritePanel.java:185-188` | INSERT의 generated key를 받지 않고 `ORDER BY notice_time DESC LIMIT 1`로 조회한다. 동시성 또는 동일 시간 저장 시 정확성이 낮다. |
| 중간 | 사용자 목표와 홈 목표치 미연결 | `src/panel/HomeDailyPanel.java:22-26`, `src/DB/GoalDAO.java:104-138` | `GoalDAO`는 목표를 조회하지만 `HomeDailyPanel`은 고정 목표값을 사용한다. |

## 6. 화면 전환 및 세션 문제

| 문제 | 관련 위치 | 판단 근거 |
|---|---|---|
| 로그인 전 사용자·관리자 패널 선생성 | `src/main/MainFrame.java:27-40` | `MainFrame` 생성 시 로그인 화면뿐 아니라 `BodyInfoSetPanel`, `MainUserPanel`, `MainAdminPanel`까지 즉시 생성되어 CardLayout에 추가된다. 따라서 로그인 전 사용자 패널 생성자와 내부 DAO 초기화가 먼저 실행될 수 있다. |
| `MainUserPanel` 생성자의 하위 패널 선생성 | `src/main/MainUserPanel.java:45-87` | `HomeDailyPanel`, `HomeMealPanel`, `FoodListPanel`, `HomeTargetPanel`, `CalendarPanel`, 운동·마이페이지·공지 패널을 생성자에서 한 번에 만든다. 각 패널 생성자 내부 DAO 호출과 초기화 부작용 확인이 필요하다. |
| 로그인 전 `HomeDailyPanel` 영양소 조회 경로 | `src/main/MainUserPanel.java:61`, `src/panel/HomeDailyPanel.java:219-223`, `src/panel/HomeDailyPanel.java:252-263` | `MainUserPanel` 생성 중 `HomeDailyPanel`이 즉시 생성되고, 생성자 끝에서 `startUpdateTimer()`와 `updateNutritionData()`가 호출된다. 로그인 전이면 `LoginManager.getInstance().getUserId()`가 비어 조기 반환하지만, 경로 자체는 로그인 전에 실행된다. |
| 관리자 세션이면 모든 `showPanel` 요청이 관리자 메인으로 고정됨 | `src/main/MainFrame.java:47-54` | `showPanel(name)`이 `name`을 무시하고 `mainAdmin`을 표시한다. 관리자 로그인 후 일반 전환을 막는 의도일 수 있으나 로그아웃·재로그인 경로에서 세션 초기화 검증이 필요하다. |
| `BodyInfoSetPanel`을 인덱스로 찾음 | `src/main/MainFrame.java:56-60` | `mainPanel.getComponent(2)`에 의존한다. 패널 추가 순서가 바뀌면 잘못된 컴포넌트를 캐스팅할 수 있다. |
| 세션 관리자가 2종류로 보임 | `src/model/LoginManager.java`, `src/ui_n_utils/UserSessionManager.java`, `src/DB/LoginDAO.java:61-72`, `src/panel/HomeDailyPanel.java:254` | `LoginManager`는 `src/model` 패키지에, `UserSessionManager`는 `src/ui_n_utils` 패키지에 있으며 서로 다른 클래스가 세션 상태를 각각 관리한다. 로그인 DAO는 `UserSessionManager`를 사용하고 여러 사용자 패널은 `LoginManager`를 참조하므로 두 세션 소스의 동기화 여부를 실행으로 확인해야 한다. |
| `logout()`의 관리자 플래그 미초기화 | `src/ui_n_utils/UserSessionManager.java:56-58`, `src/ui_n_utils/UserSessionManager.java:60-65` | `logout()`은 `currentUser`만 `null`로 만들고 `isAdmin`을 false로 바꾸지 않는다. `clearSession()`은 플래그를 초기화하므로 실제 로그아웃 경로가 어느 메서드를 호출하는지 확인 필요하다. |
| 회원정보 수정 후 세션 갱신 경로가 분산됨 | `src/DB/UserDAO.java:28-74`, `src/DB/LoginDAO.java:87-133` | `UserDAO.updateUser`와 `LoginDAO.updateUserInfo`가 모두 회원정보 수정 역할을 한다. 실제 UI가 어느 DAO를 쓰는지 확인하고 하나의 세션 갱신 정책으로 맞춰야 한다. |
| 회원정보 수정 비밀번호 판단 불안정 가능성 | `src/panel/MyMemberPanel.java`, `src/ui_n_utils/UserSessionManager.java` | `MyMemberPanel`이 기존 비밀번호를 세션에서 참조하는 흐름이라면, `UserSessionManager`가 세션 저장 시 비밀번호를 `null` 처리할 수 있어 비밀번호 변경 여부 판단이 불안정할 가능성이 있다. 정적 분석만으로 오류를 확정하지 않고 실제 회원정보 수정 시나리오 실행 확인이 필요하다. |
| 신체정보 저장 실패 시 복구 흐름 제한 | `src/panel/BodyInfoSetPanel.java:120-125` | 저장 실패 메시지만 표시하고 스키마 누락 등 원인별 안내가 없다. |

## 7. 중복 호출 및 성능 문제

| 문제 | 관련 위치 | 판단 근거 |
|---|---|---|
| 식사 시간대별 칼로리 4회 개별 조회 | `src/panel/HomeMealPanel.java:365-373`, `src/DB/MealDAO.java:84-120` | 화면 표시 때 아침·점심·저녁·간식을 각각 쿼리한다. `GROUP BY meal_type` 단일 쿼리로 줄일 수 있다. |
| 일일 영양 패널 30초 주기 DB 조회 | `src/panel/HomeDailyPanel.java:219-234`, `src/panel/HomeDailyPanel.java:252-276` | 패널 표시 여부와 관계없이 타이머가 유지될 가능성이 있다. `removeNotify`에서 취소하지만 화면 전환 시 제거가 실제로 발생하는지 확인 필요. |
| `HomeDailyPanel` 반복 영양소 조회 | `src/panel/HomeDailyPanel.java:227-234`, `src/panel/HomeDailyPanel.java:260-263`, `src/DB/MealLogDAO.java:151-211` | 생성자에서 시작한 타이머가 30초마다 `updateNutritionData()`를 호출하고, 로그인 사용자가 있으면 `getTodayNutrition(userId)`로 DB 합산 조회를 반복한다. |
| `NoticePanel` 공지 목록 중복 조회 가능성 | `src/panel/NoticePanel.java:26-31`, `src/panel/NoticePanel.java:104-105`, `src/panel/NoticePanel.java:108-113` | 생성자에서 `loadNotices()`를 호출하고, `componentShown`에서도 다시 호출한다. 패널 생성 직후 표시 이벤트가 발생하면 목록 조회가 중복될 수 있다. |
| 공지 목록·상세 조회 디버그 로그 과다 | `src/DB/NoticeDAO.java:75`, `src/DB/NoticeDAO.java:85`, `src/DB/NoticeDAO.java:89`, `src/DB/NoticeDAO.java:127-132` | 사용자에게 보이지 않는 콘솔 출력이 많아 포트폴리오 실행 중 로그가 산만해진다. |
| 식단 저장 디버그 로그 과다 | `src/DB/MealLogDAO.java:41-60`, `src/DB/MealLogDAO.java:178-192` | 음식명, 코드, 섭취량, 영양소가 콘솔에 출력된다. 개인정보 또는 사용 행태 노출 가능성이 있다. |
| 이미지 절대 경로 사용 | `src/ui_n_utils/NavUtil.java:32-35`, `src/panel/HomeMealPanel.java:84-317`, `src/panel/FoodListPanel.java:125`, `src/panel/HomeTargetPanel.java:55`, `src/panel/ExerciseListPanel.java:66`, `src/panel/ExerciseSearchPanel.java:58` | Windows 개발자 PC 경로가 여러 화면과 유틸에 분산되어 macOS 또는 다른 PC에서 이미지 로딩 실패 가능성이 높다. |

## 8. 미완성 기능

| 기능 | 관련 위치 | 판단 근거 |
|---|---|---|
| 캘린더 DAO와 화면 | `src/DB/CalendarDAO.java:1-5`, `src/panel/CalendarPanel.java:6-17` | DAO 클래스가 비어 있고 화면도 빨간 배경과 “캘린더” 라벨 중심의 placeholder 상태다. 실제 DB 일정·식단 연동은 확인되지 않았다. |
| 목표 기반 일일 권장량 | `src/DB/GoalDAO.java:104-238`, `src/panel/HomeDailyPanel.java:22-26` | 목표 DAO는 존재하지만 홈 일일 현황은 고정값을 사용한다. |
| 운동 기록 저장 | `src/DB/ExerciseLogDAO.java:20-77`, `sql/schema.sql:40-47` | DAO SQL과 `schema.sql`의 `exercise_log` 정의가 불일치한다. 현재 `schema.sql`만 적용한 DB에서는 실패 가능성이 높지만, 실제 로컬 DB 구조 확인 전에는 저장 실패로 확정하지 않는다. |
| 신체정보 저장·조회 | `src/DB/BodyInfoDAO.java:53-120`, `sql/schema.sql:7-83` | `schema.sql` 기준 `bodyinfo` 테이블이 없어 저장·조회가 실패할 수 있다. 실제 로컬 DB 존재 여부는 실행 확인 필요. |
| 공지 첨부파일 생명주기 | `src/panel/NoticeWritePanel.java:194-209`, `src/DB/NoticeDAO.java:264-305` | 업로드, 수정, 삭제, 파일 실물 보관 정책의 일관성 확인이 필요하다. |
| 공지 수정 시 기존 첨부파일 삭제 | `src/panel/NoticeEditPanel.java:198-203`, `src/panel/NoticeEditPanel.java:240-245`, `src/panel/NoticeEditPanel.java:160-163` | 기존 파일 목록은 로드하지만 삭제 버튼은 UI 모델과 `attachedFiles`만 조작한다. 기존 첨부파일 DB 삭제 로직은 정적 분석상 확인되지 않아 실행 확인과 보완이 필요하다. |
| 운동 선택 정보 전달 | `src/main/MainUserPanel.java:142-149`, `src/panel/ExerciseCaloriePanel.java:20-23`, `src/panel/ExerciseCaloriePanel.java:208-210` | `exerciseCaloriePanel.updateExerciseInfo(exercise)` 호출이 주석 처리되어 있다. 운동 기록 저장 시 기본 MET 6.0과 기본 운동 코드 1이 사용될 가능성이 있다. |
| ID/PW 찾기 | `src/panel/FindIdPwPhone.java:145-155`, `src/panel/FindIdPwEmail.java:127-137` | 버튼 이벤트가 출력 중심이며 사용자 DB 조회, 인증번호 발급·검증, 비밀번호 재설정 로직이 확인되지 않았다. |
| 물 섭취 | `src/panel/HomeMealPanel.java:302-326` | UI 표시만 확인되며 저장·증감·조회 로직은 정적 분석상 확인되지 않았다. |

## 9. 포트폴리오 제출 전 필수 수정 항목

| 항목 | 관련 위치 | 이유 |
|---|---|---|
| 실제 DB·스키마·DAO 3자 대조 | `sql/schema.sql`, `src/DB/BodyInfoDAO.java`, `src/DB/GoalDAO.java`, `src/DB/ExerciseLogDAO.java`, `src/DB/NoticeFileDAO.java` | 즉시 `schema.sql`을 고치기보다 먼저 실제 로컬 MySQL의 테이블·컬럼 구조를 조회하고, 실제 DB와 `sql/schema.sql`, DAO SQL을 3자 대조한 뒤 DAO 수정 또는 `schema.sql` 수정 방향을 결정해야 한다. |
| `bodyinfo`, `target`, `exercise_log`, `notice_files` 정리 | `sql/schema.sql:40-63`, `src/DB/BodyInfoDAO.java:69`, `src/DB/GoalDAO.java:49-51`, `src/DB/ExerciseLogDAO.java:44-46`, `src/DB/NoticeFileDAO.java:44-53` | 네 항목 모두 실제 DB, `schema.sql`, DAO SQL 3자 대조 기준을 적용한다. 현재 `schema.sql`만 적용한 DB에서는 실패 가능성이 높지만, 실제 로컬 DB 구조 확인 전에는 실패를 확정하지 않는다. |
| 세션 소스 통일 또는 동기화 검증 | `src/model/LoginManager.java`, `src/ui_n_utils/UserSessionManager.java` | 로그인 후 사용자별 데이터 조회가 흔들릴 수 있다. |
| Windows 절대 이미지 경로 제거 | `src/ui_n_utils/NavUtil.java:32-35`, `src/panel/HomeMealPanel.java:84-317`, `src/panel/FoodListPanel.java:125`, `src/panel/HomeTargetPanel.java:55`, `src/panel/ExerciseListPanel.java:66`, `src/panel/ExerciseSearchPanel.java:58` | macOS 복구 프로젝트에서 이미지가 보이지 않을 가능성이 높다. |
| 공지 저장 ID 획득 방식 개선 | `src/DB/NoticeDAO.java:226-263`, `src/panel/NoticeWritePanel.java:185-188` | 포트폴리오 시연 중 첨부파일이 다른 공지에 연결될 수 있다. |
| 로그아웃 관리자 플래그 초기화 확인 | `src/ui_n_utils/UserSessionManager.java:56-65`, `src/main/MainFrame.java:47-54` | 관리자 플래그가 남으면 `showPanel()`이 요청 화면명을 무시하고 관리자 화면으로 고정될 수 있다. |
| 콘솔 디버그 로그 정리 | `src/DB/NoticeDAO.java`, `src/DB/MealLogDAO.java`, `src/panel/FoodListPanel.java` | 시연 환경에서 예외와 정상 로그 구분이 어려워진다. |

## 10. 나중에 수정해도 되는 항목

| 항목 | 관련 위치 | 이유 |
|---|---|---|
| DAO 중복 역할 정리 | `src/DB/UserDAO.java`, `src/DB/LoginDAO.java` | 당장 실행 오류가 없다면 제출 후 구조 개선으로 미룰 수 있다. |
| `MainFrame.moveToBodyInfoSet` 인덱스 의존 제거 | `src/main/MainFrame.java:56-60` | 현재 패널 순서를 유지하면 즉시 장애는 아닐 수 있으나 유지보수 위험이다. |
| 식사 칼로리 조회 단일 쿼리화 | `src/panel/HomeMealPanel.java:365-373` | 성능 개선 항목이며 데이터량이 적은 포트폴리오 시연에서는 치명도가 낮다. |
| 목표 칼로리 계산 고도화 | `src/panel/HomeDailyPanel.java:22-26`, `src/DB/GoalDAO.java:140-238` | 기능 완성도 개선 항목이다. |
| 캘린더 기능 고도화 | `src/DB/CalendarDAO.java:1-5`, `src/panel/CalendarPanel.java:6-17` | 캘린더를 핵심 시연 기능으로 잡지 않는다면 후순위 가능하다. |
| UI 절대 좌표 개선 | `src/panel/*.java` | 전체 리팩터링 범위가 커서 제출 전 필수 오류 수정 이후에 다루는 것이 적절하다. |

## 11. 수정 우선순위 1~10

1. 실제 로컬 MySQL의 `bodyinfo`, `target`, `exercise_log`, `notice_files` 테이블과 컬럼 구조를 먼저 조회한다.
2. 실제 DB 구조, `sql/schema.sql`, DAO SQL을 3자 대조한다.
3. 대조 결과를 근거로 DAO 수정 또는 `schema.sql` 수정 방향을 결정한다. 현재 `schema.sql`만 적용한 DB에서는 실패 가능성이 높지만 실제 로컬 DB 구조 확인 전에는 저장 실패를 확정하지 않는다.
4. 세션 소스(`LoginManager`, `UserSessionManager`)와 `logout()` 관리자 플래그 초기화를 점검해 로그인 사용자 ID와 관리자 상태가 모든 패널에서 일관되는지 확인한다.
5. 로그인 전 패널 선생성과 `HomeDailyPanel` 초기 DB 조회·타이머 경로를 정리한다.
6. 공지 작성 후 ID 조회를 generated key 기반으로 수정한다.
7. 공지 첨부파일 스키마, 업로드, 다운로드, 수정 시 기존 파일 삭제 흐름을 정리한다.
8. Windows 절대 이미지 경로를 프로젝트 상대 경로 또는 classpath 리소스 로딩으로 바꾼다.
9. 운동 선택 정보 전달과 식단 기록 후 홈 일일 현황·식사별 칼로리 카드 갱신을 확인한다.
10. 캘린더, ID/PW 찾기, 물 섭취, 목표 기반 권장량, 콘솔 디버그 로그 등 미완성·정리 항목의 시연 포함 여부를 결정한다.

## 12. 실제 실행 확인이 필요한 항목

| 확인 항목 | 확인 방법 | 정적 분석상 예상 리스크 |
|---|---|---|
| 신체정보 확인 버튼 저장 | 일반 사용자 회원가입 후 신체정보 입력 저장 | `bodyinfo`는 `schema.sql` 기준 누락이다. 실제 DB 존재 여부와 저장 성공은 실행 확인 필요 |
| 목표 저장·조회 | 목표 설정 화면에서 저장 후 재진입 | `target`은 `schema.sql` 기준 누락이다. 실제 DB 존재 여부와 보조 SQL 적용 여부는 실행 확인 필요 |
| 운동 기록 저장 | 운동 선택 후 기록 저장 | `exercise_log`는 `schema.sql`과 DAO SQL이 불일치한다. 현재 `schema.sql`만 적용한 DB에서는 실패 가능성이 높으며, 실제 로컬 DB 구조는 실행 확인 필요 |
| 운동 선택 정보 반영 | 운동 목록에서 특정 운동 선택 후 기록 화면 진입 | MET 또는 운동 코드가 기본값으로 저장되는지 확인 필요 |
| 식단 기록 저장 | 음식 추가 후 식사 시간대별 저장 | 저장은 가능해 보이나 콘솔 로그·화면 갱신 확인 필요 |
| 로그인 전 초기화 부작용 | 앱 실행 직후 로그인 전 콘솔과 DB 호출 확인 | `MainFrame`과 `MainUserPanel` 선생성, `HomeDailyPanel` 초기 조회·타이머 경로 확인 필요 |
| 홈 일일 현황 갱신 | 식단 저장 후 `HomeDailyPanel` 확인 | 세션 소스 불일치 시 사용자 ID 없음 |
| 식사별 칼로리 카드 갱신 | 아침·점심·저녁·간식 저장 후 `HomeMealPanel` 확인 | 4회 쿼리 결과와 UI 갱신 타이밍 확인 필요 |
| 공지 목록 조회 횟수 | 사용자 공지 화면 최초 진입과 재진입 시 콘솔·DB 호출 확인 | 생성자 호출과 `componentShown` 호출로 중복 조회 가능 |
| 공지 작성과 첨부파일 | 관리자 로그인 후 공지 작성, 파일 첨부, 목록·상세 확인 | `getLastNoticeId()` 방식과 `notice_files` 컬럼 불일치가 있다. 현재 `schema.sql`만 적용한 DB에서는 첨부파일 저장 오류 가능성이 높으며, 실제 로컬 DB 구조는 실행 확인 필요 |
| 공지 수정·삭제 | 관리자 공지 수정·삭제 후 목록과 상세 재확인 | 기존 첨부파일 DB 삭제 불완전, 첨부파일 잔존, 목록 갱신 누락 가능 |
| 로그아웃·재로그인 | 관리자 로그인 후 일반 사용자 로그인, 반대 순서도 확인 | `showPanel` 관리자 세션 고정과 세션 초기화 문제 가능 |
| 관리자 홈 버튼 | 관리자 헤더 타이틀 클릭 | 실제 화면 이동 없이 로그만 출력되는지 확인 필요 |
| ID/PW 찾기 | 전화번호·이메일 탭에서 인증 전송과 찾기 버튼 실행 | 실제 DB 조회·인증·재설정 없이 출력만 수행하는지 확인 필요 |
| 캘린더 화면 | 캘린더 네비게이션 진입 | placeholder 화면인지, 실제 DB 연동이 없는지 확인 필요 |
| macOS 이미지 표시 | 홈 식단, 하단 네비게이션, 검색, 목표, 운동 화면 이미지 표시 확인 | 여러 화면의 Windows 절대 경로 때문에 이미지 누락 가능 |
