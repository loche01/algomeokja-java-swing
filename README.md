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

### 1. JDK 설치

JDK 21 이상을 설치합니다.

```bash
java -version
javac -version