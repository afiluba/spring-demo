import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DeprecatedBeanWarner;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class B06_DeprecatedBeanWarner {
    /**
     * Exmaple of DeprecatedBeanWarner
     * @see DeprecatedBeanWarner
     */
    @Test
    void deprecated() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean1", bd);

        new DeprecatedBeanWarner().postProcessBeanFactory(bf);

        Bean1 bean1 = bf.getBean(Bean1.class);
    }


    @Deprecated
    static class Bean1 {
    }
}
