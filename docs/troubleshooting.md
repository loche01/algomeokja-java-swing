# 알고먹자 프로젝트 트러블슈팅 기록

이 문서는 기존 Eclipse 기반 Java Swing 프로젝트를 macOS 환경에서 복구하면서 발생했던 주요 문제와 해결 과정을 정리한 문서입니다.

프로젝트 복구 과정에서는 단순히 코드를 실행하는 것뿐만 아니라, 개발 환경 차이, 외부 라이브러리 경로, MySQL 데이터베이스 구조, 파일 권한, 민감정보 관리 문제를 함께 해결했습니다.

---

## 1. MySQL Connector/J 경로 오류

### 문제 상황

기존 프로젝트는 Windows 환경에서 작성된 Eclipse Java Project였습니다.

프로젝트를 macOS의 Eclipse에서 가져온 뒤 Build Path를 확인했을 때, MySQL Connector/J 라이브러리 경로가 깨져 있었습니다.

기존 경로는 Windows 기준으로 설정되어 있었습니다.

```text
C:/java/mysql-connector-j-8.0.32.jar
```

macOS에는 `C:/java` 경로가 존재하지 않기 때문에 Eclipse에서 해당 라이브러리를 찾지 못했고, 프로젝트에 Build Path 오류가 발생했습니다.

### 원인

운영체제가 바뀌면서 외부 JAR 파일의 절대 경로가 더 이상 유효하지 않았습니다.

즉, 코드 자체의 문제라기보다는 프로젝트 설정에 저장된 외부 라이브러리 경로 문제였습니다.

### 해결 방법

기존에 깨져 있던 MySQL Connector/J 항목을 Java Build Path에서 제거했습니다.

그 후 macOS 환경에서 사용할 MySQL Connector/J를 새로 다운로드하고, Eclipse의 Classpath에 다시 추가했습니다.

```text
Project Properties
→ Java Build Path
→ Libraries
→ 기존 missing JAR 제거
→ Classpath 선택
→ Add External JARs...
→ mysql-connector-j-9.7.0.jar 추가
```

### 결과

MySQL JDBC Driver가 정상적으로 인식되었고, Java 코드에서 MySQL 데이터베이스에 연결할 수 있는 환경이 복구되었습니다.

---

## 2. 프로젝트 폴더 read-only 권한 오류

### 문제 상황

Eclipse에서 프로젝트를 빌드할 때 다음과 같은 오류가 발생했습니다.

```text
Parent of resource: /Users/macbook/Downloads/project8/bin/db.properties is marked as read-only.
```

또는 `bin` 폴더가 read-only 상태라서 Eclipse가 컴파일 결과물을 생성하지 못하는 문제가 발생했습니다.

### 원인

프로젝트 폴더 또는 하위 폴더에 현재 사용자 쓰기 권한이 없었습니다.

Eclipse는 Java 파일을 컴파일하면서 `bin` 폴더에 `.class` 파일과 리소스 파일을 생성해야 하는데, 폴더가 read-only 상태라 빌드가 실패했습니다.

### 해결 방법

터미널에서 프로젝트 폴더에 사용자 쓰기 권한을 부여했습니다.

```bash
chmod -R u+w ~/Downloads/project8
```

이후 Eclipse에서 프로젝트를 Refresh하고 Clean/Rebuild를 실행했습니다.

```text
Project
→ Clean...
→ project8 선택
→ Clean
```

### 결과

Eclipse가 `bin` 폴더에 컴파일 결과물을 정상적으로 생성할 수 있게 되었고, 프로젝트의 컴파일 오류가 사라졌습니다.

---

## 3. MySQL 데이터베이스 및 테이블 누락 오류

### 문제 상황

프로젝트 실행 후 로그인 또는 주요 화면 진입 시 다음과 같은 오류가 발생했습니다.

```text
Table 'almja.food' doesn't exist
Table 'almja.exercise' doesn't exist
Table 'almja.notice' doesn't exist
```

또한 처음에는 `almja` 데이터베이스 자체도 존재하지 않았습니다.

### 원인

Java 코드에서는 MySQL의 `almja` 데이터베이스와 여러 테이블을 사용하고 있었지만, 로컬 MySQL에는 해당 데이터베이스와 테이블이 생성되어 있지 않았습니다.

기존 프로젝트에 완성된 DB 덤프 파일이 없었기 때문에, DAO 코드에서 사용하는 SQL과 컬럼명을 기준으로 DB 구조를 복구해야 했습니다.

### 해결 방법

MySQL에 접속한 뒤 `almja` 데이터베이스를 생성했습니다.

```sql
CREATE DATABASE almja
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_general_ci;
```

이후 DAO 코드에서 사용하는 SQL을 분석하여 필요한 테이블을 생성했습니다.

생성한 주요 테이블은 다음과 같습니다.

```text
admin
user
food
exercise
exercise_log
notice
notice_files
meal
meal_log
```

예를 들어 `FoodDAO.java`에서 사용하는 컬럼을 기준으로 `food` 테이블을 생성했습니다.

```sql
CREATE TABLE food (
    food_code INT PRIMARY KEY AUTO_INCREMENT,
    food_name VARCHAR(100) NOT NULL,
    food_kcal DOUBLE DEFAULT 0,
    carb DOUBLE DEFAULT 0,
    protein DOUBLE DEFAULT 0,
    fat DOUBLE DEFAULT 0
);
```

또한 테스트 실행을 위해 기본 음식 데이터, 운동 데이터, 테스트 공지사항, 관리자 계정을 추가했습니다.

### 결과

관리자 로그인, 사용자 회원가입, 음식 조회, 운동 조회, 공지사항 조회 화면이 정상적으로 동작하는 것을 확인했습니다.

DB 구조는 이후 재사용할 수 있도록 `sql/schema.sql` 파일로 정리했습니다.

---

## 4. 신체정보 Skip 버튼 화면 전환 오류

### 문제 상황

일반 사용자가 회원가입을 완료한 뒤 신체정보 입력 화면으로 이동했습니다.

이때 신체정보 입력을 원하지 않아 `Skip` 버튼을 누르면 사용자 메인 화면으로 이동해야 했지만, 실제로는 로그인 화면으로 돌아가는 문제가 있었습니다.

### 원인

`BodyInfoSetPanel.java`에서 Skip 버튼 클릭 시 이동할 화면 이름이 잘못 지정되어 있었습니다.

기존 코드는 다음과 같았습니다.

```java
mainFrame.showPanel("login");
```

주석상 의도는 메인 화면 이동이었지만, 실제 코드는 로그인 패널로 이동하도록 되어 있었습니다.

`MainFrame.java`를 확인한 결과 사용자 메인 화면의 패널 이름은 다음과 같았습니다.

```java
mainPanel.add(mainUserPanel, "mainUser");
```

따라서 Skip 버튼은 `"login"`이 아니라 `"mainUser"`로 이동해야 했습니다.

### 해결 방법

`BodyInfoSetPanel.java`에서 Skip 버튼 클릭 시 화면 전환 코드를 수정했습니다.

수정 전:

```java
mainFrame.showPanel("login");
```

수정 후:

```java
mainFrame.showPanel("mainUser");
```

### 결과

회원가입 후 신체정보 입력 화면에서 `Skip` 버튼을 눌렀을 때 로그인 화면으로 돌아가지 않고 사용자 메인 화면으로 정상 이동하는 것을 확인했습니다.

---

## 5. DB 접속 정보 민감정보 분리

### 문제 상황

프로젝트에는 MySQL 접속 정보를 담은 설정 파일이 있었습니다.

```text
src/db.properties
```

이 파일에는 로컬 MySQL 계정과 비밀번호가 포함되어 있었습니다.

```properties
db.user=root
db.password=실제비밀번호
```

이 파일을 그대로 GitHub에 올리면 DB 비밀번호가 노출될 위험이 있었습니다.

### 원인

실행에 필요한 설정 파일과 GitHub에 공개해도 되는 예시 파일이 분리되어 있지 않았습니다.

### 해결 방법

실제 DB 접속 정보가 들어 있는 `src/db.properties`는 `.gitignore`에 추가하여 Git 추적 대상에서 제외했습니다.

```gitignore
src/db.properties
```

대신 예시 파일을 생성했습니다.

```text
src/db.properties.example
```

예시 파일에는 실제 비밀번호 대신 안내용 값을 넣었습니다.

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/almja?characterEncoding=UTF-8&serverTimezone=UTC
db.user=root
db.password=your_password_here
```

### 결과

실제 DB 비밀번호는 GitHub에 올라가지 않도록 보호했고, 다른 사용자는 `db.properties.example`을 참고하여 로컬 환경에 맞는 `db.properties` 파일을 생성할 수 있게 되었습니다.

---

## 6. GitHub Personal Access Token 노출 대응

### 문제 상황

GitHub에 push하는 과정에서 Personal Access Token을 터미널 인증에 사용했습니다.

진행 중 토큰이 채팅에 노출되는 상황이 발생했습니다.

### 원인

GitHub는 터미널에서 push할 때 계정 비밀번호 대신 Personal Access Token을 사용합니다.

토큰은 비밀번호와 비슷한 권한을 가지므로 외부에 노출되면 위험합니다.

### 해결 방법

노출된 Personal Access Token은 즉시 GitHub Settings에서 폐기했습니다.

이후 새 토큰을 발급받아 터미널에만 입력하여 push를 진행했습니다.

### 결과

노출된 토큰으로는 더 이상 GitHub 접근이 불가능하도록 조치했고, main 브랜치와 recovery 브랜치 push를 정상 완료했습니다.

---

## 정리

이번 복구 과정에서 해결한 핵심 문제는 다음과 같습니다.

```text
1. Windows 기준 MySQL Connector/J 경로 문제 해결
2. macOS 프로젝트 폴더 read-only 권한 문제 해결
3. DAO 코드 분석을 통한 누락 DB 테이블 복구
4. 회원가입 후 신체정보 Skip 화면 전환 오류 수정
5. DB 비밀번호 파일 GitHub 업로드 방지
6. GitHub Personal Access Token 노출 후 즉시 폐기
```

이를 통해 기존 Java Swing 프로젝트를 macOS 환경에서 실행 가능한 상태로 복구했고, GitHub와 README 문서를 통해 포트폴리오용 프로젝트로 정리했습니다.