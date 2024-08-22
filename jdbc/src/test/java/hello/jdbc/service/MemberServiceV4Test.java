package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import hello.jdbc.repository.MemberRepositoryV4_1;
import hello.jdbc.repository.MemberRepositoryV4_2;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

/*
 * 예외 누수 문제 해결
 * SQLException 제거
 * MemberRepository 인터페이스 의존
*/
@Slf4j
@SpringBootTest // 이 Annotation을 사용하면 Test를 위한 Spring Container를 띄워서 필요한 Spring Bean들을 등록하고 DI 주입도 시켜준다.
public class MemberServiceV4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberServiceV4 memberService;

    @TestConfiguration // SpringBoot가 등록하는 기본 Bean 외에도 Test에서 사용할 아래 Bean들을 추가해줌.
    static class TestConfig {

        private final DataSource datasource;

        public TestConfig(DataSource datasource) {
            this.datasource = datasource;
        }

        @Bean
        MemberRepository memberRepository() {
            // return new MemberRepositoryV4_1(datasource); // 단순 예외 변환
            return new MemberRepositoryV4_2(datasource); // 스프링 예외 변환
        }

        @Bean
        MemberServiceV4 memberServiceV4() {
            return new MemberServiceV4(memberRepository());
        }
    }

    /*
    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);
        memberService = new MemberServiceV3_3(memberRepository);
    }
     */

    @AfterEach
    void after() {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);

    }

    @Test
    @DisplayName("AOP 프록시 확인")
    void AopCheck() {
        log.info("memberService class={}", memberService.getClass());
        log.info("memberRepository class={}", memberRepository.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        // when
        Assertions.assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        // then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEx.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(10000); // Exception 발생 시 rollback을 통해 원래 금액으로 복구되었음을 확인.
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }

}
