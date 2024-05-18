# 2. 커넥션풀과 데이터소스 이해

## 커넥션 풀 이해

### 데이터베이스 커넥션을 매번 획득

![데이터베이스 커넥션을 매번 획득](https://github.com/taechacode/SpringDB1Repository/assets/63395751/817f0c56-a94f-48aa-8f21-c345e97e31e5)

- 데이터베이스 커넥션을 획득할 때는 다음과 같은 과정을 거친다.

1. 애플리케이션 로직은 DB 드라이버를 통해 커넥션을 조회한다.
2. DB 드라이버는 DB와 `TCP/IP` 커넥션을 연결한다. 이 과정에서 3 way handshake 같은 `TCP/IP` 연결을 위한 네트워크 동작이 발생한다.
3. DB 드라이버는 `TCP/IP` 커넥션이 연결되면 ID, PW와 기타 부가정보를 DB에 전달한다.
4. DB는 ID, PW를 통해 내부 인증을 완료하고, 내부에 DB 세션을 생성한다.
5. DB는 커넥션 생성이 완료되었다는 응답을 보낸다.
6. DB 드라이버는 커넥션 객체를 생성해서 클라이언트에 반환한다.

- 커넥션을 새로 만드는 것은 과정도 복잡하고 시간도 많이 소모된다.
- DB는 물론이고 애플리케이션 서버에서도 `TCP/IP` 커넥션을 새로 생성하기 위한 리소스를 매번 사용해야 한다.
- 고객이 애플리케이션을 사용할 때 SQL을 실행하는 시간뿐만 아니라 커넥션을 새로 만드는 시간이 추가되기 때문에 결과적으로 응답 속도에 영향을 준다.
<br/><br/>

### 커넥션 풀 초기화

![커넥션 풀 초기화](https://github.com/taechacode/SpringDB1Repository/assets/63395751/d7f6e8ef-df7f-4fd9-8c2a-d5b39eb96435)

- 애플리케이션을 시작하는 시점에 커넥션 풀은 필요한만큼 커넥션을 미리 확보해서 풀에 보관한다.
- 얼마나 커넥션을 보관할지는 서비스의 특징과 서버 스펙에 따라 다르지만 기본값은 보통 10개다.
<br/><br/>

### 커넥션 풀의 연결 상태

![커넥션 풀의 연결 상태](https://github.com/taechacode/SpringDB1Repository/assets/63395751/cd0a49d8-2445-4f83-a6c8-428705e1455f)

- 커넥션 풀에 들어있는 커넥션은 TCP/IP로 DB와 커넥션이 연결된 상태이기 때문에 언제든지 즉시 SQL을 DB에 전달할 수 있다.
<br/><br/>

### 커넥션 풀 사용1

![커넥션 풀 사용1](https://github.com/taechacode/SpringDB1Repository/assets/63395751/e82816de-1d93-41cb-aa77-df0aee8f9b78)

- 애플리케이션 로직에서 DB 드라이버를 통해서 새로운 커넥션을 획득하는 것이 아니다.
- 커넥션 풀을 통해 이미 생성된 커넥션을 객체 참조로 가져다 쓴다.
- 커넥션 풀에 커넥션을 요청하면 커넥션 풀은 자신이 가지고 있는 커넥션 중에 하나를 반환한다.
<br/><br/>

### 커넥션 풀 사용2

![커넥션 풀의 연결 상태](https://github.com/taechacode/SpringDB1Repository/assets/63395751/98fcbfb5-31d1-4a70-a25f-5cf5c9b91a23)

- 애플리케이션 로직은 커넥션 풀에서 받은 커넥션을 사용해서 SQL을 데이터베이스에 전달하고 그 결과를 받아서 처리한다.
- 커넥션을 모두 사용하고 나면 이제는 커넥션을 종료하는 것이 아니라, 다음에 다시 사용할 수 있도록 해당 커넥션을 그대로 커넥션 풀에 반환하면 된다. 커넥션이 종료되지 않고 살아있는 상태로 반환된다.
<br/><br/>

### 커넥션 풀 정리
- 적절한 커넥션 풀 숫자는 서비스 특징과 애플리케이션 서버 스펙, DB 서버 스펙에 따라 다르기 때문에 성능 테스트를 통해서 정해야 한다.
- 커넥션 풀은 서버당 최대 커넥션 수를 제한할 수 있다. 따라서 DB에 무한정 연결이 생성되는 것을 막아 DB를 보호하는 효과도 있다.
- 대표적인 커넥션 풀 오픈소스는 `commons-dbcp2`, `tomcat-jdbc pool`, `HikariCP` 등이 있다.
<br/><br/>

## DataSource 이해
- 커넥션을 얻는 방법은 JDBC `DriverManager`를 직접 사용하거나, 커넥션 풀을 사용하는 등 다양한 방법이 존재한다.

### 커넥션을 획득하는 다양한 방법

![커넥션을 획득하는 다양한 방법](https://github.com/taechacode/SpringDB1Repository/assets/63395751/b7f1a6f1-55e9-4eee-a2d5-4f35c87e7af8)
<br/><br/>

### DriverManager를 통해 커넥션 획득

![DriverManager를 통해 커넥션 획득](https://github.com/taechacode/SpringDB1Repository/assets/63395751/4c5224ae-fb0a-4bcb-9825-2aa0d5a7313f)

- JDBC로 개발한 애플리케이션처럼 `DriverManager`를 통해서 커넥션을 획득하다가, 커넥션 풀을 사용하는 방법으로 변경하려면 어떻게 해야할까?
<br/><br/>

### DriverManager를 통해 커넥션을 획득하다가 커넥션 풀로 변경 시 문제

![DriverManager를 통해 커넥션 획득하다가 커넥션 풀로 변경시 문제](https://github.com/taechacode/SpringDB1Repository/assets/63395751/810e4fdd-7a20-4be5-b98c-928720ad396b)

- 예를 들어, 애플리케이션 로직에서 `DriverManager`를 사용해서 커넥션을 획득하다가 `HikariCP` 같은 커넥션 풀을 사용하도록 변경하면 커넥션을 획득하는 애플리케이션 코드도 함께 변경해야 한다. 의존관계가 `DriverManager`에서 `HikariCP`로 변경되기 때문이다.
<br/><br/>

### 커넥션을 획득하는 방법을 추상화

![커넥션을 획득하는 방법을 추상화](https://github.com/taechacode/SpringDB1Repository/assets/63395751/3f355351-a413-4ce9-807e-c95eaf18371f)

- 자바에서는 이런 문제를 해결하기 위해 `javax.sql.DataSource`라는 인터페이스를 제공한다.
- `DataSource`는 **커넥션을 획득하는 방법을 추상화**하는 인터페이스이다.
- 이 인터페이스의 핵심 기능은 커넥션 조회 하나다.
<br/><br/>

### DataSource 핵심 기능 축약

```
public interface DataSource {
 Connection getConnection() throws SQLException;
}
```

### DataSource 이해 정리
- 대부분의 커넥션 풀은 `DataSource` 인터페이스를 이미 구현해두었다. 따라서 개발자는 `DBCP2 커넥션 풀`, `HikariCP 커넥션 풀`의 코드를 직접 의존하는 것이 아니라 `DataSource` 인터페이스에만 의존하도록 애플리케이션 로직을 작성하면 된다.
- 커넥션 풀 구현 기술을 변경하고 싶으면 해당 구현체로 갈아끼우기만 하면 된다.
- `DriverManager`는 `DataSource` 인터페이스를 사용하지 않는다. 따라서 `DriverManager`는 직접 사용해야 한다. 따라서 `DriverManager`를 사용하다가 `DataSource` 기반의 커넥션 풀을 사용하도록 변경하면 관련 코드를다 고쳐야 한다. 이런 문제를 해결하기 위해 스프링은 `DriverManager`도 `DataSource`를 통해서 사용할 수 있도록 `DriverManagerDataSource`라는 `DataSource`를 구현한 클래스를 제공한다.
- 자바는 `DataSource`를 통해 커넥션을 획득하는 방법을 추상화했다. 이제 애플리케이션 로직은 `DataSource` 인터페이스에만 의존하면 된다. 덕분에 `DriverManagerDataSource`를 통해서 `DriverManager`를 사용하다가 커넥션 풀을 사용하도록 코드를 변경해도 애플리케이션 로직을 변경하지 않아도 된다.
<br/><br/>
