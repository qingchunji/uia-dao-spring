package uia.dao.spring.annotation;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

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
