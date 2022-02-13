package hello.core.scope;


import static org.assertj.core.api.Assertions.assertThat;

import hello.core.scope.PrototypeTest.PrototypeBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

public class SingletonWithPrototypeTest1 {

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac = new
                AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);
        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);
        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(1);
    }

    @Scope("singleton")
    static class ClientBean {

      //  private final PrototypeBean prototypeBean;  //생성시점에 주입

        @Autowired
        //private ObjectProvider<PrototypeBean> prototypeBeanOProvider; // 아래보다 편의 기능 더 추가됨
        private Provider<PrototypeBean> prototypeBeanOProvider;    // 둘다 가능

        public int logic() {
            //PrototypeBean prototypeBean = prototypeBeanOProvider.getObject();   // 호출시점에 스프링 컨테이너서 프로토타입 빈을 찾아서 반환해줌
            PrototypeBean prototypeBean = prototypeBeanOProvider.get();
            prototypeBean.addCount();
            int count = prototypeBean.getCount();
            return count;
        }
    }

    @Scope("prototype")
    static class PrototypeBean {

        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
