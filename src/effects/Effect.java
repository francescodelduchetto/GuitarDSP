package effects;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Observer;

import model.InputParameter;

/**
 * A generic effect.
 * 
 * <p>
 * Each effect must implement this "contract" which specifies that it must
 * supply this functions:
 * <ul>
 * <li>process a buffer;
 * <li>initialize the effect;
 * <li>update the effect;
 * <li>return the parameters that the effect use;
 * <li>get the name of the effect and if it's showable on the view, even by
 * reflection.
 * <ul>
 * 
 * @author Francesco Del Duchetto
 */
public interface Effect extends Observer {

	/**
	 * Apply the effect on the buffer given altering it.
	 * 
	 * @param audioIn
	 *            the input buffer.
	 * @param shortsRead
	 *            the length of the input buffer.
	 */
	void process(short[] audioIn, int shortsRead);

	/**
	 * Initialize the effect making it ready to a new streaming.
	 */
	void initialize();

	/**
	 * @return the {@code List} of {@link InputParameter} that the effect uses.
	 */
	List<InputParameter<? extends Number>> getInputParameters();
	
	void touchpadEvent(int touchX, int touchY, int pressure);

	/**
	 * Annotation that specifies the name of the effect and if it's show-able on
	 * the view. It allows to get this informations by reflection, so it is not
	 * necessary to have an instance of the effect.
	 */
	@Documented
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Attributes {

		/**
		 * 
		 * @return the name of the {@code Effect}.
		 */
		String name();

		/**
		 * @return true if the {@code Effect} is show-able to user, false
		 *         otherwise.
		 */
		boolean isShowable() default true;
	}
}