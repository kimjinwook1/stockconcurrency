Pessimistic Lock
- 충돌이 빈번하게 일어난다면 Optimistic Lock보다 성능이 좋을 수 있다.
- Lock을 통해 업데이트를 제어하기 때문에 데이터 정합성이 어느정도 보장된다.
- 별도의 Lock을 잡기 떄문에 성능 감소가 있을 수 있다.

Optimistic Lock
- 별도의 Lock을 잡지 않으므로 Pessimistic Lock보다 성능상 이점이 있다.
- 업데이트가 실패했을 때 재시도 로직을 개발자가 직접 구현해야 한다.
- 충돌이 빈번한 경우 Pessimistic Lock을 사용하는 것이 나을 수 있다.

Named Lock
- 주로 분산락을 구현할 때 사용된다.
- 타임아웃을 구현하기 쉽다.
- 데이터 삽입 시 정합성을 맞춰야 하는 경우에 사용가능하다.
- 트랜잭션 종료 시 Lock 해제와 Session 관리를 잘 해주어야한다.
- 실제 사용 시 구현방법이 복잡할 수 있다.

Lettuce
- 구현이 간단하다
- spring data redis를 이용하면 lettuce가 기본이기 떄문에 별도의 라이브러리를 사용하지 않아도 된다.
- spin lock 방식이기 떄문에 동시에 많은 스레드가 lock 획득 대기 상태라면 redis에 부하가 갈 수 있다.

Redisson
- 락 획득 재시도를 기본으로 제공한다.
- pub-sub 방식으로 구현이 되어있기 때문에 lettuce와 비교했을 때 redis에 부하가 적다.
- 별도의 라이브러리를 사용해야 한다.
- lock을 라이브러리 차원에서 제공해주기 때문에 사용법을 공부해야 한다.

실무에서는 ?
- 재시도가 필요하지 않은 lock은 lettuce 활용
- 재시도가 필요한 경우에는 redisson 활용

Mysql
- 이미 Mysql을 사용하고 있다면 별도의 비용없이 사용 가능하다.
- 어느정도의 트래픽까지는 문제없이 활용이 가능하다.
- Redis 보다는 성능이 좋지않다.

Redis
- 활용중인 Redis가 없다면 별도의 구축비용과 인프라 관리비용이 발생한다.
- Mysql 보다 성능이 좋다.
