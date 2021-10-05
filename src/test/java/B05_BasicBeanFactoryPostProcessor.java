import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class B05_BasicBeanFactoryPostProcessor {
    /**
     * Exmaple of BeanFactoryPostProcessor
     * @see BeanFactoryPostProcessor
     * @see BeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)
     */
    @Test
    void bfPostProcessor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean1", bd);

        new BFPostProcessor().postProcessBeanFactory(bf);

        Bean1 bean1 = bf.getBean(Bean1.class);
        Bean1 bean2 = bf.getBean(Bean1.class);

        assertThat(bean1).isNotEqualTo(bean2);
    }


    static class Bean1 {
    }

    static class BFPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            String[] beanNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
                bd.setScope("prototype");
            }
        }
    }
}
