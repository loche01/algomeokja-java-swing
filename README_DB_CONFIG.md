# DB 설정 방법

이 프로젝트는 더 이상 DB 접속 정보를 자바 소스코드에 하드코딩하지 않습니다.

## 1) `src/db.properties` 수정
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/almja?characterEncoding=UTF-8&serverTimezone=UTC
db.user=root
db.password=비밀번호
```

## 2) 환경변수로 설정
환경변수가 있으면 `db.properties`보다 우선합니다.
- `DB_DRIVER`
- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

## 3) JVM 옵션으로 설정 파일 지정
```bash
-Ddb.config=/path/to/db.properties
```

## 우선순위
환경변수 > `-Ddb.config`로 지정한 파일 > 클래스패스의 `db.properties` > 기본값
