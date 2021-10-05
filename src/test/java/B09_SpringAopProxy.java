import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.*;

import static org.assertj.core.api.Assertions.assertThat;

public class B09_SpringAopProxy {
    /**
     * ProxyFactory example
     * @see DefaultAopProxyFactory#createAopProxy(AdvisedSupport)
     * @see CglibAopProxy#getProxy()
     * @see org.springframework.aop.framework.ObjenesisCglibAopProxy
     */
    @Test
    void springProxy() {
        ProxyFactory proxyFactory = new ProxyFactory();
        Bean bean = new Bean(new Child());
        proxyFactory.setTarget(bean);

        proxyFactory.addAdvice(new MethodInterceptor() {
                                   @Override
                                   public Object invoke(MethodInvocation invocation) throws Throwable {
                                       System.out.println("Before");
                                       return invocation.proceed();
                                   }
                               });

        Bean b = (Bean) proxyFactory.getProxy();

        b.doIt();

        assertThat(b).isNotExactlyInstanceOf(Bean.class);
    }

    static class Bean {
        private Child child;
        private String a;

        Bean(Child child) {
            this.child = child;
        }

        public String doIt() {
            return "DOIT";
        }
        public final void doIt2() { }
    }

    static class Child {
    }
}
