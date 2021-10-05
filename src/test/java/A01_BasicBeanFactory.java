import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.*;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class A01_BasicBeanFactory {
    /**
     * Basic bean definition
     * @see org.springframework.beans.factory.BeanFactory
     * @see org.springframework.beans.factory.HierarchicalBeanFactory
     * @see org.springframework.beans.factory.ListableBeanFactory
     * @see DefaultListableBeanFactory
     * @see org.springframework.beans.factory.support.AbstractBeanDefinition
     * @see org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean(String, Class, Object[], boolean)
     */
    @Test
    void basicBeanDefinition() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean1", bd);

        Bean1 bean1 = bf.getBean(Bean1.class);
        Bean1 bean2 = (Bean1) bf.getBean("bean1");
        Bean1 bean3 = bf.getBean("bean1", Bean1.class);

        assertThat(bean1).isNotNull();
        assertThat(bean2).isNotNull();
        assertThat(bean3).isNotNull();
    }

    /**
     * Singleton scope
     * @see org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean(String, Class, Object[], boolean) (else if (mbd.isPrototype()) )
     */
    @Test
    void scopeSingleton() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);
        bf.registerBeanDefinition("bean1", bd);

        Bean1 bean1 = bf.getBean(Bean1.class);
        Bean1 bean2 = bf.getBean(Bean1.class);

        assertThat(bean1).isNotNull();
        assertThat(bean2).isNotNull();
        assertThat(bean1).isEqualTo(bean2);
    }

    /**
     * Prototype scope
     * @see org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean(String, Class, Object[], boolean) (else if (mbd.isPrototype()) )
     */
    @Test
    void scopePrototype() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("bean1", bd);

        Bean1 bean1 = bf.getBean(Bean1.class);
        Bean1 bean2 = bf.getBean(Bean1.class);

        assertThat(bean1).isNotNull();
        assertThat(bean2).isNotNull();
        assertThat(bean1).isNotEqualTo(bean2);
    }

    /**
     * Singleton scope
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance(String, RootBeanDefinition, Object[]) 
     */
    @Test
    void staticFactoryMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bd.setFactoryMethodName("create");
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);
        bf.registerBeanDefinition("bean1", bd);

        Bean1 bean1 = bf.getBean(Bean1.class);

        assertThat(bean1).isNotNull();
    }

    /**
     * Init method
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods(String, Object, RootBeanDefinition)
     */
    @Test
    void initMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bd.setInitMethodName("init");
        bf.registerBeanDefinition("bean1", bd);

        Bean1 bean1 = bf.getBean(Bean1.class);

        assertThat(bean1).isNotNull();
    }

    /**
     * Destroy method
     * @see org.springframework.beans.factory.support.DisposableBeanAdapter#inferDestroyMethodIfNecessary(Object, RootBeanDefinition)
     * @see DefaultListableBeanFactory#destroySingletons()
     */
    @Test
    void detroyMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bd.setDestroyMethodName("destroy");
        bf.registerBeanDefinition("bean1", bd);

        Bean1 bean1 = bf.getBean(Bean1.class);

        assertThat(bean1).isNotNull();

        bf.destroySingletons();
    }

    /**
     * Infered destroy method
     * @see org.springframework.beans.factory.support.DisposableBeanAdapter#inferDestroyMethodIfNecessary(Object, RootBeanDefinition)
     * @see DefaultListableBeanFactory#destroySingletons()
     */
    @Test
    void inferDetroyMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(Bean1.class);
        bd.setDestroyMethodName(GenericBeanDefinition.INFER_METHOD);
        bf.registerBeanDefinition("bean1", bd);

        Bean1 bean1 = bf.getBean(Bean1.class);

        assertThat(bean1).isNotNull();

        bf.destroySingletons();
    }

    /**
     * Depends on
     * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#registerDependentBean(String, String)
     */
    @Test
    void dependsOn() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd1 = new GenericBeanDefinition();
        bd1.setBeanClass(Bean1.class);
        bd1.setDependsOn("bean2");
        bf.registerBeanDefinition("bean1", bd1);

        GenericBeanDefinition bd2 = new GenericBeanDefinition();
        bd2.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean2", bd2);

        Bean1 bean1 = bf.getBean("bean1", Bean1.class);
        Bean1 bean2 = bf.getBean("bean2", Bean1.class);

        assertThat(bean1).isNotNull();
        assertThat(bean2).isNotNull();
        assertThat(bean1.tick).isGreaterThan(bean2.tick);
    }

    /**
     * Primary
     * @see DefaultListableBeanFactory#determinePrimaryCandidate(Map, Class)
     */
    @Test
    void primary() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd1 = new GenericBeanDefinition();
        bd1.setBeanClass(Bean1.class);
        bd1.setPrimary(true);
        bf.registerBeanDefinition("bean1", bd1);

        GenericBeanDefinition bd2 = new GenericBeanDefinition();
        bd2.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean2", bd2);

        Bean1 bean1 = bf.getBean(Bean1.class);
        Bean1 bean2 = bf.getBean("bean2", Bean1.class);

        System.out.println(bean1.getClass());

        assertThat(bean1).isNotNull();
        assertThat(bean2).isNotNull();
    }

    /**
     * Method override - lookup
     * @see CglibSubclassingInstantiationStrategy.CglibSubclassCreator#instantiate(Constructor, Object...)
     * @see AbstractAutowireCapableBeanFactory#instantiateBean(String, RootBeanDefinition)
     */
    @Test
    void methodOverrideLookup() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd1 = new GenericBeanDefinition();
        bd1.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean1", bd1);

        GenericBeanDefinition bd2 = new GenericBeanDefinition();
        bd2.setBeanClass(Bean2.class);
        MethodOverrides methodOverrides = new MethodOverrides();
        methodOverrides.addOverride(new LookupOverride("lookup", "bean1"));
        bd2.setMethodOverrides(methodOverrides);
        bf.registerBeanDefinition("bean2", bd2);

        Bean2 bean2 = bf.getBean("bean2", Bean2.class);
        System.out.println(bean2.getClass());
        Bean1 bean1 = bean2.lookup();

        assertThat(bean2).isNotNull();
        assertThat(bean1).isNotNull();
    }

    /**
     * Method override - replace
     * @see CglibSubclassingInstantiationStrategy.ReplaceOverrideMethodInterceptor#intercept(Object, Method, Object[], MethodProxy)
     */
    @Test
    void methodOverrideReplace() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd1 = new GenericBeanDefinition();
        bd1.setBeanClass(Reimplementer.class);
        bf.registerBeanDefinition("reimplementer", bd1);

        GenericBeanDefinition bd2 = new GenericBeanDefinition();
        bd2.setBeanClass(Bean3.class);
        MethodOverrides methodOverrides = new MethodOverrides();
        methodOverrides.addOverride(new ReplaceOverride("replace", "reimplementer"));
        bd2.setMethodOverrides(methodOverrides);
        bf.registerBeanDefinition("bean3", bd2);

        Bean3 bean3 = bf.getBean("bean3", Bean3.class);
        System.out.println(bean3.getClass());
        assertThat(bean3.replace()).isEqualTo("REIMPLEMENTED");
    }

    /**
     * Lazy init
     * @see DefaultListableBeanFactory#preInstantiateSingletons()
     */
    @Test
    void lazyInit() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition bd1 = new GenericBeanDefinition();
        bd1.setBeanClass(Bean1.class);
        bd1.setLazyInit(true);
        bf.registerBeanDefinition("bean1", bd1);

        GenericBeanDefinition bd2 = new GenericBeanDefinition();
        bd2.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean2", bd2);

        bf.preInstantiateSingletons();

        Bean1 bean1 = bf.getBean("bean1", Bean1.class);
        Bean1 bean2 = bf.getBean("bean2", Bean1.class);

        assertThat(bean1).isNotNull();
        assertThat(bean2).isNotNull();
    }

    /**
     * Autowire byType
     * @see AbstractAutowireCapableBeanFactory#unsatisfiedNonSimpleProperties(AbstractBeanDefinition, BeanWrapper)
     * @see DefaultListableBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
     * @see DefaultListableBeanFactory#findAutowireCandidates(String, Class, DependencyDescriptor)
     * @see AbstractAutowireCapableBeanFactory#autowireByType(String, AbstractBeanDefinition, BeanWrapper, MutablePropertyValues) 
     * @see GenericBeanDefinition#setAutowireCandidate(boolean)
     */
    @Test
    void autowireByType() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition parentBd = new GenericBeanDefinition();
        parentBd.setBeanClass(Parent.class);
        parentBd.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        bf.registerBeanDefinition("parent", parentBd);

        GenericBeanDefinition childBd = new GenericBeanDefinition();
        childBd.setBeanClass(Child.class);
        bf.registerBeanDefinition("child", childBd);

        Parent parent = bf.getBean("parent", Parent.class);

        assertThat(parent).isNotNull();
        assertThat(parent.getChild()).isNotNull();
    }

    /**
     * Autowire byName
     * @see DefaultListableBeanFactory#populateBean(String, RootBeanDefinition, BeanWrapper) 
     * @see AbstractAutowireCapableBeanFactory#autowireByName(String, AbstractBeanDefinition, BeanWrapper, MutablePropertyValues) 
     */
    @Test
    void autowireByName() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition parentBd = new GenericBeanDefinition();
        parentBd.setBeanClass(Parent.class);
        parentBd.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_NAME);
        bf.registerBeanDefinition("parent", parentBd);

        GenericBeanDefinition childBd = new GenericBeanDefinition();
        childBd.setBeanClass(Child.class);
        bf.registerBeanDefinition("child", childBd);

        Parent parent = bf.getBean("parent", Parent.class);

        assertThat(parent).isNotNull();
        assertThat(parent.getChild()).isNotNull();
    }

    /**
     * Qualifier
     * @see QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate(BeanDefinitionHolder, DependencyDescriptor)
     * @see AutowireCandidateQualifier
     * @see DefaultListableBeanFactory#setAutowireCandidateResolver(AutowireCandidateResolver)
     */
    @Test
    void qualifier() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());

        GenericBeanDefinition parentBd = new GenericBeanDefinition();
        parentBd.setBeanClass(ParentQualified.class);
        parentBd.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        bf.registerBeanDefinition("parent", parentBd);

        GenericBeanDefinition childBd = new GenericBeanDefinition();
        childBd.setBeanClass(Child.class);
        childBd.addQualifier(new AutowireCandidateQualifier(Qualifier.class, "qualifier2"));
        bf.registerBeanDefinition("child", childBd);

        ParentQualified parent = bf.getBean("parent", ParentQualified.class);

        assertThat(parent).isNotNull();
        assertThat(parent.getChild()).isNotNull();
    }

    /**
     * Alias
     * @see DefaultListableBeanFactory#registerAlias(String, String)
     * @see AbstractBeanFactory#transformedBeanName(String)
     */
    @Test
    void alias() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());

        GenericBeanDefinition bd1 = new GenericBeanDefinition();
        bd1.setBeanClass(Bean1.class);
        bf.registerBeanDefinition("bean1", bd1);
        bf.registerAlias("bean1", "alias");
        bf.registerAlias("alias", "alias2");

        Bean1 bean1 = bf.getBean("bean1", Bean1.class);
        Bean1 alias = bf.getBean("alias", Bean1.class);
        Bean1 alias2 = bf.getBean("alias2", Bean1.class);

        assertThat(bean1).isNotNull();
        assertThat(alias).isNotNull();
        assertThat(alias2).isNotNull();
    }

    /**
     * Hierarchical bean definition
     * @see DefaultListableBeanFactory#getMergedLocalBeanDefinition(String)
     * @see org.springframework.beans.factory.support.AbstractBeanDefinition#overrideFrom(BeanDefinition)
     */
    @Test
    void hierarchicalBeanDefinition() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();

        GenericBeanDefinition parentBd = new GenericBeanDefinition();
        parentBd.setBeanClass(Bean1.class);
        parentBd.setAbstract(true);
        bf.registerBeanDefinition("parent", parentBd);

        GenericBeanDefinition child1Bd = new GenericBeanDefinition();
        child1Bd.setParentName("parent");
        child1Bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        bf.registerBeanDefinition("child1", child1Bd);

        GenericBeanDefinition child2Bd = new GenericBeanDefinition();
        child2Bd.setParentName("parent");
        child2Bd.setScope(BeanDefinition.SCOPE_SINGLETON);
        bf.registerBeanDefinition("child2", child2Bd);

        Bean1 child1a = bf.getBean("child1", Bean1.class);
        Bean1 child1b = bf.getBean("child1", Bean1.class);

        Bean1 child2a = bf.getBean("child2", Bean1.class);
        Bean1 child2b = bf.getBean("child2", Bean1.class);

        assertThat(child1a).isNotNull();
        assertThat(child1b).isNotNull();
        assertThat(child1a).isNotEqualTo(child1b);

        assertThat(child2a).isNotNull();
        assertThat(child2b).isNotNull();
        assertThat(child2a).isEqualTo(child2b);
    }

    static class Bean1 {
        private final long tick = System.nanoTime();

        public Bean1() {
            System.out.println("Bean1.Bean1");
        }

        public static Bean1 create() {
            System.out.println("Bean1.create");
            return new Bean1();
        }

        public void init() {
            System.out.println("Bean1.init");
        }

        public void destroy() {
            System.out.println("Bean1.destroy");
        }

        public void close() {
            System.out.println("Bean1.close");
        }
    }

    static abstract class Bean2 extends Bean1 {
        abstract Bean1 lookup();
    }

    static class Reimplementer implements MethodReplacer {
        @Override
        public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
            System.out.println("Reimplementer.reimplement");
            return "REIMPLEMENTED";
        }
    }

    static class Bean3 extends Bean1 {
        String replace() {
            return "ORIGINAL";
        }
    }

    static class Parent {
        private Child child;

        public Child getChild() {
            return child;
        }

        public void setChild(Child child) {
            this.child = child;
        }

        public Parent() {
            System.out.println("Parent.Parent");
        }

        public Parent(A01_BasicBeanFactory.Child child) {
            System.out.println("Parent.Parent(child)");
            child = child;
        }
    }

    static class Child {
        public Child() {
            System.out.println("Child.Child");
        }
    }

    static class ParentQualified {
        private Child child;

        public Child getChild() {
            return child;
        }

        @Qualifier("qualifier2")
        public void setChild(Child child) {
            this.child = child;
        }

        public ParentQualified() {
            System.out.println("ParentQualified.ParentQualified");
        }

        public ParentQualified(A01_BasicBeanFactory.Child child) {
            System.out.println("ParentQualified.ParentQualified(child)");
            child = child;
        }
    }
}
