package uia.dao.spring.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DaoScannerRegistrar.class)
public @interface DaoScan {

    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    Class<? extends Annotation> annotationClass() default Annotation.class;

    String dataSourceRef() default "dataSource";

    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;
}
