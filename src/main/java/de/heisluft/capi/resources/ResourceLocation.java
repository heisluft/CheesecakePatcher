package de.heisluft.capi.resources;

import de.heisluft.annotation.FieldsAreNonNullByDefault;
import de.heisluft.annotation.MethodsReturnNonNullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A ResourceLocation describes a path to a resource. The resourceDomain should be your mod name, otherwise it will default to 'cheesecake'.
 */
@FieldsAreNonNullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonNullByDefault
public class ResourceLocation implements Comparable<ResourceLocation> {

	private static final Pattern SPLIT_PATTERN = Pattern.compile(":");

	private final String resourceDomain, location;

	/**
	 * Constructs a new ResourceLocation by a fully qualified resource name, e.g. "cheesecake:this/is/a/test.png".
	 * If no resourceDomain is given, it will default to "cheesecake".
	 *
	 * @param name the name of the Resource
	 */
	public ResourceLocation(String name) {
		this(null, name);
	}

	@Override
	public String toString() {
		return resourceDomain + ":" + location;
	}

	public ResourceLocation(@Nullable String resourceDomain, String location) {
		if(resourceDomain != null && !resourceDomain.isEmpty()) {
			this.location = location;
			this.resourceDomain = resourceDomain;
		}
		else if(location.contains(":")) {
			String[] split = SPLIT_PATTERN.split(location, 2);
			this.resourceDomain = split[0];
			this.location = split[1];
		}
		else {
			this.resourceDomain = "cheesecake";
			this.location = location;
		}
	}

	public boolean equals(Object anObject) {
		if (this == anObject) return true;
		else if(!(anObject instanceof ResourceLocation)) return false;
		else {
			ResourceLocation loc = (ResourceLocation)anObject;
			return resourceDomain.equals(loc.resourceDomain) && this.location.equals(loc.location);
		}
	}

	public String getLocation() {
		return location;
	}

	public String getResourceDomain() {
		return resourceDomain;
	}

	public int compareTo(ResourceLocation other) {
		int i = resourceDomain.compareTo(other.resourceDomain);
		if (i == 0) i = location.compareTo(other.location);
		return i;
	}

	@Override
	public int hashCode() {
		return Objects.hash(resourceDomain, location);
	}
}
