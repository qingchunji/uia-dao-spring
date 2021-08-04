package uia.dao.spring.dao;

import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

/**
 * @author ks015774
 * @since 2021-08-03 11:59:00
 */
@SuppressWarnings("rawtypes")
public class ClassPathDaoScanner extends ClassPathBeanDefinitionScanner {

    private String dataSourceBeanName;

    private Class<? extends Annotation> annotationClass;

    private Class<? extends DaoFactoryBean> daoFactoryBeanClass;

    private Class<?> markerInterface;

    public ClassPathDaoScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }


    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setDaoFactoryBeanClass(Class<? extends DaoFactoryBean> daoFactoryBeanClass) {
        this.daoFactoryBeanClass = daoFactoryBeanClass;
    }

    public void setDataSourceBeanName(String dataSourceBeanName) {
        this.dataSourceBeanName = dataSourceBeanName;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    public void registerFilters() {
        boolean acceptAllInterfaces = true;

        // if specified, use the given annotation and / or marker interface
        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        // override AssignableTypeFilter to ignore matches on the actual marker interface
        if (this.markerInterface != null) {
            addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            // default include filter that accepts all classes
            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (!beanDefinitions.isEmpty()) {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        AbstractBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (AbstractBeanDefinition) holder.getBeanDefinition();
            if (ScopedProxyFactoryBean.class.getName().equals(definition.getBeanClassName())) {
                definition = (AbstractBeanDefinition) Optional
                        .ofNullable(((RootBeanDefinition) definition).getDecoratedDefinition())
                        .map(BeanDefinitionHolder::getBeanDefinition).orElseThrow(() -> new IllegalStateException(
                                "The target bean definition of scoped proxy bean not found. Root bean definition[" + holder + "]"));
            }
            String beanClassName = definition.getBeanClassName();

            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            definition.setBeanClass(this.daoFactoryBeanClass);
            if (StringUtils.hasText(this.dataSourceBeanName)) {
                definition.getPropertyValues().add("dataSource",
                        new RuntimeBeanReference(this.dataSourceBeanName));
            }

            definition.getPropertyValues().add("daoFactory",
                    new RuntimeBeanReference("daoFactory"));


        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && (metadata.isConcrete() || metadata.isAbstract());
    }
}
