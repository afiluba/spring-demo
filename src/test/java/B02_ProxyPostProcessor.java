import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class B02_ProxyPostProcessor {
    /**
     * Example returning proxy-object instead of created bean
     */
    @Test
    void proxyPostProcessor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean1", bd);

        bf.addBeanPostProcessor(new ProxyBeanProcessor());

        Interface1 bean1 = bf.getBean(Interface1.class);

        bean1.exampleCall("TEST");

        assertThat(bean1).isNotExactlyInstanceOf(Bean1.class);
    }

    interface Interface1 {
        String exampleCall(String in);
    }

    static class Bean1 implements Interface1 {
        public Bean1() {
            System.out.println("Bean1.Bean1");
        }

        public String exampleCall(String in) {
            return in;
        }
    }

    static class ProxyBeanProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            System.out.println("LoggingBeanProcessor.postProcessBeforeInitialization");
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), new Class[] { Interface1.class },
                    new LoggingProxy(bean));
        }

        static class LoggingProxy implements InvocationHandler {
            private Object o;

            public LoggingProxy(Object o) {
                this.o = o;
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("LoggingProxy.method before");
                Object ret = method.invoke(o, args);
                System.out.println("LoggingProxy.method after");
                return ret;
            }
        }
    }
}
