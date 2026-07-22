# DB 설정 방법

알고먹자는 MySQL과 JDBC를 사용하는 Eclipse Java Project입니다. 공개 저장소에는 실제 접속 정보와 관리자 계정을 포함하지 않으므로 로컬 환경에서 아래 항목을 준비해야 합니다.

## 1. 필요 환경

- MySQL 8.0
- MySQL Connector/J
- Eclipse Temurin JDK 21
- Eclipse IDE for Java Developers

## 2. Connector/J 배치

MySQL Connector/J를 내려받아 JAR 파일을 다음 이름으로 배치합니다.

```text
lib/mysql-connector-j.jar
```

프로젝트의 `.classpath`는 이 상대 경로를 참조합니다. JAR 파일은 저장소에 포함하지 않으며 `.gitignore`로 추적에서 제외합니다.

## 3. 신규 개발용 스키마 구성

MySQL 클라이언트에서 저장소의 `sql/schema.sql`을 실행합니다.

```sql
SOURCE /path/to/algomeokja/sql/schema.sql;
```

스키마는 `almja` 데이터베이스와 다음 테이블을 구성합니다.

- `admin`, `user`
- `bodyinfo`, `target`
- `food`, `meal`, `meal_log`
- `exercise`, `exercise_log`
- `notice`, `notice_files`

음식과 운동의 기본 조회 데이터는 포함하지만 관리자 계정과 비밀번호 seed는 포함하지 않습니다.

기존 데이터베이스에 적용할 때는 먼저 현재 테이블과 데이터를 백업하고 차이를 확인해야 합니다. 이 저장소는 기존 데이터 마이그레이션 스크립트를 제공하지 않습니다.

## 4. 로컬 접속 설정

`src/db.properties.example`을 복사하여 `src/db.properties`를 만듭니다.

```text
src/db.properties.example
→ src/db.properties
```

예시 파일의 각 값을 로컬 MySQL 환경에 맞게 변경합니다.

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/almja?characterEncoding=UTF-8&serverTimezone=UTC
db.user=root
db.password=your_password_here
```

`src/db.properties`는 실제 비밀번호가 포함될 수 있으므로 Git 추적 대상에서 제외되어 있습니다.

## 5. 관리자 계정

관리자 기능은 `admin` 테이블의 계정을 사용합니다. 공개 스키마는 고정 관리자 계정을 만들지 않으므로 로컬 환경에서 별도로 준비해야 합니다.

`admin_pwd`에는 평문이 아니라 애플리케이션의 PBKDF2 인코딩 형식을 사용해야 합니다. 관리자 ID, 비밀번호 또는 인코딩 값을 코드·SQL·문서에 커밋하지 마세요.

현재 애플리케이션에는 관리자 계정 생성·비밀번호 재설정 UI가 없습니다.

## 6. Eclipse 실행

1. 프로젝트를 `Existing Projects into Workspace`로 가져옵니다.
2. JRE System Library가 Java 21인지 확인합니다.
3. `lib/mysql-connector-j.jar`가 Build Path에서 인식되는지 확인합니다.
4. `src/main/MainFrame.java`를 Java Application으로 실행합니다.

스키마 구조와 DAO SQL의 정적 대응 관계는 [데이터베이스 스키마와 DAO 매핑](docs/database-schema.md)을 참고하세요.
