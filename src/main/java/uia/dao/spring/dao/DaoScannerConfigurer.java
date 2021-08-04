package uia.dao.spring.dao;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

import static org.springframework.util.Assert.notNull;

/**
 * @author ks015774
 * @since 2021-08-03 11:49:00
 */
@SuppressWarnings("rawtypes")
public class DaoScannerConfigurer
        implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

    private String beanName;

    private ApplicationContext applicationContext;

    private Class<? extends Annotation> annotationClass;

    private String basePackage;

    private Class<? extends DaoFactoryBean> daoFactoryBeanClass = DaoFactoryBean.class;

    private boolean processPropertyPlaceHolders;

    private BeanNameGenerator nameGenerator;

    private String dataSourceBeanName;

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setProcessPropertyPlaceHolders(boolean processPropertyPlaceHolders) {
        this.processPropertyPlaceHolders = processPropertyPlaceHolders;
    }

    public void setDaoFactoryBeanClass(Class<? extends DaoFactoryBean> daoFactoryBeanClass) {
        this.daoFactoryBeanClass = daoFactoryBeanClass;
    }

    public void setDataSourceBeanName(String dataSourceBeanName) {
        this.dataSourceBeanName = dataSourceBeanName;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public BeanNameGenerator getNameGenerator() {
        return nameGenerator;
    }

    public void setNameGenerator(BeanNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, "Property 'basePackage' is required");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (this.processPropertyPlaceHolders) {
            processPropertyPlaceHolders();
        }

        ClassPathDaoScanner scanner = new ClassPathDaoScanner(registry);
        scanner.setAnnotationClass(this.annotationClass);
        scanner.setMarkerInterface(null);
        scanner.setDataSourceBeanName(this.dataSourceBeanName);
        scanner.setResourceLoader(this.applicationContext);
        scanner.setBeanNameGenerator(this.nameGenerator);
        scanner.setDaoFactoryBeanClass(this.daoFactoryBeanClass);
        scanner.registerFilters();
        scanner.scan(
                StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    private void processPropertyPlaceHolders() {
        Map<String, PropertyResourceConfigurer> prcs = applicationContext.getBeansOfType(PropertyResourceConfigurer.class,
                false, false);

        if (!prcs.isEmpty() && applicationContext instanceof ConfigurableApplicationContext) {
            BeanDefinition mapperScannerBean = ((ConfigurableApplicationContext) applicationContext).getBeanFactory()
                    .getBeanDefinition(beanName);

            // PropertyResourceConfigurer does not expose any methods to explicitly perform
            // property placeholder substitution. Instead, create a BeanFactory that just
            // contains this mapper scanner and post process the factory.
            DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
            factory.registerBeanDefinition(beanName, mapperScannerBean);

            for (PropertyResourceConfigurer prc : prcs.values()) {
                prc.postProcessBeanFactory(factory);
            }

            PropertyValues values = mapperScannerBean.getPropertyValues();

            this.basePackage = updatePropertyValue("basePackage", values);
            this.dataSourceBeanName = updatePropertyValue("dataSourceBeanName", values);
        }
        this.basePackage = Optional.ofNullable(this.basePackage).map(getEnvironment()::resolvePlaceholders).orElse(null);
        this.dataSourceBeanName = Optional.ofNullable(this.dataSourceBeanName).map(getEnvironment()::resolvePlaceholders).orElse(null);

    }

    private Environment getEnvironment() {
        return this.applicationContext.getEnvironment();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private String updatePropertyValue(String propertyName, PropertyValues values) {
        PropertyValue property = values.getPropertyValue(propertyName);

        if (property == null) {
            return null;
        }

        Object value = property.getValue();

        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return value.toString();
        } else if (value instanceof TypedStringValue) {
            return ((TypedStringValue) value).getValue();
        } else {
            return null;
        }
    }
}
