package cuina.editor.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Beschreibt die Parameter-Namen einer Funktion, wie sie im Skript-Editor dargestellt werden.
 * @author TheWhiteShadow
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Parameters
{
	public String[] names() default {};
	public String[] types() default {};
	public String returnType() default "void";
}
