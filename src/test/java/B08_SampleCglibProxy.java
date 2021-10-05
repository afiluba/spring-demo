import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.cglib.proxy.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class B08_SampleCglibProxy {
    /**
     * Basic cglib proxy
     * @see Enhancer
     * @see Enhancer#setCallback(Callback)
     */
    @Test
    void cglibBasic() {
        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(Bean.class);
        enhancer.setCallback(new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return "OVERRIDE";
            }
        });

        Bean bean1 = (Bean) enhancer.create();
        String out = bean1.doIt();

//        bean1.doIt2();

        assertThat(out).isEqualTo("OVERRIDE");
        assertThat(bean1).isNotExactlyInstanceOf(Bean.class);
    }

    @Test
    void cglibMethodInterceptor() {
        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(Bean.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if (method.getName().equals("doIt")) {
                    return "OVERRIDE";
                } else {
                    return methodProxy.invokeSuper(o, objects);
                }
            }
        });

        Bean bean1 = (Bean) enhancer.create();
        String out = bean1.doIt();

        int out2 = bean1.doIt2();

        assertThat(out).isEqualTo("OVERRIDE");
        assertThat(out2).isEqualTo(1);
        assertThat(bean1).isNotExactlyInstanceOf(Bean.class);
    }

    @Test
    void cglibCallbackFilter() {
        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(Bean.class);
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                if (method.getName().equals("doIt")) {
                    return 1;
                } else if (method.getName().equals("doIt2")) {
                    return 2;
                }
                return 0;
            }
        });

        Callback[] callbacks = new Callback[] {
                new NoOp() {},
                new FixedValue() {
                    @Override
                    public Object loadObject() throws Exception {
                        return "DOIT";
                    }
                },
                new FixedValue() {
                    @Override
                    public Object loadObject() throws Exception {
                        return -1;
                    }
                }
        };
        enhancer.setCallbacks(callbacks);

        Bean bean1 = (Bean) enhancer.create();

        String out = bean1.doIt();
        int out2 = bean1.doIt2();
        BigDecimal bd = bean1.noop();

        assertThat(out).isEqualTo("DOIT");
        assertThat(out2).isEqualTo(-1);
        assertThat(bd).isEqualTo(BigDecimal.TEN);
        assertThat(bean1).isNotExactlyInstanceOf(Bean.class);
    }


    static class Bean {
        public String doIt() {
            return "DOIT";
        }

        public int doIt2() {
            return 1;
        }

        public BigDecimal noop() {
            return BigDecimal.TEN;
        }
    }
}
