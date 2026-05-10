# DB 설정 방법

이 프로젝트는 MySQL 데이터베이스를 사용하는 Java Swing 프로젝트입니다.  
DB 접속 정보는 `src/db.properties` 파일을 통해 관리합니다.

실제 DB 비밀번호가 포함되는 `src/db.properties` 파일은 보안상 GitHub에 업로드하지 않습니다.  
대신 `src/db.properties.example` 파일을 참고하여 로컬 환경에서 직접 설정 파일을 생성해야 합니다.

---

## 1. MySQL 데이터베이스 생성

MySQL에 접속한 뒤 프로젝트에 포함된 SQL 파일을 실행합니다.

```bash
/usr/local/mysql/bin/mysql -u root -p
```

MySQL 접속 후 아래 명령어로 스키마 파일을 실행합니다.

```sql
SOURCE /Users/사용자명/Downloads/project8/sql/schema.sql;
```

`schema.sql` 실행 시 다음 항목이 생성됩니다.

- `almja` 데이터베이스
- `admin` 테이블
- `user` 테이블
- `food` 테이블
- `exercise` 테이블
- `exercise_log` 테이블
- `notice` 테이블
- `notice_files` 테이블
- `meal` 테이블
- `meal_log` 테이블
- 테스트 관리자 계정
- 기본 음식, 운동, 공지사항 테스트 데이터

---

## 2. db.properties 파일 생성

`src/db.properties.example` 파일을 복사하여 `src/db.properties` 파일을 생성합니다.

파일 위치:

```text
src/db.properties
```

예시:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/almja?characterEncoding=UTF-8&serverTimezone=UTC
db.user=root
db.password=your_password_here
```

`db.password`에는 본인의 MySQL root 비밀번호를 입력합니다.

예시:

```properties
db.password=your_mysql_password
```

---

## 3. db.properties.example 파일

GitHub에는 실제 비밀번호가 없는 예시 파일만 포함합니다.

```text
src/db.properties.example
```

예시 파일 내용:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/almja?characterEncoding=UTF-8&serverTimezone=UTC
db.user=root
db.password=your_password_here
```

---

## 4. 보안 주의사항

`src/db.properties` 파일에는 실제 DB 비밀번호가 들어갑니다.  
따라서 이 파일은 `.gitignore`에 등록되어 있으며 GitHub에 업로드하지 않습니다.

현재 `.gitignore`에는 아래 설정이 포함되어 있습니다.

```gitignore
src/db.properties
```

---

## 5. 테스트 관리자 계정

`schema.sql` 실행 시 테스트용 관리자 계정이 생성됩니다.

```text
ID: admin
PW: admin1234
```

이 계정은 로컬 실행 확인용입니다.  
실제 서비스 환경에서는 기본 관리자 계정을 그대로 사용하면 안 됩니다.

---

## 6. 참고

현재 프로젝트는 Eclipse 기반 Java 프로젝트로 복구된 상태입니다.  
DB 설정은 `src/db.properties` 파일 기준으로 동작하도록 정리되어 있습니다.