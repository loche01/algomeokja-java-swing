# 알고먹자 - Java Swing 식단 관리 프로그램

## 프로젝트 소개

알고먹자는 Java Swing 기반의 데스크톱 식단 및 건강 관리 프로그램입니다.  
사용자는 회원가입과 로그인을 통해 식단, 음식 정보, 운동 정보, 공지사항 등을 확인할 수 있으며, 관리자는 공지사항을 관리할 수 있습니다.

이 프로젝트는 기존 Eclipse 기반 Java 프로젝트를 macOS 환경에서 복구하고, MySQL 연동 및 실행 오류를 수정하여 포트폴리오용으로 정리한 프로젝트입니다.

## 주요 기능

### 사용자 기능

- 회원가입
- 로그인
- 신체정보 입력 또는 Skip
- 음식 목록 조회
- 음식 검색
- 운동 카테고리별 조회
- 일일 영양소 정보 조회
- 공지사항 조회

### 관리자 기능

- 관리자 로그인
- 공지사항 조회
- 공지사항 작성, 수정, 삭제 기능 기반 구조

## 기술 스택

- Java
- Java Swing
- Eclipse IDE
- MySQL 8.0
- MySQL Connector/J
- Git
- GitHub

## 개발 및 실행 환경

| 항목 | 내용 |
|---|---|
| OS | macOS |
| JDK | Eclipse Temurin JDK 21 |
| IDE | Eclipse IDE for Java Developers |
| DB | MySQL Community Server 8.0 |
| JDBC Driver | MySQL Connector/J 9.7.0 |
| Build 방식 | Eclipse Java Project |

## 프로젝트 실행 방법

이 프로젝트는 Eclipse 기반 Java Swing 프로젝트입니다.  
실행을 위해서는 JDK, MySQL Server, MySQL Connector/J 설정이 필요합니다.

### 1. JDK 설치 확인

JDK 21 이상을 설치한 뒤 터미널에서 아래 명령어로 확인합니다.

```bash
java -version
javac -version
```

### 2. MySQL Server 설치 및 실행 확인

MySQL Community Server 8.0 이상을 설치합니다.

macOS에서 MySQL 공식 설치 파일을 사용한 경우, 터미널에서 아래 명령어로 접속할 수 있습니다.

```bash
/usr/local/mysql/bin/mysql -u root -p
```

비밀번호 입력 후 아래처럼 표시되면 정상 접속입니다.

```text
mysql>
```

### 3. 데이터베이스 및 테이블 생성

MySQL에 접속한 뒤, 프로젝트에 포함된 `sql/schema.sql` 파일을 실행합니다.

예시:

```sql
SOURCE /Users/사용자명/Downloads/project8/sql/schema.sql;
```

또는 `schema.sql` 파일 내용을 MySQL 콘솔에 직접 붙여넣어 실행할 수 있습니다.

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
- 기본 음식/운동/공지사항 테스트 데이터

### 4. DB 설정 파일 생성

`src/db.properties.example` 파일을 복사하여 `src/db.properties` 파일을 생성합니다.

예시:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/almja?characterEncoding=UTF-8&serverTimezone=UTC
db.user=root
db.password=your_password_here
```

`db.password`에는 본인의 MySQL root 비밀번호를 입력합니다.

보안상 `src/db.properties`는 `.gitignore`에 포함되어 있으며 GitHub에 업로드하지 않습니다.

### 5. Eclipse에서 프로젝트 Import

Eclipse에서 아래 순서로 프로젝트를 가져옵니다.

1. `File`
2. `Import`
3. `General`
4. `Existing Projects into Workspace`
5. 프로젝트 루트 폴더 선택
6. `Finish`

### 6. MySQL Connector/J 설정

프로젝트에 MySQL Connector/J 라이브러리가 필요합니다.

Eclipse에서 아래 순서로 `.jar` 파일을 추가합니다.

1. 프로젝트 우클릭
2. `Properties`
3. `Java Build Path`
4. `Libraries`
5. `Classpath` 선택
6. `Add External JARs...`
7. `mysql-connector-j-x.x.x.jar` 선택
8. `Apply and Close`

현재 복구 환경에서는 `mysql-connector-j-9.7.0.jar`를 사용했습니다.

### 7. 프로젝트 실행

Eclipse에서 아래 파일을 실행합니다.

```text
src/main/MainFrame.java
```

실행 방법:

1. `MainFrame.java` 우클릭
2. `Run As`
3. `Java Application`

실행에 성공하면 `알고먹자` 로그인 화면이 표시됩니다.