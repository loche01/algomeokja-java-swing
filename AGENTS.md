# AGENTS.md

이 프로젝트는 기존 Eclipse 기반 Java Swing 식단 관리 프로그램이며, macOS에서 복구하여 포트폴리오용으로 정리 중이다.

## 프로젝트 환경

* Java Swing 기반 기존 프로젝트 구조 유지
* Eclipse Java Project 구조 유지
* Eclipse Temurin JDK 21 사용
* MySQL 스키마 `almja` 사용
* 현재 작업 브랜치는 `codex-audit`

## 작업 원칙

* 한 번의 요청에는 하나의 명확한 목표만 처리
* 먼저 원인을 분석하고, 사용자 승인 후 수정
* 관련 없는 파일 수정 금지
* 요청하지 않은 대규모 리팩터링 금지
* Maven, Gradle, Spring, Spring Boot로 임의 변환 금지
* 기존 UI, 패널 이름, 화면 전환 구조 최대한 보존
* 실행하지 못한 기능을 정상이라고 보고하지 않기
* 정적 분석과 실제 실행 검증 결과를 구분하기

## Git 규칙

* `main`, `recovery` 브랜치를 직접 수정하지 않기
* 별도 작업 브랜치에서만 수정
* 수정 전후 `git status`와 `git diff` 확인
* 의도하지 않은 줄바꿈 전체 변경 금지
* 문제 하나를 해결하고 검증한 뒤 커밋
* Git 커밋 메시지는 한국어로 작성
* 사용자 승인 없이 commit 또는 push 금지

## 보안 규칙

* `src/db.properties`를 읽어서 출력하거나 변경하지 않기
* DB 비밀번호, GitHub 토큰, 개인정보 출력 금지
* `src/db.properties`를 Git 추적 대상으로 추가하지 않기
* API 키나 토큰을 코드, 문서, 로그에 기록하지 않기

## 기본 분석 제외 대상

* `backup/`
* `bin/`
* `out/`
* `.git/`
* `.DS_Store`
* `src/db.properties`
* `.class`, 로그, 임시 파일

## 문제 해결 우선순위

1. 컴파일 오류
2. DB 연결 실패
3. DAO와 DB 스키마 불일치
4. 로그인 및 세션 문제
5. 화면 전환 문제
6. 패널 및 UI 초기화 문제
7. 중복 DB 호출
8. 미완성 핵심 기능
9. 디버그 로그
10. 경고 및 코드 스타일

## 수정 후 검증

* `git diff --check`
* 변경 파일 목록 확인
* Eclipse Problems에서 컴파일 오류 확인
* 수정 기능 실제 실행
* 콘솔 예외 확인
* `git status` 확인

## 현재 이미 확인된 사항

* macOS 실행 복구 완료
* MySQL 연결 및 주요 테이블 생성 완료
* 관리자 로그인 확인 완료
* 일반 사용자 회원가입 및 로그인 확인 완료
* 신체정보 Skip 화면 전환 오류 수정 완료
* README, DB 설정 문서, 화면 캡처, 트러블슈팅 문서 작성 완료
* 일부 DAO 디버그 로그 정리 완료
* 전체 정적 분석에서 DB 스키마 불일치와 초기화 문제가 발견됨
