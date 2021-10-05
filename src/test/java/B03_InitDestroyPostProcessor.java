import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class B03_InitDestroyPostProcessor {
    /**
     * @see InitDestroyAnnotationBeanPostProcessor
     * @see org.springframework.context.annotation.CommonAnnotationBeanPostProcessor
     */
    @Test
    void initDestroy() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean1", bd);

        InitDestroyAnnotationBeanPostProcessor initDestroyAnnotationBeanPostProcessor = new InitDestroyAnnotationBeanPostProcessor();
        initDestroyAnnotationBeanPostProcessor.setInitAnnotationType(MyInit.class);
        initDestroyAnnotationBeanPostProcessor.setDestroyAnnotationType(MyDestroy.class);

        bf.addBeanPostProcessor(initDestroyAnnotationBeanPostProcessor);

        Bean1 bean1 = bf.getBean(Bean1.class);

        bf.destroySingletons();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface MyInit {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface MyDestroy {}

    static class Bean1 {
        @MyInit
        public void iiinit() {
            System.out.println("Bean1.iiinit");
        }

        @MyDestroy
        public void dddestroy() {
            System.out.println("Bean1.dddestroy");
        }
    }
}
