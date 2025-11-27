
**포트폴리오 국플릭스 웹사이트**
- **https://gukarchive.github.io/**
<br>

**관련 링크**
- [개발 흐름 및 학습 과정을 정리한 개인 노션](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d)
- 백엔드 깃허브 : https://github.com/gukarchive/guckflix_backend
- 프론트엔드 깃허브 : https://github.com/gukarchive/guckflix_frontend
- [API 호출 주소 예시](https://api.guckflix.site/movies/popular?page=1)
<br>

**국플릭스 어플리케이션 개요**

- 넷플릭스를 모방한 영화 소개 어플리케이션
- tmdb api를 통해 약 2000건의 영화 엔티티를 크롤링하여 더미로 구축
- 크롤링, 백엔드, 프론트엔드, AWS 설정 모두 1인 개발
<br>
  
**주요 기능**
  
- 영화 CRUD, 페이징, 슬라이스
- 배우 CRUD
- 출연작 CRUD
- 유튜브 트레일러 및 영화, 배우, 사진 제공
- 영화 제목 검색 기능
- 일반 로그인 및 OAUTH2 로그인 지원
<br>

**프로젝트 주요 특징 및 적용점 포스팅**
<br>
이하 링크가 동작하지 않거나 느리다면 최상단의 노션 링크로 접속하시면 됩니다.
<br>

- [304 응답코드를 활용한 이미지 캐싱](https://github.com/gukarchive/guckflix_backend/blob/master/src/main/java/guckflix/backend/controller/ImageController.java)
- [인기 API 캐싱](https://github.com/gukarchive/guckflix_backend/blob/master/src/main/java/guckflix/backend/config/RedisConfig.java)
- [DTO 체계화](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1356248714fb8014b1a7fa21cb6de302&pm=s)
- [빈 라이프사이클 이해](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1376248714fb80799e63e0e5507ae1b9&pm=s)
- [빌더 패턴](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=3458c1ec23e54503adebaf1a00cea9db&pm=s)
- [커스텀 ArgumentResolver](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1356248714fb809f8796e4ba43f11187&pm=s)
- [AOP 및 로깅](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1296248714fb80c2ba68e608c160ccdc&pm=s)
- [스프링 시큐리티 client 라이브러리 이해 (1)](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1496248714fb8064b260ee3c005b9786&pm=s)
- [스프링 시큐리티 client 라이브러리 이해 (2)](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1516248714fb807bbbb5f633ca7a5997&pm=s)
- [JPA N+1 해결](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1776248714fb8050b021f0a95701346f&pm=s)
- [JPA OSIV 개념](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1776248714fb80e7ae4ef9dc20603262&pm=s)
- [체크 예외 전환](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1346248714fb80bc9466f0c502444fd1&pm=s)
- [전역적 예외 관리](https://github.com/gukarchive/guckflix_backend/blob/master/src/main/java/guckflix/backend/exception/ApiExceptionHandler.java)
- [검증기와 날짜 커스텀 검증기](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=1346248714fb80c1917ccfb00ccf8223&pm=s)
- [정적 팩토리 메서드](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=2b06248714fb800fa661cfa6da6b2cc9&pm=s)
- [자동 CI/CD](https://github.com/gukarchive/guckflix_backend/blob/master/.github/workflows/github-actions.yml)
<br>

**프로젝트 외적으로 학습중인 사항**
<br>
노션에 지속적으로 학습한 것을 정리하고 있습니다.
<br>

- [추상화, 다형성, 상속, 캡슐화](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=2b26248714fb8071a645c87cdb6d584b&pm=s)
- [스프링 레거시 프로젝트(1) - JSP, 서블릿, JSTL, EL](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=2b46248714fb80e486c4c7db070c801b&pm=s)
- [스프링 레거시 프로젝트(2) - web.xml, RootApplicationContext, WebApplicationContext](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=2b56248714fb80039e24f3aa1381f7e0&pm=s)
- [스프링 레거시 프로젝트(3) - mybatis (작성중)](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=2b66248714fb80579dd1d6f49208b139&pm=s)
- [컬렉션 프레임워크, Iterator, ConcurrentModificationException, Comparator](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=3ae715430a0f4967893cf120a3cade21&pm=s)
- [Enum](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=0ae5c46d5af7449e8f8f7eab39682d09&pm=s)
- [static](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=7f876f82272940a6b31cdc2639a88fcb&pm=s)
- [Integer.valueOf()와 IntegerCache](https://gukarchive1994.notion.site/1e8014017e4c4b8d82604d1dbbc47aea?v=086b8d29b8334e88b3bfe61a9b20084d&p=50a375cdda444e178f758a05059f0743&pm=s)
<br>

**프로젝트 활용 기술 스택**

1. 백엔드 개발 환경
- 자바 11.0.28
- 스프링부트 2.7.6
- 스프링 시큐리티 5.75 (로그인, OAUTH2, 권한 관리)
<br>

2. DB 통신 관련 
- 메인 DB MYSQL 8
- 테스트용 DB H2
- 스프링 Data JPA (ORM)
- Redis (인기 API 캐싱 목적)
<br>

3. 프론트엔드
- React
- Redux (전역 상태 관리)
<br>

4. 환경 및 CI/CD
- git CLI
- github action (자동 CI/CD)
- 도커 컴포즈
- AWS EC2
- AWS 로드밸런서

