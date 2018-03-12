package de.heisluft.capi.resources;

import de.heisluft.annotation.FieldsAreNonNullByDefault;
import de.heisluft.annotation.MethodsReturnNonNullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonNullByDefault
@FieldsAreNonNullByDefault
public class MissingResourceException extends Exception {
	private final ResourceLocation location;

	public MissingResourceException(ResourceLocation location) {
		super("Could not find resource '" + location + "'");
		this.location = location;
	}

	public ResourceLocation getLocation() {
		return location;
	}
}
