package model;

import java.util.Observable;

import javax.swing.JSlider;

/**
 * Realizes the concept of parameter modifiable by the user.
 * 
 * <p>
 * An instantiation of this class must specifies the type of parameter that must
 * be {@code Integer} of {@code Double}. The vale of the parameter have
 * initially the initial value given in the constructor together with the name
 * and the the limits within which the value can oscillate.
 * 
 * <p>
 * Since the {@link JSlider}, that mainly use {@code InputParameters}, works
 * only with {@code int} numbers the class provides methods for getting and
 * setting the value of the parameter either using the appropriate number type
 * that {@code int}. If the type {@code N} is {@code Double} the value taken by
 * a {@code JSlider} is divided by 100 and stored as a {@code Double} number, it
 * is then multiplied, when it's asked as an int value, by the same number.
 * Instead if the type {@code N} is {@code Integer} the value does not receive
 * any modification.
 * 
 * @author Francesco Del Duchetto
 * 
 * @param <N>
 *            type which must be {@link Integer} or {@link Double} obtaining
 *            otherwise a {@link IllegalArgumentException}.
 */
public class InputParameter<N extends Number> extends Observable {

	/**
	 * The name of the parameter.
	 */
	private String name;

	/**
	 * The value of the parameter.
	 */
	private volatile N value;

	/**
	 * The maximum number that the value can be.
	 */
	private N maxValue;

	/**
	 * The minimum number that the value can be.
	 */
	private N minValue;

	/**
	 * The multiplier used for the instances with N = {@code Double}.
	 */
	private static final int DOUBLE_MULTIPLIER = 100;

	/**
	 * The multiplier used for the instances with N = {@code Integer}.
	 */
	private static final int INTEGER_MULTIPLIER = 1;

	/**
	 * The multiplier which allows to return always a good value for the
	 * sliders.
	 */
	private int multiplier;

	/**
	 * Construct an {@code InputParameter}.
	 * 
	 * @param name
	 *            the name of the parameter, that maybe will be shown on the
	 *            view.
	 * @param minValue
	 *            the minimum value that the parameter can assume.
	 * @param maxValue
	 *            the maximum value that the parameter can assume.
	 * @param initialValue
	 *            the intial value that the parameter assume.
	 */
	public InputParameter(final String name, final N minValue,
			final N maxValue, final N initialValue) {
		super();
		this.name = name;
		this.value = initialValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		if (initialValue instanceof Integer) {
			this.multiplier = INTEGER_MULTIPLIER;
		} else if (initialValue instanceof Double) {
			this.multiplier = DOUBLE_MULTIPLIER;
		} else {
			throw new IllegalArgumentException("Parameter not supported.");
		}
	}

	/**
	 * @return the parameter value.
	 */
	public final N getValue() {
		return value;
	}

	/**
	 * @return the maximum value that the parameter can assume.
	 */
	public final N getMaxValue() {
		return maxValue;
	}

	/**
	 * @return return the minimum value that the parameter can assume.
	 */
	public final N getMinValue() {
		return minValue;
	}

	/**
	 * @return return the parameter value modified to assume a reasonable
	 *         meaning for a {@code JSlider}.
	 */
	public final int getIntValue() {
		return (int) (value.doubleValue() * multiplier);
	}

	/**
	 * @return return the maximum value of the parameter modified to assume a
	 *         reasonable meaning for a {@code JSlider}.
	 */
	public final int getIntMaxValue() {
		return (int) (maxValue.doubleValue() * multiplier);
	}

	/**
	 * @return return the minimum value of the modified to assume a reasonable
	 *         meaning for a {@code JSlider}.
	 */
	public final int getIntMinValue() {
		return (int) (minValue.doubleValue() * multiplier);
	}

	/**
	 * @return the name of the parameter.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Set the value of the parameter.
	 * 
	 * @param value
	 *            that the parameter must assume.
	 */
	public final void setValue(final N value) {
		if (value.doubleValue() < minValue.doubleValue()
				|| value.doubleValue() > maxValue.doubleValue()) {
			throw new IllegalArgumentException();
		}
		this.value = value;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Set the value, that will be normalized, of the parameter.
	 * 
	 * @param intValue
	 *            the value that the parameter must assume.
	 */
	@SuppressWarnings("unchecked")
	public final void setIntValue(final int intValue) {
		Double newValue = (double) (intValue) / multiplier;
		if (value instanceof Integer) {
			Integer integerValue = newValue.intValue();
			setValue((N) integerValue);
		} else if (value instanceof Double) {
			setValue((N) newValue);
		}
	}
}
