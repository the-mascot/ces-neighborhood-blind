## ces-neighborhood-blind
- __The-Mascot의 개발 연습용 익명 게시판 프로젝트__
- 같은 컨셉의 익명 게시판 프로젝트를 기술 스택 별로 여러개 생성중입니다.
- ERD는 resource 밑에 neighborhood_ver.damx 파일 참조, ERD는 DA#5 를 이용하여 작성했습니다.
- **resource/sql/neighborhood_ddl.sql 에 neighborhoodDb 생성 쿼리로 DB를 생성**합니다.
- AWS S3 파일 업로드 기능은 application.yml에 s3 buket, secret key를 넣어야합니다.
- Oauth2 로그인 기능은 application.yml에 네이버와 google client-id와 secret key를 넣어야합니다.
- JPA 연습을 위해 QeuryDSL과 PSQL을 혼용해서 사용했습니다.
- test 파일에 여러 예제 코드를 넣었습니다. **JpaTestWithH2를 사용하면 H2 DB로 실행되어 별도의 PostgreSQL 설치가 필요없습니다.**

## 연관 프로젝트
- bes-neighborhood-blind: thymeleaf + Spring Boot 2.6.12 + Mabatis 백엔드 프로젝트
- bes-neighborhood-vue: vue.js 프론트 프로젝트
- **ces-neighborhood-blind**: Spring Boot 3.1.5 + JPA 백엔드 프로젝트
- ces-neighborhood-react: react 프론트 프로젝트

---

## 개발환경
- front-end: __react__
- java: jdk 17
- framework: Spring Boot 3.1.5
- persistence: __JPA__
- database: PostgreSQL
- build: gradle

- 로그인 방식: Spring Security JWT
- 로깅: log4jdbc2


## 사용기술
- Spring Data JPA
- Spring Security
- Spring OAuth2
- QueryDSL
- AWS S3
