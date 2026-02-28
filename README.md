
**포트폴리오 국플릭스 웹사이트**
- **https://www.guckflix.site/**
  
<br>

**관련 링크**
- [개발 흐름 및 학습 과정을 정리한 개인 노션](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d)
- [백엔드 깃허브](https://github.com/gukarchive/guckflix_backend)
- [프론트엔드 깃허브](https://github.com/gukarchive/guckflix_frontend)
- [API 호출 주소 예시](https://api.guckflix.site/movies/popular?page=1)
<br>

**국플릭스 어플리케이션 개요**

- 넷플릭스를 모방한 영화 소개 어플리케이션
- tmdb api를 통해 약 2000건의 영화 엔티티를 크롤링하여 더미로 구축
- 크롤링, 백엔드, 프론트엔드, AWS 설정 포함 1인 개발
<br>
  
**주요 기능**

아래는 노션 링크 또는 작성한 자바 파일 링크입니다.
노션 링크로 글이 자동으로 열리지 않으면 새로고침 부탁드립니다.
<br>

- 영화, 배우, 출연정보 CRUD, 페이징, 검색 기능 제공
- [Vector DB(Qdrant) 기반 의미 검색과 Spring AI 기반 RAG 영화 추천 챗봇 기능](https://gukarchive1994.notion.site/Vector-DB-Qdrant-Spring-AI-RAG-3146248714fb806a94f7c57fb69f2ae6)
- 일반 로그인 및 OAuth2 로그인 지원, 인증 후 원래 요청 페이지로 리다이렉트 [[1]](https://gukarchive1994.notion.site/Spring-Security-OAuth2-Client-1496248714fb8064b260ee3c005b9786) [[2]](https://gukarchive1994.notion.site/Spring-Security-OAuth2-Client-1516248714fb807bbbb5f633ca7a5997)
- logback 기반 레벨 별 로그 파일 생성 및 AOP 기반 Slack 알림 연동 [[1]](https://gukarchive1994.notion.site/logback-logback-1296248714fb80deb137fde9d39803a4) [[2]](https://gukarchive1994.notion.site/AOP-Slack-Slack-1296248714fb80c2ba68e608c160ccdc)
- [GitHub Actions + Docker Compose 기반 CI/CD 자동화 파이프라인 구축](https://github.com/gukarchive/guckflix_backend/blob/master/.github/workflows/github-actions.yml)
<br>

**프로젝트 세부 구현**
- [빈번한 API 호출에 Spring Cache (TTL 3m) 도입 → 응답 속도 단축](https://github.com/gukarchive/guckflix_backend/blob/master/src/main/java/guckflix/backend/config/RedisConfig.java)
- [이미지 요청에 대해 ETag/304(Not Modified) 처리로 브라우저 캐싱 이미지 사용 유도](https://github.com/gukarchive/guckflix_backend/blob/master/src/main/java/guckflix/backend/controller/ImageController.java)
- [커스텀 ArgumentResolver 작성으로 정렬/방향/검색 조건 표준화 및 기본값 제공](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1356248714fb809f8796e4ba43f11187&pm=s)
- [특정 날짜 검증 처리를 위한 커스텀 Bean Validator 작성](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1346248714fb80c1917ccfb00ccf8223&pm=s)
- [@ExceptionHandler 기반 전역 예외 처리](https://github.com/gukarchive/guckflix_backend/blob/master/src/main/java/guckflix/backend/exception/ApiExceptionHandler.java)
- [nested class를 통한 요청별 DTO 관리](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1356248714fb8014b1a7fa21cb6de302&pm=s)
- [RDBMS 트랜잭션 이후 @TransactionalEventListener 활용 → Vector DB(Qdrant)와 정합성 유지](https://www.notion.so/gukarchive1994/Vector-DB-Qdrant-Spring-AI-RAG-3146248714fb806a94f7c57fb69f2ae6?source=copy_link#3146248714fb817393e9cadf4d85a396)
- React router의 권한 기반 라우트와 Spring Security를 통한 프론트-백엔드의 일관적인 권한 검증
- CORS 설정을 위한 프론트-백엔드 배포 환경 도메인 작업

기타
- [빈 라이프사이클 이해](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1376248714fb80799e63e0e5507ae1b9&pm=s)
- [빌더 패턴](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=3458c1ec23e54503adebaf1a00cea9db&pm=s)
- [JPA N+1 해결](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1776248714fb8050b021f0a95701346f&pm=s)
- [JPA OSIV 개념](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1776248714fb80e7ae4ef9dc20603262&pm=s)
- [체크 예외 전환](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1346248714fb80bc9466f0c502444fd1&pm=s)
- [정적 팩토리 메서드](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=2b06248714fb800fa661cfa6da6b2cc9&pm=s)
- [추상화, 다형성, 상속, 캡슐화](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=2b26248714fb8071a645c87cdb6d584b&pm=s)
- [스프링 레거시 프로젝트(1) - JSP, 서블릿, JSTL, EL](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=2b46248714fb80e486c4c7db070c801b&pm=s)
- [스프링 레거시 프로젝트(2) - web.xml, RootApplicationContext, WebApplicationContext](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=2b56248714fb80039e24f3aa1381f7e0&pm=s)
- [컬렉션 프레임워크, Iterator, ConcurrentModificationException, Comparator](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=3ae715430a0f4967893cf120a3cade21&pm=s)
- [Enum](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=0ae5c46d5af7449e8f8f7eab39682d09&pm=s)
- [static](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=7f876f82272940a6b31cdc2639a88fcb&pm=s)
- [Integer.valueOf()와 IntegerCache](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=50a375cdda444e178f758a05059f0743&pm=s)
<br>

**프로젝트 활용 기술 스택**

- 스프링부트 3.5.10, 스프링 시큐리티 5.75, 스프링 AI 1.1.1, 스프링 Data JPA
- MYSQL 8, H2, Redis, Qdrant
- React, ReduxCD
- git, docker, AWS EC2, route53, LB
