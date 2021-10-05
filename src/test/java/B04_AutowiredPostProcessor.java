import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.assertj.core.api.Assertions.assertThat;

public class B04_AutowiredPostProcessor {
    @Test
    void noAutowired() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean1", bd);

        GenericBeanDefinition bd2 = new GenericBeanDefinition();
        bd2.setBeanClass(Bean2.class);
        bf.registerBeanDefinition("bean2", bd2);

        Bean1 bean1 = bf.getBean(Bean1.class);

        assertThat(bean1.getBean2()).isNull();
    }

    @Test
    void autowired() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean1", bd);

        GenericBeanDefinition bd2 = new GenericBeanDefinition();
        bd2.setBeanClass(Bean2.class);
        bf.registerBeanDefinition("bean2", bd2);

        AutowiredAnnotationBeanPostProcessor processor = new AutowiredAnnotationBeanPostProcessor();
        processor.setBeanFactory(bf);
        bf.addBeanPostProcessor(processor);

        Bean1 bean1 = bf.getBean(Bean1.class);

        assertThat(bean1.getBean2()).isNotNull();
    }


    static class Bean1 {
        @Autowired
        private Bean2 bean2;

        public Bean2 getBean2() {
            return bean2;
        }
    }

    static class Bean2 {

    }
}
