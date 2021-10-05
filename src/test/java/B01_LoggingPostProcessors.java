import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class B01_LoggingPostProcessors {
    /**
     * Basic example of logging postprocessor
     * @see DefaultListableBeanFactory#addBeanPostProcessor(BeanPostProcessor)
     * @see BeanPostProcessor
     */
    @Test
    void loggingPostProcessor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bd.setInitMethodName("init");
        bf.registerBeanDefinition("bean1", bd);

        LoggingBeanProcessor processor = new LoggingBeanProcessor();
        bf.addBeanPostProcessor(processor);

        Bean1 bean1 = bf.getBean(Bean1.class);

        assertThat(bean1).isNotNull();
        assertThat(processor.beforeInitializationCalled).isTrue();
        assertThat(processor.afterInitializationCalled).isTrue();
    }

    static class Bean1 {
        public Bean1() {
            System.out.println("Bean1.Bean1");
        }

        public void init() {
            System.out.println("Bean1.init");
        }
    }

    static class LoggingBeanProcessor implements BeanPostProcessor {
        private boolean beforeInitializationCalled;
        private boolean afterInitializationCalled;

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            beforeInitializationCalled = true;
            System.out.println("LoggingBeanProcessor.postProcessBeforeInitialization");
            return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            afterInitializationCalled = true;
            System.out.println("LoggingBeanProcessor.postProcessAfterInitialization");
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }

        public boolean isBeforeInitializationCalled() {
            return beforeInitializationCalled;
        }

        public boolean isAfterInitializationCalled() {
            return afterInitializationCalled;
        }
    }
}
