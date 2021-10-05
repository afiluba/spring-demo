import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DeprecatedBeanWarner;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;

public class B07_ConfigurationClassPostProcessor {
    /**
     * Exmaple of {@link org.springframework.context.annotation.ConfigurationClassPostProcessor}
     * @see ConfigurationClassPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory) 
     */
    @Test
    void test() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Config.class);
        bf.registerBeanDefinition("config", bd);

        new ConfigurationClassPostProcessor().postProcessBeanFactory(bf);

        Bean1 bean1 = bf.getBean(Bean1.class);

        assertThat(bean1).isNotNull();
    }


    static class Bean1 {
    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

    }
}
