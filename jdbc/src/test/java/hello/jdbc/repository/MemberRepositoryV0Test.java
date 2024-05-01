package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repositoryV0 = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV0", 10000);

        // save
        repositoryV0.save(member);

        // findById
        Member findMember = repositoryV0.findById(member.getMemberId());

        // Member Class에 붙인 Annotation인 @Data는 ToString을 Override 해주기 때문에 로그로 출력 가능.
        log.info("findMember={}", findMember);
        log.info("findMember == member : {}", findMember == member);
        log.info("findMember equals member : {}", findMember.equals(member)); // @Data는 해당 객체의 모든 필드를 사용하도록 equals를 오버라이드.

        // 조회해온 Member가 처음 생성한 Member와 같은 Member인지 비교.
        org.assertj.core.api.Assertions.assertThat(findMember).isEqualTo(member);

        // update: money 10000 -> 20000
        repositoryV0.update(member.getMemberId(), 20000);
        Member updateMember = repositoryV0.findById(member.getMemberId());
        org.assertj.core.api.Assertions.assertThat(updateMember.getMoney()).isEqualTo(20000);

        // delete
        repositoryV0.delete(member.getMemberId());

        // 조회 Method를 실행하면 NoSuchElementException이 발생하는지 확인. (이미 DELETE 문이 실행되었으므로)
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> repositoryV0.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }
}