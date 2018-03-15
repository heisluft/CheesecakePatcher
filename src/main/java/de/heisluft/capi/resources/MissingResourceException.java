package de.heisluft.capi.resources;

import de.heisluft.annotation.FieldsAreNonNullByDefault;

/**
 * A {@link MissingResourceException} is used to describe an error
 * while trying to retrieve the resource described by a specified {@link ResourceLocation}
 *
 * @author Heisluft
 */
@FieldsAreNonNullByDefault
public class MissingResourceException extends Exception {

	/** The resource */
	private final ResourceLocation location;

	/**
	 * Constructs a new {@link MissingResourceException} with the default error message.
	 *
	 * @param resource The nonnull {@link ResourceLocation} which could not be found
	 */
	public MissingResourceException(ResourceLocation resource) {
		this(resource, "Could not find resource '" + resource + "'");
	}
	/**
	 * Constructs a new {@link MissingResourceException} with a custom message.
	 *
	 * @param resource The nonnull {@link ResourceLocation} which could not be found
	 * @param message The nonnull Error message
	 */
	public MissingResourceException(ResourceLocation resource, String message) {
		super(message);
		this.location = resource;
	}

	/**
	 * Returns the {@link ResourceLocation} describing the resource which couldn't be found.
	 *
	 * @return The resource
	 */
	public ResourceLocation getErroredResource() {
		return location;
	}
}
